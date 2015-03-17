scout.TableLayout = function(table) {
  scout.TableLayout.parent.call(this);
  this.table = table;
};
scout.inherits(scout.TableLayout, scout.AbstractLayout);

scout.TableLayout.prototype.layout = function($container) {
  var menuBar = this.table.menuBar,
    footer = this.table.footer,
    header = this.table.header,
    $data = this.table.$data,
    height = 0;

  if (menuBar.$container.isVisible()){
    height += scout.graphics.getSize(menuBar.$container).height;
  }
  if (footer) {
    height += scout.graphics.getSize(footer.$container).height;
    height += scout.graphics.getSize(footer.$controlContainer).height;
  }
  if (header) {
    height += scout.graphics.getSize(header.$container).height;
  }
  height += $data.cssMarginTop() + $data.cssMarginBottom();
  $data.css('height', 'calc(100% - '+ height + 'px)');

  if (this.table.autoResizeColumns) {
    this._layoutColumns();
  }
  scout.scrollbars.update(this.table.$data);
};

/**
 * Resizes the columns to make them use all the available space.
 */
scout.TableLayout.prototype._layoutColumns = function() {
  var i, column, newWidth, weight,
    relevantColumns = [],
    columns = this.table.columns,
    currentWidth = 0,
    totalInitialWidth = 0,
    availableWidth = this.table.$data.outerWidth() - this.table.tableRowBorderWidth();

  columns.forEach(function(column) {
    if (column.fixedWidth) {
      availableWidth -= column.width;
    } else {
      relevantColumns.push(column);
      currentWidth += column.width;
      totalInitialWidth += column.initialWidth;
    }
  }.bind(this));

  if (availableWidth === currentWidth) {
    // Columns already use the available space, no need to resize
    return;
  }

  relevantColumns.forEach(function(column) {
    weight = column.initialWidth / totalInitialWidth;
    newWidth = weight * availableWidth;
    if (newWidth !== column.width) {
      this.table.resizeColumn(column, newWidth, true);
    }
  }.bind(this));
};

scout.TableLayout.prototype.preferredLayoutSize = function($comp) {
  return scout.graphics.getSize($comp);
};
