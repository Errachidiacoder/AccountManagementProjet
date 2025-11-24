package com.Account.Management.Project.port.output;

import com.Account.Management.Project.domain.model.AccountStatement;

/**
 * Port de sortie pour la génération de PDF.
 * Implémenté par PdfGeneratorAdapter dans la couche infrastructure.
 */
public interface PdfGeneratorPort {

    /**
     * Génère un PDF à partir d'un relevé de compte
     * @param statement Le relevé de compte à exporter
     * @return Bytes du fichier PDF généré
     */
    byte[] generateStatementPdf(AccountStatement statement);

    /**
     * Génère un PDF avec un nom de fichier personnalisé
     * @param statement Le relevé de compte
     * @param filename Nom du fichier (sans extension)
     * @return Bytes du fichier PDF
     */
    byte[] generateStatementPdf(AccountStatement statement, String filename);
}