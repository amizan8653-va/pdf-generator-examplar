package org.example.model.corprecord;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@NoArgsConstructor
@AllArgsConstructor
public class Service {
  String branchOfServiceName;

  String enteredOnDutyDate;

  String releasedActiveDutyDate;

  String characterOfServiceCode;

}
