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

import {GroupBox, GroupBoxMenuItemsOrder, MenuBar, scout, Widget} from '../../../index';

export default class GroupBoxHeader extends Widget {

  constructor() {
    super();
    this.menus = [];
    this.menuBarVisible = true;
    this.menuBarPosition = GroupBox.MenuBarPosition.AUTO;
    this.menuBarEllipsisPosition = MenuBar.EllipsisPosition.RIGHT;
  }

  _init(model) {
    super._init(model);

    this.title = scout.create('GroupBoxTitle', {
      parent: this
    });

    this.menuBar = scout.create('MenuBar', {
      parent: this,
      menuOrder: new GroupBoxMenuItemsOrder()
    });
  }

  _render() {
    super._render();
    this.$container = this.$parent.appendDiv('header');
  }

  setMenuBarVisible(visible) {
    this.setProperty('menuBarVisible', visible);
  }

  _setMenuBarVisible(visible) {
    this._setProperty('menuBarVisible', visible);
    this._updateMenuBar();
  }

  _renderMenuBarVisible() {
    if (this.menuBarVisible) {
      this._renderMenuBar();
    } else {
      this.menuBar.remove();
    }
    this._updateMenus();
    this.invalidateLayoutTree();
  }

  _renderMenuBar() {
    this.menuBar.render();
    if (this.menuBarPosition === GroupBox.MenuBarPosition.TITLE) {
      // move right of title
      this.menuBar.$container.insertAfter(this.$subLabel);
    } else if (this.menuBar.position === MenuBar.Position.TOP) {
      // move below title
      this.menuBar.$container.insertAfter(this.$title);
    }
  }

  setMenuBarPosition(menuBarPosition) {
    this.setProperty('menuBarPosition', menuBarPosition);
  }

  _renderMenuBarPosition() {
    var position = this.menuBarPosition;
    if (position === GroupBox.MenuBarPosition.AUTO) {
      position = GroupBox.MenuBarPosition.TOP;
    }

    var hasMenubar = position === GroupBox.MenuBarPosition.TITLE;
    this.$title.toggleClass('has-menubar', hasMenubar);

    if (position === GroupBox.MenuBarPosition.BOTTOM) {
      this.menuBar.setPosition(MenuBar.Position.BOTTOM);
    } else { // top + title
      this.menuBar.setPosition(MenuBar.Position.TOP);
    }

    if (this.rendered) {
      this.menuBar.remove();
      this._renderMenuBarVisible();
    }
  }

  setMenuBarEllipsisPosition(menuBarEllipsisPosition) {
    this.setProperty('menuBarEllipsisPosition', menuBarEllipsisPosition);
    this.menuBar.setEllipsisPosition(menuBarEllipsisPosition);
  }

  _renderMenuBarEllipsisPosition() {
    this.menuBar.reorderMenus();
    if (this.rendered) {
      this.menuBar.remove();
      this._renderMenuBarVisible();
    }
  }
}
