package com.example.imagetextgen.controller;

import com.example.imagetextgen.entity.ImageAnalysisVO;
import com.example.imagetextgen.service.ImageTextGenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/image-text")
public class ImageTextGenController {

    private final ImageTextGenService imageTextGenService;

    @Value("${upload.path}")
    private String uploadPath;

    public ImageTextGenController(ImageTextGenService imageTextGenService) {
        this.imageTextGenService = imageTextGenService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<ImageAnalysisVO> getMultimodalResponse(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam(defaultValue = "이 이미지에 무엇이 있나요?") String message)
                                                                                             throws IOException {

        // Ensure the upload directory exists
        File uploadDirectory = new File(uploadPath);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs(); // uploads
        }

        // Save the uploaded file to the specified upload path
        String filename = imageFile.getOriginalFilename();
        Path filePath = Paths.get(uploadPath, filename);
        Files.write(filePath, imageFile.getBytes()); // 업로드

        // Analyze the image
        String analysisText = imageTextGenService.analyzeImage(imageFile, message);
        // http://localhost:8080/uploads/323232323.png
        String imageUrl = "/uploads/" + filename;

        ImageAnalysisVO response = new ImageAnalysisVO(imageUrl, analysisText, null);
        return ResponseEntity.ok(response);  //  { "imageUrl":".....", "analysisText":".........." }
    }
}
