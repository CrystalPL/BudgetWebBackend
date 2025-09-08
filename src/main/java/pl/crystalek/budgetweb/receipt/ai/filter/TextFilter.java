package pl.crystalek.budgetweb.receipt.ai.filter;

import java.util.List;

public interface TextFilter {

    String filterText(final String text, final List<String> blockedPhrases);
}
