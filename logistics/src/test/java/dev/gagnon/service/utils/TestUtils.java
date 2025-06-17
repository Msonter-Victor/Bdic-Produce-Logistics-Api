package dev.gagnon.service.utils;

import dev.gagnon.dto.request.AddCompanyRequest;
import lombok.SneakyThrows;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestUtils {
    private static final String TEST_IMAGE = "C:\\Users\\Gagnon\\IdeaProjects\\Bdic-Produce-Logistics-Api\\logistics\\src\\main\\resources\\static\\google icon.png";

    @SneakyThrows
    public static AddCompanyRequest buildCompanyOnboardRequest() {
        // Prepare MultipartFile for logo
        Path path = Paths.get(TEST_IMAGE);
        InputStream logoInputStream = Files.newInputStream(path);
        MultipartFile logo = new MockMultipartFile("logo", "logo.png", "image/png", logoInputStream);

        // Prepare MultipartFile for cacImage
        InputStream cacInputStream = Files.newInputStream(path);
        MultipartFile cacImage = new MockMultipartFile("cacImage", "cac.png", "image/png", cacInputStream);

        // Prepare List<MultipartFile> for otherDocuments
        InputStream docInputStream1 = Files.newInputStream(path);
        InputStream docInputStream2 = Files.newInputStream(path);
        MultipartFile doc1 = new MockMultipartFile("document1", "doc1.png", "image/png", docInputStream1);
        MultipartFile doc2 = new MockMultipartFile("document2", "doc2.png", "image/png", docInputStream2);
        List<MultipartFile> otherDocs = List.of(doc1, doc2);

        // Build and return the request
        return AddCompanyRequest.builder()
                .companyName("ArabianLogistics")
                .companyAddress("Area 1, Abuja")
                .ownerName("Arabian")
                .logo(logo)
                .cacImage(cacImage)
                .otherDocuments(otherDocs)
                .cacNumber("1111111111")
                .tin("1111111111")
                .build();
    }
}
