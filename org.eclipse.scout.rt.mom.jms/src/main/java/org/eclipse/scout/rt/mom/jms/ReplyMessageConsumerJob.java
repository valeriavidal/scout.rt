package org.eclipse.scout.rt.mom.jms;

import static org.eclipse.scout.rt.mom.jms.IJmsMomProperties.JMS_PROP_REPLY_ID;
import static org.eclipse.scout.rt.platform.util.Assertions.assertNotNull;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.eclipse.scout.rt.mom.api.IBiDestination;
import org.eclipse.scout.rt.mom.api.IMessage;
import org.eclipse.scout.rt.mom.api.IRequestListener;
import org.eclipse.scout.rt.mom.api.SubscribeInput;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.context.RunContext;
import org.eclipse.scout.rt.platform.context.RunMonitor;
import org.eclipse.scout.rt.platform.exception.ExceptionHandler;
import org.eclipse.scout.rt.platform.exception.PlatformException;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.job.Jobs;
import org.eclipse.scout.rt.platform.util.concurrent.ThreadInterruption;
import org.eclipse.scout.rt.platform.util.concurrent.ThreadInterruption.IRestorer;

public class ReplyMessageConsumerJob<REQUEST, REPLY> extends AbstractMessageConsumerJob<REQUEST> {
  protected final IRequestListener<REQUEST, REPLY> m_listener;

  public ReplyMessageConsumerJob(JmsMomImplementor mom, IJmsSessionProvider sessionProvider, IBiDestination<REQUEST, REPLY> destination, IRequestListener<REQUEST, REPLY> listener, SubscribeInput input) throws JMSException {
    super(mom, sessionProvider, destination, input);
    m_listener = listener;
  }

  @Override
  protected void onJmsMessage(final Message jmsRequest) throws JMSException {
    final String replyId = assertNotNull(jmsRequest.getStringProperty(JMS_PROP_REPLY_ID), "missing 'replyId' [msg={}]", jmsRequest);

    // Read and process the message asynchronously because JMS session is single-threaded. This allows concurrent message processing.
    // Unlike AutoAcknowledgeSubscriptionStrategy, a job is scheduled for 'single-threaded' mode to support cancellation (execution hint).
    final IFuture<Void> future = Jobs.schedule(() -> {
      handleMessageInRunContext(jmsRequest, replyId);
    }, m_mom.newJobInput()
        .withName("Receiving JMS message [dest={}]", m_destination)
        .withExecutionHint(replyId)); // Register for cancellation

    if (isSingleThreaded()) {
      future.awaitDone();
    }
  }

  @Override
  protected RunContext createRunContext() throws JMSException {
    return super.createRunContext()
        .withRunMonitor(RunMonitor.CURRENT.get()); // associate with the calling monitor to propagate cancellation
  }

  protected void handleMessageInRunContext(final Message jmsRequest, final String replyId) throws JMSException {
    final JmsMessageReader<REQUEST> messageReader = JmsMessageReader.newInstance(jmsRequest, m_marshaller);
    final IMessage<REQUEST> request = messageReader.readMessage();
    final Destination replyTopic = messageReader.readReplyTo();

    createRunContext()
        .withCorrelationId(messageReader.readCorrelationId())
        .withThreadLocal(IMessage.CURRENT, request)
        .run(() -> handleRequest(jmsRequest, request, replyId, replyTopic));
  }

  /**
   * Delegates the request to the listener, and returns the message to be replied.
   */
  protected void handleRequest(Message jmsRequest, IMessage<REQUEST> request, String replyId, Destination replyTopic) throws JMSException {
    Object transferObject = null;
    boolean success = true;
    try {
      transferObject = m_listener.onRequest(request);
    }
    catch (Throwable t) { // NOSONAR (Always send a response, even if a PlatformError is thrown. Otherwise the caller might wait forever.)
      BEANS.get(ExceptionHandler.class).handle(t);

      transferObject = interceptRequestReplyException(t);
      success = false;
    }

    if (IFuture.CURRENT.get().isCancelled()) {
      return;
    }

    IRestorer interruption = ThreadInterruption.clear(); // Temporarily clear the thread's interrupted status while sending a message.
    IJmsSessionProvider sessionProvider = m_mom.createSessionProvider();
    try {
      JmsMessageWriter replyMessageWriter = JmsMessageWriter.newInstance(sessionProvider.getSession(), m_marshaller)
          .writeReplyId(replyId)
          .writeTransferObject(transferObject)
          .writeRequestReplySuccess(success);
      m_mom.send(sessionProvider.getProducer(), replyTopic, replyMessageWriter, jmsRequest.getJMSDeliveryMode(), jmsRequest.getJMSPriority(), Message.DEFAULT_TIME_TO_LIVE);
    }
    finally {
      sessionProvider.close();
      interruption.restore();
    }
  }

  /**
   * Allows to intercept the exception if request processing failed.
   */
  protected Throwable interceptRequestReplyException(Throwable t) {
    Throwable interceptedThrowable = t;

    // Replace PlatformException to ensure serialization
    if (t instanceof PlatformException) {
      interceptedThrowable = new RuntimeException(t.getMessage());
    }

    // Unset cause and stracktrace (security)
    if (interceptedThrowable.getCause() == t) {
      interceptedThrowable.initCause(null);
    }
    interceptedThrowable.setStackTrace(new StackTraceElement[0]);

    return interceptedThrowable;
  }
}