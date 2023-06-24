package com.feed.engine.userImage;


import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
@AllArgsConstructor
public class FileUploadController {

    private final S3Service s3service;
    @PostMapping("/upload")
    public ResponseEntity<UserImage> handleFileUpload(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "Id") String Id, @RequestParam("imageOrderId") String imageOrderId) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is Empty");
        }
        UserImage userImage = s3service.uploadFile(file,Id,imageOrderId);

        return new ResponseEntity<>(userImage, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<String> getImageURI( @RequestParam(value = "Id") String Id, @RequestParam("imageOrderId") String imageOrderId) {
        return new ResponseEntity<>(s3service.getFileURI(Id,imageOrderId),HttpStatus.OK);
    }
}
