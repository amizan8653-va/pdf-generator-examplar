package org.example.model.corprecord;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = false)
public class CorpRecordOtherRating {

  private final String disabilityTypeName;

  private final String decisionTypeName;

  @Override
  public String toString() {
    return "CorpRecordOtherRating{"
        + "disabilityTypeName='"
        + disabilityTypeName
        + '\''
        + ", decisionTypeName='"
        + decisionTypeName
        + '\''
        + '}';
  }
}
