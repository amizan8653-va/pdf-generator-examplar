package org.example.model.corprecord;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = false)
public class CorpRecordAwardInfo {
  private final String frequencyCode;

  private final String frequencyName;

  private final String benefitCode;

  @Override
  public String toString() {
    return "CorpRecordAwardInfo{"
        + "frequencyCode='"
        + frequencyCode
        + '\''
        + ", frequencyName='"
        + frequencyName
        + '\''
        + ", benefitCode='"
        + benefitCode
        + '\''
        + '}';
  }
}
