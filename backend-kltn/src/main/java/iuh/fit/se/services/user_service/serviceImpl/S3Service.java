package iuh.fit.se.services.user_service.serviceImpl;


import java.io.IOException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import iuh.fit.se.util.S3Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@Profile("prod")
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    
    @org.springframework.beans.factory.annotation.Value("${aws.s3.bucket}")
    private String bucketName;
    
    // Upload và return resource để quản lý
    public S3Resource uploadToS3(MultipartFile file, String keyName) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromBytes(file.getBytes()));

        log.info("File uploaded to S3: {}", keyName);
        return new S3Resource(this, keyName);
    }

    // Method để commit resource
    public String commit(S3Resource resource) {
        resource.commit();
        return generatePublicUrl(resource.getFileKey());
    }

    // Generate public URL
    public String generatePublicUrl(String keyName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, s3Client.serviceClientConfiguration().region(), keyName);
    }

    // Delete file
    public void deleteFile(String keyName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted from S3: {}", keyName);
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to delete file: " + keyName, e);
        }
    }
}