package org.example;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class CustomPdfTextStripper extends PDFTextStripper {

  private final List<TextPosition> aggregatedTextPositions;

  /**
   * Instantiate a new PDFTextStripper object.
   *
   * @throws IOException If there is an error loading the properties.
   */
  public CustomPdfTextStripper() throws IOException {
    aggregatedTextPositions = new ArrayList<>();
  }

  /**
   * Write a Java string to the output stream. The default implementation will ignore the <code>
   * textPositions</code> and just calls {@link #writeString(String)}.
   *
   * @param text The text to write to the stream.
   * @param textPositions The TextPositions belonging to the text.
   * @throws IOException If there is an error when writing the text.
   */
  protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
    aggregatedTextPositions.addAll(textPositions);
    writeString(text);
  }
}
