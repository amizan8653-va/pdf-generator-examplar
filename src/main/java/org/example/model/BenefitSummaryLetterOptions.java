package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(fluent = false)
@NoArgsConstructor
@AllArgsConstructor
public class BenefitSummaryLetterOptions {
  boolean militaryService;
  boolean serviceConnectedDisabilities;
  boolean serviceConnectedEvaluation;
  boolean nonServiceConnectedPension;
  boolean monthlyAward;
  boolean unemployable;
  boolean specialMonthlyCompensation;
  boolean adaptedHousing;
  boolean chapter35Eligibility;

  @Override
  public String toString() {
    return "BenefitSummaryLetterOptions{"
        + "militaryService="
        + militaryService
        + ", serviceConnectedDisabilities="
        + serviceConnectedDisabilities
        + ", serviceConnectedEvaluation="
        + serviceConnectedEvaluation
        + ", nonServiceConnectedPension="
        + nonServiceConnectedPension
        + ", monthlyAward="
        + monthlyAward
        + ", unemployable="
        + unemployable
        + ", specialMonthlyCompensation="
        + specialMonthlyCompensation
        + ", adaptedHousing="
        + adaptedHousing
        + ", chapter35Eligibility="
        + chapter35Eligibility
        + '}';
  }
}
