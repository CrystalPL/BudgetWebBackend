package pl.crystalek.budgetweb.receipt.ai.filter.strategy;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class DifferentOrderWithMismatchFilterStrategy implements FilterStrategy {

    @Override
    public String apply(final String input, final String blockedPhrase) {
        if (StringUtils.isEmpty(input) || StringUtils.isEmpty(blockedPhrase)) {
            return input;
        }

        final List<String> blockedWords = getSignificantWords(blockedPhrase);
        if (blockedWords.size() <= 1) {
            return input;
        }

        final List<WordContext> inputWordsWithContext = getWordsWithContext(input);
        final List<List<WordContext>> matches = findMatchesInDifferentOrderWithMismatch(inputWordsWithContext, blockedWords);
        if (!matches.isEmpty()) {
            List<int[]> positionsToRemove = collectPositionsToRemove(matches);
            return removePositionsFromText(input, positionsToRemove);
        }

        return input;
    }

    private List<String> getSignificantWords(final String phrase) {
        return Arrays.stream(phrase.toLowerCase().split("\\s+"))
                .filter(word -> word.length() >= 3)
                .map(this::normalizeWord)
                .toList();
    }

    private List<WordContext> getWordsWithContext(final String input) {
        final List<WordContext> result = new ArrayList<>();

        final Pattern wordPattern = Pattern.compile("\\b\\w+\\b");
        final Matcher matcher = wordPattern.matcher(input);
        while (matcher.find()) {
            final String word = matcher.group();
            final String normalizedWord = normalizeWord(word);

            if (normalizedWord.length() >= 3) {
                result.add(new WordContext(word, normalizedWord, matcher.start(), matcher.end()));
            }
        }

        return result;
    }

    private List<List<WordContext>> findMatchesInDifferentOrderWithMismatch(
            final List<WordContext> inputWords,
            final List<String> blockedWords
    ) {
        final List<List<WordContext>> results = new ArrayList<>();

        for (int i = 0; i < inputWords.size(); i++) {
            for (final String blockedWord : blockedWords) {
                if (calculateLevenshteinDistance(inputWords.get(i).normalizedWord(), blockedWord) > 1) {
                    continue;
                }

                List<WordContext> potentialMatch = findAllSimilarBlockedWords(inputWords, blockedWords, i, blockedWords.size());
                if (potentialMatch != null && potentialMatch.size() == blockedWords.size()) {
                    results.add(potentialMatch);
                }

                break;
            }
        }

        return results;
    }

    private List<int[]> collectPositionsToRemove(final List<List<WordContext>> matches) {
        return matches.stream()
                .flatMap(List::stream)
                .map(wc -> new int[]{wc.startPosition(), wc.endPosition()})
                .sorted((a, b) -> Integer.compare(b[0], a[0]))
                .toList();
    }

    private String removePositionsFromText(final String input, final List<int[]> positionsToRemove) {
        final Set<String> processedPositions = new HashSet<>();
        final StringBuilder resultBuilder = new StringBuilder(input);

        for (int[] position : positionsToRemove) {
            int start = position[0];
            int end = position[1];

            if (!isValidPosition(start, end, resultBuilder.length())) {
                continue;
            }

            final String posKey = start + ":" + end;
            if (!processedPositions.contains(posKey)) {
                resultBuilder.delete(start, end);
                processedPositions.add(posKey);
            }
        }

        return resultBuilder.toString();
    }

    private String normalizeWord(final String word) {
        return word.toLowerCase().replaceAll("[,.;:!?()\\[\\]{}\"'\\-_]+", "");
    }

    private boolean isValidPosition(final int start, final int end, final int maxLength) {
        return start < end && start >= 0 && end <= maxLength;
    }

    private List<WordContext> findAllSimilarBlockedWords(
            final List<WordContext> inputWords,
            final List<String> blockedWords,
            final int startIndex,
            final int requiredCount
    ) {
        final int maxDistance = 10;
        final List<WordContext> result = new ArrayList<>();
        final WordContext startWord = inputWords.get(startIndex);
        result.add(startWord);
        final Set<String> matchedBlockedWords = getMatchedBlockedWord(startWord, blockedWords);

        final int leftBound = Math.max(0, startIndex - maxDistance);
        final int rightBound = Math.min(inputWords.size() - 1, startIndex + maxDistance);

        if (searchLeftForMatches(inputWords, blockedWords, startIndex, leftBound, result, matchedBlockedWords, requiredCount)) {
            return result;
        }

        if (searchRightForMatches(inputWords, blockedWords, startIndex, rightBound, result, matchedBlockedWords, requiredCount)) {
            return result;
        }

        return matchedBlockedWords.size() == requiredCount ? result : null;
    }

    private Set<String> getMatchedBlockedWord(final WordContext word, final List<String> blockedWords) {
        return blockedWords.stream()
                .filter(blockedWord -> calculateLevenshteinDistance(word.normalizedWord(), blockedWord) <= 1)
                .collect(Collectors.toSet());
    }

    private boolean searchLeftForMatches(
            final List<WordContext> inputWords,
            final List<String> blockedWords,
            final int startIndex,
            final int leftBound,
            final List<WordContext> result,
            final Set<String> matchedBlockedWords,
            final int requiredCount
    ) {
        for (int i = startIndex - 1; i >= leftBound; i--) {
            final WordContext current = inputWords.get(i);
            if (!tryMatchWord(current, blockedWords, matchedBlockedWords, result)) {
                continue;
            }

            if (matchedBlockedWords.size() == requiredCount) {
                return true;
            }
        }

        return false;
    }

    private boolean searchRightForMatches(
            final List<WordContext> inputWords,
            final List<String> blockedWords,
            final int startIndex,
            final int rightBound,
            final List<WordContext> result,
            final Set<String> matchedBlockedWords,
            final int requiredCount
    ) {
        for (int i = startIndex + 1; i <= rightBound; i++) {
            final WordContext current = inputWords.get(i);
            if (!tryMatchWord(current, blockedWords, matchedBlockedWords, result)) {
                continue;
            }

            if (matchedBlockedWords.size() == requiredCount) {
                return true;
            }
        }

        return false;
    }

    private boolean tryMatchWord(
            final WordContext word,
            final List<String> blockedWords,
            final Set<String> matchedBlockedWords,
            final List<WordContext> result
    ) {
        for (final String blockedWord : blockedWords) {
            if (matchedBlockedWords.contains(blockedWord) || calculateLevenshteinDistance(word.normalizedWord(), blockedWord) > 1) {
                continue;
            }

            result.add(word);
            matchedBlockedWords.add(blockedWord);
            return true;
        }

        return false;
    }

    private int calculateLevenshteinDistance(final String s1, final String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[s1.length()][s2.length()];
    }
}
