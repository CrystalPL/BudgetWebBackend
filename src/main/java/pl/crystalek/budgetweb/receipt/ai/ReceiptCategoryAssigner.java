package pl.crystalek.budgetweb.receipt.ai;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;
import pl.crystalek.budgetweb.category.Category;
import pl.crystalek.budgetweb.receipt.ai.filter.TextFilter;
import pl.crystalek.budgetweb.receipt.ai.filter.TextFilterImpl;
import pl.crystalek.budgetweb.receipt.ai.filter.strategy.FilterStrategy;
import pl.crystalek.budgetweb.receipt.ai.model.AIProcessedReceipt;
import pl.crystalek.budgetweb.receipt.ai.model.AIProcessedReceiptItem;
import pl.crystalek.budgetweb.receipt.ai.model.ReceiptCategorization;
import pl.crystalek.budgetweb.utils.JsonDeserializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class ReceiptCategoryAssigner {
    static TextFilter TEXT_FILTER = new TextFilterImpl(FilterStrategy.getAllFilters());
    AIProperties aiProperties;
    ChatModel chatModel;

    AIProcessedReceipt assign(final AIProcessedReceipt receipt, final String rawText, final Set<Category> categories) {
        final List<AIProcessedReceiptItem> items = receipt.aiReceiptItems();
        if (items.isEmpty()) {
            return receipt;
        }

        final ReceiptCategorization categorization = categorize(items, rawText, categories);
        final Map<Integer, ReceiptCategorization.CategorizedProduct> byIndex = indexByPosition(categorization.products());

        final List<AIProcessedReceiptItem> resultItems = new ArrayList<>(IntStream.range(0, items.size())
                .mapToObj(index -> enrichItem(items.get(index), byIndex.get(index)))
                .toList());
        resultItems.addAll(toDepositItems(categorization.deposits()));

        return new AIProcessedReceipt(receipt.shop(), receipt.shoppingTime(), resultItems);
    }

    private Map<Integer, ReceiptCategorization.CategorizedProduct> indexByPosition(final List<ReceiptCategorization.CategorizedProduct> products) {
        if (products == null) {
            return Map.of();
        }

        final Map<Integer, ReceiptCategorization.CategorizedProduct> result = new HashMap<>();
        for (final ReceiptCategorization.CategorizedProduct product : products) {
            if (product.index() != null) {
                result.put(product.index(), product);
            }
        }

        return result;
    }

    private List<AIProcessedReceiptItem> toDepositItems(final List<ReceiptCategorization.Deposit> deposits) {
        if (deposits == null) {
            return List.of();
        }

        final List<AIProcessedReceiptItem> result = new ArrayList<>();
        for (final ReceiptCategorization.Deposit deposit : deposits) {
            if (deposit.price() == null) {
                continue;
            }

            final Double quantity = deposit.quantity() != null ? deposit.quantity() : 1.0;
            result.add(new AIProcessedReceiptItem(deposit.name(), quantity, deposit.price(), deposit.category(), List.of()));
        }

        return result;
    }

    private AIProcessedReceiptItem enrichItem(final AIProcessedReceiptItem item, final ReceiptCategorization.CategorizedProduct categorized) {
        if (categorized == null) {
            return item;
        }

        final String productName = StringUtils.isNotBlank(categorized.proposedName()) ? categorized.proposedName() : item.productName();
        final List<Double> discounts = (categorized.discount() != null && categorized.discount() != 0)
                ? List.of(categorized.discount())
                : item.discounts();

        return new AIProcessedReceiptItem(productName, item.quantity(), item.price(), categorized.category(), discounts);
    }

    private ReceiptCategorization categorize(final List<AIProcessedReceiptItem> items, final String rawText, final Set<Category> categories) {
        final ReceiptCategorization empty = new ReceiptCategorization(List.of(), List.of());
        final String prompt = buildPrompt(items, rawText, categories);
        final String response;
        try {
            response = chatModel.call(prompt);
        } catch (final RuntimeException exception) {
            log.warn("Kategoryzacja przez model czatu nie powiodła się — kategorie/rabaty/kaucje pozostaną puste", exception);
            return empty;
        }

        return JsonDeserializer.deserializeJson(clearResponse(response), ReceiptCategorization.class).orElse(empty);
    }

    private String buildPrompt(final List<AIProcessedReceiptItem> items, final String rawText, final Set<Category> categories) {
        final List<String> forbiddenCharacters = aiProperties.getForbiddenCharacters();
        final String categoriesString = TEXT_FILTER.filterText(joinCategories(categories), forbiddenCharacters);
        final String productsString = TEXT_FILTER.filterText(joinProducts(items), forbiddenCharacters);
        final String receiptString = TEXT_FILTER.filterText(StringUtils.defaultString(rawText), forbiddenCharacters);

        return aiProperties.getCategorizationPrompt()
                .replace("{KATEGORIE}", categoriesString)
                .replace("{PRODUKTY}", productsString)
                .replace("{PARAGON}", receiptString);
    }

    private String joinCategories(final Set<Category> categories) {
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));
    }

    private String joinProducts(final List<AIProcessedReceiptItem> items) {
        return IntStream.range(0, items.size())
                .mapToObj(index -> index + ". " + StringUtils.defaultString(items.get(index).productName()))
                .collect(Collectors.joining("\n"));
    }

    private String clearResponse(final String response) {
        return response
                .replace("json", "")
                .replace("```", "");
    }
}
