package pl.crystalek.budgetweb.receipt.request.save;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pl.crystalek.budgetweb.utils.BaseTest;
import pl.crystalek.budgetweb.utils.request.RequestHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

class SaveReceiptItemsDataTest extends BaseTest {

    static Stream<Arguments> provideInvalidData() {
        return Stream.of(
                //receiptItemId
                Arguments.of(null, "Produkt", "1.0", "10.0", "1", null, null, "MISSING_RECEIPT_ITEM_ID"),
                Arguments.of("", "Produkt", "1.0", "10.0", "1", null, null, "MISSING_RECEIPT_ITEM_ID"),
                Arguments.of(" ", "Produkt", "1.0", "10.0", "1", null, null, "MISSING_RECEIPT_ITEM_ID"),
                Arguments.of("-1", "Produkt", "1.0", "10.0", "1", "0.0", "0", "INVALID_RECEIPT_ITEM_ID"),
                Arguments.of("abc", "Produkt", "1.0", "10.0", "1", "0.0", "0", "INVALID_NUMBER_FORMAT"),

                //productName
                Arguments.of("1", "", "1.0", "10.0", "1", "0.0", "0", "MISSING_NAME"),
                Arguments.of("1", " ", "1.0", "10.0", "1", "0.0", "0", "MISSING_NAME"),
                Arguments.of("1", null, "1.0", "10.0", "1", "0.0", "0", "MISSING_NAME"),
                Arguments.of("1", "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklm", "1.0", "10.0", "1", "0.0", "0", "NAME_TOO_LONG"),
                Arguments.of("1", "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmsdadadadsads", "1.0", "10.0", "1", "0.0", "0", "NAME_TOO_LONG"),
                Arguments.of("1", "a", "1.0", "10.0", "1", "0.0", "0", "NAME_TOO_SHORT"),

                //quantity
                Arguments.of("1", "Produkt", null, "10.0", "1", "0.0", "0", "MISSING_QUANTITY"),
                Arguments.of("1", "Produkt", "", "10.0", "1", "0.0", "0", "MISSING_QUANTITY"),
                Arguments.of("1", "Produkt", "0.0", "10.0", "1", "0.0", "0", "INVALID_QUANTITY"),
                Arguments.of("1", "Produkt", "siema", "10.0", "1", "0.0", "0", "INVALID_NUMBER_FORMAT"),
                Arguments.of("1", "Produkt", "-1.0", "10.0", "1", "0.0", "0", "INVALID_QUANTITY"),

                //price
                Arguments.of("1", "Produkt", "1.0", null, "1", "0.0", "0", "MISSING_PRICE"),
                Arguments.of("1", "Produkt", "1.0", "", "1", "0.0", "0", "MISSING_PRICE"),
                Arguments.of("1", "Produkt", "1.0", "0.0", "1", "0.0", "0", "INVALID_PRICE"),
                Arguments.of("1", "Produkt", "1.0", "siema", "1", "0.0", "0", "INVALID_NUMBER_FORMAT"),
                Arguments.of("1", "Produkt", "1.0", "-1.0", "1", "0.0", "0", "INVALID_PRICE"),

                //categoryId
                Arguments.of("1", "Produkt", "1.0", "10.0", null, "0.0", "0", "MISSING_CATEGORY_ID"),
                Arguments.of("1", "Produkt", "1.0", "10.0", "0", "0.0", "0", "INVALID_CATEGORY_ID"),
                Arguments.of("1", "Produkt", "1.0", "10.0", "-1", "0.0", "0", "INVALID_CATEGORY_ID"),
                Arguments.of("1", "Produkt", "1.0", "10.0", "abc", "0.0", "0", "INVALID_NUMBER_FORMAT")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidData")
    void shouldFailValidation(final String receiptId, final String name, final String quantity,
                              final String price, final String categoryId, final String moneyDividing,
                              final String userToReturnMoney, final String errorMessage) throws Exception {
        // Given
        final Map<String, Object> requestData = new HashMap<>();
        requestData.put("receiptItemId", receiptId);
        requestData.put("name", name);
        requestData.put("quantity", quantity);
        requestData.put("price", price);
        requestData.put("categoryId", categoryId);
        requestData.put("moneyDividing", moneyDividing);
        requestData.put("userToReturnMoneyId", userToReturnMoney);

        RequestHelper.builder()
                .withUser(userAccountUtil)
                .content(requestData)
                .httpMethod(HttpMethod.POST)
                .expectedResponseMessage(errorMessage)
                .expectedResponseCode(HttpStatus.BAD_REQUEST)
                .path("/receipts/save")
                .build().sendRequest(mockMvc);
    }
}