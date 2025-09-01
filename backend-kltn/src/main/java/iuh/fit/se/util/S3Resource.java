package iuh.fit.se.util;


import iuh.fit.se.services.user_service.serviceImpl.S3Service;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class S3Resource implements AutoCloseable {
    private final String fileKey;
    private final S3Service s3Service;
    private boolean committed = false;

    public S3Resource(S3Service s3Service, String fileKey) {
        this.s3Service = s3Service;
        this.fileKey = fileKey;
    }

    public String getFileUrl() {
        return s3Service.generatePublicUrl(fileKey);
    }

    public void commit() {
        this.committed = true;
    }

    @Override
    public void close() {
        if (!committed) {
            // Nếu chưa commit thì xóa file khi thoát khỏi block
            try {
                s3Service.deleteFile(fileKey);
                log.debug("Rollback S3 file: {}", fileKey);
            } catch (Exception e) {
                log.warn("Failed to rollback S3 file: {}", fileKey, e);
            }
        }
    }
}