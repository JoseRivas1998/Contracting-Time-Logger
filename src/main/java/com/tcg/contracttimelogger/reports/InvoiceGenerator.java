package com.tcg.contracttimelogger.reports;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.tcg.contracttimelogger.data.TimeSheet;
import com.tcg.contracttimelogger.utils.App;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Optional;

public final class InvoiceGenerator {

    private Document pdfDocument;

    public static InvoiceGenerator newInstance() {
        return new InvoiceGenerator();
    }

    public void generate(TimeSheet timeSheet, LocalDate invoiceStartDate, LocalDate invoiceEndDate) throws FileNotFoundException, DocumentException {
        if (pdfDocument != null) {
            throw new IllegalStateException("This generator has already been activated");
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        App app = App.instance();
        File file = fileChooser.showSaveDialog(app.mainStage);
        if (file.exists()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("File Exists");
            confirm.setContentText("That file already exists, overwrite the file?");
            confirm.initOwner(app.mainStage);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() != ButtonType.OK) {
                return;
            }
        }
        pdfDocument = new Document();
        PdfWriter.getInstance(pdfDocument, new FileOutputStream(file));
        pdfDocument.open();
        pdfDocument.add(new Paragraph("Test"));
        pdfDocument.close();

    }

}
