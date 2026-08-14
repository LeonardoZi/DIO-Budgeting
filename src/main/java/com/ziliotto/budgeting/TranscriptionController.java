package com.ziliotto.budgeting;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class TranscriptionController {
    @Autowired
    private final ChatClient chatClient;

    public TranscriptionController(ChatClient chatClient){
        this.chatClient = chatClient;
    }

    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String transcribe(@RequestParam("file") MultipartFile file) throws IOException {
        Resource resource = new ByteArrayResource(file.getBytes());

        return chatClient.prompt()
                .user(u -> u
                        .text("Transcreva este áudio literalmente, palavra por palavra, sem resumir, sem corrigir gramática, sem comentários. Áudio em português brasileiro. Áudio contém descrição de gastos financeiros. As frases geralmente contêm: - um valor em reais, responda no formato: (número + reais), sem R$ / - uma ação (gastei, paguei, comprei, recebi) / - um local ou estabelecimento (mercado, farmácia, restaurante, loja, etc.")
                .media(MimeTypeUtils.parseMimeType(file.getContentType()), resource))
                .call()
                .content();

    }
}
