package com.ziliotto.budgeting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Transaction {
    private TransactionId id;
    private String description;
    private long amount;
    private Category category;
    private LocalDateTime dateTime;

    public Transaction(String description, long amount, Category category, LocalDateTime dateTime){
        this.id = new TransactionId();
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.dateTime = dateTime;
    }


}
