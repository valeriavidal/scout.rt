/*******************************************************************************
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.client.job;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.security.auth.Subject;

import org.eclipse.scout.commons.Assertions;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.context.ClientContext;
import org.eclipse.scout.rt.platform.job.IJobManager;
import org.eclipse.scout.rt.shared.ui.UserAgent;

/**
 * Describes a model job with context information to be applied onto the executing worker thread during the time
 * of the job's execution.
 * <p/>
 * The 'setter-methods' returns <code>this</code> in order to support for method chaining.
 *
 * @see ClientContext
 * @see ModelJobs
 * @see IJobManager
 * @since 5.1
 */
public class ModelJobInput extends ClientJobInput {

  protected ModelJobInput(final ClientJobInput origin) {
    super(origin);
  }

  @Override
  public ModelJobInput id(final String id) {
    return (ModelJobInput) super.id(id);
  }

  @Override
  public ModelJobInput name(final String name) {
    return (ModelJobInput) super.name(name);
  }

  @Override
  public Object getMutex() {
    return Assertions.assertNotNull(getContext().getSession(), "For model jobs, the session must not be null because used as mutex object, so that no two jobs of the same session run in parallel at any time.");
  }

  @Override
  public ModelJobInput mutex(final Object mutexObject) {
    throw new UnsupportedOperationException("For model jobs, the mutex object cannot be set. Implicitly, the session is used as mutex object so that no two jobs of the same session run in parallel at any time.");
  }

  @Override
  public ModelJobInput expirationTime(final long time, final TimeUnit timeUnit) {
    return (ModelJobInput) super.expirationTime(time, timeUnit);
  }

  @Override
  public ModelJobInput context(final ClientContext context) {
    return (ModelJobInput) super.context(context);
  }

  @Override
  public ModelJobInput subject(final Subject subject) {
    return (ModelJobInput) super.subject(subject);
  }

  @Override
  public ModelJobInput locale(final Locale locale) {
    return (ModelJobInput) super.locale(locale);
  }

  @Override
  public ModelJobInput logOnError(final boolean logOnError) {
    return (ModelJobInput) super.logOnError(logOnError);
  }

  @Override
  public ModelJobInput session(final IClientSession session) {
    return (ModelJobInput) super.session(session);
  }

  @Override
  public ModelJobInput sessionRequired(final boolean sessionRequired) {
    return (ModelJobInput) super.sessionRequired(true);
  }

  @Override
  public ModelJobInput userAgent(final UserAgent userAgent) {
    return (ModelJobInput) super.userAgent(userAgent);
  }

  @Override
  public String getThreadName() {
    return "scout-model-thread";
  }

  //=== construction methods ===

  @Override
  public ModelJobInput copy() {
    return new ModelJobInput(this).context(getContext().copy());
  }

  public static ModelJobInput defaults() {
    return new ModelJobInput(ClientJobInput.defaults()).context(ClientContext.defaults());
  }

  public static ModelJobInput empty() {
    return new ModelJobInput(ClientJobInput.empty()).context(ClientContext.empty());
  }
}
