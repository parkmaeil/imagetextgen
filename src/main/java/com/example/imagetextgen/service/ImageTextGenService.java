package com.example.imagetextgen.service;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
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

    @Value("classpath:/system.message")
    private Resource defaultSystemMessage;

    public ImageTextGenService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String analyzeImage(MultipartFile imageFile, String message) {
        // MIME 타입 결정
        String contentType = imageFile.getContentType();
        if (!MimeTypeUtils.IMAGE_PNG_VALUE.equals(contentType) &&
                !MimeTypeUtils.IMAGE_JPEG_VALUE.equals(contentType)) {
            throw new IllegalArgumentException("지원되지 않는 이미지 형식입니다.");
        }
        try {
            // Media 객체 생성
            var media = new Media(MimeType.valueOf(contentType), imageFile.getResource());
            // 사용자 메시지 생성
            var userMessage = new UserMessage(message, media);
            // 시스템 메시지 생성
            var systemMessage = new SystemMessage(defaultSystemMessage);
            // AI 모델 호출
            return chatModel.call(userMessage, systemMessage);
        } catch (Exception e) {
            throw new RuntimeException("이미지 처리 중 오류가 발생했습니다.", e);
        }
    }
}
