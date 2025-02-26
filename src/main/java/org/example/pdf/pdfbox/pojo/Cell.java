package org.example.pdf.pdfbox.pojo;

import lombok.experimental.Accessors;
import org.example.pdf.pdfbox.enums.CellFormatting;
import org.example.pdf.pdfbox.enums.Font;
import lombok.Getter;

import java.awt.*;

@Getter
@Accessors(fluent = true)
public class Cell extends Text {
  private final CellFormatting align;
  private final boolean header;
  private final float width;

  /**
   * Constructor for this table cell pojo.
   *
   * @param text The actual string to write.
   * @param font Enum indicating the font go to draw with.
   * @param fontSize The font size to drawn.
   * @param width The absolute width of the cell.
   * @param align Enum text alignment within the cell.
   * @param header boolean indicating if this cell is <TH> header cell, or <TD> data cell.
   */
  public Cell(
      String text, Font font, float fontSize, float width, CellFormatting align, boolean header) {
    super(fontSize, text, Color.black, font);
    this.width = width;
    this.align = align;
    this.header = header;
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
