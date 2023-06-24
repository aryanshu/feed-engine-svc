package com.feed.engine.userImage;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class S3Service {

    @Value("${application.bucket.name}")
    private String bucketName;
    @Autowired
    private AmazonS3 s3Client;
    @Autowired
    private UserImagesRepo userImagesRepo;

    public UserImage uploadFile(MultipartFile file, String Id, String imageOrderId) {
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = Id+"/"+imageOrderId+"/"+file.getOriginalFilename();
        UserImage userImage = new UserImage(Long.valueOf(Id),Integer.valueOf(imageOrderId),fileName);

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));

        userImagesRepo.save(userImage);

        fileObj.delete();
        return userImage;
    }


    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }


    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }

    public String getFileURI(String Id, String imageOrderId ) {
        UserImage userImage = userImagesRepo.findByAttributes(Long.valueOf(Id),Integer.valueOf(imageOrderId));
        String fileName = userImage.getFilename();
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
                .withMethod(HttpMethod.GET)
                .withExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)));

        URL fileURL = s3Client.generatePresignedUrl(urlRequest);
        return String.valueOf(String.valueOf(fileURL));
    }
}