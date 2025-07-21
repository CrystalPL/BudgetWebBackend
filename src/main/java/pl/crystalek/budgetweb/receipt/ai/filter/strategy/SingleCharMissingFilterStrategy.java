package pl.crystalek.budgetweb.receipt.ai.filter.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strategia usuwająca frazy z jedną brakującą literą (w dowolnym miejscu)
 */
class SingleCharMissingFilterStrategy implements FilterStrategy {
    private static final String NON_ALPHANUMERIC_CHARS = "[^a-zA-Ząćęłńóśźż0-9]*";
    private static final int PATTERN_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

    @Override
    public String apply(final String input, final String phrase) {
        final char[] chars = phrase.toCharArray();
        String result = input;

        for (int skipPosition = 0; skipPosition < chars.length; skipPosition++) {
            String regex = buildRegexWithSkippedChar(chars, skipPosition);
            result = removeMatchingText(result, regex);
        }

        return result;
    }

    private String buildRegexWithSkippedChar(final char[] chars, final int skipPosition) {
        final StringBuilder regexBuilder = new StringBuilder();
        boolean first = true;

        for (int i = 0; i < chars.length; i++) {
            if (i == skipPosition) {
                continue;
            }

            if (!first) {
                regexBuilder.append(NON_ALPHANUMERIC_CHARS);
            }

            regexBuilder.append(Pattern.quote(String.valueOf(chars[i])));
            first = false;
        }

        regexBuilder.append(NON_ALPHANUMERIC_CHARS).append("[a-zA-Ząćęłńóśźż]?([,\\s]+)?\\b");
        return regexBuilder.toString();
    }

    private String removeMatchingText(final String text, final String regex) {
        final Pattern pattern = Pattern.compile(regex, PATTERN_FLAGS);
        final Matcher matcher = pattern.matcher(text);

        return matcher.replaceAll("");
    }
}
