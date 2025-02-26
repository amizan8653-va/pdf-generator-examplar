package org.example.model.corprecord;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.model.BenefitSummaryLetterOptions;
import  org.example.model.LetterAddress;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.text.SimpleDateFormat;
import java.util.List;

@Builder
@Getter
@Accessors(fluent = false)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@NoArgsConstructor
@AllArgsConstructor
public class CorpRecord {
  private String fileNumber;

  private String ssn;

  private String letterDate;

  private String firstName;

  private String middleName;

  private String lastName;

  private String suffixName;

  private String salutationName;

  private String headOfFamilyFirstName;

  private String headOfFamilyLastName;

  private String country;

  private String state;

  private String addressLine1;

  private String addressLine2;

  private String addressLine3;

  private String city;

  private String zipCode;

  private List<Service> services;

  private String dateOfFutureExam;

  private String branchOfService;

  private String dateOfBirth;

  private String edipi;

  private BenefitSummaryLetterOptions benefitSummaryLetterOptions;

  private String vadsInd;

  private String vadsInd3;

  private String vadsInd2;

  private String verifiedServiceDataInd;

  private String verifiedServiceDataInd2;

  private String verifiedServiceDataInd3;

  private List<CorpRecordOtherRating> otherRatings;

  private CorpRecordOtherRating nonServiceConnectedOtherRating;

  private List<SpecialMonthlyCompensationRating> specialMonthlyCompensationRating;

  private List<SpecialMonthlyCompensationParagraphRating>
      specialMonthlyCompensationParagraphRating;

  private String specialAdaptiveHousing;

  private String serviceConnectedCombinedDegree;

  private CorpRecordAwardInfo awardInfo;

  private String claimantCurrentMonthlyRate;

  private boolean chapter35Eligibility;

  private String chapter35EligibilityDateTime;

  private ActiveAwardLine activeAwardLine;

  private boolean isDeathRatingServiceConnectedDisability;

  private SimpleDateFormat dateFormatterInput;

  private SimpleDateFormat dateFormatterOutput;

  private boolean isServiceEntitlement;

  private boolean isPensionEntitlement;

  // specifying builder so that it can implement common interface. Lombok @Builder fills the rest.
  // reference: https://stackoverflow.com/a/71668089/4832515
  public static class CorpRecordBuilder implements LetterAddress {}
}
