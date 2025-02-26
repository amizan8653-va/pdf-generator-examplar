package org.example.pdf.pdfbox.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.example.pdf.pdfbox.enums.Font;

import java.awt.*;
import java.util.List;

@Getter
@Accessors(fluent = true)
public class Text {
  protected final String text;

  protected final Color textColor;

  protected final Font font;

  protected float fontSize;

  @Setter protected List<String> wrappedText;

  /**
   * Constructor for a text element. Text Elements are elements that can be drawn onto a PDF page.
   *
   * @param fontSize The font size to drawn.
   * @param text The actual string to write.
   * @param textColor Color of text to draw.
   * @param font Enum indicating the font go to draw with.
   */
  public Text(float fontSize, String text, Color textColor, Font font) {
    this.fontSize = fontSize;
    this.text = text;
    this.textColor = textColor;
    this.font = font;
  }

  @Override
  public String toString() {
    return text;
  }
}
