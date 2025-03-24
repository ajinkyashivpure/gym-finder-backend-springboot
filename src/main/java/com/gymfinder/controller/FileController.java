package com.gymfinder.controller;

import com.gymfinder.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/api/files")
@CrossOrigin("http://localhost:4200")
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileService.storeFile(file);
            String fileUrl = "/api/files/" + fileName; // URL to access the file
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("fileName", fileName);
                put("fileUrl", fileUrl);
            }});
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload file: " + e.getMessage());
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> getFile(@PathVariable String fileName) {
        try {
            byte[] fileData = fileService.getFile(fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG) // Adjust based on file type
                    .body(fileData);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
