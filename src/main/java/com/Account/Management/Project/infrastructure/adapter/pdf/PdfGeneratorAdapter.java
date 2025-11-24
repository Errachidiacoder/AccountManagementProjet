package com.Account.Management.Project.infrastructure.adapter.pdf;


import com.Account.Management.Project.domain.model.AccountStatement;
import com.Account.Management.Project.domain.model.Transaction;
import com.Account.Management.Project.port.output.PdfGeneratorPort;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Adapter implémentant le port de sortie PdfGeneratorPort
 * Utilise iText 7 pour la génération de PDF
 *
 * Génère des relevés de compte formatés en PDF avec:
 * - En-tête avec informations du compte
 * - Tableau des transactions
 * - Résumé des soldes
 */
@Component
public class PdfGeneratorAdapter implements PdfGeneratorPort {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public byte[] generateStatementPdf(AccountStatement statement) {
        return generateStatementPdf(statement, "releve_compte");
    }

    @Override
    public byte[] generateStatementPdf(AccountStatement statement, String filename) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Créer le document PDF
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Ajouter l'en-tête
            addHeader(document, statement);

            // Ajouter les informations du compte
            addAccountInfo(document, statement);

            // Ajouter le tableau des transactions
            addTransactionsTable(document, statement);

            // Ajouter le résumé
            addSummary(document, statement);

            // Fermer le document
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    /**
     * Ajoute l'en-tête du relevé
     */
    private void addHeader(Document document, AccountStatement statement) {
        Paragraph title = new Paragraph("RELEVÉ DE COMPTE BANCAIRE")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        Paragraph subtitle = new Paragraph("Banque AccountManagement")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30);
        document.add(subtitle);
    }

    /**
     * Ajoute les informations du compte
     */
    private void addAccountInfo(Document document, AccountStatement statement) {
        document.add(new Paragraph("INFORMATIONS DU COMPTE")
                .setBold()
                .setFontSize(12)
                .setMarginBottom(10));

        // Tableau d'informations
        Table infoTable = new Table(2);
        infoTable.setWidth(UnitValue.createPercentValue(60));

        addInfoRow(infoTable, "Titulaire:", statement.getAccountHolderName());
        addInfoRow(infoTable, "N° de compte:", statement.getAccountNumber());
        addInfoRow(infoTable, "Période du:",
                statement.getPeriodStart().format(DATE_ONLY_FORMATTER));
        addInfoRow(infoTable, "Au:",
                statement.getPeriodEnd().format(DATE_ONLY_FORMATTER));
        addInfoRow(infoTable, "Date d'édition:",
                statement.getGeneratedAt().format(DATE_FORMATTER));

        document.add(infoTable);
        document.add(new Paragraph("\n"));
    }

    /**
     * Ajoute une ligne d'information au tableau
     */
    private void addInfoRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold())
                .setBorder(null));
        table.addCell(new Cell().add(new Paragraph(value))
                .setBorder(null));
    }

    /**
     * Ajoute le tableau des transactions
     */
    private void addTransactionsTable(Document document, AccountStatement statement) {
        document.add(new Paragraph("DÉTAIL DES OPÉRATIONS")
                .setBold()
                .setFontSize(12)
                .setMarginTop(20)
                .setMarginBottom(10));

        if (statement.getTransactions().isEmpty()) {
            document.add(new Paragraph("Aucune transaction sur cette période.")
                    .setItalic());
            return;
        }

        // Créer le tableau avec 5 colonnes
        Table table = new Table(new float[]{2, 3, 2, 2, 3});
        table.setWidth(UnitValue.createPercentValue(100));

        // En-têtes
        addTableHeader(table, "Date");
        addTableHeader(table, "Description");
        addTableHeader(table, "Type");
        addTableHeader(table, "Montant");
        addTableHeader(table, "Statut");

        // Lignes de transactions
        for (Transaction tx : statement.getTransactions()) {
            table.addCell(new Cell().add(
                    new Paragraph(tx.getCreatedAt().format(DATE_FORMATTER))
                            .setFontSize(9)));

            table.addCell(new Cell().add(
                    new Paragraph(tx.getDescription() != null ?
                            tx.getDescription() : "-")
                            .setFontSize(9)));

            table.addCell(new Cell().add(
                    new Paragraph(tx.getType().name())
                            .setFontSize(9)));

            // Déterminer si c'est un débit ou crédit
            String amountStr;
            if (tx.getSourceAccountId().equals(statement.getAccountId())) {
                amountStr = "- " + tx.getSourceAmount().toFormattedString();
            } else {
                amountStr = "+ " + tx.getTargetAmount().toFormattedString();
            }
            table.addCell(new Cell().add(
                    new Paragraph(amountStr)
                            .setFontSize(9)));

            table.addCell(new Cell().add(
                    new Paragraph(tx.getStatus().name())
                            .setFontSize(9)));
        }

        document.add(table);
    }

    /**
     * Ajoute un en-tête de colonne au tableau
     */
    private void addTableHeader(Table table, String text) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(cell);
    }

    /**
     * Ajoute le résumé des soldes
     */
    private void addSummary(Document document, AccountStatement statement) {
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("RÉSUMÉ")
                .setBold()
                .setFontSize(12)
                .setMarginTop(20)
                .setMarginBottom(10));

        Table summaryTable = new Table(2);
        summaryTable.setWidth(UnitValue.createPercentValue(50));

        addInfoRow(summaryTable, "Solde d'ouverture:",
                statement.getOpeningBalance() != null ?
                        statement.getOpeningBalance().toFormattedString() : "N/A");
        addInfoRow(summaryTable, "Solde de clôture:",
                statement.getClosingBalance() != null ?
                        statement.getClosingBalance().toFormattedString() : "N/A");
        addInfoRow(summaryTable, "Nombre d'opérations:",
                String.valueOf(statement.getTransactionCount()));

        document.add(summaryTable);

        // Pied de page
        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph(
                "Ce document est généré automatiquement et ne nécessite pas de signature.")
                .setFontSize(8)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER));
    }
}