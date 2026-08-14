package com.ziliotto.budgeting.application;

import com.ziliotto.budgeting.application.input.PersistTransactionInput;
import com.ziliotto.budgeting.application.output.TransactionOutput;
import com.ziliotto.budgeting.domain.Transaction;
import com.ziliotto.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PersistTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public PersistTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "persist-transaction", description = "Persiste uma nova transação financeira")
    public TransactionOutput execute(PersistTransactionInput input){
        var transaction = transactionRepository.save(
                new Transaction(input.description(), input.amount(), input.category(), LocalDateTime.now()));
        return TransactionOutput.from(transaction);
    }
}
