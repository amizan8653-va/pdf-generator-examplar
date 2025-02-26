package org.example.model.corprecord;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = false)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@NoArgsConstructor
@AllArgsConstructor
public class CorpRecordOtherRating {

  private String disabilityTypeName;

  private String decisionTypeName;

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
