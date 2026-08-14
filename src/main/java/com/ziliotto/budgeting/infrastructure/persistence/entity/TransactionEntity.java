package com.ziliotto.budgeting.infrastructure.persistence.entity;

import com.ziliotto.budgeting.domain.Category;
import com.ziliotto.budgeting.domain.Transaction;
import com.ziliotto.budgeting.domain.TransactionId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {
    @Id
    private UUID id;
    private String description;
    private long amount;

    @Enumerated(EnumType.STRING)
    private Category category;

    private LocalDateTime dateTime;

    public static TransactionEntity from(Transaction transaction){

        return new TransactionEntity(
                transaction.getId().uuid(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getDateTime());
    }

    public Transaction toDomain() {
        return new Transaction(
                new TransactionId(this.id),
                this.description,
                this.amount,
                this.category,
                this.dateTime
        );
    }
}
