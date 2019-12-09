/*
 * Copyright (c) 2010-2019 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
import {scout, tooltips, Widget} from '../../../index';

export default class GroupboxTitle extends Widget {

  constructor() {
    super();
    this.label = null;
    this.subtitle = null;
    this.labelVisible = false;
    this.expandable = false;
  }

  _init(model) {
    return super._init(model);
  }

  _render() {
    super._render();
    this.$container = this.$parent.appendDiv('title-area');
    this.$label = this.$container.appendDiv('label');
    tooltips.installForEllipsis(this.$label, {
      parent: this
    });
    this.$subtitle = this.$container.appendDiv('subtitle');
  }

  setLabel(label) {
    this.setProperty('label', label);
  }

  _renderLabel() {
    this.$label.textOrNbsp(this.label);
    if (this.rendered) {
      this._renderLabelVisible();
    }
  }

  /**
   * @override FormField.js
   */
  _renderLabelVisible(labelVisible) {
    this.$title.setVisible(this._computeLabelVisible(labelVisible));
    this._updateFieldStatus();
    if (this.menuBarPosition === GroupBox.MenuBarPosition.TITLE) {
      this.invalidateLayoutTree();
    }
  }

  _computeLabelVisible(labelVisible) {
    labelVisible = scout.nvl(labelVisible, this.labelVisible);
    return !!(labelVisible && this.label && !this.mainBox);
  }

  setSubtitle(subtitle) {
    this.setProperty('subtitle', subtitle);
  }

  _renderSubtitle(subtitle) {

  }
}
