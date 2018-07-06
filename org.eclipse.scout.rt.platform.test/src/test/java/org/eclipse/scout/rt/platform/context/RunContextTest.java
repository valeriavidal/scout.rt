/*******************************************************************************
 * Copyright (c) 2010-2017 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.platform.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.security.auth.Subject;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.PlatformException;
import org.eclipse.scout.rt.platform.job.IBlockingCondition;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.job.Jobs;
import org.eclipse.scout.rt.platform.logger.DiagnosticContextValueProcessor.IDiagnosticContextValueProvider;
import org.eclipse.scout.rt.platform.nls.NlsLocale;
import org.eclipse.scout.rt.platform.security.SimplePrincipal;
import org.eclipse.scout.rt.platform.transaction.ITransaction;
import org.eclipse.scout.rt.platform.transaction.ITransactionMember;
import org.eclipse.scout.rt.platform.transaction.TransactionScope;
import org.eclipse.scout.rt.platform.util.Assertions.AssertionException;
import org.eclipse.scout.rt.platform.util.concurrent.ICancellable;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.testing.platform.runner.PlatformTestRunner;
import org.eclipse.scout.rt.testing.platform.util.BlockingCountDownLatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.MDC;

@RunWith(PlatformTestRunner.class)
public class RunContextTest {

  private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

  @Test
  public void testEmpty() {
    RunContext runContext = RunContexts.empty();
    assertNull(runContext.getSubject());
    assertNull(runContext.getLocale());
    assertTrue(toSet(runContext.getPropertyMap().iterator()).isEmpty());
    assertNull(runContext.getCorrelationId());
    assertNull(runContext.getTransaction());
    assertEquals(TransactionScope.REQUIRED, runContext.getTransactionScope());

    assertNotNull(runContext.getRunMonitor());
    RunContexts.empty().run(new IRunnable() {

      @Override
      public void run() throws Exception {
        RunMonitor currentMonitor = RunMonitor.CURRENT.get();
        RunMonitor newMonitor = RunContexts.empty().getRunMonitor();
        assertNotSame(currentMonitor, newMonitor);
        assertFalse(currentMonitor.getCancellables().contains(newMonitor));
      }
    });
  }

  @Test
  public void testCopy() {
    final Subject subject = newSubject("john");
    final ITransaction tx = mock(ITransaction.class);
    final ITransactionMember txMember1 = mock(ITransactionMember.class);
    when(txMember1.getMemberId()).thenReturn("txMember1");
    final ITransactionMember txMember2 = mock(ITransactionMember.class);
    when(txMember2.getMemberId()).thenReturn("txMember2");
    final RunMonitor monitor = new RunMonitor();

    // prepare the RunContext to be copied
    RunContext runContext = RunContexts.empty()
        .withProperty("key", "value")
        .withSubject(subject)
        .withLocale(Locale.CANADA_FRENCH)
        .withRunMonitor(monitor)
        .withCorrelationId("cid")
        .withTransaction(tx)
        .withTransactionScope(TransactionScope.MANDATORY)
        .withThreadLocal(THREAD_LOCAL, "thread-local");

    // test
    RunContext copy = runContext.copy();

    // verify
    assertEquals(toSet(runContext.getPropertyMap().iterator()), toSet(copy.getPropertyMap().iterator()));
    assertSame(runContext.getSubject(), copy.getSubject());
    assertSame(runContext.getLocale(), copy.getLocale());
    assertSame(runContext.getRunMonitor(), copy.getRunMonitor());
    assertEquals("cid", runContext.getCorrelationId());
    assertSame(tx, runContext.getTransaction());
    assertEquals("thread-local", runContext.getThreadLocal(THREAD_LOCAL));

    // test running on the copy
    copy
        .withTransactionScope(TransactionScope.REQUIRED)
        .run(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertEquals("value", PropertyMap.CURRENT.get().get("key"));
            assertSame(subject, Subject.getSubject(AccessController.getContext()));
            assertEquals(Locale.CANADA_FRENCH, NlsLocale.CURRENT.get());
            assertSame(monitor, RunMonitor.CURRENT.get());
            assertEquals("cid", CorrelationId.CURRENT.get());
            assertSame(tx, ITransaction.CURRENT.get());
            assertEquals("thread-local", THREAD_LOCAL.get());

            RunContexts.copyCurrent().run(new IRunnable() {

              @Override
              public void run() throws Exception {
                assertEquals("value", PropertyMap.CURRENT.get().get("key"));
                assertSame(subject, Subject.getSubject(AccessController.getContext()));
                assertEquals(Locale.CANADA_FRENCH, NlsLocale.CURRENT.get());
                assertNotSame(monitor, RunMonitor.CURRENT.get());
                assertEquals("cid", CorrelationId.CURRENT.get());
                assertSame(tx, ITransaction.CURRENT.get());
                assertEquals("thread-local", THREAD_LOCAL.get());
              }
            });

            RunContexts.empty().run(new IRunnable() {

              @Override
              public void run() throws Exception {
                assertNull(PropertyMap.CURRENT.get().get("key"));
                assertNull(Subject.getSubject(AccessController.getContext()));
                assertNull(NlsLocale.CURRENT.get());
                assertNotSame(monitor, RunMonitor.CURRENT.get());
                assertNull(CorrelationId.CURRENT.get());
                assertNotSame(tx, ITransaction.CURRENT.get());
                assertEquals("thread-local", THREAD_LOCAL.get());
              }
            });
          }
        });

    // test copy of transaction members
    runContext
        .withTransactionScope(TransactionScope.REQUIRES_NEW)
        .withTransactionMember(txMember1)
        .withTransactionMember(txMember2)
        .copy()
        .run(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertSame(txMember1, ITransaction.CURRENT.get().getMember("txMember1"));
            assertSame(txMember2, ITransaction.CURRENT.get().getMember("txMember2"));
          }
        });
  }

  @Test
  public void testCopySubject() {
    RunContexts.empty().run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertNull(RunContexts.copyCurrent().getSubject());
        assertNull(Subject.getSubject(AccessController.getContext()));
      }
    });

    final Subject john = newSubject("john");
    RunContexts.empty().withSubject(john).run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertSame(john, RunContexts.copyCurrent().getSubject());
        assertSame(Subject.getSubject(AccessController.getContext()), RunContexts.copyCurrent().getSubject());

        // Change Subject directly
        final Subject anna = newSubject("anna");
        Subject.doAs(anna, new PrivilegedExceptionAction<Void>() {

          @Override
          public Void run() throws Exception {
            // Test copy via 'RunContexts.copyCurrent'
            assertSame(anna, RunContexts.copyCurrent().getSubject());
            RunContexts.copyCurrent().run(new IRunnable() {

              @Override
              public void run() throws Exception {
                assertSame(anna, RunContexts.copyCurrent().getSubject());
                assertSame(anna, Subject.getSubject(AccessController.getContext()));
              }
            });

            RunContext.CURRENT.get().run(new IRunnable() {

              @Override
              public void run() throws Exception {
                assertSame(john, RunContexts.copyCurrent().getSubject());
                assertSame(john, Subject.getSubject(AccessController.getContext()));
              }
            });

            // Test copy via direct 'RunContext.copy'
            assertEquals(john, RunContext.CURRENT.get().copy().getSubject());
            Jobs.schedule(new IRunnable() {

              @Override
              public void run() throws Exception {
                assertSame(john, RunContexts.copyCurrent().getSubject());
                assertSame(john, Subject.getSubject(AccessController.getContext()));
              }
            }, Jobs.newInput()
                .withRunContext(RunContext.CURRENT.get().copy()))
                .awaitDoneAndGet();
            return null;
          }
        });
      }
    });

    RunContexts.empty().withSubject(null).run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertNull(RunContexts.copyCurrent().getSubject());
        assertNull(Subject.getSubject(AccessController.getContext()));
      }
    });
  }

  @Test
  public void testCopyCurrent_PropertyMap() {
    RunContexts.empty().run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertNotNull(RunContexts.copyCurrent());
        assertTrue(toSet(RunContexts.copyCurrent().getPropertyMap().iterator()).isEmpty());
      }
    });
    RunContexts.empty().withProperties(Collections.singletonMap("prop", "value")).run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertNotSame(PropertyMap.CURRENT.get(), RunContexts.copyCurrent().getPropertyMap()); // test for copy
        assertEquals(toSet(PropertyMap.CURRENT.get().iterator()), toSet(RunContexts.copyCurrent().getPropertyMap().iterator()));
      }
    });
  }

  @Test
  public void testCopyLocale() {
    RunContexts.empty().withLocale(null).run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertNull(RunContexts.copyCurrent().getLocale());
        assertNull(NlsLocale.CURRENT.get());
      }
    });

    RunContexts.empty().withLocale(Locale.CANADA_FRENCH).run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertEquals(Locale.CANADA_FRENCH, NlsLocale.CURRENT.get());
        assertEquals(Locale.CANADA_FRENCH, RunContexts.copyCurrent().getLocale());
        assertEquals(Locale.KOREAN, RunContexts.copyCurrent().withLocale(Locale.KOREAN).getLocale());

        // Change Locale directly via 'thread-local'
        NlsLocale.CURRENT.set(Locale.ITALY);
        assertEquals(Locale.ITALY, RunContexts.copyCurrent().getLocale());
        RunContexts.copyCurrent().run(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertEquals(Locale.ITALY, NlsLocale.get());
          }
        });

        // Test copy via 'RunContexts.copyCurrent'
        Jobs.schedule(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertEquals(Locale.ITALY, NlsLocale.get());
          }
        }, Jobs.newInput()
            .withRunContext(RunContexts.copyCurrent()))
            .awaitDoneAndGet();

        // Test copy via direct 'RunContext.copy'
        assertEquals(Locale.CANADA_FRENCH, RunContext.CURRENT.get().copy().getLocale());
        Jobs.schedule(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertEquals(Locale.CANADA_FRENCH, NlsLocale.get());
            assertEquals(Locale.CANADA_FRENCH, RunContexts.copyCurrent().getLocale());
          }
        }, Jobs.newInput()
            .withRunContext(RunContext.CURRENT.get().copy()))
            .awaitDoneAndGet();

      }
    });
  }

  @Test
  public void testCopyRunMonitor() {
    RunContexts.empty().run(new IRunnable() {

      @Override
      public void run() throws Exception {
        final RunMonitor currentMonitor = RunMonitor.CURRENT.get();
        assertNotNull(currentMonitor);

        // Test new run monitor and no registration as child monitor
        RunMonitor childMonitor = RunContexts.copyCurrent().getRunMonitor();
        assertNotSame(currentMonitor, childMonitor);
        assertFalse(currentMonitor.getCancellables().contains(childMonitor));

        // Test specific monitor and no registration as child monitor
        final RunMonitor specificRunMonitor = BEANS.get(RunMonitor.class);
        assertSame(specificRunMonitor, RunContexts.copyCurrent().withRunMonitor(specificRunMonitor).getRunMonitor());
        assertFalse(currentMonitor.getCancellables().contains(RunContexts.copyCurrent().withRunMonitor(specificRunMonitor).getRunMonitor()));
        RunContexts.copyCurrent().withRunMonitor(specificRunMonitor).run(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertSame(specificRunMonitor, RunMonitor.CURRENT.get());
          }
        });

        // Test new run monitor and no registration as child monitor (empty)
        RunMonitor detachedMonitor = RunContexts.empty().getRunMonitor();
        assertNotSame(currentMonitor, detachedMonitor);
        assertFalse(currentMonitor.getCancellables().contains(detachedMonitor));

        // Change monitor directly via 'thread-local'
        final RunMonitor newMonitor = BEANS.get(RunMonitor.class);
        RunMonitor.CURRENT.set(newMonitor);

        // Test copy via 'RunContexts.copyCurrent'
        assertNotSame(newMonitor, RunContexts.copyCurrent().getRunMonitor());
        assertNotSame(currentMonitor, RunContexts.copyCurrent().getRunMonitor());
        assertFalse(newMonitor.getCancellables().contains(RunContexts.copyCurrent().getRunMonitor()));
        Jobs.schedule(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertNotNull(RunMonitor.CURRENT.get());
            assertNotSame(newMonitor, RunMonitor.CURRENT.get());
            assertNotSame(currentMonitor, RunMonitor.CURRENT.get());
            assertTrue(newMonitor.getCancellables().contains(RunMonitor.CURRENT.get()));
          }
        }, Jobs.newInput()
            .withRunContext(RunContexts.copyCurrent()))
            .awaitDoneAndGet();

        // Test copy via direct 'RunContext.copy'
        assertSame(currentMonitor, RunContext.CURRENT.get().copy().getRunMonitor());
        Jobs.schedule(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertSame(currentMonitor, RunMonitor.CURRENT.get());
          }
        }, Jobs.newInput()
            .withRunContext(RunContext.CURRENT.get().copy()))
            .awaitDoneAndGet();

      }
    });
  }

  @Test(expected = AssertionException.class)
  public void testCopyCurrent_NullRunMonitor() {
    RunContexts.copyCurrent().withRunMonitor(null);
  }

  @Test(expected = AssertionException.class)
  public void testEmpty_NullRunMonitor() {
    RunContexts.empty().withRunMonitor(null);
  }

  @Test
  public void testCopyCurrent_TransactionScope() {
    RunContexts.empty().run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertEquals(TransactionScope.REQUIRED, RunContexts.copyCurrent().getTransactionScope());
      }
    });
    RunContexts.empty().withTransactionScope(TransactionScope.REQUIRES_NEW).run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertEquals(TransactionScope.REQUIRED, RunContexts.copyCurrent().getTransactionScope());
      }
    });
  }

  @Test
  public void testCopyCurrent_Transaction() {
    final ITransaction tx = mock(ITransaction.class);

    RunContexts.empty().withTransaction(tx).run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertEquals(tx, RunContexts.copyCurrent().getTransaction());
      }
    });

    RunContexts.empty().withTransaction(null).run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertNotNull(ITransaction.CURRENT.get());
        assertNotNull(RunContexts.copyCurrent().getTransaction());
        assertNull(RunContext.CURRENT.get().getTransaction());

        RunContexts.copyCurrent().run(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertNotNull(ITransaction.CURRENT.get());
          }
        });
      }
    });

    RunContexts.empty().withTransaction(tx).run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertSame(tx, ITransaction.CURRENT.get());
        assertSame(tx, RunContexts.copyCurrent().getTransaction());
        assertSame(tx, RunContext.CURRENT.get().getTransaction());

        RunContexts.copyCurrent().run(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertSame(tx, ITransaction.CURRENT.get());
            assertSame(tx, RunContexts.copyCurrent().getTransaction());
            assertSame(tx, RunContext.CURRENT.get().getTransaction());
          }
        });
      }
    });
  }

  @Test
  public void testCopyCurrent_TransactionMember() {
    final ITransactionMember txMember = mock(ITransactionMember.class);
    when(txMember.getMemberId()).thenReturn("txMember");

    RunContexts.empty().withTransactionMember(txMember).run(new IRunnable() {

      @Override
      public void run() throws Exception {
        final ITransaction tx = ITransaction.CURRENT.get();
        assertNotNull(tx);
        assertSame(txMember, ITransaction.CURRENT.get().getMember("txMember"));

        RunContexts.copyCurrent()
            .withTransactionScope(TransactionScope.REQUIRED)
            .run(new IRunnable() {

              @Override
              public void run() throws Exception {
                assertSame(tx, ITransaction.CURRENT.get());
                assertSame(txMember, ITransaction.CURRENT.get().getMember("txMember"));
              }
            });
        RunContexts.copyCurrent()
            .withTransactionScope(TransactionScope.REQUIRES_NEW)
            .run(new IRunnable() {

              @Override
              public void run() throws Exception {
                assertNotSame(tx, ITransaction.CURRENT.get());
                assertNull(ITransaction.CURRENT.get().getMember("txMember"));
              }
            });
        RunContexts.copyCurrent()
            .withTransactionScope(TransactionScope.MANDATORY)
            .run(new IRunnable() {

              @Override
              public void run() throws Exception {
                assertSame(tx, ITransaction.CURRENT.get());
                assertSame(txMember, ITransaction.CURRENT.get().getMember("txMember"));
              }
            });
      }
    });
  }

  @Test
  public void testTransactionMember_TxRequiresNew() {
    final ITransactionMember txMember = mock(ITransactionMember.class);
    when(txMember.getMemberId()).thenReturn("abc");
    when(txMember.needsCommit()).thenReturn(true);
    when(txMember.commitPhase1()).thenReturn(true);

    RunContexts.empty()
        .withTransactionScope(TransactionScope.REQUIRES_NEW)
        .withTransactionMember(txMember)
        .run(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertSame(txMember, ITransaction.CURRENT.get().getMember("abc"));
          }
        });

    InOrder inOrder = Mockito.inOrder(txMember);
    inOrder.verify(txMember, times(1)).commitPhase1();
    inOrder.verify(txMember, times(1)).commitPhase2();
    inOrder.verify(txMember, never()).rollback();
    inOrder.verify(txMember, times(1)).release();
  }

  @Test(expected = AssertionException.class)
  public void testTransactionMember_TxRequired_TxPresent() {
    final ITransactionMember txMember = mock(ITransactionMember.class);

    RunContexts.empty()
        .withTransactionScope(TransactionScope.REQUIRES_NEW)
        .run(new IRunnable() {

          @Override
          public void run() throws Exception {
            RunContexts.copyCurrent()
                .withTransactionScope(TransactionScope.REQUIRED)
                .withTransactionMember(txMember)
                .run(mock(IRunnable.class));
          }
        });
  }

  @Test
  public void testTransactionMember_TxRequired_TxNotPresent() {
    final ITransactionMember txMember = mock(ITransactionMember.class);
    when(txMember.getMemberId()).thenReturn("abc");
    when(txMember.needsCommit()).thenReturn(true);
    when(txMember.commitPhase1()).thenReturn(true);

    RunContexts.empty()
        .withTransactionScope(TransactionScope.REQUIRED)
        .withTransactionMember(txMember)
        .run(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertSame(txMember, ITransaction.CURRENT.get().getMember("abc"));
          }
        });

    InOrder inOrder = Mockito.inOrder(txMember);
    inOrder.verify(txMember, times(1)).commitPhase1();
    inOrder.verify(txMember, times(1)).commitPhase2();
    inOrder.verify(txMember, never()).rollback();
    inOrder.verify(txMember, times(1)).release();
  }

  @Test
  public void testThreadLocal() {
    RunContexts.empty()
        .withThreadLocal(THREAD_LOCAL, "initial_value")
        .run(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertEquals("initial_value", THREAD_LOCAL.get());
          }
        });
  }

  @Test
  public void testCopyThreadLocal() {
    RunContexts.empty()
        .withThreadLocal(THREAD_LOCAL, "initial_value")
        .run(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertEquals("initial_value", THREAD_LOCAL.get());

            // Change ThreadLocal directly
            THREAD_LOCAL.set("updated_value");

            RunContexts.copyCurrent().run(new IRunnable() {

              @Override
              public void run() throws Exception {
                assertEquals("updated_value", THREAD_LOCAL.get());
              }
            });

            // Test copy via copyCurrent
            Jobs.schedule(new IRunnable() {

              @Override
              public void run() throws Exception {
                assertEquals("updated_value", THREAD_LOCAL.get());
              }
            }, Jobs.newInput()
                .withRunContext(RunContexts.copyCurrent()))
                .awaitDoneAndGet();

            // Test copy directly
            assertEquals("initial_value", RunContext.CURRENT.get().copy().getThreadLocal(THREAD_LOCAL));
            Jobs.schedule(new IRunnable() {

              @Override
              public void run() throws Exception {
                assertEquals("initial_value", THREAD_LOCAL.get());
                assertEquals("initial_value", RunContext.CURRENT.get().copy().getThreadLocal(THREAD_LOCAL));
              }
            }, Jobs.newInput()
                .withRunContext(RunContext.CURRENT.get().copy()))
                .awaitDoneAndGet();
          }
        });
  }

  @Test
  public void testCurrentRunContext() {
    final RunContext runContext = RunContexts.empty();

    runContext.run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertSame(runContext, RunContext.CURRENT.get());
      }
    });
  }

  @Test
  public void testHardCancellation() throws InterruptedException {
    final BlockingCountDownLatch setupLatch = new BlockingCountDownLatch(1);
    final BlockingCountDownLatch verifyLatch = new BlockingCountDownLatch(1);
    final AtomicBoolean interrupted = new AtomicBoolean();
    final RunMonitor monitor = BEANS.get(RunMonitor.class);

    Jobs.schedule(new IRunnable() {

      @Override
      public void run() throws Exception {
        RunContexts.empty()
            .withRunMonitor(monitor)
            .run(new IRunnable() {

              @Override
              public void run() throws Exception {
                try {
                  assertTrue(setupLatch.countDownAndBlock(10, TimeUnit.SECONDS));
                }
                catch (InterruptedException e) {
                  interrupted.set(true);
                }
                finally {
                  verifyLatch.countDown();
                }
              }
            });
      }
    }, Jobs.newInput());

    assertTrue(setupLatch.await());
    monitor.cancel(true);
    assertTrue(verifyLatch.await());
    assertTrue(interrupted.get());
  }

  @Test
  public void testCopyCurrentOrElseEmpty() {
    Jobs.schedule(new IRunnable() {

      @Override
      public void run() throws Exception {
        try {
          RunContexts.copyCurrent();
          fail("AssertionException expected because not running in a RunContext");
        }
        catch (AssertionException e) {
          // expected
        }

        try {
          RunContexts.copyCurrent(false);
          fail("AssertionException expected because not running in a RunContext");
        }
        catch (AssertionException e) {
          // expected
        }

        assertNotNull(RunContexts.copyCurrent(true));
      }
    }, Jobs.newInput()
        .withRunContext(null))
        .awaitDoneAndGet();
  }

  @Test
  public void testDiagnostics() {
    final ThreadLocal<String> diagnosticThreadLocal1 = new ThreadLocal<>();
    final ThreadLocal<String> diagnosticThreadLocal2 = new ThreadLocal<>();

    IDiagnosticContextValueProvider diagnostic1 = mock(IDiagnosticContextValueProvider.class);
    when(diagnostic1.key()).thenReturn("test-key-1");
    when(diagnostic1.value()).thenAnswer(new Answer<String>() {

      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
        return diagnosticThreadLocal1.get();
      }
    });

    IDiagnosticContextValueProvider diagnostic2 = mock(IDiagnosticContextValueProvider.class);
    when(diagnostic2.key()).thenReturn("test-key-2");
    when(diagnostic2.value()).thenAnswer(new Answer<String>() {

      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
        return diagnosticThreadLocal2.get();
      }
    });

    RunContexts.empty()
        .withDiagnostic(diagnostic1)
        .withDiagnostic(diagnostic2)
        .withThreadLocal(diagnosticThreadLocal1, "value-1")
        .withThreadLocal(diagnosticThreadLocal2, "value-2")
        .run(new IRunnable() {

          @Override
          public void run() throws Exception {
            assertEquals("value-1", MDC.get("test-key-1"));
            assertEquals("value-2", MDC.get("test-key-2"));

            RunContexts.copyCurrent()
                .withThreadLocal(diagnosticThreadLocal2, "ABC")
                .run(new IRunnable() {

                  @Override
                  public void run() throws Exception {
                    assertEquals("value-1", MDC.get("test-key-1"));
                    assertEquals("ABC", MDC.get("test-key-2"));
                  }
                });

            Jobs.schedule(new IRunnable() {

              @Override
              public void run() throws Exception {
                assertEquals("value-1", MDC.get("test-key-1"));
                assertEquals("value-2", MDC.get("test-key-2"));
              }
            }, Jobs.newInput()
                .withRunContext(RunContext.CURRENT.get()))
                .awaitDoneAndGet();
          }
        });
  }

  /**
   * Tests RunMonitor functionality with:
   * <ul>
   * <li>Current RunMonitor available</li>
   * <li>Explicit RunMonitor not set</li>
   * <li>Runnable throws an Exception</li>
   * </ul>
   * Expected: a new RunMonitor is CREATED, registered as child and set as CURRENT for the time of execution.
   */
  @Test
  public void testCopyCurrent_RunMonitorException() {
    final RunMonitor currentRunMonitor = new RunMonitor();
    RunMonitor.CURRENT.set(currentRunMonitor);

    RunContext runContext = RunContexts.copyCurrent();
    final RunMonitor newRunMonitor = runContext.getRunMonitor();

    assertNotSame("RunMonitor should not be same instance a current RunMonitor", currentRunMonitor, newRunMonitor);

    final Exception exception = new Exception();
    try {
      runContext.run(new IRunnable() {

        @Override
        public void run() throws Exception {
          throw exception;
        }
      });
    }
    catch (PlatformException e) {
      assertEquals(exception, e.getCause());
    }

    assertSame(currentRunMonitor, RunMonitor.CURRENT.get());
    assertSame(newRunMonitor, runContext.getRunMonitor());
    assertFalse("RunMonitor should not be registered within current RunMonitor anymore", currentRunMonitor.getCancellables().contains(newRunMonitor));
  }

  /**
   * Tests RunMonitor functionality with:
   * <ul>
   * <li>Current RunMonitor available</li>
   * <li>Explicit RunMonitor not set</li>
   * <li>Additional cancellables are registered as new child which are not removed until some time after completion of
   * run context</li>
   * </ul>
   * Expected: a new RunMonitor is CREATED, registered as child and set as CURRENT for the time of execution until the
   * last cancellable is removed.
   */
  @Test
  public void testCopyCurrent_RunMonitorLeftoverCancellables() {
    final RunMonitor currentRunMonitor = new RunMonitor();
    RunMonitor.CURRENT.set(currentRunMonitor);

    RunContext runContext = RunContexts.copyCurrent();
    final RunMonitor newRunMonitor = runContext.getRunMonitor();

    assertNotSame("Run monitor should not be same instance a current run monitor", currentRunMonitor, newRunMonitor);

    class LocalCancellable implements ICancellable {

      @Override
      public boolean isCancelled() {
        return false;
      }

      @Override
      public boolean cancel(boolean interruptIfRunning) {
        return true;
      }
    }
    final ICancellable c1 = new LocalCancellable();
    final ICancellable c2 = new LocalCancellable();

    runContext.run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertSame(newRunMonitor, RunMonitor.CURRENT.get());
        newRunMonitor.registerCancellable(c1);
        newRunMonitor.registerCancellable(c2);
        assertTrue("Run monitor should be registered within current run monitor", currentRunMonitor.getCancellables().contains(RunMonitor.CURRENT.get()));
      }
    });

    assertSame(currentRunMonitor, RunMonitor.CURRENT.get());
    assertSame(newRunMonitor, runContext.getRunMonitor());
    assertEquals(2, newRunMonitor.getCancellables().size());
    assertTrue("New run monitor should be registered within current run monitor as long a cancellables remain", currentRunMonitor.getCancellables().contains(newRunMonitor));

    // Remove cancellable 1/2
    newRunMonitor.unregisterCancellable(c1);
    assertEquals(1, newRunMonitor.getCancellables().size());
    assertTrue("New run monitor should be registered within current run monitor as long a cancellables remain", currentRunMonitor.getCancellables().contains(newRunMonitor));

    // Remove cancellable 2/2
    newRunMonitor.unregisterCancellable(c2);
    assertEquals(0, newRunMonitor.getCancellables().size());
    assertFalse("New run monitor should not be registered within current run monitor anymore", currentRunMonitor.getCancellables().contains(newRunMonitor));
  }

  /**
   * Tests RunMonitor functionality with:
   * <ul>
   * <li>Different parent current run monitors available</li>
   * <li>Explicit run monitor not set (e.g. new one created)</li>
   * <li>Additional cancellables are registered as new child which are not removed until some time after completion of
   * run context</li>
   * </ul>
   * Expected: One new run monitor is <b>created</b>, registered as child and set as <b>current</b> for the time of
   * execution until the last cancellable is removed.
   */
  @Test
  public void testCopyCurrent_RunMonitorLeftoverMultipleParent() {
    final RunMonitor currentRunMonitor1 = new RunMonitor();
    RunMonitor.CURRENT.set(currentRunMonitor1);

    RunContext runContext = RunContexts.copyCurrent();
    final RunMonitor newRunMonitor = runContext.getRunMonitor();

    assertNotSame("Run monitor should not be same instance a current run monitor", currentRunMonitor1, newRunMonitor);

    class LocalCancellable implements ICancellable {

      @Override
      public boolean isCancelled() {
        return false;
      }

      @Override
      public boolean cancel(boolean interruptIfRunning) {
        return true;
      }
    }
    final ICancellable c1 = new LocalCancellable();
    final ICancellable c2 = new LocalCancellable();

    runContext.run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertSame(newRunMonitor, RunMonitor.CURRENT.get());
        newRunMonitor.registerCancellable(c1);
        assertTrue("Run monitor should be registered within current run monitor", currentRunMonitor1.getCancellables().contains(RunMonitor.CURRENT.get()));
      }
    });

    // set new current run monitor
    assertSame(currentRunMonitor1, RunMonitor.CURRENT.get());
    final RunMonitor currentRunMonitor2 = new RunMonitor();
    RunMonitor.CURRENT.set(currentRunMonitor2);

    runContext.withParentRunMonitor(currentRunMonitor2).run(new IRunnable() {

      @Override
      public void run() throws Exception {
        assertSame(newRunMonitor, RunMonitor.CURRENT.get());
        newRunMonitor.registerCancellable(c2);
        assertTrue("Run monitor should be registered within current run monitor", currentRunMonitor1.getCancellables().contains(RunMonitor.CURRENT.get()));
      }
    });

    assertSame(currentRunMonitor2, RunMonitor.CURRENT.get());
    assertSame(newRunMonitor, runContext.getRunMonitor());
    assertEquals(2, newRunMonitor.getCancellables().size());

    assertTrue("New run monitor should be registered within parent run monitor as long a cancellables remain", currentRunMonitor1.getCancellables().contains(newRunMonitor));
    assertTrue("New run monitor should be registered within parent run monitor as long a cancellables remain", currentRunMonitor2.getCancellables().contains(newRunMonitor));

    // Remove cancellable 1/2
    newRunMonitor.unregisterCancellable(c1);
    assertTrue("New run monitor should be registered within parent run monitor as long a cancellables remain", currentRunMonitor1.getCancellables().contains(newRunMonitor));
    assertTrue("New run monitor should be registered within parent run monitor as long a cancellables remain", currentRunMonitor2.getCancellables().contains(newRunMonitor));

    // Remove cancellable 2/2
    newRunMonitor.unregisterCancellable(c2);
    assertFalse("New run monitor should not be registered within parent run monitor anymore", currentRunMonitor1.getCancellables().contains(newRunMonitor));
    assertFalse("New run monitor should not be registered within parent run monitor anymore", currentRunMonitor2.getCancellables().contains(newRunMonitor));
  }

  /**
   * <p>
   * Tests RunMonitor functionality with:
   * </p>
   * <ul>
   * <li>Current RunMonitor available</li>
   * <li>Explicit RunMonitor not set</li>
   * <li>Run context is used to start two {@link Jobs} in new threads</li>
   * </ul>
   * <p>
   * Expected: exactly one new RunMonitor is created, registered as child and set as current for the time of execution,
   * must be removed after the last job finishes.
   * </p>
   */
  @Test
  public void testCopyCurrent_RunMonitorTwoConcurrentJobs() {
    final RunMonitorEx currentRunMonitor = new RunMonitorEx();
    RunMonitor.CURRENT.set(currentRunMonitor);
    RunContext runContext = RunContexts.copyCurrent();
    final RunMonitor newRunMonitor = runContext.getRunMonitor();
    assertNotNull(newRunMonitor);
    assertNotSame(currentRunMonitor, newRunMonitor);
    assertSame(currentRunMonitor, runContext.getParentRunMonitor());

    final IBlockingCondition startedCondition1 = Jobs.newBlockingCondition(true);
    final IBlockingCondition startedCondition2 = Jobs.newBlockingCondition(true);
    final IBlockingCondition blockingCondition1 = Jobs.newBlockingCondition(true);
    final IBlockingCondition blockingCondition2 = Jobs.newBlockingCondition(true);

    assertFalse("New run monitor must not be registered with current run monitor", currentRunMonitor.containsCancellable(newRunMonitor));

    IFuture<Void> future1 = Jobs.schedule(new IRunnable() {

      @Override
      public void run() throws Exception {
        startedCondition1.setBlocking(false);
        blockingCondition1.waitFor();
        assertSame(newRunMonitor, RunMonitor.CURRENT.get());
        assertTrue("New run monitor must be registered with current run monitor", currentRunMonitor.containsCancellable(newRunMonitor));
      }
    }, Jobs.newInput().withRunContext(runContext));

    IFuture<Void> future2 = Jobs.schedule(new IRunnable() {

      @Override
      public void run() throws Exception {
        startedCondition2.setBlocking(false);
        blockingCondition2.waitFor();
        assertSame(newRunMonitor, RunMonitor.CURRENT.get());
        assertTrue("New run monitor must be registered with current run monitor", currentRunMonitor.containsCancellable(newRunMonitor));
      }
    }, Jobs.newInput().withRunContext(runContext));

    assertSame(currentRunMonitor, RunMonitor.CURRENT.get());

    // Wait for jobs to be started
    startedCondition1.waitFor();
    startedCondition2.waitFor();

    // Stop latter job first
    blockingCondition2.setBlocking(false);
    future2.awaitDoneAndGet();

    // Stop first job
    blockingCondition1.setBlocking(false);
    future1.awaitDoneAndGet();

    assertSame(currentRunMonitor, RunMonitor.CURRENT.get());
    assertFalse("New run monitor must not be registered with current run monitor", currentRunMonitor.containsCancellable(newRunMonitor));
  }

  private static Set<Object> toSet(Iterator<?> iterator) {
    Set<Object> set = new HashSet<>();
    while (iterator.hasNext()) {
      set.add(iterator.next());
    }
    return set;
  }

  private static Subject newSubject(String principal) {
    Subject subject = new Subject();
    subject.getPrincipals().add(new SimplePrincipal(principal));
    subject.setReadOnly();
    return subject;
  }

  private static class RunMonitorEx extends RunMonitor {

    public boolean containsCancellable(ICancellable cancellable) {
      return getCancellables().contains(cancellable);
    }
  }
}