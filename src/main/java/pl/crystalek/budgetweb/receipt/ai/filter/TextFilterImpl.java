package pl.crystalek.budgetweb.receipt.ai.filter;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.crystalek.budgetweb.receipt.ai.filter.strategy.FilterStrategy;

import java.util.List;

@RequiredArgsConstructor
public class TextFilterImpl implements TextFilter {
    private final List<FilterStrategy> strategies;

    @Override
    public String filterText(final String input, final List<String> forbiddenPhrases) {
        if (StringUtils.isEmpty(input) || forbiddenPhrases == null || forbiddenPhrases.isEmpty()) {
            return input;
        }

        String result = input;

        for (final String blockedPhrase : forbiddenPhrases) {
            for (final FilterStrategy strategy : strategies) {
                final String filtered = strategy.apply(result, blockedPhrase);

                if (!filtered.equals(result)) {
                    result = filtered;
                    break;
                }
            }
        }

        return TextCleaner.clean(result);
    }
}
