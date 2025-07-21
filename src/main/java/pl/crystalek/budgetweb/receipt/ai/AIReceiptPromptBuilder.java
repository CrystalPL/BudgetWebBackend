package pl.crystalek.budgetweb.receipt.ai;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FilenameUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import pl.crystalek.budgetweb.category.Category;
import pl.crystalek.budgetweb.receipt.ai.filter.TextFilter;
import pl.crystalek.budgetweb.receipt.ai.filter.TextFilterImpl;
import pl.crystalek.budgetweb.receipt.ai.filter.strategy.FilterStrategy;
import pl.crystalek.budgetweb.receipt.ai.model.AIReceiptPrompt;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class AIReceiptPromptBuilder {
    static TextFilter TEXT_FILTER = new TextFilterImpl(FilterStrategy.getAllFilters());
    AIProperties aiProperties;

    public AIReceiptPrompt buildAIMessage(final File imageFile, final Set<Category> categories) {
        final String categoriesInString = getCategoriesInString(categories);
        final List<String> forbiddenCharacters = aiProperties.getForbiddenCharacters();
        final String filteredCategories = TEXT_FILTER.filterText(categoriesInString, forbiddenCharacters);
        final String prompt = aiProperties.getPrompt().replace("{KATEGORIE}", filteredCategories);

        final SystemMessage systemMessage = new SystemMessage(prompt);
        final Message categoriesUserMessage = prepareChatMessage(filteredCategories, imageFile);

        return new AIReceiptPrompt(systemMessage, categoriesUserMessage);
    }

    private String getCategoriesInString(final Set<Category> categories) {
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));
    }

    private Message prepareChatMessage(final String categories, final File imageFile) {
        final String fileExtension = FilenameUtils.getExtension(imageFile.getName());
        final MimeType mimeType = fileExtension.contains("png") ? MimeTypeUtils.IMAGE_PNG : MimeTypeUtils.IMAGE_JPEG;
        final Media media = new Media(mimeType, new FileSystemResource(imageFile));

        return new UserMessage(categories, media);
    }
}
