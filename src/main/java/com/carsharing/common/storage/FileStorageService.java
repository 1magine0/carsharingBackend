package com.carsharing.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    UploadedFileResponse upload(MultipartFile file, String folder);
    void delete(String publicId);
}