package org.example.pdf.pdfbox.pojo;

import lombok.experimental.Accessors;
import org.example.pdf.pdfbox.enums.TableHeaderType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Accessors(fluent = true)
public class DataTable {

  private final List<Row> rows = new ArrayList<>();
  private final String summary;
  private final TableHeaderType tableHeaderType;

  /**
   * Data table that consists of rows, which consists of cells, that can be drawn onto PDF.
   *
   * @param summary Summary of the table.
   * @param tableHeaderType Whether or not the top row, or left column, contains the headers.
   */
  public DataTable(String summary, TableHeaderType tableHeaderType) {
    this.summary = summary;
    this.tableHeaderType = tableHeaderType;
  }

  /**
   * Add a row to the table.
   *
   * @param row Row to add.
   */
  public void addRow(Row row) {
    this.rows.add(row);
  }

  /**
   * Get a particular cell from the table.
   *
   * @param row Row containing the cell.
   * @param col Column within the row where the cell is located.
   * @return The cell at the given row & col.
   */
  public Cell getCell(int row, int col) {
    return rows.get(row).cells().get(col);
  }

  /**
   * Vertical distance from row measured from top edge of row at index rowIndexStart to the top of
   * edge of row at rowIndexEnd. You can also think of this as adding the heights of these rows
   * starting at rowIndexStart inclusive, ending at rowIndexEnd exclusive.
   *
   * @param rowIndexStart Starting row to go and get a height for & adding to sum.
   * @param rowIndexEnd Ending row for the sum (exclusive).
   * @return The distance.
   */
  public float getRowPosition(int rowIndexStart, int rowIndexEnd) {
    float currentPosition = 0;
    for (int i = rowIndexStart; i < rowIndexEnd; i++) {
      currentPosition += rows.get(i).height();
    }
    return currentPosition;
  }

  public TableHeaderType getTableHeaderType() {
    return tableHeaderType;
  }

  @Override
  public String toString() {
    return rows.stream().map(Row::toString).collect(Collectors.joining(" "));
  }
}
