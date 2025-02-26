package org.example.pdf.pdfbox.letters;

import org.example.model.corprecord.CorpRecord;
import org.example.pdf.pdfbox.CustomTaggedPdfBuilder;
import org.example.pdf.pdfbox.enums.Font;
import org.example.pdf.pdfbox.pojo.Text;
import org.example.pdf.pdfbox.pojo.UpdatedPagePosition;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommonPdfLetterUtil {
  private static final String CONTACT_US_TITLE = "How You Can Contact Us";


  /**
   * Draws the "Contact Us" section on a PDF, which is part of every letter.
   *
   * @param formBuilder the form to go and draw the "contact us" section onto
   * @param rootElement for tagging purposes, the element to append the contact us tree to.
   * @param offsetY the vertical position measured from top of page to begin drawing.
   */
  public static void drawContactUs(
      CustomTaggedPdfBuilder formBuilder,
      PDStructureElement rootElement,
      float offsetY) {
    final var staticMessage = "How You Can Contact Us";
    final Text staticMessageText = new Text(12.24f, staticMessage, Color.black, Font.INTER_BOLD);
    var pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + offsetY,
        0.0f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
    pageAndPosition = formBuilder.getUpdatedPosition();
    List<Text> bulletedListWithLinks =
        Stream.of(
                "If you need general information about benefits and eligibility, please visit us at https://www.va.gov.",
                "Call us at 1-800-827-1000.",
                "Contact us using Telecommunications Relay Services (TTY) at 711 24/7.",
                "Send electronic inquiries through the Internet at https://www.va.gov/contact-us.")
            .map(str -> new Text(10.2f, str, Color.BLACK, Font.INTER_REGULAR))
            .collect(Collectors.toList());
    formBuilder.drawBulletList(
        bulletedListWithLinks,
        0,
        pageAndPosition.verticalPositionFromTop() + 22f,
        pageAndPosition.pageIndex(),
        rootElement,
        13f,
        9f,
        18f);
    String letterContents =
        bulletedListWithLinks.stream().map(Text::toString).collect(Collectors.joining(" "));
  }

  static void drawHeading(
      CorpRecord record, CustomTaggedPdfBuilder formBuilder, PDStructureElement rootElement) {
    // print out letter date.
    var letterDateText =
        new Text(
            10.2f,
            record.getLetterDate(),
            Color.BLACK,
            org.example.pdf.pdfbox.enums.Font.INTER_REGULAR);
    formBuilder.drawTextElement(
        letterDateText, 0f, 145f, 0.0f, rootElement, StandardStructureTypes.P, 0, false);
    UpdatedPagePosition pageAndPosition = formBuilder.getUpdatedPosition();
    final List<String> addressLines = CorpRecordUtil.computeRecipientAddress(record);
    Optional.of(addressLines)
        .filter(lines -> lines.size() > 0)
        .ifPresent(
            givenAddressLines -> {
              // print out name & address
              final List<String> nameAndAddress = new ArrayList<>();
              final String recipientName = CorpRecordUtil.computeRecipientFullName(record);
              nameAndAddress.add(recipientName);
              nameAndAddress.addAll(givenAddressLines);
              final Text addressText =
                  new Text(
                      10.2f, String.join("\n", nameAndAddress), Color.black, Font.INTER_REGULAR);
              formBuilder.drawTextElement(
                  addressText,
                  0.0f,
                  pageAndPosition.verticalPositionFromTop() + 10.0f,
                  2.25f,
                  rootElement,
                  StandardStructureTypes.P,
                  pageAndPosition.pageIndex(),
                  false);
            });
  }

  static void drawRegionalOfficeDirectory(
      CustomTaggedPdfBuilder formBuilder, PDStructureElement rootElement, float offsetY) {
    final var staticMessage = "Regional Office Director";
    final Text staticMessageText = new Text(10.2f, staticMessage, Color.black, Font.INTER_BOLD);
    var pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + offsetY,
        0.0f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
  }

  static void drawSincerelyYours(
      CustomTaggedPdfBuilder formBuilder, PDStructureElement rootElement, float offsetY) {
    final var staticMessage = "Sincerely Yours,";
    final Text staticMessageText = new Text(10.2f, staticMessage, Color.black, Font.INTER_REGULAR);
    var pageAndPosition = formBuilder.getUpdatedPosition();
    formBuilder.drawTextElement(
        staticMessageText,
        0.0f,
        pageAndPosition.verticalPositionFromTop() + offsetY,
        0.0f,
        rootElement,
        StandardStructureTypes.P,
        pageAndPosition.pageIndex(),
        false);
  }
}
