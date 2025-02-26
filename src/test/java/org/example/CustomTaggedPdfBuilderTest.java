package org.example;


import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.example.model.corprecord.CorpRecord;
import org.example.pdf.PdfBoxGenerator;
import org.example.pdf.pdfbox.CustomTaggedPdfBuilder;
import org.example.pdf.pdfbox.enums.CellFormatting;
import org.example.pdf.pdfbox.enums.Font;
import org.example.pdf.pdfbox.enums.TableHeaderType;
import org.example.pdf.pdfbox.pojo.Cell;
import org.example.pdf.pdfbox.pojo.DataTable;
import org.example.pdf.pdfbox.pojo.PageMargins;
import org.example.pdf.pdfbox.pojo.Row;
import org.example.pdf.pdfbox.pojo.Text;
import org.example.pdf.pdfbox.pojo.UpdatedPagePosition;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.example.Main.loadCorpRecord;

public class CustomTaggedPdfBuilderTest {
  final float spaceBetweenBulletListItems = 5;

  final float spaceBetweenBulletAndText = 5;

  final float spaceBeforeBullet = 5;

  private final List<String> urls =
      List.of(
          "https://www.va.gov",
          "https://www.va.gov/contact-us",
          "http://www.va.gov/statedva.htm",
          "www.healthcare.gov",
          "https://www.va.gov/health-care/about-affordable-care-act");

  private final String phoneNumber = "1-800-827-1000";

  private final List<Text> simpleBulletedList =
      Stream.of(
              "test item 1",
              "test item 2",
              "test item 3. This will be a very long string that will end up being more than 1 line when rendered. "
                  + "It's not quite there just after that first sentence, but it will be after the 2nd.",
              "test item 4",
              "test item 5. This will be a very long string that will end up being more than 1 line when rendered. "
                  + "It's not quite there just after that first sentence, but it will be after the 2nd.",
              "test item 6",
              "test item 7")
          .map(str -> new Text(12, str, Color.BLACK, Font.INTER_REGULAR))
          .toList();

  private final List<Text> bulletedListWithLinks =
      Stream.of(
              "test item 1",
              "test item 2",
              "test item 3. "
                  + String.format(
                      "This is a very long string %d. Here is a url that will be "
                          + "injected into it: %s.\nHere is a phone number too on a new line: %s.",
                      3, urls.get(3), phoneNumber),
              "test item 4",
              "test item 5. "
                  + String.format(
                      "This is a very long string %d. Here is a url that will be "
                          + "injected into it: %s.\nHere is a phone number too on a new line: %s.",
                      5, urls.get(0), phoneNumber),
              "test item 6",
              "test item 7")
          .map(str -> new Text(12, str, Color.BLACK, Font.INTER_REGULAR))
          .toList();

  @Test
  @SneakyThrows
  public void bulletedListWithLinksTest() {
    CustomTaggedPdfBuilder formBuilder =
        new CustomTaggedPdfBuilder("UA EXAMPLE", new PageMargins(40, 50), false);
    PDStructureElement rootElement = formBuilder.rootElem();
    drawBulletList(bulletedListWithLinks, formBuilder, rootElement, 125f, 0);
    var bytes = formBuilder.getPdfBytes();
    // Files.write((new
    // File("./src/test/resources/pdf-library/bulleted-list-with-links.pdf")).toPath(),
    // bytes);
    PdfTestingUtil.testPdfsAreEqualExceptDatesAndUuids(
        bytes, "pdf-library/bulleted-list-with-links.pdf", 0.01f);
  }

  private void drawBulletList(
      List<Text> bulletedList,
      CustomTaggedPdfBuilder formBuilder,
      PDStructureElement rootElement,
      float y,
      int pageIndex) {
    // test extra x padding, and also test page overflow halfway through bullet point.
    formBuilder.drawBulletList(
        bulletedList,
        10,
        y,
        pageIndex,
        rootElement,
        spaceBetweenBulletListItems,
        spaceBetweenBulletAndText,
        spaceBeforeBullet);
  }

  private void drawHeader(
          CustomTaggedPdfBuilder formBuilder, PDStructureElement rootElement, float y, int pageIndex) {
    formBuilder.drawTextElement(
        new Text(14, "PDF HEADER 1", Color.BLUE.darker().darker(), Font.INTER_BOLD),
        0,
        y,
        5,
        rootElement,
        StandardStructureTypes.H1,
        pageIndex,
        false);
  }

  private void drawLongText(
      CustomTaggedPdfBuilder formBuilder, PDStructureElement rootElement, float y, int pageIndex) {
    formBuilder.drawTextElement(
        new Text(
            12,
            IntStream.range(0, 30)
                .mapToObj(integer -> String.format("This is a very long string %d. ", integer))
                .collect(Collectors.joining()),
            Color.BLACK,
            Font.INTER_REGULAR),
        0,
        y,
        5,
        rootElement,
        StandardStructureTypes.P,
        pageIndex,
        true);
  }

  private void drawTableOne(
      CustomTaggedPdfBuilder formBuilder, PDStructureElement sec1, float y, int pageIndex) {
    // Hard coded table1
    DataTable table1 = new DataTable("Table Summary 1", TableHeaderType.COLUMN_HEADERS);
    table1.addRow(
        new Row(
            Arrays.asList(
                new Cell(
                    "Row Header 1 (ID) BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH:",
                    Font.INTER_REGULAR,
                    10,
                    100,
                    CellFormatting.DEFAULT,
                    true),
                new Cell(
                    "56-8987 BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH",
                    Font.INTER_REGULAR,
                    10,
                    200,
                    CellFormatting.DEFAULT,
                    false))));
    table1.addRow(
        new Row(
            Arrays.asList(
                new Cell(
                    "Row Header 2 (Name) BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH:",
                    Font.INTER_REGULAR,
                    10,
                    100,
                    CellFormatting.DEFAULT,
                    true),
                new Cell(
                    "Some name BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH",
                    Font.INTER_REGULAR,
                    10,
                    200,
                    CellFormatting.DEFAULT,
                    false))));
    table1.addRow(
        new Row(
            Arrays.asList(
                new Cell(
                    "Row Header 3 (Date) BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH:",
                    Font.INTER_REGULAR,
                    10,
                    100,
                    CellFormatting.DEFAULT,
                    true),
                new Cell(
                    "12/31/2016 BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH BLAH",
                    Font.INTER_REGULAR,
                    10,
                    200,
                    CellFormatting.DEFAULT,
                    false))));
    formBuilder.drawTable(table1, 50, y, pageIndex, sec1, null, 5, 10);
  }

  private void drawTableTwo(
      CustomTaggedPdfBuilder formBuilder, PDStructureElement rootElement, float y, int pageIndex) {
    // Hard coded table2
    DataTable table2 = new DataTable("Table Summary 2", TableHeaderType.ROW_HEADERS);
    table2.addRow(
        new Row(
            Arrays.asList(
                new Cell(
                    "Column \nHeader \n1 (Header)",
                    Font.INTER_REGULAR,
                    10,
                    45,
                    CellFormatting.DEFAULT,
                    true),
                new Cell(
                    "Column \nHeader \n2 (Description)",
                    Font.INTER_REGULAR,
                    10,
                    215,
                    CellFormatting.DEFAULT,
                    true),
                new Cell(
                    "Column \nHeader \n3 (Text)",
                    Font.INTER_REGULAR,
                    10,
                    75,
                    CellFormatting.DEFAULT,
                    true))));
    table2.addRow(
        new Row(
            Arrays.asList(
                new Cell(
                    "Row \nHeader \n1", Font.INTER_REGULAR, 10, 45, CellFormatting.DEFAULT, false),
                new Cell(
                    "Hi. This is a long paragraph about absolutely nothing. I hope you enjoy reading it! \n"
                        + urls.get(0)
                        + "\n"
                        + "This is a long paragraph about absolutely nothing. I hope you enjoy reading it!\n"
                        + "Goodbye.",
                    Font.INTER_REGULAR,
                    10,
                    215,
                    CellFormatting.DEFAULT,
                    false),
                new Cell(
                    "System Verification: N/A.",
                    Font.INTER_REGULAR,
                    10,
                    75,
                    CellFormatting.DEFAULT,
                    false))));
    table2.addRow(
        new Row(
            Arrays.asList(
                new Cell(
                    "Row \nHeader \n2", Font.INTER_REGULAR, 10, 45, CellFormatting.DEFAULT, false),
                new Cell(
                    "Hi. This is a long paragraph about absolutely nothing. I hope you enjoy reading it! \n"
                        + "This is a long paragraph about absolutely nothing. I hope you enjoy reading it!\n"
                        + "This is a long paragraph about absolutely nothing. I hope you enjoy reading it!\n"
                        + "Goodbye.",
                    Font.INTER_REGULAR,
                    10,
                    215,
                    CellFormatting.DEFAULT,
                    false),
                new Cell(
                    "System Verification: N/A.",
                    Font.INTER_REGULAR,
                    10,
                    75,
                    CellFormatting.DEFAULT,
                    false))));
    table2.addRow(
        new Row(
            Arrays.asList(
                new Cell(
                    "Row \nHeader \n3", Font.INTER_REGULAR, 10, 45, CellFormatting.DEFAULT, false),
                new Cell(
                    "Hi. This is a long paragraph about absolutely nothing. I hope you enjoy reading it! \n"
                        + "This is a long paragraph about absolutely nothing. I hope you enjoy reading it!\n"
                        + "This is a long paragraph about absolutely nothing. I hope you enjoy reading it!\n"
                        + "Goodbye.",
                    Font.INTER_REGULAR,
                    10,
                    215,
                    CellFormatting.DEFAULT,
                    false),
                new Cell(
                    "System Verification: N/A.",
                    Font.INTER_REGULAR,
                    10,
                    75,
                    CellFormatting.DEFAULT,
                    false))));
    table2.addRow(
        new Row(
            Arrays.asList(
                new Cell(
                    "Row \nHeader \n4", Font.INTER_REGULAR, 10, 45, CellFormatting.DEFAULT, false),
                new Cell(
                    "Hi. This is a long paragraph about absolutely nothing. I hope you enjoy reading it! \n"
                        + "This is a long paragraph about absolutely nothing. I hope you enjoy reading it!\n"
                        + "This is a long paragraph about absolutely nothing. I hope you enjoy reading it!\n"
                        + "Goodbye.",
                    Font.INTER_REGULAR,
                    10,
                    215,
                    CellFormatting.DEFAULT,
                    false),
                new Cell(
                    "System Verification: N/A.",
                    Font.INTER_REGULAR,
                    10,
                    75,
                    CellFormatting.DEFAULT,
                    false))));
    formBuilder.drawTable(table2, 50, y, pageIndex, rootElement, null, 5, 10);
  }

  @Test
  @SneakyThrows
  public void fullPdfTest() {
    CustomTaggedPdfBuilder formBuilder =
        new CustomTaggedPdfBuilder("UA EXAMPLE", new PageMargins(40, 50), false);
    PDStructureElement rootElement = formBuilder.rootElem();
    drawHeader(formBuilder, rootElement, 0, 0);
    UpdatedPagePosition newPosition = formBuilder.getUpdatedPosition();
    drawTableOne(
        formBuilder,
        rootElement,
        newPosition.verticalPositionFromTop() + 25.0f,
        newPosition.pageIndex());
    newPosition = formBuilder.getUpdatedPosition();
    drawTableTwo(
        formBuilder,
        rootElement,
        newPosition.verticalPositionFromTop() + 50.0f,
        newPosition.pageIndex());
    newPosition = formBuilder.getUpdatedPosition();
    drawLongText(
        formBuilder,
        rootElement,
        newPosition.verticalPositionFromTop() + 450,
        newPosition.pageIndex());
    newPosition = formBuilder.getUpdatedPosition();
    // test drawing bullet list  when not split over multiple pages.
    drawBulletList(
        simpleBulletedList,
        formBuilder,
        rootElement,
        newPosition.verticalPositionFromTop() + 125.0f,
        newPosition.pageIndex());
    newPosition = formBuilder.getUpdatedPosition();
    // make sure that a multi line bullet point does not get split: it will all be on the next page.
    drawBulletList(
        simpleBulletedList,
        formBuilder,
        rootElement,
        newPosition.verticalPositionFromTop() + 270.0f,
        newPosition.pageIndex());
    newPosition = formBuilder.getUpdatedPosition();
    drawBulletList(
        bulletedListWithLinks,
        formBuilder,
        rootElement,
        newPosition.verticalPositionFromTop() + 500.0f,
        newPosition.pageIndex());
    formBuilder.addFinalTaggedFooter();
    var bytes = formBuilder.getPdfBytes();
    // If you made changes & need to updated expected PDF, temporarily uncomment lines below.
    // Files.write((new File("./src/test/resources/pdf-library/full-example.pdf")).toPath(),
    // bytes);
    PdfTestingUtil.testPdfsAreEqualExceptDatesAndUuids(
        bytes, "pdf-library/full-example.pdf", 0.01f);
  }

  @Test
  @SneakyThrows
  public void headerTest() {
    CustomTaggedPdfBuilder formBuilder =
        new CustomTaggedPdfBuilder("UA EXAMPLE", new PageMargins(40, 50), false);
    PDStructureElement rootElement = formBuilder.rootElem();
    drawHeader(formBuilder, rootElement, 0, 0);
    var bytes = formBuilder.getPdfBytes();
    // If you made changes & need to updated expected PDF, temporarily uncomment lines below.
    // Files.write((new File("./src/test/resources/pdf-library/header-only.pdf")).toPath(),
    // bytes);
    PdfTestingUtil.testPdfsAreEqualExceptDatesAndUuids(bytes, "pdf-library/header-only.pdf", 0.01f);
  }

  @Test
  @SneakyThrows
  public void longTextTest() {
    CustomTaggedPdfBuilder formBuilder =
        new CustomTaggedPdfBuilder("UA EXAMPLE", new PageMargins(40, 50), false);
    PDStructureElement rootElement = formBuilder.rootElem();
    drawLongText(formBuilder, rootElement, 200, 0);
    var bytes = formBuilder.getPdfBytes();
    // If you made changes & need to updated expected PDF, temporarily uncomment lines below.
    // Files.write((new File("./src/test/resources/pdf-library/long-text-only.pdf")).toPath(),
    // bytes);
    PdfTestingUtil.testPdfsAreEqualExceptDatesAndUuids(
        bytes, "pdf-library/long-text-only.pdf", 0.01f);
  }

  @Test
  @SneakyThrows
  public void simpleBulletedListTest() {
    CustomTaggedPdfBuilder formBuilder =
        new CustomTaggedPdfBuilder("UA EXAMPLE", new PageMargins(40, 50), false);
    PDStructureElement rootElement = formBuilder.rootElem();
    drawBulletList(simpleBulletedList, formBuilder, rootElement, 125.0f, 0);
    var bytes = formBuilder.getPdfBytes();
    // If you made changes & need to updated expected PDF, temporarily uncomment lines below.
    // Files.write((new File("./src/test/resources/pdf-library/simple-bulleted-list.pdf")).toPath(),
    // bytes);
    PdfTestingUtil.testPdfsAreEqualExceptDatesAndUuids(
        bytes, "pdf-library/simple-bulleted-list.pdf", 0.01f);
  }

  @Test
  @SneakyThrows
  public void tableWithColumnOneAsHeadersTest() {
    CustomTaggedPdfBuilder formBuilder =
        new CustomTaggedPdfBuilder("UA EXAMPLE", new PageMargins(40, 50), false);
    PDStructureElement rootElement = formBuilder.rootElem();
    drawTableOne(formBuilder, rootElement, 0, 0);
    var bytes = formBuilder.getPdfBytes();
    // If you made changes & need to updated expected PDF, temporarily uncomment lines below.
    // Files.write((new File("./src/test/resources/pdf-library/table-one-only.pdf")).toPath(),
    // bytes);
    PdfTestingUtil.testPdfsAreEqualExceptDatesAndUuids(
        bytes, "pdf-library/table-one-only.pdf", 0.01f);
  }

  @Test
  @SneakyThrows
  public void tableWithRowOneAsHeadersTest() {
    CustomTaggedPdfBuilder formBuilder =
        new CustomTaggedPdfBuilder("UA EXAMPLE", new PageMargins(40, 50), false);
    PDStructureElement rootElement = formBuilder.rootElem();
    drawTableTwo(formBuilder, rootElement, 0, 0);
    var bytes = formBuilder.getPdfBytes();
    // If you made changes & need to updated expected PDF, temporarily uncomment lines below.
    // Files.write((new File("./src/test/resources/pdf-library/table-two-only.pdf")).toPath(),
    // bytes);
    PdfTestingUtil.testPdfsAreEqualExceptDatesAndUuids(
        bytes, "pdf-library/table-two-only.pdf", 0.01f);
  }


  @Test
  public void testJesseGrayBenefitSummaryLetter(){
    CorpRecord corpRecord = loadCorpRecord();
    PdfBoxGenerator generator = new PdfBoxGenerator();
    byte[] bytes = generator.generatePdfForTemplate(corpRecord, "Some footer. Hello World!");
    PdfTestingUtil.testPdfsAreEqualExceptDatesAndUuids(
            bytes, "pdf-library/jesse-gray-benefit-summary.pdf", 0.01f);
  }
}
