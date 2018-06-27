/*******************************************************************************
 * Copyright (c) 2010-2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.commons.html.internal;

import org.eclipse.scout.commons.html.HtmlHelper;
import org.eclipse.scout.commons.html.IHtmlContent;

/**
 * Buffer for expressions</br>
 * (not thread safe)
 *
 * @since 6.0 (backported)
 */
public abstract class AbstractExpressionBuilder implements CharSequence, IHtmlContent {

  private StringBuilder m_buf;

  private boolean m_newLineToBr = true;

  protected StringBuilder validate() {
    if (m_buf == null) {
      m_buf = new StringBuilder();
      build();
    }
    return m_buf;
  }

  protected void invalidate() {
    m_buf = null;
  }

  @Override
  public boolean isNewLineToBr() {
    return m_newLineToBr;
  }

  @Override
  public IHtmlContent withNewLineToBr(boolean newLineToBr) {
    m_newLineToBr = newLineToBr;
    return this;
  }

  protected abstract void build();

  @Override
  public int length() {
    return validate().length();
  }

  @Override
  public char charAt(int index) {
    return validate().charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return validate().subSequence(start, end);
  }

  @Override
  public String toString() {
    return validate().toString();
  }

  protected void append(CharSequence arg, boolean escape) {
    String toAppend = null;
    if (arg == null) {
      toAppend = "";
    }
    else if (escape) {
      if (isNewLineToBr()) {
        toAppend = escapeAndNewLineToBr(arg);
      }
      else {
        toAppend = escape(arg);
      }
    }
    else {
      toAppend = arg.toString();
    }
    m_buf.append(toAppend);
  }

  @Override
  public String toHtml() {
    return toString();
  }

  @Override
  public String toPlainText() {
    return HtmlHelper.toPlainText(toHtml());
  }

  /**
   * @return the escaped value.
   */
  protected String escape(Object value) {
    return HtmlHelper.escape(value.toString());
  }

  protected String escapeAndNewLineToBr(Object value) {
    return HtmlHelper.escapeAndNewLineToBr(value.toString());
  }
}