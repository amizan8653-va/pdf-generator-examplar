package org.example.pdf.pdfbox.letters;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.example.model.BenefitSummaryLetterOptions;
import org.example.model.corprecord.CorpRecord;
import org.example.model.corprecord.CorpRecordOtherRating;
import org.example.model.corprecord.SpecialMonthlyCompensationParagraphRating;
import org.example.model.corprecord.SpecialMonthlyCompensationRating;
import org.example.pdf.pdfbox.CustomTaggedPdfBuilder;
import org.example.pdf.pdfbox.PdfBoxLetter;
import org.example.pdf.pdfbox.enums.CellFormatting;
import org.example.pdf.pdfbox.enums.Font;
import org.example.pdf.pdfbox.enums.TableHeaderType;
import org.example.pdf.pdfbox.pojo.Cell;
import org.example.pdf.pdfbox.pojo.DataTable;
import org.example.pdf.pdfbox.pojo.PageMargins;
import org.example.pdf.pdfbox.pojo.Row;
import org.example.pdf.pdfbox.pojo.Text;
import org.example.pdf.pdfbox.pojo.UpdatedPagePosition;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class BenefitSummaryLetterVeteranPdf extends BenefitSummaryLetterPdf
    implements PdfBoxLetter {
  private static final String PERSONAL_INFORMATION_TITLE = "Personal Claim Information";
  private static final String BENEFIT_INFORMATION_TITLE = "VA Benefit Information";

  private String computeClaimNumber(final CorpRecord record) {
    final var fallback = String.format("xxx-xx-%s", record.getSsn().substring(5, 9));
    return Optional.ofNullable(record.getFileNumber())
        .map(
            givenFileNumber -> {
              if (givenFileNumber.length() == 8) {
                return String.format(
                    "%s-%s-%s",
                    record.getFileNumber().substring(0, 2),
                    record.getFileNumber().substring(2, 5),
                    record.getFileNumber().substring(5, 8));
              } else if (givenFileNumber.length() == 9) {
                return String.format("xxx-xx-%s", record.getFileNumber().substring(5, 9));
              } else {
                return fallback;
              }
            })
        .orElse(fallback);
  }

  private CustomTaggedPdfBuilder createPdf(
      CorpRecord record) {
    // construct the PDF & get root element to append all content to.
    CustomTaggedPdfBuilder formBuilder =
        new CustomTaggedPdfBuilder("Benefit Summary Letter", new PageMargins(60, 50), false);
    PDStructureElement rootElement = formBuilder.rootElem();
    CommonPdfLetterUtil.drawHeading(record, formBuilder, rootElement);
    // print out Dear <name>
    final var salutationName = CorpRecordUtil.computeRecipientSalutation(record);
    final Text salutationText =
        new Text(10.2f, String.format("Dear %s:", salutationName), Color.black, Font.INTER_REGULAR);
    UpdatedPagePosition pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        salutationText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + 42.5f,
        0,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
    // print out some static text
    final var staticMessageOne =
        "This letter is a summary of benefits you currently receive from the Department "
            + "of Veterans Affairs (VA). We are providing this letter to disabled "
            + "Veterans to use in applying for benefits such as state or local property "
            + "or vehicle tax relief, civil service preference, to obtain housing entitlements, "
            + "free or reduced state park annual memberships, or any other "
            + "program or entitlement in which verification of VA benefits is required. "
            + "Please safeguard this important document. This letter is considered an official "
            + "record of your VA entitlement.";
    final Text staticMessageOneText =
        new Text(10.2f, staticMessageOne, Color.black, Font.INTER_REGULAR);
    pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageOneText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + 19.0f,
        1.75f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
    final var staticMessageTwo = "Our records contain the following information:";
    final Text staticMessageTwoText =
        new Text(10.2f, staticMessageTwo, Color.black, Font.INTER_REGULAR);
    pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageTwoText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + 17f,
        0.0f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
    final Text staticMessageThreeText =
        new Text(12.24f, PERSONAL_INFORMATION_TITLE, Color.black, Font.INTER_BOLD);
    pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageThreeText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + 25.0f,
        0.0f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
    // print out the claim number
    final var claimNumberMessage =
        String.format("Your VA claim number is: %s", computeClaimNumber(record));
    final Text staticMessageFourText =
        new Text(10.2f, claimNumberMessage, Color.black, Font.INTER_REGULAR);
    pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageFourText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + 15.0f,
        0.0f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
    // more static text
    final var staticMessageFive = "You are the Veteran.";
    final Text staticMessageFiveText =
        new Text(10.2f, staticMessageFive, Color.black, Font.INTER_REGULAR);
    pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageFiveText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + 15.0f,
        0.0f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
    // check for military information, and if there is any draw it.
    Optional.ofNullable(record.getBenefitSummaryLetterOptions())
        .map(BenefitSummaryLetterOptions::isMilitaryService)
        .filter(isMilitaryService -> isMilitaryService)
        .map(ignored -> drawMilitaryInformation(record, formBuilder, rootElement, true));

    final var staticMessageSix = "(There may be additional periods of service not listed above.)";
    final Text staticMessageSixText =
        new Text(10.2f, staticMessageSix, Color.black, Font.INTER_REGULAR);
    pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageSixText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + 16.0f,
        0.0f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
    // potentially draw a 2nd table
    DisplayFlags displayFlags = generateDisplayFlags(record);
    if (displayFlags.anyDisplayFlagTrue()) {
      drawDynamicTable(record, formBuilder, rootElement, displayFlags);
    }
    // static text again
    drawEnding(formBuilder, rootElement);
    return formBuilder;
  }

  @SneakyThrows
  private void drawDynamicTable(
      CorpRecord record,
      CustomTaggedPdfBuilder formBuilder,
      PDStructureElement rootElement,
      DisplayFlags displayFlags) {
    final Text staticMessageThreeText =
        new Text(12.24f, BENEFIT_INFORMATION_TITLE, Color.black, Font.INTER_BOLD);
    var pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageThreeText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + 25.0f,
        0.0f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
    DataTable vaBenefitInformationTable =
        new DataTable(
            "This table lists out all benefits. The first column is the benefit description and "
                + "the second column is the benefit's status. ",
            TableHeaderType.COLUMN_HEADERS);
    var rows = vaBenefitInformationTable.rows();
    // width of each cell.
    final var leftCellWidth = 368f;
    final var rightCellWidth = leftCellWidth * 0.75f;
    if (displayFlags.displayServiceConnectedDisabilities()) {
      vaBenefitInformationTable.addRow(
          new Row(
              Arrays.asList(
                  new Cell(
                      "You have one or more service-connected disabilities:",
                      Font.INTER_BOLD,
                      8.67f,
                      leftCellWidth,
                      CellFormatting.DEFAULT,
                      true),
                  new Cell(
                      Optional.ofNullable(record.getServiceConnectedCombinedDegree())
                          .map(StringUtils::isNotBlank)
                          .map(ignored -> "Yes")
                          .orElse("No"),
                      Font.INTER_REGULAR,
                      8.67f,
                      rightCellWidth,
                      CellFormatting.DEFAULT,
                      false))));
    }
    if (displayFlags.displayCombinedServiceConnectedEvaluation()) {
      vaBenefitInformationTable.addRow(
          new Row(
              Arrays.asList(
                  new Cell(
                      "Your combined service-connected evaluation is:",
                      Font.INTER_BOLD,
                      8.67f,
                      leftCellWidth,
                      CellFormatting.DEFAULT,
                      true),
                  new Cell(
                      String.format("%s%%", record.getServiceConnectedCombinedDegree()),
                      Font.INTER_REGULAR,
                      8.67f,
                      rightCellWidth,
                      CellFormatting.DEFAULT,
                      false))));
    }
    if (displayFlags.displayNonServiceConnectedPension()) {
      vaBenefitInformationTable.addRow(
          new Row(
              Arrays.asList(
                  new Cell(
                      "You are receiving non-service connected pension:",
                      Font.INTER_BOLD,
                      8.67f,
                      leftCellWidth,
                      CellFormatting.DEFAULT,
                      true),
                  new Cell(
                      "Yes",
                      Font.INTER_REGULAR,
                      8.67f,
                      rightCellWidth,
                      CellFormatting.DEFAULT,
                      false))));
    }
    if (displayFlags.displayMonthlyAwardAmount()) {
      var column2 = "Unknown";
      if (displayFlags.isAwardInfoMonthlyAwardAmount) {
        column2 = "$" + record.getActiveAwardLine().getNetAward();
      } else if (displayFlags.isClaimantCurrentMonthlyRate()) {
        column2 = "$" + record.getClaimantCurrentMonthlyRate();
      }
      vaBenefitInformationTable.addRow(
          new Row(
              Arrays.asList(
                  new Cell(
                      "Your current monthly award amount is:",
                      Font.INTER_BOLD,
                      8.67f,
                      leftCellWidth,
                      CellFormatting.DEFAULT,
                      true),
                  new Cell(
                      column2,
                      Font.INTER_REGULAR,
                      8.67f,
                      rightCellWidth,
                      CellFormatting.DEFAULT,
                      false))));
    }
    if (displayFlags.displayEffectiveDateOfLastChangeToCurrentAward()) {
      vaBenefitInformationTable.addRow(
          new Row(
              Arrays.asList(
                  new Cell(
                      "The effective date of the last change to your current award was:",
                      Font.INTER_BOLD,
                      8.67f,
                      leftCellWidth,
                      CellFormatting.DEFAULT,
                      true),
                  new Cell(
                      record
                          .getDateFormatterOutput()
                          .format(
                              record
                                  .getDateFormatterInput()
                                  .parse(record.getActiveAwardLine().getEffectiveDate())),
                      Font.INTER_REGULAR,
                      8.67f,
                      rightCellWidth,
                      CellFormatting.DEFAULT,
                      false))));
    }
    if (displayFlags.displayPaidAt100BecauseUnemployable()) {
      vaBenefitInformationTable.addRow(
          new Row(
              Arrays.asList(
                  new Cell(
                      "You are being paid at the 100 percent rate because you are "
                          + "unemployable due to your service-connected disabilities:",
                      Font.INTER_BOLD,
                      8.67f,
                      leftCellWidth,
                      CellFormatting.DEFAULT,
                      true),
                  new Cell(
                      "Yes",
                      Font.INTER_REGULAR,
                      8.67f,
                      rightCellWidth,
                      CellFormatting.DEFAULT,
                      false))));
    }
    if (displayFlags.displayChapter35()) {
      vaBenefitInformationTable.addRow(
          new Row(
              Arrays.asList(
                  new Cell(
                      "You are considered to be totally and permanently disabled due solely "
                          + "to your service-connected disabilities:",
                      Font.INTER_BOLD,
                      8.67f,
                      leftCellWidth,
                      CellFormatting.DEFAULT,
                      true),
                  new Cell(
                      record.isChapter35Eligibility() ? "Yes" : "No",
                      Font.INTER_REGULAR,
                      8.67f,
                      rightCellWidth,
                      CellFormatting.DEFAULT,
                      false))));
    }
    if (displayFlags.displayChapter35Date()) {
      vaBenefitInformationTable.addRow(
          new Row(
              Arrays.asList(
                  new Cell(
                      "The effective date of when you became totally and permanently "
                          + "disabled due to your service-connected disabilities:",
                      Font.INTER_BOLD,
                      8.67f,
                      leftCellWidth,
                      CellFormatting.DEFAULT,
                      true),
                  new Cell(
                      record.getChapter35EligibilityDateTime(),
                      Font.INTER_REGULAR,
                      8.67f,
                      rightCellWidth,
                      CellFormatting.DEFAULT,
                      false))));
    }
    if (displayFlags.displaySpecialMonthlyCompensation()) {
      vaBenefitInformationTable.addRow(
          new Row(
              Arrays.asList(
                  new Cell(
                      "You are in receipt of special monthly compensation due to "
                          + "the type and severity of your service-connected disabilities:",
                      Font.INTER_BOLD,
                      8.67f,
                      leftCellWidth,
                      CellFormatting.DEFAULT,
                      true),
                  new Cell(
                      "Yes",
                      Font.INTER_REGULAR,
                      8.67f,
                      rightCellWidth,
                      CellFormatting.DEFAULT,
                      false))));
    }
    if (displayFlags.displayAdaptedHousing()) {
      vaBenefitInformationTable.addRow(
          new Row(
              Arrays.asList(
                  new Cell(
                      "You have been found entitled to a Specially Adapted Housing "
                          + "(SAH) and/or Special Home Adaptation (SHA) grant:",
                      Font.INTER_BOLD,
                      8.67f,
                      leftCellWidth,
                      CellFormatting.DEFAULT,
                      true),
                  new Cell(
                      "Yes",
                      Font.INTER_REGULAR,
                      8.67f,
                      rightCellWidth,
                      CellFormatting.DEFAULT,
                      false))));
    }
    // actually render the table.
    pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTable(
        vaBenefitInformationTable,
        24.0f,
        pageAndPosition.verticalPositionFromTop() + 8.0f,
        pageAndPosition.pageIndex(),
        rootElement,
        null,
        0.0f,
        10);
  }

  private DisplayFlags generateDisplayFlags(CorpRecord record) {
    // 4 booleans just have to be computed before calling the builder
    var displayMonthlyAwardAmount = false;
    var isAwardInfoMonthlyAwardAmount = false;
    var isClaimantCurrentMonthlyRate = false;
    if (record.getBenefitSummaryLetterOptions() != null
        && record.getBenefitSummaryLetterOptions().isMonthlyAward()
        && record.getAwardInfo() != null
        && (record.getAwardInfo().getFrequencyCode() != null
            && "mo".equalsIgnoreCase(record.getAwardInfo().getFrequencyCode()))
        && record.getAwardInfo().getFrequencyName() != null
        && "monthly".equalsIgnoreCase(record.getAwardInfo().getFrequencyName())) {
      if (record.getActiveAwardLine() != null
          && StringUtils.isNotBlank(record.getActiveAwardLine().getNetAward())
          && "cpl".equalsIgnoreCase(record.getAwardInfo().getBenefitCode())) {
        isAwardInfoMonthlyAwardAmount = true;
        displayMonthlyAwardAmount = true;
      }
      if (StringUtils.isNotBlank(record.getClaimantCurrentMonthlyRate())) {
        isClaimantCurrentMonthlyRate = true;
        displayMonthlyAwardAmount = true;
      }
    }
    boolean unEmployabilityValid =
        Optional.ofNullable(record.getOtherRatings())
            .flatMap(
                otherRatings ->
                    otherRatings.stream()
                        .filter(rating -> rating.getDecisionTypeName() != null)
                        .filter(rating -> rating.getDisabilityTypeName() != null)
                        .filter(
                            rating ->
                                "individual unemployment"
                                    .equalsIgnoreCase(rating.getDecisionTypeName()))
                        .filter(
                            rating ->
                                "individual unemployability granted"
                                    .equalsIgnoreCase(rating.getDisabilityTypeName()))
                        .findAny())
            .isPresent();
    boolean chapter35Eligibility =
        Optional.ofNullable(record.getBenefitSummaryLetterOptions())
            .map(BenefitSummaryLetterOptions::isChapter35Eligibility)
            .orElse(false);
    // This value is used to determine displaySpecialMonthlyCompensation
    boolean smcRatingTypeIsValid =
        Optional.ofNullable(record.getSpecialMonthlyCompensationRating())
            .flatMap(
                smcRatings ->
                    smcRatings.stream()
                        .filter(Objects::nonNull)
                        .map(SpecialMonthlyCompensationRating::getRatingTypeName)
                        .filter(Objects::nonNull)
                        .filter(
                            ratingTypeName ->
                                Set.of("k", "k-*").contains(StringUtils.lowerCase(ratingTypeName)))
                        .findAny())
            .isPresent();
    // This value is used to determine displaySpecialMonthlyCompensation
    boolean smcParagraphRatingsValueValid =
        Optional.ofNullable(record.getSpecialMonthlyCompensationParagraphRating())
            .flatMap(
                smcParagraphRatings ->
                    smcParagraphRatings.stream()
                        .filter(Objects::nonNull)
                        .map(SpecialMonthlyCompensationParagraphRating::getParagraphText)
                        .filter(
                            paragraphText ->
                                StringUtils.equalsIgnoreCase(
                                    paragraphText,
                                    "entitled to special monthly compensation under "
                                        + "38 u.s.c. 1114, subsection (k) and 38 cfr 3.350(a) on"))
                        .findAny())
            .isPresent();
    // This value is used to determine displayAdaptedHousing
    boolean isSpecialAdaptedHousing =
        Optional.ofNullable(record.getOtherRatings())
            .flatMap(
                otherRatings ->
                    otherRatings.stream()
                        .filter(Objects::nonNull)
                        .filter(
                            rating ->
                                "entitled to specially adapted housing"
                                    .equalsIgnoreCase(rating.getDisabilityTypeName()))
                        .findAny())
            .isPresent();
    return DisplayFlags.builder()
        .displayServiceConnectedDisabilities(
            record.getBenefitSummaryLetterOptions().isServiceConnectedDisabilities())
        .displayCombinedServiceConnectedEvaluation(
            Optional.ofNullable(record.getBenefitSummaryLetterOptions())
                    .map(BenefitSummaryLetterOptions::isServiceConnectedEvaluation)
                    .isPresent()
                && Optional.ofNullable(record.getServiceConnectedCombinedDegree()).isPresent())
        .displayNonServiceConnectedPension(
            Optional.ofNullable(record.getBenefitSummaryLetterOptions())
                    .map(BenefitSummaryLetterOptions::isNonServiceConnectedPension)
                    .isPresent()
                && Optional.ofNullable(record.getNonServiceConnectedOtherRating())
                    .map(CorpRecordOtherRating::getDecisionTypeName)
                    .map(StringUtils::lowerCase)
                    .filter("permanent and total disability"::equals)
                    .isPresent()
                && Optional.ofNullable(record.getNonServiceConnectedOtherRating())
                    .map(CorpRecordOtherRating::getDisabilityTypeName)
                    .map(StringUtils::lowerCase)
                    .filter("permanent and total for nsc"::equals)
                    .isPresent())
        .displayMonthlyAwardAmount(displayMonthlyAwardAmount)
        .isAwardInfoMonthlyAwardAmount(isAwardInfoMonthlyAwardAmount)
        .isClaimantCurrentMonthlyRate(isClaimantCurrentMonthlyRate)
        .displayEffectiveDateOfLastChangeToCurrentAward(
            isAwardInfoMonthlyAwardAmount && record.getActiveAwardLine().getEffectiveDate() != null)
        .unEmployabilityValid(unEmployabilityValid)
        .displayPaidAt100BecauseUnemployable(
            record.getBenefitSummaryLetterOptions() != null
                && record.getBenefitSummaryLetterOptions().isUnemployable()
                && unEmployabilityValid)
        .displayChapter35(chapter35Eligibility)
        .displayChapter35Date(chapter35Eligibility)
        .smcRatingTypeIsValid(smcRatingTypeIsValid)
        .smcParagraphRatingsValueValid(smcParagraphRatingsValueValid)
        .displaySpecialMonthlyCompensation(
            Optional.ofNullable(record.getBenefitSummaryLetterOptions())
                    .map(BenefitSummaryLetterOptions::isSpecialMonthlyCompensation)
                    .orElse(false)
                && smcRatingTypeIsValid
                && smcParagraphRatingsValueValid)
        .isSpecialAdaptedHousing(isSpecialAdaptedHousing)
        .displayAdaptedHousing(
            Optional.ofNullable(record.getBenefitSummaryLetterOptions())
                    .map(BenefitSummaryLetterOptions::isAdaptedHousing)
                    .filter(isAdaptedHousing -> isAdaptedHousing)
                    .filter(
                        isAdaptedHousing ->
                            "y".equalsIgnoreCase(record.getSpecialAdaptiveHousing()))
                    .isPresent()
                && isSpecialAdaptedHousing)
        .build();
  }


  @Override
  public byte[] getPdfBytes(CorpRecord record) {
    return createPdf(record).getPdfBytes();
  }

  @Getter
  @Setter
  @Builder
  @Accessors(fluent = true)
  private static class DisplayFlags {
    boolean displayServiceConnectedDisabilities;

    boolean displayCombinedServiceConnectedEvaluation;

    boolean displayNonServiceConnectedPension;

    boolean displayMonthlyAwardAmount;

    boolean isAwardInfoMonthlyAwardAmount;

    boolean isClaimantCurrentMonthlyRate;

    boolean displayEffectiveDateOfLastChangeToCurrentAward;

    boolean unEmployabilityValid;

    boolean displayPaidAt100BecauseUnemployable;

    boolean displayChapter35;

    boolean displayChapter35Date;

    boolean smcRatingTypeIsValid;

    boolean smcParagraphRatingsValueValid;

    boolean displaySpecialMonthlyCompensation;

    boolean isSpecialAdaptedHousing;

    boolean displayAdaptedHousing;

    boolean anyDisplayFlagTrue() {
      return displayServiceConnectedDisabilities
          || displayCombinedServiceConnectedEvaluation
          || displayNonServiceConnectedPension
          || displayMonthlyAwardAmount
          || displayEffectiveDateOfLastChangeToCurrentAward
          || displayPaidAt100BecauseUnemployable
          || displayChapter35
          || displayChapter35Date
          || displaySpecialMonthlyCompensation
          || displayAdaptedHousing;
    }
  }
}
