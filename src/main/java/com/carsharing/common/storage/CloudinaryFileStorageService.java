package com.carsharing.common.storage;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryFileStorageService implements FileStorageService {

    private final Cloudinary cloudinary;

    @Override
    public UploadedFileResponse upload(MultipartFile file, String folder) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("folder", folder)
            );

            return UploadedFileResponse.builder()
                    .url((String) result.get("secure_url"))
                    .publicId((String) result.get("public_id"))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Не вдалося завантажити файл у Cloudinary", e);
        }
    }

    @Override
    public void delete(String publicId) {
        try {
            if (publicId != null && !publicId.isBlank()) {
                cloudinary.uploader().destroy(publicId, Map.of());
            }
        } catch (Exception e) {
            throw new RuntimeException("Не вдалося видалити файл з Cloudinary", e);
        }
    }
}