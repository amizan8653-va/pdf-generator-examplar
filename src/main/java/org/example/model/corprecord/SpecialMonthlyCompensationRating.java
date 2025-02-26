package org.example.model.corprecord;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = false)
public class SpecialMonthlyCompensationRating {
  private final String ratingTypeName;

  @Override
  public String toString() {
    return "SpecialMonthlyCompensationRating{" + "ratingTypeName='" + ratingTypeName + '\'' + '}';
  }
}
