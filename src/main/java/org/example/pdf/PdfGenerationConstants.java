package org.example.pdf;

/** Constants for pdf generation. */
public final class PdfGenerationConstants {
  /** The Constant BASE_URI. */
  public static final String BASE_URI =
      PdfGenerationConstants.class.getClassLoader().getResource("templates/").getPath();

  /** The Constant DEFAULT_LANGUAGE. */
  public static final String DEFAULT_DOCUMENT_LANGUAGE = "en-US";

  /** The Constant DEFAULT_LANGUAGE. */
  public static final String GENERIC_DOCUMENT_TITLE = "Letter to Veteran";

  /** The Constant DEFAULT_LANGUAGE. */
  public static final String WATERMARK_ALT_TEXT = "Department of Veterans Affairs Watermark";
}
