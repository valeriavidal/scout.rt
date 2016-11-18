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
scout.OutlineAdapter = function() {
  scout.OutlineAdapter.parent.call(this);
  this._addAdapterProperties(['defaultDetailForm', 'views', 'dialogs', 'messageBoxes', 'fileChoosers']);
  this._nodeIdToRowMap = {};
  this._detailTableRowHandler = this._onDetailTableRowInitialized.bind(this);
};
scout.inherits(scout.OutlineAdapter, scout.TreeAdapter);

scout.OutlineAdapter.prototype._init = function(model) {
  scout.OutlineAdapter.parent.prototype._init.call(this, model);
};

scout.OutlineAdapter.prototype._initPage = function(page, parentNode) {
  if (!page.childNodes) {
    page.childNodes = [];
  }
  if (page.detailTable) {
    page.detailTable = this.session.getOrCreateWidget(page.detailTable, this.widget);
  }
  if (page.detailForm) {
    page.detailForm = this.session.getOrCreateWidget(page.detailForm, this.widget);
  }
};

scout.OutlineAdapter.prototype._onPageChanged = function(event) {
  var page;
  if (event.nodeId) {
    page = this.widget._nodeById(event.nodeId);

    page.detailFormVisible = event.detailFormVisible;
    page.detailForm = this.session.getOrCreateWidget(event.detailForm, this.widget);

    page.detailTableVisible = event.detailTableVisible;
    page.detailTable = this.session.getOrCreateWidget(event.detailTable, this.widget);
  } else {
    this.widget.defaultDetailForm = this.session.getOrCreateWidget(event.detailForm, this.widget);
  }

  this.widget.pageChanged(page);
};

scout.OutlineAdapter.prototype._onWidgetEvent = function(event) {
  if (event.type === 'initPage') {
    this._onWidgetInitPage(event);
  } else {
    scout.OutlineAdapter.parent.prototype._onWidgetEvent.call(this, event);
  }
};

scout.OutlineAdapter.prototype.onModelAction = function(event) {
  if (event.type === 'pageChanged') {
    this._onPageChanged(event);
  } else {
    scout.OutlineAdapter.parent.prototype.onModelAction.call(this, event);
  }
};

scout.OutlineAdapter.prototype._onWidgetInitPage = function(event) {
  var page = event.page;
  if (page.detailTable) {
    this._initDetailTable(page);
  }
  this._linkNodeWithRowLater(page);
};

scout.OutlineAdapter.prototype._initDetailTable = function(page) {
  // link already existing rows now
  page.detailTable.rows.forEach(this._linkNodeWithRow.bind(this));
  // rows which are inserted later are linked by _onDetailTableRowInitialized
  page.detailTable.on('rowInitialized', this._detailTableRowHandler);
};

scout.OutlineAdapter.prototype._linkNodeWithRow = function(row) {
  scout.assertParameter('row', row);
  var nodeId = row.nodeId,
    node = this.widget.nodesMap[nodeId];
  if (node) {
    scout.Page.linkRowWithPage(row, node);
  } else {
    // Prepare for linking later because node has not been inserted yet
    // see: #_linkNodeWithRowLater
    this._nodeIdToRowMap[nodeId] = row;
  }
};

scout.OutlineAdapter.prototype._onDetailTableRowInitialized = function(event) {
  var node,
    outline = this.widget,
    nodeId = event.row.nodeId;
  this._linkNodeWithRow(event.row);
  node = this.widget.nodesMap[nodeId];

  // If a row, which was already linked to a node, gets initialized again, re-apply the filter to make sure the node has the correct state
  if (outline.rendered && node && outline._applyFiltersForNode(node)){
    if (node.isFilterAccepted()) {
      outline._addToVisibleFlatList(node, false);
    } else {
      outline._removeFromFlatList(node, false);
    }
  }
};

/**
 * Link node with row, if it hasn't been linked yet.
 */
scout.OutlineAdapter.prototype._linkNodeWithRowLater = function(page) {
  if (!page.parentNode || !page.parentNode.detailTable) {
    return;
  }
  if (!this._nodeIdToRowMap.hasOwnProperty(page.id)) {
    return;
  }
  var row = this._nodeIdToRowMap[page.id];
  scout.Page.linkRowWithPage(row, page);
  delete this._nodeIdToRowMap[page.id];
};

