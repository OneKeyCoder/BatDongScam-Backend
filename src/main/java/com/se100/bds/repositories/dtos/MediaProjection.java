package com.se100.bds.repositories.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MediaProjection {
    UUID getId();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getMediaType();
    String getFileName();
    String getFilePath();
    String getMimeType();
    String getDocumentType();
}

