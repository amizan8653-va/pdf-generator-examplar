package org.example.pdf.pdfbox.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

@Builder
@Getter
@Accessors(fluent = true)
public class NewPageRelatedVariables {
  PDPageContentStream newContent;
  float newVerticalOffsetFromBottom;
  int newPageIndex;
}
