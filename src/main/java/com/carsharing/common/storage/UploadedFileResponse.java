package com.carsharing.common.storage;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadedFileResponse {
    private String url;
    private String publicId;
}