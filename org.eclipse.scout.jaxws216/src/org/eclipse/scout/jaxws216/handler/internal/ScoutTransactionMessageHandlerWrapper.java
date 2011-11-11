/*******************************************************************************
 * Copyright (c) 2011 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Daniel Wiehl (BSI Business Systems Integration AG) - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.jaxws216.handler.internal;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

import org.eclipse.scout.commons.RunnableWithException;
import org.eclipse.scout.jaxws216.internal.ScoutTransactionDelegate;
import org.eclipse.scout.rt.server.IServerSession;

import com.sun.xml.internal.ws.api.handler.MessageHandler;
import com.sun.xml.internal.ws.api.handler.MessageHandlerContext;

public class ScoutTransactionMessageHandlerWrapper<T extends MessageHandlerContext> implements MessageHandler<T>, IScoutTransactionHandlerWrapper<T> {

  private ScoutTransactionDelegate m_transactionDelegate;

  private MessageHandler<T> m_messageHandler;
  private IServerSession m_serverSession;

  public ScoutTransactionMessageHandlerWrapper(MessageHandler<T> messageHandler, IServerSession serverSession) {
    m_transactionDelegate = createTransactionDelegate();
    m_messageHandler = messageHandler;
    m_serverSession = serverSession;
  }

  @Override
  public boolean handleMessage(final T context) {
    RunnableWithException<Boolean> runnable = new RunnableWithException<Boolean>() {

      @Override
      public Boolean run() throws Throwable {
        return m_messageHandler.handleMessage(context);
      }
    };
    return m_transactionDelegate.runInTransaction(runnable, m_serverSession);
  }

  @Override
  public boolean handleFault(final T context) {
    RunnableWithException<Boolean> runnable = new RunnableWithException<Boolean>() {

      @Override
      public Boolean run() throws Throwable {
        return m_messageHandler.handleFault(context);
      }
    };
    return m_transactionDelegate.runInTransaction(runnable, m_serverSession);
  }

  @Override
  public void close(final MessageContext context) {
    RunnableWithException<Boolean> runnable = new RunnableWithException<Boolean>() {

      @Override
      public Boolean run() throws Throwable {
        m_messageHandler.close(context);
        return null;
      }
    };
    m_transactionDelegate.runInTransaction(runnable, m_serverSession);
  }

  @Override
  public Set<QName> getHeaders() {
    return m_messageHandler.getHeaders();
  }

  protected ScoutTransactionDelegate createTransactionDelegate() {
    return new ScoutTransactionDelegate();
  }

  @Override
  public Handler<T> getHandler() {
    return m_messageHandler;
  }
}
