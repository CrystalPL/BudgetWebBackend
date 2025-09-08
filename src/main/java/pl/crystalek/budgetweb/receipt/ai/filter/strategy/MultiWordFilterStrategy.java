package pl.crystalek.budgetweb.receipt.ai.filter.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strategia usuwająca standardowe wielowyrazowe frazy z błędami
 */
class MultiWordFilterStrategy implements FilterStrategy {

    @Override
    public String apply(final String input, final String phrase) {
        if (!phrase.contains(" ")) {
            return input;
        }

        return standardMultiWordFuzzyMatch(input, phrase);
    }

    private String standardMultiWordFuzzyMatch(final String input, final String phrase) {
        final String[] words = phrase.toLowerCase().split("\\s+");
        final String regex = buildMultiWordRegex(words);

        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("");
    }

    private String buildMultiWordRegex(final String[] words) {
        final StringBuilder regexBuilder = new StringBuilder("\\b");

        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                regexBuilder.append("\\s+");
            }

            regexBuilder.append(buildWordPatternWithVariants(words[i]));
        }

        regexBuilder.append("\\b");
        return regexBuilder.toString();
    }

    private String buildWordPatternWithVariants(final String word) {
        final StringBuilder patternBuilder = new StringBuilder("(");

        patternBuilder.append(Pattern.quote(word));

        for (int skip = 0; skip < word.length(); skip++) {
            patternBuilder.append("|");
            patternBuilder.append(buildWordWithMissingChar(word, skip));
        }

        patternBuilder.append(")");
        return patternBuilder.toString();
    }

    private String buildWordWithMissingChar(final String word, final int skipPosition) {
        final StringBuilder result = new StringBuilder();
        for (int j = 0; j < word.length(); j++) {
            if (j == skipPosition) {
                continue;
            }

            final String character = String.valueOf(word.charAt(j));
            result.append(Pattern.quote(character));
        }

        return result.toString();
    }
}
