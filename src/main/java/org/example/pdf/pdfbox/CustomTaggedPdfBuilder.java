package org.example.pdf.pdfbox;

import lombok.experimental.Accessors;
import org.example.pdf.pdfbox.enums.Font;
import org.example.pdf.pdfbox.enums.TableHeaderType;
import org.example.pdf.pdfbox.pojo.Cell;
import org.example.pdf.pdfbox.pojo.DataTable;
import org.example.pdf.pdfbox.pojo.NewPageRelatedVariables;
import org.example.pdf.pdfbox.pojo.PageMargins;
import org.example.pdf.pdfbox.pojo.Row;
import org.example.pdf.pdfbox.pojo.Text;
import org.example.pdf.pdfbox.pojo.UpdatedPagePosition;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDNumberTreeNode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkInfo;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDObjectReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDLayoutAttributeObject;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDTableAttributeObject;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.xml.XmpSerializer;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE;

// todo: PAC accessibility tool reports inconsistent entries in struct tree. find out why.
@Slf4j
@Accessors(fluent = true)
public class CustomTaggedPdfBuilder {
  private static final String WATERMARK_PATH_FILENAME = "templates/common/watermark.jpg";

  private static final String CARD_WATERMARK_FILENAME = "templates/common/card-watermark.jpg";

  private static final String LHARBIN_SIGNATURE_FILENAME = "templates/common/lharbin-signature.gif";

  private static final String REGULAR_FONT_PATH = "embedded-fonts/Inter-Regular.ttf";

  private static final String BOLD_FONT_PATH = "embedded-fonts/Inter-Bold.ttf";

  private static final String VA_LOGO_FILENAME = "templates/common/va_seal.jpg";

  private static final float vaSealHeight = 69;

  // image is 164 x 48
  private static final float lharbinSignatureHeight = 48;

  private static final byte[] WATERMARK_BYTES =
      CustomTaggedPdfBuilder.readBinaryFile(WATERMARK_PATH_FILENAME);

  private static final byte[] CARD_WATERMARK_BYTES =
      CustomTaggedPdfBuilder.readBinaryFile(CARD_WATERMARK_FILENAME);

  private static final byte[] VA_SEAL_BYTES =
      CustomTaggedPdfBuilder.readBinaryFile(VA_LOGO_FILENAME);

  private static final byte[] LHARBIN_SIGNATURE_BYTES =
      CustomTaggedPdfBuilder.readBinaryFile(LHARBIN_SIGNATURE_FILENAME);

  // will match urls such as "www.healthcare.gov" or
  // "https://www.va.gov/health-care/about-affordable-care-act"
  private static final String urlRegex = "(http|www)[^\\s]*[a-zA-Z0-9]";

  private static final String phoneNumberRegex = "(1-)?\\d{3}-\\d{3}-\\d{4}";

  private static final float footerDistanceToBottom = 11.0f;

  private static final float footerFontSize = 8.5f;

  // how far from the top of the page to draw va seal
  private static final float vaSealTopOffset = 35;

  private final PDRectangle letterRectangle = PDRectangle.A4;

  @Getter private final float pageWidth = letterRectangle.getWidth();

  private final float pageHeight = letterRectangle.getHeight();

  private final PDDocument pdf;

  private final ArrayList<PDPage> pages = new ArrayList<>();

  private final PDFont interRegularFont;

  private final PDFont interBoldFont;

  private final COSArray nums = new COSArray();

  private final COSArray numDictionaries = new COSArray();

  @Getter private final PageMargins pageMargins;

  private final PDResources resources;

  private final COSArray cosArrayForAdditionalPages;

  private final COSArray boxArray;

  private final String urlOrPhoneNumberRegex = String.format("(%s|%s)", urlRegex, phoneNumberRegex);

  private final Pattern urlOrPhoneNumberPattern = Pattern.compile(urlOrPhoneNumberRegex);

  private final Pattern phoneNumberPattern = Pattern.compile(phoneNumberRegex);

  private final ArrayList<COSDictionary> annotDicts = new ArrayList<>();

  private final Text footerText =
      new Text(footerFontSize, UUID.randomUUID().toString(), Color.BLACK, Font.INTER_REGULAR);

  @Getter @Accessors(fluent = true)
  private PDStructureElement rootElem;

  private COSDictionary currentMarkedContentDictionary;

  private int currentMcid = 0;

  private int currentStructParent = 1;

  private UpdatedPagePosition updatedPagePosition;

  /**
   * Constructor for a class that will wrap around PDFBox and provide an easier interface for
   * constructing accessible PDFs.
   *
   * @param title The PDF Document title.
   * @param margins Margins that define top and bottom margins of any given PDF page.
   * @param useCardWatermark Boolean to indicate if you want to use the card watermark.
   */
  @SneakyThrows
  @SuppressWarnings("this-escape")
  public CustomTaggedPdfBuilder(String title, PageMargins margins, boolean useCardWatermark) {
    // Setup new document
    pdf = new PDDocument();
    pdf.setVersion(1.7f);
    pdf.getDocumentInformation().setTitle(title);
    this.pageMargins = margins;
    // setup the fonts and embed them
    resources = new PDResources();
    // using font "inter" as it is a free alternative to "Helvetica" which looks close enough.
    // about alternatives:
    // https://www.learnui.design/blog/helvetica-similar-fonts.html
    // source for inter font:
    // https://fonts.google.com/specimen/Inter?preview.text=test&preview.text_type=custom
    this.interRegularFont = getFont(pdf, REGULAR_FONT_PATH);
    this.interBoldFont = getFont(pdf, BOLD_FONT_PATH);
    resources.put(COSName.getPDFName("Inter"), interRegularFont);
    resources.put(COSName.getPDFName("Inter-Bold"), interBoldFont);
    cosArrayForAdditionalPages = new COSArray();
    boxArray = new COSArray();
    addXmpMetadata(title);
    setupDocumentCatalog();
    // setup page 1
    prePageOne();
    addPage();
    addRoot(0);
    drawVaSeal(pdf, 0, vaSealTopOffset);
    drawTitle();
    if (!useCardWatermark) {
      addAndTagWatermarkToPage();
    } else {
      addAndTagCardWatermarkToPage();
    }
    nums.add(COSInteger.get(0));
  }

  @SneakyThrows
  private static PDType0Font getFont(PDDocument pdDocument, String path) {
    return PDType0Font.load(
        pdDocument, CustomTaggedPdfBuilder.class.getClassLoader().getResourceAsStream(path));
  }

  @SneakyThrows
  private static byte[] readBinaryFile(final String filePath) {
    return CustomTaggedPdfBuilder.class
        .getClassLoader()
        .getResource(filePath)
        .openStream()
        .readAllBytes();
  }

  private void addAndTagCardWatermarkToPage() {
    PDPageContentStream contentStream = drawCardWatermark(pages.size() - 1);
    appendArtifactToPage(contentStream, pages.size() - 1);
  }

  private void addAndTagWatermarkToPage() {
    PDPageContentStream contentStream = drawStandardWatermark(pages.size() - 1);
    appendArtifactToPage(contentStream, pages.size() - 1);
  }

  private void addAnnotationContent(
      PDObjectReference objectReference,
      PDStructureElement annotationContainerElement,
      String type,
      int pageIndex) {
    COSDictionary annotDict = new COSDictionary();
    COSArray annotArray = new COSArray();
    annotArray.add(COSInteger.get(currentMcid));
    annotArray.add(objectReference);
    annotDict.setItem(COSName.K, annotArray);
    annotDict.setItem(COSName.P, annotationContainerElement.getCOSObject());
    annotDict.setItem(COSName.PG, pages.get(pageIndex).getCOSObject());
    annotDict.setName(COSName.S, type);
    annotDict.setName(COSName.TYPE, "StructElem");
    annotDicts.add(annotDict);
    setNextMarkedContentDictionary();
    numDictionaries.add(annotDict);
    annotationContainerElement.appendKid(objectReference);
  }

  /**
   * Once all elements have been added to the PDF, call this function to add the final UUID footer.
   */
  public void addFinalTaggedFooter() {
    var paragraphElem =
        appendToTagTree(StandardStructureTypes.P, pages.get(pages.size() - 1), rootElem);
    footerText.wrappedText(List.of(footerText.text()));
    drawSimpleText(
        footerText,
        0.0f,
        pageHeight - footerDistanceToBottom,
        pdf.getNumberOfPages() - 1,
        StandardStructureTypes.SPAN,
        paragraphElem,
        1.0f,
        null,
        false,
        false);
  }

  private void addPage() {
    PDPage page = new PDPage(letterRectangle);
    page.getCOSObject().setItem(COSName.getPDFName("Tabs"), COSName.S);
    page.setResources(resources);
    page.getResources().getCOSObject().setItem(COSName.PROC_SET, cosArrayForAdditionalPages);
    page.getCOSObject().setItem(COSName.CROP_BOX, boxArray);
    page.getCOSObject().setItem(COSName.ROTATE, COSInteger.get(0));
    page.getCOSObject().setItem(COSName.STRUCT_PARENTS, COSInteger.get(0));
    pages.add(page);
    pdf.addPage(pages.get(pages.size() - 1));
    appendArtifactFooterToPreviousPage();
  }

  // Adds the parent tree to root struct element to identify tagged content
  private void addParentTree() {
    COSDictionary dict = new COSDictionary();
    nums.add(numDictionaries);
    for (int i = 1; i < currentStructParent; i++) {
      nums.add(COSInteger.get(i));
      nums.add(annotDicts.get(i - 1));
    }
    dict.setItem(COSName.NUMS, nums);
    PDNumberTreeNode numberTreeNode = new PDNumberTreeNode(dict, dict.getClass());
    pdf.getDocumentCatalog().getStructureTreeRoot().setParentTreeNextKey(currentStructParent);
    pdf.getDocumentCatalog().getStructureTreeRoot().setParentTree(numberTreeNode);
    pdf.getDocumentCatalog().getStructureTreeRoot().appendKid(rootElem);
  }

  /**
   * Adds a DOCUMENT Structure element as the structure tree root.
   *
   * @param pageIndex The page index to go and add root to. Usually just 0.
   */
  public void addRoot(int pageIndex) {
    rootElem = new PDStructureElement(StandardStructureTypes.DOCUMENT, null);
    rootElem.setTitle("PDF Document");
    rootElem.setPage(pages.get(pageIndex));
    rootElem.setLanguage("EN-US");
  }

  private PDStructureElement addTableCellParentTag(
      Cell cell, int pageIndex, PDStructureElement currentRow, TableHeaderType tableHeaderType) {
    COSDictionary cellAttr = new COSDictionary();
    cellAttr.setName(COSName.O, "Table");
    cellAttr.setName(COSName.TYPE, "StructElem");
    if (cell.header()) {
      if (tableHeaderType == TableHeaderType.ROW_HEADERS) {
        cellAttr.setName(COSName.getPDFName("Scope"), PDTableAttributeObject.SCOPE_COLUMN);
      } else {
        // for some reason commonlook will complain about this.
        // it isn't happy about table cells only having row headers & not any column headers.
        cellAttr.setName(COSName.getPDFName("Scope"), PDTableAttributeObject.SCOPE_ROW);
      }
    }
    cellAttr.setInt(COSName.getPDFName("ColSpan"), 1);
    cellAttr.setInt(COSName.getPDFName("RowSpan"), 1);
    String structureType = cell.header() ? StandardStructureTypes.TH : StandardStructureTypes.TD;
    PDStructureElement cellElement =
        appendToTagTree(structureType, pages.get(pageIndex), currentRow);
    cellElement.getCOSObject().setItem(COSName.A, cellAttr);
    return cellElement;
  }

  @SneakyThrows
  private void addXmpMetadata(String title) {
    // Add UA XMP metadata based on specs at
    // https://taggedpdf.com/508-pdf-help-center/pdfua-identifier-missing/
    XMPMetadata xmp = XMPMetadata.createXMPMetadata();
    xmp.createAndAddDublinCoreSchema();
    xmp.getDublinCoreSchema().setTitle(title);
    xmp.getDublinCoreSchema().setDescription(title);
    xmp.createAndAddPDFAExtensionSchemaWithDefaultNS();
    xmp.getPDFExtensionSchema().addNamespace("http://www.aiim.org/pdfa/ns/schema#", "pdfaSchema");
    xmp.getPDFExtensionSchema()
        .addNamespace("http://www.aiim.org/pdfa/ns/property#", "pdfaProperty");
    xmp.getPDFExtensionSchema().addNamespace("http://www.aiim.org/pdfua/ns/id/", "pdfuaid");
    XMPSchema uaSchema =
        new XMPSchema(XMPMetadata.createXMPMetadata(), "pdfaSchema", "pdfaSchema", "pdfaSchema");
    uaSchema.setTextPropertyValue("schema", "PDF/UA Universal Accessibility Schema");
    uaSchema.setTextPropertyValue("namespaceURI", "http://www.aiim.org/pdfua/ns/id/");
    uaSchema.setTextPropertyValue("prefix", "pdfuaid");
    XMPSchema uaProp =
        new XMPSchema(
            XMPMetadata.createXMPMetadata(), "pdfaProperty", "pdfaProperty", "pdfaProperty");
    uaProp.setTextPropertyValue("name", "part");
    uaProp.setTextPropertyValue("valueType", "Integer");
    uaProp.setTextPropertyValue("category", "internal");
    uaProp.setTextPropertyValue(
        "description", "Indicates, which part of ISO 14289 standard is followed");
    uaSchema.addUnqualifiedSequenceValue("property", uaProp);
    xmp.getPDFExtensionSchema().addBagValue("schemas", uaSchema);
    xmp.getPDFExtensionSchema().setPrefix("pdfuaid");
    xmp.getPDFExtensionSchema().setTextPropertyValue("part", "1");
    XmpSerializer serializer = new XmpSerializer();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    serializer.serialize(xmp, baos, true);
    PDMetadata metadata = new PDMetadata(pdf);
    metadata.importXMPMetadata(baos.toByteArray());
    pdf.getDocumentCatalog().setMetadata(metadata);
  }

  @SneakyThrows
  private void appendArtifactFooterToPreviousPage() {
    if (pages.size() < 2) {
      // don't start appending until there are at least 2 pages. Append to previous page.
      return;
    }
    PDPageContentStream contentStream =
        new PDPageContentStream(
            pdf, pages.get(pages.size() - 2), PDPageContentStream.AppendMode.APPEND, false);
    setNextMarkedContentDictionary();
    contentStream.beginMarkedContent(
        COSName.ARTIFACT, PDPropertyList.create(currentMarkedContentDictionary));
    // Open up a stream to draw text at a given location.
    contentStream.beginText();
    contentStream.setFont(getPdFont(footerText.font()), footerText.fontSize());
    contentStream.newLineAtOffset(this.pageMargins.leftMargin(), footerDistanceToBottom);
    contentStream.setNonStrokingColor(footerText.textColor());
    contentStream.showText(footerText.text());
    contentStream.endText();
    appendArtifactToPage(contentStream, pages.size() - 2);
  }

  @SneakyThrows
  private void appendArtifactToPage(PDPageContentStream contentStream, int pageIndex) {
    COSDictionary numDict = new COSDictionary();
    numDict.setInt(COSName.K, currentMcid - 1);
    numDict.setItem(COSName.PG, pdf.getPage(pageIndex).getCOSObject());
    numDict.setName(COSName.S, COSName.ARTIFACT.getName());
    numDictionaries.add(numDict);
    contentStream.endMarkedContent();
    contentStream.close();
  }

  @SneakyThrows
  private void appendToLinkAnnotationToLinkTag(
      int pageIndex,
      String hyperLinkOrPhoneNumber,
      PDStructureElement linkElem,
      float x,
      float y,
      float width,
      float height) {
    // the question & this answer https://stackoverflow.com/a/21163795/4832515 were the basis for
    // this code
    PDAnnotationLink linkAnnotation = new PDAnnotationLink();
    linkAnnotation.setReadOnly(true);
    linkAnnotation.setHighlightMode(HIGHLIGHT_MODE_OUTLINE);
    // note: set this value to false if you're debugging
    linkAnnotation.setInvisible(true);
    float[] blue = {0.0f, 0.0f, 1.0f};
    linkAnnotation.setColor(new PDColor(blue, PDDeviceRGB.INSTANCE));
    linkAnnotation.setContents(hyperLinkOrPhoneNumber);
    Matcher matcher = phoneNumberPattern.matcher(hyperLinkOrPhoneNumber);
    var action = new PDActionURI();
    action.setTrackMousePosition(false);
    if (matcher.find()) {
      // this is a phone number, stick 'tel:' as a prefix to denote that.
      hyperLinkOrPhoneNumber = "tel:" + hyperLinkOrPhoneNumber;
    }
    action.setURI(hyperLinkOrPhoneNumber);
    linkAnnotation.setAction(action);
    // set position of annotation on page.
    PDRectangle position = new PDRectangle();
    position.setLowerLeftX(x);
    position.setLowerLeftY(y);
    position.setUpperRightX(x + width);
    position.setUpperRightY(y + height);
    linkAnnotation.setRectangle(position);
    // not sure if this is even needed really
    linkAnnotation.setStructParent(currentStructParent);
    currentStructParent++;
    linkAnnotation.setPage(pdf.getPage(pageIndex));
    // This line will add a link to your page
    pdf.getPage(pageIndex).getAnnotations().add(linkAnnotation);
    PDObjectReference objectReference = new PDObjectReference();
    objectReference.setReferencedObject(linkAnnotation);
    addAnnotationContent(objectReference, linkElem, StandardStructureTypes.LINK, pageIndex);
  }

  private PDStructureElement appendToTagTree(
      COSDictionary cosDictionary, PDPage currentPage, PDStructureElement parent) {
    // Create a structure element and add it as a chile to the given parent structure element.
    PDStructureElement structureElement = new PDStructureElement(cosDictionary);
    structureElement.setPage(currentPage);
    parent.appendKid(structureElement);
    structureElement.setParent(parent);
    return structureElement;
  }

  private PDStructureElement appendToTagTree(
      String structureType, PDPage currentPage, PDStructureElement parent) {
    // Create a structure element and add it as a chile to the given parent structure element.
    PDStructureElement structureElement = new PDStructureElement(structureType, parent);
    structureElement.setPage(currentPage);
    parent.appendKid(structureElement);
    return structureElement;
  }

  private PDMarkedContent appendToTagTree(PDPage currentPage, PDStructureElement parent) {
    COSName parentCosName = COSName.getPDFName(parent.getStructureType());
    COSDictionary numDict = new COSDictionary();
    numDict.setInt(COSName.K, currentMcid - 1);
    numDict.setName(COSName.TYPE, "StructElem");
    numDict.setItem(COSName.PG, currentPage.getCOSObject());
    PDMarkedContent markedContent =
        new PDMarkedContent(parentCosName, currentMarkedContentDictionary);
    parent.appendKid(markedContent);
    numDict.setItem(COSName.P, parent.getCOSObject());
    numDict.setName(COSName.S, parentCosName.getName());
    numDictionaries.add(numDict);
    return markedContent;
  }

  @SneakyThrows
  private void computeWrappedLines(Text text, float lineLimit) {
    if (text == null) {
      return;
    }
    if (text.text() == null) {
      text.wrappedText(List.of());
      return;
    }
    if (text.wrappedText() != null) {
      // already defined. No need to recompute
      return;
    }
    List<List<String>> linesOfWords =
        Stream.of(text.text().split("\n")).map(line -> List.of(line.split(" "))).toList();
    List<String> wrappedLines = new ArrayList<>();
    float spaceWidth = getStringWidth(text, " ");
    float currentLineWidth;
    int startingWordIndex;
    for (List<String> words : linesOfWords) {
      currentLineWidth = 0;
      startingWordIndex = 0;
      for (int i = 0; i < words.size(); i++) {
        float currentWordWidth = getStringWidth(text, words.get(i));
        currentLineWidth += currentWordWidth;
        if (currentLineWidth > lineLimit) {
          // make a new line ending with the word before.
          String line = String.join(" ", words.subList(startingWordIndex, i));
          wrappedLines.add(line.trim() + " ");
          // update starting word index to the current word. This word will be start of next line.
          startingWordIndex = i;
          // reset current line width back to width of the current word
          currentLineWidth = currentWordWidth;
        } else {
          // didn't hit a new line yet. Add a space to the count.
          currentLineWidth += spaceWidth;
        }
      }
      // last line will have to be added.
      String lastLine = String.join(" ", words.subList(startingWordIndex, words.size()));
      wrappedLines.add(lastLine.trim() + " ");
    }
    text.wrappedText(wrappedLines);
  }

  /**
   * Render a bulleted list onto the PDF. Text in each list item will be parsed for telephone and
   * URL links. But, the logic will only identify & annotate 1 link per printed line of text.
   *
   * @param items List of itmes, where each item represent a single bullet point to be drawn.
   * @param x The X coordinate of the list, measured from the left edge of page.
   * @param y The y coordinate of the list, measure from top edge of page.
   * @param pageIndex The page number in which to draw the list. Page numbers start at 0.
   * @param parent The parent element to attach the list. This is for tagging purposes.
   * @param spaceBetweenLines Between each list item, how much vertical space do you want?
   * @param spaceBetweenBulletAndText For each bullet point, how much horizontal space between
   *     bullet and text?
   * @param spaceBeforeBullet For each bullet point, how much horizontal space before bullet?
   */
  @SneakyThrows
  public void drawBulletList(
      final List<Text> items,
      final float x,
      final float y,
      final int pageIndex,
      final PDStructureElement parent,
      final float spaceBetweenLines,
      final float spaceBetweenBulletAndText,
      final float spaceBeforeBullet) {
    final var prefix = "• ";
    final float prefixWidth = getStringWidth(items.get(0), prefix);
    final var lineLimit =
        pageWidth
            - pageMargins.leftMargin()
            - pageMargins.rightMargin()
            - prefixWidth
            - spaceBetweenBulletAndText
            - spaceBeforeBullet;
    items.forEach(text -> computeWrappedLines(text, lineLimit));
    float newY = y;
    int newPageIndex = pageIndex;
    // do not try to break a single list item across multiple pages: tagging gets screwed up
    // instead, compute if all lines fit as a single unit.
    // If so, add the unit to the current page. If not, add a new page before adding the unit.
    // This assumes that a single bullet item on a list will *not* be longer than an entire page.
    final float verticalOffsetFromBottom = pageHeight - y;
    final double finalVerticalPosition =
        verticalOffsetFromBottom
            + items.stream()
                .map(
                    text -> {
                      final float newOffset = -text.fontSize() - spaceBetweenLines;
                      float bulletItemHeight = newOffset * text.wrappedText().size();
                      return bulletItemHeight;
                    })
                .mapToDouble(blah -> (double) blah)
                .sum();
    if (finalVerticalPosition <= this.pageMargins.bottomMargin()) {
      // need to add a new page before drawing on this content.
      addPage();
      addAndTagWatermarkToPage();
      newPageIndex++;
      newY = pageMargins().topMargin();
    }
    PDStructureElement pdfList =
        appendToTagTree(StandardStructureTypes.L, pages.get(pageIndex), parent);
    for (int i = 0; i < items.size(); i++) {
      PDStructureElement pdfListElement =
          appendToTagTree(StandardStructureTypes.LI, pages.get(newPageIndex), pdfList);
      Text text = items.get(i);
      drawBulletListItem(
          prefix,
          text,
          x,
          newY,
          newPageIndex,
          pdfListElement,
          spaceBetweenLines,
          spaceBetweenBulletAndText,
          spaceBeforeBullet);
      newY = updatedPagePosition.verticalPositionFromTop();
      newPageIndex = updatedPagePosition.pageIndex();
    }
  }

  @SneakyThrows
  private void drawBulletListItem(
      final String prefix,
      final Text text,
      final float x,
      final float y,
      final int pageIndex,
      final PDStructureElement listItemParent,
      final float spaceBetweenLines,
      final float spaceBetweenBulletAndText,
      final float spaceBeforeBullet) {
    // Set up the next marked content element with an MCID and create the containing P structure
    // element.
    PDPageContentStream contentStream =
        new PDPageContentStream(
            pdf, pages.get(pageIndex), PDPageContentStream.AppendMode.APPEND, false);
    setNextMarkedContentDictionary();
    contentStream.beginMarkedContent(
        COSName.P, PDPropertyList.create(currentMarkedContentDictionary));
    // Open up a stream to draw text at a given location.
    contentStream.beginText();
    contentStream.setFont(getPdFont(text.font()), text.fontSize());
    float verticalOffsetFromBottom = pageHeight - y;
    contentStream.newLineAtOffset(
        this.pageMargins.leftMargin() + x + spaceBeforeBullet, verticalOffsetFromBottom);
    contentStream.setNonStrokingColor(text.textColor());
    PDStructureElement listTextTagElement = null;
    float lineOffset = -text.fontSize() - spaceBetweenLines;
    final float prefixWidth = getStringWidth(text, prefix);
    for (int i = 0; i < text.wrappedText().size(); i++) {
      if (i == 0) {
        var bulletTagElement =
            appendToTagTree(StandardStructureTypes.LBL, pages.get(pageIndex), listItemParent);
        contentStream.showText(prefix);
        contentStream.newLineAtOffset(prefixWidth + spaceBetweenBulletAndText, 0);
        appendToTagTree(pages.get(pageIndex), bulletTagElement);
        // make the bullet point be tagged in just <LBL>, and the text right after separately in
        // <LBODY>
        contentStream.endMarkedContent();
        // tag the list element's text body
        setNextMarkedContentDictionary();
        contentStream.beginMarkedContent(
            COSName.P, PDPropertyList.create(currentMarkedContentDictionary));
        listTextTagElement =
            appendToTagTree(StandardStructureTypes.L_BODY, pages.get(pageIndex), listItemParent);
      }
      String line = text.wrappedText().get(i);
      final PDStructureElement currentElem = listTextTagElement;
      drawLineThatMightHaveLink(
          text,
          contentStream,
          pageIndex,
          line,
          currentElem,
          x + prefixWidth + spaceBetweenBulletAndText + spaceBeforeBullet,
          verticalOffsetFromBottom,
          lineOffset,
          true);
      verticalOffsetFromBottom += lineOffset;
    }
    contentStream.endText();
    appendToTagTree(pages.get(pageIndex), listTextTagElement);
    // End the marked content and append it's P structure element to the containing P structure
    // element.
    contentStream.endMarkedContent();
    contentStream.close();
    updatedPagePosition = new UpdatedPagePosition(pageHeight - verticalOffsetFromBottom, pageIndex);
  }

  private PDPageContentStream drawCardWatermark(int pageIndex) {
    float height = 218;
    var marginTop = pageHeight / 2.0f - height * .45f;
    float width = 529;
    var marginLeft = pageWidth / 2.0f - width / 2.0f;
    return drawWatermarkImage(
        CARD_WATERMARK_BYTES,
        COSName.ARTIFACT,
        "Watermark",
        pageIndex,
        marginTop,
        marginLeft,
        (int) width,
        (int) height);
  }

  private void drawCellContents(
      int pageIndex,
      Row currentRow,
      PDStructureElement cellStructureElement,
      Cell currentCell,
      BiFunction<Font, String, Font> fontFunction,
      float cellX,
      float cellY,
      float spaceBetweenLines) {
    switch (currentCell.align()) {
      case PROOF_OF_SERVICE ->
          drawSimpleText(
              currentCell,
              cellX + 15,
              cellY,
              pageIndex,
              StandardStructureTypes.SPAN,
              cellStructureElement,
              spaceBetweenLines,
              fontFunction,
              false,
              false);
      case DEFAULT ->
          drawSimpleText(
              currentCell,
              cellX,
              cellY + currentRow.height() / 2 + currentCell.fontSize() / 4.0f,
              pageIndex,
              StandardStructureTypes.SPAN,
              cellStructureElement,
              spaceBetweenLines,
              fontFunction,
              false,
              false);
      default -> throw new RuntimeException("invalid cell formatting specified.");
    }
  }

  /**
   * Draw the signature of Lharbin onto the letter. If there is not enough space, there is overflow
   * logic to add another page & put signature on top.
   *
   * @param pageNumber Page number where the signature will be drawn.
   * @param verticalOffsetFromTop the vertical position where signature will be placed.
   */
  @SneakyThrows
  public void drawLharbinSignature(int pageNumber, float verticalOffsetFromTop) {
    PDDocument pdfDocument = this.pdf;
    var offsetY = pageHeight - lharbinSignatureHeight - verticalOffsetFromTop;
    if (offsetY <= this.pageMargins.bottomMargin()) {
      var newParams = handleImageOverflow(pageNumber);
      pageNumber = newParams.newPageIndex();
      offsetY = newParams.newVerticalOffsetFromBottom();
    }
    var offsetX = this.pageMargins.leftMargin();
    PDImageXObject pdImageObject =
        PDImageXObject.createFromByteArray(pdfDocument, LHARBIN_SIGNATURE_BYTES, "logo");
    PDPage page = pdfDocument.getPage(pageNumber);
    PDPageContentStream contentStream =
        new PDPageContentStream(pdfDocument, page, PDPageContentStream.AppendMode.APPEND, true);
    setNextMarkedContentDictionary();
    contentStream.beginMarkedContent(
        COSName.getPDFName("Figure"), PDPropertyList.create(currentMarkedContentDictionary));
    PDStructureElement divElem =
        appendToTagTree(StandardStructureTypes.DIV, pdfDocument.getPage(pageNumber), rootElem);
    PDStructureElement paragraphElem =
        appendToTagTree(StandardStructureTypes.P, pdfDocument.getPage(pageNumber), divElem);
    float lharbinSignatureWidth = 164;
    contentStream.drawImage(
        pdImageObject, offsetX, offsetY, lharbinSignatureWidth, lharbinSignatureHeight);
    COSDictionary figureCosDict = new COSDictionary();
    figureCosDict.setName(COSName.S, StandardStructureTypes.Figure);
    figureCosDict.setItem(COSName.P, paragraphElem);
    figureCosDict.setName(COSName.TYPE, "StructElem");
    final PDStructureElement currentElem =
        appendToTagTree(figureCosDict, pdfDocument.getPage(pageNumber), paragraphElem);
    var layoutAttribute = new PDLayoutAttributeObject();
    layoutAttribute.setBBox(
        new PDRectangle(offsetX, offsetY, lharbinSignatureWidth, lharbinSignatureHeight));
    // todo: find out why adding this layout attribute causes "Object reference not set to an
    // instance of an object"
    // Maybe this has something to do with adding something to the struct parent tree?
    currentElem.addAttribute(layoutAttribute);
    var altText = "Signature by Lharbin";
    currentElem.setAlternateDescription(altText);
    currentMarkedContentDictionary.setString(COSName.ALT, altText);
    PDMarkedContent markedImg = appendToTagTree(page, currentElem);
    markedImg.addXObject(pdImageObject);
    contentStream.endMarkedContent();
    contentStream.close();
    updatedPagePosition = new UpdatedPagePosition(pageHeight - offsetY, pageNumber);
  }

  @SneakyThrows
  private void drawLineThatMightHaveLink(
      Text text,
      PDPageContentStream contentStream,
      int pageIndex,
      String line,
      final PDStructureElement currentElem,
      float x,
      float verticalOffsetFromBottom,
      float newOffset,
      boolean checkForLink) {
    Matcher matcher = urlOrPhoneNumberPattern.matcher(line);
    if (checkForLink && matcher.find()) {
      // get the MatchResult Object
      final MatchResult regexMatch = matcher.toMatchResult();
      final String beforeLinkText = line.substring(0, regexMatch.start());
      // prefix before the link
      contentStream.setNonStrokingColor(text.textColor());
      contentStream.showText(beforeLinkText);
      appendToTagTree(pages.get(pageIndex), currentElem);
      // segment tags
      contentStream.endMarkedContent();
      setNextMarkedContentDictionary();
      contentStream.beginMarkedContent(
          COSName.P, PDPropertyList.create(currentMarkedContentDictionary));
      contentStream.setNonStrokingColor(Color.blue);
      float beforeLinkTextWidth = getStringWidth(text, beforeLinkText);
      contentStream.newLineAtOffset(beforeLinkTextWidth, 0);
      final String linkText = matcher.group();
      contentStream.showText(linkText);
      // actual link
      final var linkElem =
          appendToTagTree(StandardStructureTypes.LINK, pages.get(pageIndex), currentElem);
      linkElem.setAlternateDescription(linkText);
      appendToTagTree(pages.get(pageIndex), linkElem);
      // segment tags
      contentStream.endMarkedContent();
      setNextMarkedContentDictionary();
      contentStream.beginMarkedContent(
          COSName.P, PDPropertyList.create(currentMarkedContentDictionary));
      // postfix after the link
      contentStream.setNonStrokingColor(text.textColor());
      final float linkTextWidth = getStringWidth(text, linkText);
      contentStream.newLineAtOffset(linkTextWidth, 0);
      final String afterLinkText = line.substring(regexMatch.end());
      contentStream.showText(afterLinkText);
      appendToTagTree(pages.get(pageIndex), currentElem);
      // segment text
      contentStream.endMarkedContent();
      setNextMarkedContentDictionary();
      contentStream.beginMarkedContent(
          COSName.P, PDPropertyList.create(currentMarkedContentDictionary));
      contentStream.newLineAtOffset(-(beforeLinkTextWidth + linkTextWidth), newOffset);
      // paint link annotation onto page after the fact. Link it to the Link element.
      appendToLinkAnnotationToLinkTag(
          pageIndex,
          linkText,
          linkElem,
          x + this.pageMargins.leftMargin() + beforeLinkTextWidth,
          verticalOffsetFromBottom,
          linkTextWidth,
          text.fontSize());
    } else {
      contentStream.showText(line);
      contentStream.newLineAtOffset(0, newOffset);
    }
  }

  // Add text at a given location starting from the top-left corner.
  // this function is the core rendering logic shared by all.
  @SneakyThrows
  private void drawSimpleText(
      Text text,
      float x,
      float y,
      int pageIndex,
      String structType,
      PDStructureElement parent,
      float spaceBetweenLines,
      BiFunction<Font, String, Font> fontFunction,
      boolean checkForLink,
      boolean allowNewPages) {
    float verticalOffsetFromBottom = pageHeight - y;
    final float newOffset = -text.fontSize() - spaceBetweenLines;
    final float finalVerticalPosition =
        verticalOffsetFromBottom + (newOffset * text.wrappedText().size());
    if (allowNewPages && (finalVerticalPosition <= this.pageMargins.bottomMargin())) {
      // need to add a new page before drawing on this content.
      addPage();
      addAndTagWatermarkToPage();
      pageIndex++;
      verticalOffsetFromBottom = pageHeight - pageMargins().topMargin();
    }
    // Set up the next marked content element with an MCID and create the containing P structure
    // element.
    PDPageContentStream contentStream =
        new PDPageContentStream(
            pdf, pages.get(pageIndex), PDPageContentStream.AppendMode.APPEND, false);
    setNextMarkedContentDictionary();
    contentStream.beginMarkedContent(
        COSName.P, PDPropertyList.create(currentMarkedContentDictionary));
    final PDStructureElement currentElem =
        appendToTagTree(structType, pages.get(pageIndex), parent);
    // Open up a stream to draw text at a given location.
    contentStream.beginText();
    contentStream.newLineAtOffset(x + this.pageMargins.leftMargin(), verticalOffsetFromBottom);
    contentStream.setNonStrokingColor(text.textColor());
    for (String line : text.wrappedText()) {
      // use the font function to get the right font per line, or just default to the text font.
      var fontFunctionToApply = Optional.ofNullable(fontFunction).orElse((font, string) -> font);
      contentStream.setFont(
          getPdFont(fontFunctionToApply.apply(text.font(), line)), text.fontSize());
      drawLineThatMightHaveLink(
          text,
          contentStream,
          pageIndex,
          line,
          currentElem,
          x,
          verticalOffsetFromBottom,
          newOffset,
          checkForLink);
      verticalOffsetFromBottom += newOffset;
    }
    contentStream.endText();
    // End the marked content and append it's P structure element to the containing P structure
    // element.
    appendToTagTree(pages.get(pageIndex), currentElem);
    contentStream.endMarkedContent();
    contentStream.close();
    // only update the position if we went lower. Sometimes you're drawing a row on a table
    // such as in proof of letter service where cells on the right aren't as tall as cells on the
    // left.
    // in events like this, we want to keep the _older_ updated page position.
    if (updatedPagePosition == null
        || updatedPagePosition.pageIndex() < pageIndex
        || updatedPagePosition.verticalPositionFromTop()
            < (pageHeight - verticalOffsetFromBottom)) {
      updatedPagePosition =
          new UpdatedPagePosition(pageHeight - verticalOffsetFromBottom, pageIndex);
    }
  }

  private PDPageContentStream drawStandardWatermark(int pageIndex) {
    float standardWatermarkHeight = 354;
    var marginTop = pageHeight / 2.0f - standardWatermarkHeight * 0.66f;
    float standardWatermarkWidth = 360;
    var marginLeft = pageWidth / 2.0f - standardWatermarkWidth / 2.0f;
    return drawWatermarkImage(
        WATERMARK_BYTES,
        COSName.ARTIFACT,
        "Watermark",
        pageIndex,
        marginTop,
        marginLeft,
        (int) standardWatermarkWidth,
        (int) standardWatermarkHeight);
  }

  /**
   * Draws a table on the PDF.
   *
   * @param table The table to draw.
   * @param x The X position as measured from left margin to go and draw table.
   * @param y The vertical position of the table as measured from top edge of page (not top margin).
   * @param pageIndex The page index to go and draw the page.
   * @param parent For tagging purposes, the parent element to append this table to.
   * @param spaceBetweenLines Vertical space between lines if a given piece of text wraps around.
   * @param spaceBetweenRows Vertical space between table rows.
   */
  @SneakyThrows
  public void drawTable(
      DataTable table,
      float x,
      float y,
      int pageIndex,
      PDStructureElement parent,
      BiFunction<Font, String, Font> fontFunction,
      float spaceBetweenLines,
      float spaceBetweenRows) {
    // compute all wrapped lines per cell & all row heights
    for (int i = 0; i < table.rows().size(); i++) {
      table.rows().get(i).cells().forEach(cell -> computeWrappedLines(cell, cell.width() * 0.9f));
      float maxFontSize =
          (float)
              table.rows().get(i).cells().stream().mapToDouble(Text::fontSize).max().orElseThrow();
      int maxNumberOfLines =
          table.rows().get(i).cells().stream()
              .mapToInt(cell -> cell.wrappedText().size())
              .max()
              .orElseThrow();
      float newHeight =
          maxNumberOfLines * maxFontSize
              + (maxNumberOfLines - 1) * spaceBetweenLines
              + spaceBetweenRows;
      table.rows().get(i).height(newHeight);
    }
    // If table overflows past page, just add new page.
    // Will not handle the case where table can be longer than a page.
    // This  is  being done because accessibility tagging seems to break.
    // when the table continues onto next page. Not sure how to fix it.
    var tableHeight = table.rows().stream().mapToDouble(row -> (double) row.height()).sum();
    if ((y + tableHeight) >= (pageHeight - pageMargins.bottomMargin())) {
      addPage();
      pageIndex += 1;
      y = pageMargins.topMargin();
      addAndTagWatermarkToPage();
    }
    COSDictionary attr = new COSDictionary();
    attr.setName(COSName.O, "Table");
    attr.setString(COSName.getPDFName("Summary"), table.summary());
    // Create a stream for drawing table's contents and append table structure element to the
    // current form's structure element.
    PDStructureElement currentTable =
        appendToTagTree(StandardStructureTypes.TABLE, pages.get(pageIndex), parent);
    currentTable.getCOSObject().setItem(COSName.A, attr);
    currentTable.setAlternateDescription(table.summary());
    int rowIndexStart = 0;
    // Go through each row and add a TR structure element to the table structure element.
    for (int i = 0; i < table.rows().size(); i++) {
      // Go through each column and draw the cell and any cell's text with given alignment.
      PDStructureElement currentTableRow =
          appendToTagTree(StandardStructureTypes.TR, pages.get(pageIndex), currentTable);
      float cellY;
      Row currentRow = table.rows().get(i);
      cellY = y + table.getRowPosition(rowIndexStart, i);
      for (int j = 0; j < table.rows().get(i).cells().size(); j++) {
        Cell currentCell = table.getCell(i, j);
        float cellX = x + currentRow.getCellPosition(j);
        PDStructureElement cellStructureElement =
            addTableCellParentTag(
                currentCell, pageIndex, currentTableRow, table.getTableHeaderType());
        drawCellContents(
            pageIndex,
            currentRow,
            cellStructureElement,
            currentCell,
            fontFunction,
            cellX,
            cellY,
            spaceBetweenLines);
      }
    }
  }

  /**
   * This function draws some text on the PDF. Text in each list item will be parsed for telephone
   * and URL links. But, the logic will only identify & annotate 1 link per printed line of text.
   *
   * @param text The text to be drawn on screen.
   * @param x The X coordinate of the text, measured from the left edge of page.
   * @param y The y coordinate of the text, measure from top edge of page.
   * @param spaceBetweenLines Between each line of text, how much vertical space do you want?
   * @param parent The parent element to attach the list. This is for tagging purposes.
   * @param structType The type of tag you want wrapping the text, such as 'P' or 'H1'
   * @param pageIndex The page number in which to draw the text. Page numbers start at 0.
   * @param allowNewPages If true, then text overflowing will trigger a new page being added. If
   *     false, no page will be added when text gets past the bottom margin.
   */
  @SneakyThrows
  public void drawTextElement(
      Text text,
      float x,
      float y,
      float spaceBetweenLines,
      PDStructureElement parent,
      String structType,
      int pageIndex,
      boolean allowNewPages) {
    computeWrappedLines(text, pageWidth - pageMargins.leftMargin() - pageMargins.rightMargin());
    // Draws the given texts
    drawSimpleText(
        text, x, y, pageIndex, structType, parent, spaceBetweenLines, null, true, allowNewPages);
  }

  @SneakyThrows
  private void drawTitle() {
    var title = "DEPARTMENT OF VETERANS AFFAIRS";
    var font = getPdFont(Font.INTER_BOLD);
    var fontSize = 10.71f;
    float titleWidth = font.getStringWidth(title) / 1000 * fontSize;
    Text titleText = new Text(fontSize, title, new Color(0, 0, 128), Font.INTER_BOLD);
    float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
    titleText.wrappedText(List.of(title));
    drawSimpleText(
        titleText,
        (pageWidth - titleWidth) / 2.0f - pageMargins.leftMargin(),
        vaSealTopOffset + vaSealHeight / 2.0f + titleHeight * 0.33f,
        0,
        StandardStructureTypes.H1,
        rootElem,
        0,
        null,
        false,
        false);
  }

  // todo:  find out  why this causes "inconsistent entry found" in PAC checker tool.
  @SneakyThrows
  void drawVaSeal(PDDocument pdfDocument, int pageNumber, float vaSealTopOffset) {
    var offsetX = 0.5f / 8.25f * pdfDocument.getPage(0).getMediaBox().getWidth();
    this.pageMargins.leftMargin(offsetX);
    this.pageMargins.rightMargin(offsetX);
    PDImageXObject pdImageObject =
        PDImageXObject.createFromByteArray(pdfDocument, VA_SEAL_BYTES, "logo");
    PDPage page = pdfDocument.getPage(pageNumber);
    PDPageContentStream contentStream =
        new PDPageContentStream(pdfDocument, page, PDPageContentStream.AppendMode.APPEND, true);
    setNextMarkedContentDictionary();
    contentStream.beginMarkedContent(
        COSName.getPDFName("Figure"), PDPropertyList.create(currentMarkedContentDictionary));
    PDStructureElement divElem =
        appendToTagTree(StandardStructureTypes.DIV, pdfDocument.getPage(pageNumber), rootElem);
    PDStructureElement paragraphElem =
        appendToTagTree(StandardStructureTypes.P, pdfDocument.getPage(pageNumber), divElem);
    float vaSealWidth = 69;
    var offsetY = pageHeight - vaSealHeight - vaSealTopOffset;
    contentStream.drawImage(pdImageObject, offsetX, offsetY, vaSealWidth, vaSealHeight);
    COSDictionary figureCosDict = new COSDictionary();
    figureCosDict.setName(COSName.S, StandardStructureTypes.Figure);
    figureCosDict.setItem(COSName.P, paragraphElem);
    figureCosDict.setName(COSName.TYPE, "StructElem");
    final PDStructureElement currentElem =
        appendToTagTree(figureCosDict, pdfDocument.getPage(pageNumber), paragraphElem);
    // bounding box values taken from the raw internal structure of the benefits summary PDF:
    // 14 0 obj
    // <</A<</BBox[36 737 105 806]/Height 69/O/Layout/Width 69>>/Alt(Veteran Affairs Seal)/K 1/P 13
    // 0 R/Pg 6 0 R/S/Figure/Type/StructElem>>
    // endobj
    var layoutAttribute = new PDLayoutAttributeObject();
    layoutAttribute.setBBox(new PDRectangle(36, 737, 105, 806));
    // todo: find out why adding this layout attribute causes "Object reference not set to an
    // instance of an object"
    // Maybe this has something to do with adding something to the struct parent tree?
    currentElem.addAttribute(layoutAttribute);
    var altText = "Veteran Affairs Seal";
    currentElem.setAlternateDescription(altText);
    currentMarkedContentDictionary.setString(COSName.ALT, altText);
    PDMarkedContent markedImg = appendToTagTree(page, currentElem);
    markedImg.addXObject(pdImageObject);
    contentStream.endMarkedContent();
    contentStream.close();
  }

  @SneakyThrows
  private PDPageContentStream drawWatermarkImage(
      byte[] imageBytes,
      COSName cosName,
      String imageName,
      int pageIndex,
      float marginTop,
      float marginLeft,
      int width,
      int height) {
    PDImageXObject pdImageObject = PDImageXObject.createFromByteArray(pdf, imageBytes, imageName);
    PDPage page = pdf.getPage(pageIndex);
    PDPageContentStream contentStream =
        new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true);
    setNextMarkedContentDictionary();
    contentStream.beginMarkedContent(
        cosName, PDPropertyList.create(currentMarkedContentDictionary));
    contentStream.drawImage(pdImageObject, marginLeft, marginTop, width, height);
    return contentStream;
  }

  private PDFont getPdFont(Font font) {
    return switch (font) {
      case INTER_REGULAR -> this.interRegularFont;
      case INTER_BOLD -> this.interBoldFont;
    };
  }

  /**
   * The finalized PDF as an array of bytes. This byte array can be returned in a postman response,
   * or saved to file. Call this only after you've drawn everything to the PDF.
   *
   * @return The PDF as a byte array.
   */
  @SneakyThrows
  public byte[] getPdfBytes() {
    addParentTree();
    var output = new ByteArrayOutputStream();
    pdf.save(output);
    pdf.close();
    return output.toByteArray();
  }

  @SneakyThrows
  private float getStringWidth(final Text text, final String stringToFindWidthFor) {
    Font font = text.font();
    PDFont pdFont = getPdFont(font);
    float fontSize = text.fontSize();
    return pdFont.getStringWidth(stringToFindWidthFor) / 1000.0f * fontSize;
  }

  public UpdatedPagePosition getUpdatedPosition() {
    return updatedPagePosition;
  }

  @SneakyThrows
  private NewPageRelatedVariables handleImageOverflow(final int pageIndex) {
    addPage();
    addAndTagWatermarkToPage();
    var newVerticalOffsetFromBottom = pageHeight - this.pageMargins.topMargin();
    return NewPageRelatedVariables.builder()
        .newPageIndex(pageIndex + 1)
        .newVerticalOffsetFromBottom(newVerticalOffsetFromBottom)
        .build();
  }

  private void prePageOne() {
    // Create document initial page
    cosArrayForAdditionalPages.add(COSName.getPDFName("PDF"));
    cosArrayForAdditionalPages.add(COSName.getPDFName("Text"));
    boxArray.add(new COSFloat(0.0f));
    boxArray.add(new COSFloat(0.0f));
    boxArray.add(new COSFloat(pageWidth));
    boxArray.add(new COSFloat(pageHeight));
  }

  // Assign an id for the next marked content element.
  private void setNextMarkedContentDictionary() {
    currentMarkedContentDictionary = new COSDictionary();
    currentMarkedContentDictionary.setInt(COSName.MCID, currentMcid);
    currentMcid++;
  }

  private void setupDocumentCatalog() {
    // Adjust other document metadata
    PDDocumentCatalog documentCatalog = pdf.getDocumentCatalog();
    documentCatalog.setLanguage("EN-US");
    documentCatalog.setViewerPreferences(new PDViewerPreferences(new COSDictionary()));
    documentCatalog.getViewerPreferences().setDisplayDocTitle(true);
    HashMap<String, String> roleMap = new HashMap<>();
    roleMap.put("Annotation", "Span");
    roleMap.put("Artifact", "P");
    roleMap.put("Bibliography", "BibEntry");
    roleMap.put("Chart", "Figure");
    roleMap.put("Diagram", "Figure");
    roleMap.put("DropCap", "Figure");
    roleMap.put("EndNote", "Note");
    roleMap.put("FootNote", "Note");
    roleMap.put("InlineShape", "Figure");
    roleMap.put("Outline", "Span");
    roleMap.put("Strikeout", "Span");
    roleMap.put("Subscript", "Span");
    roleMap.put("Superscript", "Span");
    roleMap.put("Underline", "Span");
    PDStructureTreeRoot structureTreeRoot = new PDStructureTreeRoot();
    structureTreeRoot.setRoleMap(roleMap);
    documentCatalog.setStructureTreeRoot(structureTreeRoot);
    PDMarkInfo markInfo = new PDMarkInfo();
    markInfo.setMarked(true);
    documentCatalog.setMarkInfo(markInfo);
  }
}
