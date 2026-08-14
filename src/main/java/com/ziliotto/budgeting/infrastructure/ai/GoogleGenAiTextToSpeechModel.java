package com.ziliotto.budgeting.infrastructure.ai;

import com.google.genai.Client;
import com.google.genai.types.*;
import org.springframework.stereotype.Component;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Component
public class GoogleGenAiTextToSpeechModel {

    public byte[] call(String text) {
        try (Client client = Client.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .build()) {

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
                    text,
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

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, baos);
            byte[] wavBytes = baos.toByteArray();

            return wavBytes;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar áudio com o Gemini", e);
        }
    }

    private static String bytesToHex(byte[] bytes, int limit) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(bytes.length, limit); i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        return sb.toString();
    }
}