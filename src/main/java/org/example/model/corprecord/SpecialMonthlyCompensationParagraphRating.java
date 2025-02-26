package org.example.model.corprecord;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = false)
public class SpecialMonthlyCompensationParagraphRating {
  private final String paragraphText;

  @Override
  public String toString() {
    return "SpecialMonthlyCompensationParagraphRating{"
        + "paragraphText='"
        + paragraphText
        + '\''
        + '}';
  }
}
