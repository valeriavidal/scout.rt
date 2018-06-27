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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.scout.commons.CollectionUtility;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.commons.html.IHtmlElement;

/**
 * Builder for a html node with start tag, end tag and attributes.
 *
 * @since 6.0 (backported)
 */
public class HtmlNodeBuilder extends HtmlContentBuilder implements IHtmlElement {

  private final List<CharSequence> m_attributes = new ArrayList<CharSequence>();
  private String m_tag;

  protected String getTag() {
    return m_tag;
  }

  public HtmlNodeBuilder(String tag, CharSequence... texts) {
    this(tag, Arrays.asList(texts));
  }

  public HtmlNodeBuilder(String tag) {
    this(tag, new ArrayList<String>());
  }

  public HtmlNodeBuilder(String tag, List<? extends CharSequence> texts) {
    super(texts);
    m_tag = tag;
  }

  @Override
  public void build() {
    appendStartTag();
    if (getTexts().size() > 0) {
      appendText();
    }
    appendEndTag();
  }

  protected void appendStartTag() {
    append("<", false);
    append(getTag(), true);
    appendAttributes();
    append(">", false);
  }

  protected void appendEndTag() {
    append("</", false);
    append(getTag(), true);
    append(">", false);
  }

  private void appendAttributes() {
    if (m_attributes.size() > 0) {
      append(" ", false);
      append(CollectionUtility.format(m_attributes, " "), false);
    }
  }

  protected void addAttribute(String name, int value) {
    addAttribute(name, Integer.toString(value));
  }

  @Override
  public IHtmlElement addAttribute(String name, CharSequence value) {
    String attribValue = null;
    final String doubleQuote = "\"";
    if (value == null) {
      attribValue = "";
    }
    else {
      attribValue = StringUtility.replace(value.toString(), doubleQuote, "&quot;");
    }

    HtmlContentBuilder content = new HtmlContentBuilder(
        new HtmlPlainBuilder(escape(name)),
        new HtmlPlainBuilder("=" + doubleQuote),
        new HtmlPlainBuilder(attribValue),
        new HtmlPlainBuilder(doubleQuote));
    m_attributes.add(content);
    return this;
  }

/// GLOBAL ATTRIBUTES
  @Override
  public IHtmlElement style(CharSequence value) {
    addAttribute("style", value);
    return this;
  }

  @Override
  public IHtmlElement cssClass(CharSequence cssClass) {
    addAttribute("class", cssClass);
    return this;
  }

  @Override
  public IHtmlElement appLink(CharSequence ref) {
    cssClass("app-link");
    addAttribute("data-ref", ref);
    return this;
  }

}