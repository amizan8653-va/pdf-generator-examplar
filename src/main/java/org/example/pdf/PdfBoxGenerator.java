package org.example.pdf;

import org.example.model.corprecord.CorpRecord;
import org.example.pdf.pdfbox.letters.BenefitSummaryLetterVeteranPdf;
import lombok.SneakyThrows;

public class PdfBoxGenerator implements PdfGenerator {

  @Override
  @SneakyThrows
  public byte[] generatePdfForTemplate(CorpRecord params, String footer) {
    return new BenefitSummaryLetterVeteranPdf().getPdfBytes(params);
  }
}
