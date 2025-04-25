package dev.gagnon.Service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String save(MultipartFile file);
}