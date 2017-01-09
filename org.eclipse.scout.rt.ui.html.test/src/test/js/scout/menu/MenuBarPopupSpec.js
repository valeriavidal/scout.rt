/*******************************************************************************
 * Copyright (c) 2014-2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
describe("MenuBarPopup", function() {

  var helper, session, $sandbox, modelMenu1, modelMenu2, menu1, menu2;

  beforeEach(function() {
    setFixtures(sandbox());
    session = sandboxSession();
    helper = new scout.MenuSpecHelper(session);
  });

  it('is opened on doAction if the menu has child actions', function() {
    var childMenu = helper.createMenu({text: 'child menu'});
    var menu = helper.createMenu({
      text: 'the menu',
      childActions: [childMenu]
    });
    menu.render(session.$entryPoint);
    menu.doAction();
    expect(menu.popup).toBeDefined();
    expect(menu.popup.rendered).toBe(true);
  });

  it('rerenders the head on a menu property change', function() {
    var childMenu = helper.createMenu({text: 'child menu'});
    var menu = helper.createMenu({
      text: 'the menu',
      childActions: [childMenu]
    });
    menu.render(session.$entryPoint);
    menu.doAction();
    expect(menu.popup.$head.text()).toEqual('the menu');

    menu.setText('new text');
    session.layoutValidator.validate();
    expect(menu.popup.$head.text()).toEqual('new text');
  });
});
