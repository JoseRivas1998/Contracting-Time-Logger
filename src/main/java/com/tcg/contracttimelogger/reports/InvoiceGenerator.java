package com.tcg.contracttimelogger.reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tcg.contracttimelogger.data.Address;
import com.tcg.contracttimelogger.data.Contract;
import com.tcg.contracttimelogger.data.TimeRecord;
import com.tcg.contracttimelogger.data.TimeSheet;
import com.tcg.contracttimelogger.utils.App;
import com.tcg.contracttimelogger.utils.UserData;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import javax.print.Doc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

public final class InvoiceGenerator {

    private Document pdfDocument;

    public static InvoiceGenerator newInstance() {
        return new InvoiceGenerator();
    }

    public File generate(TimeSheet timeSheet, LocalDate invoiceStartDate, LocalDate invoiceEndDate) throws FileNotFoundException, DocumentException {
        if (pdfDocument != null) {
            throw new IllegalStateException("This generator has already been activated");
        }
        FileChooser fileChooser = new FileChooser();
        UserData userData = UserData.getInstance();
        if(userData.getLastSaveLocation().length() > 0) {
            fileChooser.setInitialDirectory(new File(userData.getLastSaveLocation()));
        }
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        App app = App.instance();
        File file = fileChooser.showSaveDialog(app.mainStage);
        if(file == null) return null;
        if (file.exists()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("File Exists");
            confirm.setContentText("That file already exists, overwrite the file?");
            confirm.initOwner(app.mainStage);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() != ButtonType.OK) {
                return null;
            }
        }
        userData.setLastSaveLocation(file.getParent());
        userData.saveData();
        pdfDocument = new Document();
        PdfWriter.getInstance(pdfDocument, new FileOutputStream(file));
        pdfDocument.open();

        addHeader();
        addName();
        addUserAddress();
        addContractAddress(timeSheet);
        addInvoice(timeSheet, invoiceStartDate, invoiceEndDate);

        pdfDocument.close();
        return file;
    }

    private void addHeader() throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Paragraph chunk = new Paragraph("Invoice", font);
        pdfDocument.add(chunk);
    }

    private void addName() throws DocumentException {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        String name = UserData.getInstance().getProfile().name;
        Paragraph chunk = new Paragraph(name, font);
        pdfDocument.add(chunk);
    }

    private void addUserAddress() throws DocumentException {
        final Address address = UserData.getInstance().getProfile().address;
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Paragraph paragraph = new Paragraph(address.toString(), font);
        pdfDocument.add(paragraph);
    }

    private void addContractAddress(TimeSheet timeSheet) throws DocumentException {
        final Contract contract = timeSheet.getContract();
        final Address address = contract.address;

        Font grey = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY);
        Paragraph billedTo = new Paragraph("\nBilled To", grey);

        Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        final String contractAddress = contract.name + "\n" + address + "\n\n";
        Paragraph paragraph = new Paragraph(contractAddress, font);

        pdfDocument.add(billedTo);
        pdfDocument.add(paragraph);

    }

    private void addInvoice(TimeSheet timeSheet, LocalDate start, LocalDate end) throws DocumentException {

        final PdfPTable table = new PdfPTable(5);

        Font invoiceDateFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        final String invoiceDate = "Invoice Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("M/d/YYYY"));
        pdfDocument.add(new Paragraph(invoiceDate + "\n\n", invoiceDateFont));

        Stream.of("Clock In", "Clock Out", "Hours", "Rate", "Amount")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/YYYY h:mm:ss a");
        final Contract contract = timeSheet.getContract();

        double totalHoursWorked = 0;
        final String rate = String.format("$%.2f", contract.centsPerHour / 100.0);

        for (TimeRecord timeRecord : timeSheet.recordsBetween(start, end)) {
            final String clockIn = timeRecord.clockIn.format(formatter);
            final String clockOut = timeRecord.getClockOut().format(formatter);
            final String hours = String.format("%.2f", timeRecord.hoursWorked());
            final String amount = String.format("$%.2f", (contract.centsPerHour / 100.0) * timeRecord.hoursWorked());
            totalHoursWorked += timeRecord.hoursWorked();
            table.addCell(clockIn);
            table.addCell(clockOut);
            table.addCell(hours);
            table.addCell(rate);
            table.addCell(amount);
        }

        PdfPCell total = new PdfPCell(new Phrase("Total"));
        total.setHorizontalAlignment(Element.ALIGN_RIGHT);
        total.setColspan(2);
        table.addCell(total);

        final String hoursWorked = String.format("%.2f", totalHoursWorked);
        final double amountOwed = totalHoursWorked * (contract.centsPerHour / 100.0);

        table.addCell(hoursWorked);
        table.addCell(rate);
        table.addCell(String.format("$%.2f", amountOwed));

        table.setWidthPercentage(100);

        pdfDocument.add(table);
    }

}
