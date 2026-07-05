package pl.crystalek.budgetweb.receipt.ai.model;

/**
 * Wynik analizy paragonu przez Document Intelligence: ustrukturyzowany paragon
 * oraz surowy tekst OCR (potrzebny m.in. do wykrycia rabatów, których DI nie zwraca per pozycja).
 */
public record ReceiptAnalysisResult(AIProcessedReceipt receipt, String rawText) {
}
