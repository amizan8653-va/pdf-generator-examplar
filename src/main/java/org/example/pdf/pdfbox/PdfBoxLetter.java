package org.example.pdf.pdfbox;

import org.example.model.corprecord.CorpRecord;

public interface PdfBoxLetter {
  byte[] getPdfBytes(CorpRecord record);
}
