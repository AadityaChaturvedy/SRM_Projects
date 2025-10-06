package com.railway.booking.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.railway.booking.model.Train;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PdfService {

    public static void generateTicket(Train train, int seats, String trainClass, double finalPrice, String pnr, Map<String, String> travelerInfo) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Ticket");
        fileChooser.setInitialFileName("ticket_" + pnr + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                document.add(new Paragraph("Railway E-Ticket").setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("PNR: " + pnr).setBold());
                document.add(new Paragraph("Train: " + train.getName()));
                document.add(new Paragraph("From: " + train.getSource() + " To: " + train.getDestination() + " Via: " + train.getVia()));
                document.add(new Paragraph("Date: " + train.getDate() + " Time: " + train.getDepartureTime()));
                document.add(new Paragraph("Class: " + trainClass));
                document.add(new Paragraph("Seats: " + seats));
                document.add(new Paragraph("Total Price: " + String.format("₹%.2f", finalPrice)));

                document.add(new Paragraph("Traveler Information").setBold().setMarginTop(20));
                travelerInfo.forEach((key, value) -> {
                    document.add(new Paragraph(key + ": " + value));
                });

                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
