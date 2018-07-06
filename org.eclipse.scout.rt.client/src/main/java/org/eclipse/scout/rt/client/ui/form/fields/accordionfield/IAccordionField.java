/*******************************************************************************
 * Copyright (c) 2017 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.client.ui.form.fields.accordionfield;

import org.eclipse.scout.rt.client.ui.accordion.IAccordion;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;

public interface IAccordionField<T extends IAccordion> extends IFormField {

  /**
   * {@link IAccordion}
   */
  String PROP_ACCORDION = "accordion";

  void setAccordion(T accordion);

  T getAccordion();
}