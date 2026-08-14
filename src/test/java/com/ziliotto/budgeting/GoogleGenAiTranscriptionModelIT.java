package com.ziliotto.budgeting;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
public class GoogleGenAiTranscriptionModelIT {

    @Autowired
    private ChatClient chatClient;

//    private final Resource audioFile = new ClassPathResource("audio/1.mp3");

//    @Test
//    void should_transcribeAudio_when_prompted(){
//
//        var response = chatClient.prompt()
//                .user(u -> u
//                        .text("Transcreva este áudio literalmente, palavra por palavra, sem resumir, sem corrigir gramática, sem comentários. Áudio em português brasileiro. Áudio contém descrição de gastos financeiros. As frases geralmente contêm: - um valor em reais (número + reais) / - uma ação (gastei, paguei, comprei, recebi) / - um local ou estabelecimento (mercado, farmácia, restaurante, loja, etc.")
//                        .media(MimeTypeUtils.parseMimeType("audio/mp3"), audioFile))
//                .call()
//                .content();
//
//        assertThat(response).isNotEmpty();
//        System.out.println(response);
//
//    }

    @ParameterizedTest
    @CsvSource({
            "1.mp3, 80 reais",
            "2.mp3, 40 reais",
            "3.mp3, 120 reais",
            "4.mp3, 90 reais",
            "5.mp3, 200 reais",
            "6.mp3, 60 reais",
    })
    public void should_containExpectedKeywords_when_audioFilesAreProcessed(String fileName, String expectedKeyword){
        var audioFile = new ClassPathResource("audio/" + fileName);

        var response = chatClient.prompt()
                .user(u -> u
                        .text("Transcreva este áudio literalmente, palavra por palavra, sem resumir, sem corrigir gramática, sem comentários. Áudio em português brasileiro. Áudio contém descrição de gastos financeiros. As frases geralmente contêm: - um valor em reais, responda no formato: (número + reais), sem R$ / - uma ação (gastei, paguei, comprei, recebi) / - um local ou estabelecimento (mercado, farmácia, restaurante, loja, etc.")
                        .media(MimeTypeUtils.parseMimeType("audio/mp3"), audioFile))
                .call()
                .content();

        assertThat(response).contains(expectedKeyword);
        System.out.println(response);
    }

}
