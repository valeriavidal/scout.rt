describe("NavigateDownButton", function() {

  var session, outline, menu, node = {};

  beforeEach(function() {
    session = new scout.Session($('#sandbox'), '1.1');
    outline = {session: session};
    menu = new scout.NavigateDownButton(outline, node);
  });

  it("_toggleDetail is always false", function() {
    expect(menu._toggleDetail()).toBe(false);
  });

  it("_isDetail returns true or false depending on the state of the detail-form and detail-table", function() {
    // true when both detailForm and detailTable are visible
    node.detailForm = {};
    node.detailFormVisible = true;
    node.detailTable = {};
    node.detailTableVisible = true;
    expect(menu._isDetail()).toBe(true);

    // false when detailForm is absent, even when if detailFormVisible=true
    delete node.detailForm;
    expect(menu._isDetail()).toBe(false);
    node.detailForm = {};

    // false when detailTable is absent, even when if detailTableVisible=true
    delete node.detailTable;
    expect(menu._isDetail()).toBe(false);
    node.detailTable = {};

    // false when hidden by UI
    node.detailFormHiddenByUi = true;
    expect(menu._isDetail()).toBe(false);
    node.detailFormHiddenByUi = false;

    // false when property says to
    node.detailFormVisible = false;
    expect(menu._isDetail()).toBe(false);
    node.detailFormVisible = true;
    node.detailTableVisible = false;
    expect(menu._isDetail()).toBe(false);
  });

  describe("_buttonEnabled", function() {

    it("is disabled when node is a leaf", function() {
      node.leaf = true; // node is a leaf
      expect(menu._buttonEnabled()).toBe(false);
    });

    it("is enabled when node is not a leaf and we're currently displaying the detail", function() {
      node.leaf = false;  // node is not a leaf
      menu._isDetail = function() { // currently we're displaying the detail-form
        return true;
      };
      expect(menu._buttonEnabled()).toBe(true);
    });

    it("is only enabled when detail-table has exactly one selected row", function() {
      node.leaf = false; // node is not a leaf
      menu._isDetail = function() { // currently we're not displaying the detail-form
        return false;
      };
      node.detailTable = {selectedRowIds: []};
      expect(menu._buttonEnabled()).toBe(false);

      node.detailTable.selectedRowIds = [1];
      expect(menu._buttonEnabled()).toBe(true);
    });
  });

  it("_drill drills down to first selected row in the detail table", function() {
    var drillNode = {};
    node.detailTable = {
      selectedRowIds: ['123'],
      rowById: function(rowIds) {
        return {
          nodeId: '123'
        };
      }
    };
    outline.nodesMap = {'123': drillNode};
    outline.setNodesSelected = function(node) {};
    outline.setNodeExpanded = function(node, expanded) {};

    spyOn(outline, 'setNodesSelected');
    spyOn(outline, 'setNodeExpanded');
    menu._drill();
    expect(outline.setNodesSelected).toHaveBeenCalledWith(drillNode);
    expect(outline.setNodeExpanded).toHaveBeenCalledWith(drillNode, false);
  });

});
