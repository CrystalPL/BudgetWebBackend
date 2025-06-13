package pl.crystalek.budgetweb.receipt;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.content.Media;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.crystalek.budgetweb.receipt.request.save.SaveReceiptRequest;
import pl.crystalek.budgetweb.receipt.response.CreateReceiptDetailsResponse;
import pl.crystalek.budgetweb.receipt.response.DeleteReceiptResponse;
import pl.crystalek.budgetweb.receipt.response.GetReceiptResponse;
import pl.crystalek.budgetweb.receipt.response.ShopOccurrence;
import pl.crystalek.budgetweb.receipt.response.UserWhoPaid;
import pl.crystalek.budgetweb.receipt.response.save.SaveReceiptResponse;
import pl.crystalek.budgetweb.receipt.response.save.SaveReceiptResponseMessage;
import pl.crystalek.budgetweb.share.ResponseAPI;
import pl.crystalek.budgetweb.user.UserService;
import pl.crystalek.budgetweb.user.model.User;

import java.io.File;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReceiptService {
    ReceiptRepository repository;
    UserService userService;
    ChatModel chatModel;

    @SneakyThrows
    public void loadByAI(final MultipartFile file, final long userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Plik nie może być pusty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Plik musi być obrazem");
        }

//        if (file.getSize() > 5 * 1024 * 1024) {
//            throw new IllegalArgumentException("Plik jest zbyt duży. Maksymalny rozmiar to 5MB");
//        }

        File tempFile = File.createTempFile("receipt-", ".tmp");
        file.transferTo(tempFile);

//        final String promptText = """
//                Wysłałem Ci zdjęcie paragonu zawierającego nazwę sklepu, date zakupu oraz informacje o produktach które kupiłem.
//                Podaj mi proszę nazwę sklepu(pole nazwa_sklepu), date zakupów(pole data_zakupow), dane te mają być w formacie JSON.
//                Następnie daj mi listę produktów (nazwa produktu(pole nazwa_produktu), ilość(pole ilosc), cena(pole cena), suma(pole suma)), które kupiłem w formacie JSON.
//                Gdy nazwa produktu jest niekompletna, dziwna albo nieczytelna dla człowieka, to podaj prawidłową nazwę w nowym polu proponowana_nazwa, a odczytaną wartość dać w polu nazwa_produktu.
//                Usuwaj z proponowanej nazwy różne zbędne przyrostki, które nie tworzą nazwy produktu.
//                Gdy znajdziesz pod jakimś produktem opust/rabat to dodaj do produktu wyżej liste (pole listy ma się nazywac upusty)
//                z opustami(same kwoty) i nie twórz dodatkowej pozycji na opust.
//                """;
        final String promptText = "Wysłałem Ci zdjęcie paragonu zawierającego nazwę sklepu, date zakupu oraz informacje o produktach, które kupiłem. Podaj mi proszę nazwę sklepu(pole nazwa_sklepu), date zakupów(pole data_zakupow), dane te mają być w formacie JSON. Następnie daj mi listę produktów (nazwa produktu(pole nazwa_produktu), ilość(pole ilosc), cena(pole cena), suma(pole suma)), które kupiłem w formacie JSON. Zawsze dodawaj pole proponowana_nazwa (masz zaproponować własną nazwę dla produktu), a odczytaną wartość dać w polu nazwa_produktu. Usuwaj z proponowanej nazwy różne zbędne przyrostki, które nie tworzą nazwy produktu. Gdy znajdziesz pod jakimś produktem opust/rabat to dodaj do produktu wyżej liste (pole listy ma się nazywac upusty) z opustami(same kwoty) i nie twórz dodatkowej pozycji na opust.";
        final Media media = new Media(MimeTypeUtils.IMAGE_PNG, new FileSystemResource(tempFile));
        final String call = chatModel.call(new UserMessage(promptText, media));
        System.out.println(call);
    }

    public ResponseAPI<SaveReceiptResponseMessage> saveReceipt(final SaveReceiptRequest saveReceiptRequest, final long requesterId) {
        final User requesterUser = userService.getUserById(requesterId).get();

        final ReceiptCreationValidator receiptCreationValidator = new ReceiptCreationValidator(requesterUser, saveReceiptRequest);
        final SaveReceiptResponse verifyReceiptResponse = receiptCreationValidator.verifyAll();
        if (!verifyReceiptResponse.isSuccess()) {
            return verifyReceiptResponse;
        }

        final ReceiptCreationAssembler receiptCreationAssembler = new ReceiptCreationAssembler(requesterUser, saveReceiptRequest);
        receiptCreationAssembler.createReceipt();
        final Receipt receipt = receiptCreationAssembler.getReceipt();

        repository.save(receipt);

        return verifyReceiptResponse;
    }

    public ResponseAPI<DeleteReceiptResponse> deleteReceipt(final String stringReceiptId, final long requesterId) {
        if (stringReceiptId == null || stringReceiptId.isBlank()) {
            return new ResponseAPI<>(false, DeleteReceiptResponse.MISSING_RECEIPT_ID);
        }

        final long receiptId;
        try {
            receiptId = Long.parseLong(stringReceiptId);
        } catch (final NumberFormatException exception) {
            return new ResponseAPI<>(false, DeleteReceiptResponse.ERROR_NUMBER_FORMAT);
        }

        final Optional<Receipt> receiptOptional = repository.findById(receiptId);
        if (receiptOptional.isEmpty()) {
            return new ResponseAPI<>(false, DeleteReceiptResponse.RECEIPT_NOT_FOUND);
        }

        final Receipt receipt = receiptOptional.get();
        final boolean userNotExistsInHousehold = receipt.getHousehold().getMembers().stream().noneMatch(member -> member.getUser().getId() == requesterId);
        if (userNotExistsInHousehold) {
            return new ResponseAPI<>(false, DeleteReceiptResponse.USER_NOT_FOUND_IN_HOUSEHOLD);
        }

        repository.delete(receipt);

        return new ResponseAPI<>(true, DeleteReceiptResponse.SUCCESS);
    }

    public Set<GetReceiptResponse> getReceipts(final long requesterId) {
        return repository.getReceiptsByUserId(requesterId);
    }

    public CreateReceiptDetailsResponse getCreateReceiptDetails(final long requesterId) {
        final Set<UserWhoPaid> whoPaidList = repository.getWhoPaidList(requesterId);
        final Set<ShopOccurrence> shopOccurrences = repository.getShopOccurrences(requesterId);

        return new CreateReceiptDetailsResponse(whoPaidList, shopOccurrences);
    }
}