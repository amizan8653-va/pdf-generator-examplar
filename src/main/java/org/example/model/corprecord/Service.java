package org.example.model.corprecord;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Service {
  final String branchOfServiceName;

  final String enteredOnDutyDate;

  final String releasedActiveDutyDate;

  final String characterOfServiceCode;

}
