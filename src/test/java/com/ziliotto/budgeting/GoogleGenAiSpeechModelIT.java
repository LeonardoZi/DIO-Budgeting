package com.ziliotto.budgeting;


import com.google.genai.Client;
import com.google.genai.types.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import java.io.File;
import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
public class GoogleGenAiSpeechModelIT {

    @Autowired
    private ChatClient chatClient;


    @Test
    public void should_produceAudio_when_textIsProvided() throws Exception{
        String texto = "Olá! Este é um teste de conversão de texto em fala usando o Gemini.";

        Client client = Client.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .build();

        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseModalities("AUDIO")
                .speechConfig(SpeechConfig.builder()
                    .voiceConfig(VoiceConfig.builder()
                        .prebuiltVoiceConfig(PrebuiltVoiceConfig.builder()
                            .voiceName("Kore")
                            .build())
                        .build())
                    .build())
                .build();

        GenerateContentResponse response = client.models.generateContent(
                "gemini-3.1-flash-tts-preview",
                texto,
                config
        );

        byte[] pcmData = response.candidates().get().get(0)
                .content().get()
                .parts().get().get(0)
                .inlineData().get()
                .data().get();

        AudioFormat format = new AudioFormat(24000, 16, 1, true, false);
        AudioInputStream audioStream = new AudioInputStream(
                new ByteArrayInputStream(pcmData),
                format,
                pcmData.length / format.getFrameSize()
        );

        File outputFile = new File("audio.wav");
        AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, outputFile);

        assertThat(outputFile).exists();
        assertThat(outputFile.length()).isGreaterThan(0);
        System.out.println("Áudio salvo em: " + outputFile.getAbsolutePath());
    }

}
