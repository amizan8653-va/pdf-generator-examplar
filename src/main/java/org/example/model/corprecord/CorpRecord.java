package org.example.model.corprecord;

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
public class CorpRecord {
  private final String fileNumber;

  private final String ssn;

  private final String letterDate;

  private final String firstName;

  private final String middleName;

  private final String lastName;

  private final String suffixName;

  private final String salutationName;

  private final String headOfFamilyFirstName;

  private final String headOfFamilyLastName;

  private final String country;

  private final String state;

  private final String addressLine1;

  private final String addressLine2;

  private final String addressLine3;

  private final String city;

  private final String zipCode;

  private final List<Service> services;

  private final String dateOfFutureExam;

  private final String branchOfService;

  private final String dateOfBirth;

  private final String edipi;

  private final BenefitSummaryLetterOptions benefitSummaryLetterOptions;

  private final String vadsInd;

  private final String vadsInd3;

  private final String vadsInd2;

  private final String verifiedServiceDataInd;

  private final String verifiedServiceDataInd2;

  private final String verifiedServiceDataInd3;

  private final List<CorpRecordOtherRating> otherRatings;

  private final CorpRecordOtherRating nonServiceConnectedOtherRating;

  private final List<SpecialMonthlyCompensationRating> specialMonthlyCompensationRating;

  private final List<SpecialMonthlyCompensationParagraphRating>
      specialMonthlyCompensationParagraphRating;

  private final String specialAdaptiveHousing;

  private final String serviceConnectedCombinedDegree;

  private final CorpRecordAwardInfo awardInfo;

  private final String claimantCurrentMonthlyRate;

  private final boolean chapter35Eligibility;

  private final String chapter35EligibilityDateTime;

  private final ActiveAwardLine activeAwardLine;

  private final boolean isDeathRatingServiceConnectedDisability;

  private final SimpleDateFormat dateFormatterInput;

  private final SimpleDateFormat dateFormatterOutput;

  private final boolean isServiceEntitlement;

  private final boolean isPensionEntitlement;

  // specifying builder so that it can implement common interface. Lombok @Builder fills the rest.
  // reference: https://stackoverflow.com/a/71668089/4832515
  public static final class CorpRecordBuilder implements LetterAddress {}
}
