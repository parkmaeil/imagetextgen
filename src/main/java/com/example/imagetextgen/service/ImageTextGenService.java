package com.example.imagetextgen.service;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImageTextGenService {

    private final ChatModel chatModel;

    public ImageTextGenService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String analyzeImage(MultipartFile imageFile, String message) throws IOException {
        var imageType = imageFile.getOriginalFilename().endsWith(".png")
                ? MimeTypeUtils.IMAGE_PNG
                : MimeTypeUtils.IMAGE_JPEG;

        var media = new Media(imageType, new ByteArrayResource(imageFile.getBytes()));
        var userMessage = new UserMessage(message, media);
        // 1. 일반이미지
        var systemMessage = new SystemMessage(
                "이미지를 분석해서 텍스트로 만들어주고 자세하게 설명해줘"
         );
        return chatModel.call(userMessage, systemMessage);
    }
}
