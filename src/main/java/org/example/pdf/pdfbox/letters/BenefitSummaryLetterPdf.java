package org.example.pdf.pdfbox.letters;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.example.model.corprecord.CorpRecord;
import org.example.model.corprecord.Service;
import org.example.pdf.pdfbox.CustomTaggedPdfBuilder;
import org.example.pdf.pdfbox.enums.CellFormatting;
import org.example.pdf.pdfbox.enums.Font;
import org.example.pdf.pdfbox.enums.TableHeaderType;
import org.example.pdf.pdfbox.pojo.Cell;
import org.example.pdf.pdfbox.pojo.DataTable;
import org.example.pdf.pdfbox.pojo.Row;
import org.example.pdf.pdfbox.pojo.Text;

import java.awt.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.min;

public abstract class BenefitSummaryLetterPdf {
  public static final String MILITARY_INFORMATION_TITLE = "Military Information";

  protected void drawEnding(
      CustomTaggedPdfBuilder formBuilder,
      PDStructureElement rootElement) {
    final var staticMessage =
        "You should contact your state or local office of Veterans' "
            + "affairs for information on any tax, license, "
            + "or fee-related benefits for which you may "
            + "be eligible. State offices of Veterans' affairs are "
            + "available at http://www.va.gov/statedva.htm.";
    final Text staticMessageSevenText =
        new Text(10.2f, staticMessage, Color.black, Font.INTER_REGULAR);
    var pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageSevenText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + 39f,
        2.0f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        true);
    CommonPdfLetterUtil.drawContactUs(formBuilder, rootElement, 31.25f);
    CommonPdfLetterUtil.drawSincerelyYours(formBuilder, rootElement, 25f);
    CommonPdfLetterUtil.drawRegionalOfficeDirectory(formBuilder, rootElement, 20f);
    formBuilder.addFinalTaggedFooter();
  }

  @SneakyThrows
  protected DataTable drawMilitaryInformation(
      CorpRecord record,
      CustomTaggedPdfBuilder formBuilder,
      PDStructureElement rootElement,
      boolean isVeteran) {
    final Text staticMessageOneText =
        new Text(
            12.24f,
            MILITARY_INFORMATION_TITLE,
            Color.black,
            org.example.pdf.pdfbox.enums.Font.INTER_BOLD);
    var pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageOneText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + 25.0f,
        0.0f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
    var staticMessageTwo =
        isVeteran
            ? "Your most recent, verified periods of service (up to three) include:"
            : "The Veteran's most recent, verified periods of service (up to three) include:";
    final Text staticMessageTwoText =
        new Text(
            10.2f,
            staticMessageTwo,
            Color.black,
            org.example.pdf.pdfbox.enums.Font.INTER_REGULAR);
    pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageTwoText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + 10.0f,
        0.0f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
    // actually draw the table
    DataTable militaryInformationTable =
        new DataTable(
            "This table shows periods of service. There are four columns and each "
                + "row is one period of service.",
            TableHeaderType.ROW_HEADERS);
    // width of each cell.
    var cellWidth =
        (formBuilder.pageWidth()
                - formBuilder.pageMargins().leftMargin()
                - formBuilder.pageMargins().rightMargin())
            / 4.0f;
    // table  header
    militaryInformationTable.addRow(
        new Row(
            Arrays.asList(
                new Cell(
                    "Branch of Service",
                    org.example.pdf.pdfbox.enums.Font.INTER_BOLD,
                    8.67f,
                    cellWidth,
                    CellFormatting.DEFAULT,
                    true),
                new Cell(
                    "Character of Service",
                    org.example.pdf.pdfbox.enums.Font.INTER_BOLD,
                    8.67f,
                    cellWidth,
                    CellFormatting.DEFAULT,
                    true),
                new Cell(
                    "Entered Active Duty",
                    org.example.pdf.pdfbox.enums.Font.INTER_BOLD,
                    8.67f,
                    cellWidth,
                    CellFormatting.DEFAULT,
                    true),
                new Cell(
                    "Released/Discharged",
                    org.example.pdf.pdfbox.enums.Font.INTER_BOLD,
                    8.67f,
                    cellWidth,
                    CellFormatting.DEFAULT,
                    true))));
    // each condition corresponds to an entry in the table.
    // only draw the entry if condition is true.
    boolean conditionOne =
        "y".equalsIgnoreCase(record.getVerifiedServiceDataInd())
            || "y".equalsIgnoreCase(record.getVadsInd())
            || "d".equalsIgnoreCase(record.getVadsInd());
    boolean conditionTwo =
        "y".equalsIgnoreCase(record.getVerifiedServiceDataInd2())
            || "y".equalsIgnoreCase(record.getVadsInd2())
            || "d".equalsIgnoreCase(record.getVadsInd2());
    boolean conditionThree =
        "y".equalsIgnoreCase(record.getVerifiedServiceDataInd3())
            || "y".equalsIgnoreCase(record.getVadsInd3())
            || "d".equalsIgnoreCase(record.getVadsInd3());
    List<Boolean> conditionsList = List.of(conditionOne, conditionTwo, conditionThree);
    for (int i = 0; i < min(record.getServices().size(), 3); i++) {
      if (!conditionsList.get(i)) {
        continue;
      }
      Service service = record.getServices().get(i);
      militaryInformationTable.addRow(
          new Row(
              Arrays.asList(
                  new Cell(
                      service.getBranchOfServiceName(),
                      org.example.pdf.pdfbox.enums.Font.INTER_REGULAR,
                      8.67f,
                      cellWidth,
                      CellFormatting.DEFAULT,
                      false),
                  new Cell(
                      getCharacterOfServiceFromCode(service),
                      org.example.pdf.pdfbox.enums.Font.INTER_REGULAR,
                      8.67f,
                      cellWidth,
                      CellFormatting.DEFAULT,
                      false),
                  new Cell(
                      Optional.ofNullable(service.getEnteredOnDutyDate())
                          .map(givenDate -> getFormattedDate(givenDate, record))
                          .orElse("UNKNOWN"),
                      org.example.pdf.pdfbox.enums.Font.INTER_REGULAR,
                      8.67f,
                      cellWidth,
                      CellFormatting.DEFAULT,
                      false),
                  new Cell(
                      Optional.ofNullable(service.getReleasedActiveDutyDate())
                          .map(givenDate -> getFormattedDate(givenDate, record))
                          .orElse("UNKNOWN"),
                      Font.INTER_REGULAR,
                      8.67f,
                      cellWidth,
                      CellFormatting.DEFAULT,
                      false))));
    }
    // actually render the table.
    formBuilder.drawTable(
        militaryInformationTable,
        0,
        pageAndPosition.verticalPositionFromTop() + 30.0f,
        pageAndPosition.pageIndex(),
        rootElement,
        null,
        0.0f,
        5.0f);
    return militaryInformationTable;
  }

  private String getCharacterOfServiceFromCode(final Service service) {
    return switch (service.getCharacterOfServiceCode().toLowerCase()) {
      case "hon" -> "Honorable";
      case "hva", "oth", "dva" -> "Other Than Honorable";
      case "uhc" -> "Under Honorable Conditions";
      case "gen" -> "General";
      case "unc" -> "Uncharacterized";
      case "uel" -> "Uncharacterized Entry Level";
      case "dis" -> "Dishonorable";
      default -> "UNKNOWN";
    };
  }

  private String getFormattedDate(final String maybeDate, final CorpRecord record) {
    return Optional.ofNullable(maybeDate)
        .filter(StringUtils::isNotBlank)
        .map(
            activeDutyReleaseDate -> {
              String returnDate;
              try {
                returnDate =
                    record
                        .getDateFormatterOutput()
                        .format(record.getDateFormatterInput().parse(activeDutyReleaseDate));
              } catch (ParseException e) {
                System.out.println(
                    "Date Parse Exception: Failed to parse date "
                        + "for Benefit Summary Letter Military Experience");
                throw new RuntimeException(e);
              }
              return returnDate;
            })
        .orElse("UNKNOWN");
  }
}
