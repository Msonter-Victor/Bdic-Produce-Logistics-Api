package dev.gagnon.Service.ServiceImplementation;

import dev.gagnon.Service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    public String save(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID() + fileExtension;

        try {
            Files.createDirectories(Paths.get(uploadDir));
            File destination = new File(uploadDir + File.separator + uniqueFilename);
            file.transferTo(destination);
            return "/uploads/" + uniqueFilename; // if exposed through a static folder
        } catch (IOException e) {
            throw new RuntimeException("File saving failed", e);
        }
    }
}