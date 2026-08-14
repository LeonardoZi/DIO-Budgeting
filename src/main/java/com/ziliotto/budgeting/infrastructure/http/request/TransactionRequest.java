package com.ziliotto.budgeting.infrastructure.http.request;

import com.ziliotto.budgeting.application.input.PersistTransactionInput;
import com.ziliotto.budgeting.domain.Category;

public record TransactionRequest(String description, Category category, long amount) {

    public PersistTransactionInput toInput() {
        return new PersistTransactionInput(description, amount, category);
    }
}
