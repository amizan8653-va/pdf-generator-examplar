package org.example.pdf.pdfbox.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class PageMargins {
  float topMargin;

  float bottomMargin;

  float leftMargin;

  float rightMargin;

  public PageMargins(float topMargin, float bottomMargin) {
    this.topMargin = topMargin;
    this.bottomMargin = bottomMargin;
  }
}
