package org.example.pdf.pdfbox.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Accessors(fluent = true)
public class Row {
  private final List<Cell> cells;

  @Setter private float height;

  public Row(List<Cell> cells) {
    this.height = 0;
    this.cells = cells;
  }

  /**
   * Get the distance measured from left edge of leftmost cell to left edge of cell at cellIndex.
   *
   * @param cellIndex Index of cell within the row.
   * @return The distance.
   */
  public float getCellPosition(int cellIndex) {
    float currentPosition = 0;
    for (int i = 0; i < cellIndex; i++) {
      currentPosition += cells.get(i).width();
    }
    return currentPosition;
  }

  @Override
  public String toString() {
    return cells.stream().map(Cell::toString).collect(Collectors.joining(" "));
  }
}
