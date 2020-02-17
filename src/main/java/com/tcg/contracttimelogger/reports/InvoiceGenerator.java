package com.tcg.contracttimelogger.reports;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.tcg.contracttimelogger.data.TimeSheet;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;

public final class InvoiceGenerator {

    private Document pdfDocument;

    public static InvoiceGenerator newInstance() {
        return new InvoiceGenerator();
    }

    public void generate(TimeSheet timeSheet, LocalDate invoiceStartDate, LocalDate invoiceEndDate) throws FileNotFoundException, DocumentException {
        if(pdfDocument != null) {
            throw new IllegalStateException("This generator has already been activated");
        }
        pdfDocument = new Document();
        PdfWriter.getInstance(pdfDocument, new FileOutputStream("Temp File"));
    }

}
