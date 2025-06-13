package pl.crystalek.budgetweb.receipt;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.crystalek.budgetweb.category.Category;
import pl.crystalek.budgetweb.household.Household;
import pl.crystalek.budgetweb.household.member.HouseholdMember;
import pl.crystalek.budgetweb.receipt.request.save.SaveReceiptItemsData;
import pl.crystalek.budgetweb.receipt.request.save.SaveReceiptRequest;
import pl.crystalek.budgetweb.receipt.response.save.SaveReceiptResponse;
import pl.crystalek.budgetweb.receipt.response.save.SaveReceiptResponseMessage;
import pl.crystalek.budgetweb.user.model.User;

import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
class ReceiptCreationValidator {
    final User requesterUser;
    final SaveReceiptRequest requestContent;

    Household household;
    SaveReceiptResponseMessage responseContent;
    String additionalMessage;

    public SaveReceiptResponse verifyAll() {
        verifyRequester();
        verifyReceiptDetails();
        verifyReceiptItemsData();

        final SaveReceiptResponseMessage responseMessage = responseContent == null ? SaveReceiptResponseMessage.SUCCESS : responseContent;
        return new SaveReceiptResponse(responseContent == null, responseMessage, additionalMessage);
    }

    private void verifyRequester() {
        final HouseholdMember householdMember = requesterUser.getHouseholdMember();
        if (householdMember == null) {
            responseContent = SaveReceiptResponseMessage.REQUESTER_HOUSEHOLD_NOT_FOUND;
            return;
        }

        this.household = householdMember.getHousehold();
    }

    private void verifyReceiptDetails() {
        if (responseContent != SaveReceiptResponseMessage.SUCCESS) {
            return;
        }

        final Set<HouseholdMember> householdMembers = household.getMembers();
        final boolean userDontHaveHousehold = householdMembers.stream().noneMatch(member -> member.getUser().getId() == requestContent.receiptDetails().whoPaidId());
        if (userDontHaveHousehold) {
            responseContent = SaveReceiptResponseMessage.USER_NOT_FOUND_IN_HOUSEHOLD;
            additionalMessage = "Nie odnaleziono użytkownika płacącego za paragon";
        }
    }

    private void verifyReceiptItemsData() {
        if (responseContent != SaveReceiptResponseMessage.SUCCESS) {
            return;
        }

        final Set<SaveReceiptItemsData> itemDataList = requestContent.itemsDataList();
        final Set<Category> categories = household.getCategories();
        final Set<HouseholdMember> members = household.getMembers();

        for (final SaveReceiptItemsData data : itemDataList) {
            final boolean categoryNotExists = categories.stream().noneMatch(category -> category.getId() == data.categoryId());
            if (categoryNotExists) {
                responseContent = SaveReceiptResponseMessage.CATEGORY_NOT_FOUND;
                additionalMessage = "Nie odnaleziono kategorii dla produktu: " + data.productName();
                return;
            }

            final Long userToReturnMoneyId = data.userToReturnMoneyId();
            if (userToReturnMoneyId == null) {
                continue;
            }

            final boolean userNotExistsInHousehold = members.stream().noneMatch(member -> member.getUser().getId() == userToReturnMoneyId);
            if (userNotExistsInHousehold) {
                responseContent = SaveReceiptResponseMessage.USER_NOT_FOUND_IN_HOUSEHOLD;
                additionalMessage = "Nie odnaleziono użytkownika dla produktu: " + data.productName();
            }
        }
    }
}
