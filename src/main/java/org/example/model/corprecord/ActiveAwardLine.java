package org.example.model.corprecord;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = false)
public class ActiveAwardLine {
  private final String netAward;
  private final String effectiveDate;
  private final String entitlementCd;
  private final String entitlementNm;
  private final String totalAward;

  @Override
  public String toString() {
    return "ActiveAwardLine{"
        + "netAward='"
        + netAward
        + '\''
        + ", effectiveDate='"
        + effectiveDate
        + '\''
        + ", entitlementCd='"
        + entitlementCd
        + '\''
        + ", entitlementNm='"
        + entitlementNm
        + '\''
        + ", totalAward='"
        + totalAward
        + '\''
        + '}';
  }
}
