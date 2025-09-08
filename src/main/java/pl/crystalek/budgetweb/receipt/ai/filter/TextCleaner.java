package pl.crystalek.budgetweb.receipt.ai.filter;

import lombok.experimental.UtilityClass;

@UtilityClass
class TextCleaner {

    String clean(String input) {
        return input
                .replaceAll(",\\s*,", ", ")
                .replaceAll("(,\\s*)+$", "")
                .replaceAll("^,\\s*", "")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }
}
