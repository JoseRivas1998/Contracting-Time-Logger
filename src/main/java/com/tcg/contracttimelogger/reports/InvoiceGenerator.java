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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class InvoiceGenerator {

    private Document pdfDocument;
    private final UserData userData = UserData.getInstance();

    public static InvoiceGenerator newInstance() {
        return new InvoiceGenerator();
    }

    public File generate(TimeSheet timeSheet, LocalDate invoiceStartDate, LocalDate invoiceEndDate) throws FileNotFoundException, DocumentException {
        if (pdfDocument != null) throw new IllegalStateException("This generator has already been activated");

        File file = new InvoiceFileSelector().selectFileToSaveTo();
        if (file == null) return null;

        setUserDataLastSaveLocation(file);

        writeDocument(timeSheet, invoiceStartDate, invoiceEndDate, file);
        return file;
    }

    private void writeDocument(TimeSheet timeSheet, LocalDate invoiceStartDate, LocalDate invoiceEndDate, File file) throws DocumentException, FileNotFoundException {
        setupDocument(file);
        writeDocumentContent(timeSheet, invoiceStartDate, invoiceEndDate);
        pdfDocument.close();
    }

    private void setupDocument(File file) throws DocumentException, FileNotFoundException {
        pdfDocument = new Document();
        PdfWriter.getInstance(pdfDocument, new FileOutputStream(file));
        pdfDocument.open();
    }

    private void writeDocumentContent(TimeSheet timeSheet, LocalDate invoiceStartDate, LocalDate invoiceEndDate) throws DocumentException {
        addHeader();
        addName();
        addUserAddress();
        addContractAddress(timeSheet);
        addInvoice(timeSheet, invoiceStartDate, invoiceEndDate);
    }

    private void setUserDataLastSaveLocation(File file) {
        userData.setLastSaveLocation(file.getParent());
        userData.saveData();
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
        final String invoiceDate = "Invoice Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("M/d/yyyy"));
        pdfDocument.add(new Paragraph(invoiceDate + "\n\n", invoiceDateFont));

        Stream.of("Clock In", "Clock Out", "Hours", "Rate", "Amount")
                .map(this::createTableColumnLabel)
                .forEach(table::addCell);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
        final Contract contract = timeSheet.getContract();

        double totalHoursWorked = 0;
        final String rate = String.format("$%.2f", contract.centsPerHour / 100.0);

        final List<TimeRecord> timeRecords = timeSheet.recordsBetween(start, end);
        for (TimeRecord timeRecord : timeRecords) {
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

        PdfPCell total = createTotalLabelCell();
        table.addCell(total);

        final String hoursWorked = String.format("%.2f", totalHoursWorked);
        final double amountOwed = totalHoursWorked * (contract.centsPerHour / 100.0);

        table.addCell(hoursWorked);
        table.addCell(rate);
        table.addCell(String.format("$%.2f", amountOwed));

        table.setWidthPercentage(100);

        pdfDocument.add(table);
    }

    private PdfPCell createTotalLabelCell() {
        PdfPCell total = new PdfPCell(new Phrase("Total"));
        total.setHorizontalAlignment(Element.ALIGN_RIGHT);
        total.setColspan(2);
        return total;
    }

    private PdfPCell createTableColumnLabel(String columnTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setPhrase(new Phrase(columnTitle));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        return header;
    }

    private static class InvoiceFileSelector {

        private FileChooser fileChooser;
        private UserData userData;
        private App app;

        public File selectFileToSaveTo() {
            userData = UserData.getInstance();
            app = App.instance();
            setupFileChooser();

            File file = fileChooser.showSaveDialog(app.mainStage);
            if(file == null) return null;
            if (file.exists()) {
                Optional<ButtonType> result = showReplaceFileConfirmationDialog();
                if (result.isPresent() && result.get() != ButtonType.OK) {
                    return null;
                }
            }

            return file;
        }

        private Optional<ButtonType> showReplaceFileConfirmationDialog() {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("File Exists");
            confirm.setContentText("That file already exists, overwrite the file?");
            confirm.initOwner(app.mainStage);
            return confirm.showAndWait();
        }

        private void setupFileChooser() {
            fileChooser = new FileChooser();
            setInitialSelectionDirectory();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        }

        private void setInitialSelectionDirectory() {
            if(userData.hasLastSaveLocation()) {
                fileChooser.setInitialDirectory(new File(userData.getLastSaveLocation()));
            }
        }
    }
}
