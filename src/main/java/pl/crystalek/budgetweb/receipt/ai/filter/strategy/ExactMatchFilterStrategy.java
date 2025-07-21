package pl.crystalek.budgetweb.receipt.ai.filter.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ExactMatchFilterStrategy implements FilterStrategy {
    private static final String NON_ALPHANUMERIC_CHARS = "[^a-zA-Ząćęłńóśźż0-9]*";
    private static final int PATTERN_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;

    @Override
    public String apply(final String input, final String phrase) {
        final StringBuilder regexBuilder = new StringBuilder();
        final char[] chars = phrase.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i > 0) {
                regexBuilder.append(NON_ALPHANUMERIC_CHARS);
            }

            final String charInString = String.valueOf(chars[i]);
            regexBuilder.append(Pattern.quote(charInString));
        }
        regexBuilder.append(NON_ALPHANUMERIC_CHARS).append("[a-zA-Ząćęłńóśźż]?([,\\s]+)?\\b");

        final Pattern pattern = Pattern.compile(regexBuilder.toString(), PATTERN_FLAGS);
        final Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("");
    }
}
