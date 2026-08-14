package com.ziliotto.budgeting;


import com.google.genai.Client;
import com.google.genai.types.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api")
public class TextToSpeechController {

    @PostMapping(value = "/synthesize", produces = "audio/wav")
    public ResponseEntity<byte[]> synthetize(@RequestBody SynthesizeRequest request) throws Exception {
        try (Client client = Client.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .build()){
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
                    request.text(),
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

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/wav"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audio.wav")
                    .body(wavBytes);
        }
    }

    record SynthesizeRequest(String text){

    }
}
