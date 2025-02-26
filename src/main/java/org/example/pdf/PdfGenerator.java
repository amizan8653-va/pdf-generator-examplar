package org.example.pdf;

import org.example.model.corprecord.CorpRecord;

public interface PdfGenerator {
  byte[] generatePdfForTemplate(CorpRecord params, String footer);
}
