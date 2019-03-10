package org.eclipse.scout.rt.platform.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadInterruptUtil {
  private static final Logger LOG = LoggerFactory.getLogger(ThreadInterruptUtil.class);

  public static void logInvoke(Object obj, String method, RunMonitor runMonitor) {
    /*
    LOG.info("INVOKE {}@{}.{}, runMonitor={}{}",
        obj.getClass().getSimpleName(),
        Integer.toHexString(obj.hashCode()),
        method,
        runMonitor,
        (runMonitor != null && runMonitor.isCancelled() ? "(cancelled)" : ""));
    */
  }

  public static void logCallCancel(Object obj, String method, boolean interrupt, Object target) {
    if (target == null) {
      return;
    }
    /*
    LOG.info("CALL_CANCEL {}@{}.{}, interrupt={}, target={}@{}",
        obj.getClass().getSimpleName(),
        Integer.toHexString(obj.hashCode()),
        method,
        interrupt,
        target.getClass().getSimpleName(),
        Integer.toHexString(target.hashCode()));
    */
  }

  public static void logCancel(Object obj, String method, boolean interrupt, Thread runner) {
    LOG.info("CANCEL {}@{}.{}, interrupt={}, runner={}",
        obj.getClass().getSimpleName(),
        Integer.toHexString(obj.hashCode()),
        method,
        interrupt,
        runner);
  }

  public static void logInterrupt(Object obj, String method, Thread thread) {
    LOG.info("INTERRUPT {}@{}.{}, thread={}",
        obj.getClass().getSimpleName(),
        Integer.toHexString(obj.hashCode()),
        method,
        thread);
  }

  public static void detectAndClearThreadInterruption(Object obj, String method) {
    if (Thread.currentThread().isInterrupted()) {
      LOG.warn("DETECTED_THREAD_INTERRUPTION {}@{}.{}, clearing interrupt status",
          obj.getClass().getSimpleName(),
          Integer.toHexString(obj.hashCode()),
          method);
      Thread.interrupted();
      LOG.info("CHECK_THREAD_UNINTERRUPTED {}@{}.{}, interrupted={}",
          obj.getClass().getSimpleName(),
          Integer.toHexString(obj.hashCode()),
          method,
          Thread.currentThread().isInterrupted());
    }
  }
}
