package com.ziliotto.budgeting.infrastructure.http;

import com.ziliotto.budgeting.application.ListTransactionsByCategoryUseCase;
import com.ziliotto.budgeting.application.PersistTransactionUseCase;
import com.ziliotto.budgeting.domain.Category;
import com.ziliotto.budgeting.infrastructure.ai.GoogleGenAiTextToSpeechModel;
import com.ziliotto.budgeting.infrastructure.http.request.TransactionRequest;
import com.ziliotto.budgeting.infrastructure.http.response.TransactionResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final PersistTransactionUseCase persistTransactionUseCase;
    private final ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;
    private final ChatClient chatClient;
    private final ChatClient transcriptionChatClient;
    private final GoogleGenAiTextToSpeechModel textToSpeechModel;

    public TransactionController(PersistTransactionUseCase persistTransactionUseCase,
                                 ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase,
                                 ChatModel chatModel,
                                 @Value("classpath:prompts/system-message.st") Resource systemPrompt,
                                 GoogleGenAiTextToSpeechModel textToSpeechModel) {
        this.persistTransactionUseCase = persistTransactionUseCase;
        this.listTransactionsByCategoryUseCase = listTransactionsByCategoryUseCase;
        this.transcriptionChatClient = ChatClient.builder(chatModel).build();
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .defaultTools(persistTransactionUseCase, listTransactionsByCategoryUseCase)
                .build();
        this.textToSpeechModel = textToSpeechModel;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody TransactionRequest request){
        var transaction = persistTransactionUseCase.execute(request.toInput());
        return TransactionResponse.from(transaction);
    }

    @GetMapping("/{category}")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> readTransactions(@PathVariable Category category) {
        return listTransactionsByCategoryUseCase.execute(category).stream().map(TransactionResponse::from).toList();
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/wav")
    ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file) throws IOException {
        Resource audioFile = new ByteArrayResource(file.getBytes());

        var userMessage = transcriptionChatClient.prompt()
                .user(u -> u
                        .text("Transcreva este áudio literalmente, palavra por palavra, sem resumir, sem corrigir gramática, sem comentários. Áudio em português brasileiro. Áudio contém descrição de gastos financeiros. As frases geralmente contêm: - um valor em reais, responda no formato: (número + reais), sem R$ / - uma ação (gastei, paguei, comprei, recebi) / - um local ou estabelecimento (mercado, farmácia, restaurante, loja, etc.")
                        .media(MimeTypeUtils.parseMimeType("audio/mp3"), audioFile))
                .call()
                .content();

        assert userMessage != null : "Não foi possível transcrever o áudio.";
        var response = chatClient.prompt()
                .user(userMessage)
                .call()
                .content();

        byte[] audio = textToSpeechModel.call(response);
        var resource = new ByteArrayResource(audio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("audio.wav")
                                .build()
                                .toString())
                .body(resource);
    }
}
