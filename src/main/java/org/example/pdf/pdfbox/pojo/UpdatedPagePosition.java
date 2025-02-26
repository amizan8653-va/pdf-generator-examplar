package org.example.pdf.pdfbox.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Builder
@Accessors(fluent = true)
public class UpdatedPagePosition {
  private final float verticalPositionFromTop;
  private final int pageIndex;
}
