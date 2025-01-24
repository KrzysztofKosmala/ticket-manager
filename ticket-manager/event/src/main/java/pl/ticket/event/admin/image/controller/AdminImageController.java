package pl.ticket.event.admin.image.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.ticket.dto.UploadResponse;
import pl.ticket.event.admin.image.service.AdminImageService;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("api/v1/admin/images")
@RequiredArgsConstructor
public class AdminImageController
{
    private final AdminImageService imageService;

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> uploadImage
            (
                    @RequestParam("image") MultipartFile image,
                    @RequestParam("description") String description
            )
    {
        return ResponseEntity.ok(imageService.uploadFile(image, description));
    }

    @DeleteMapping("/images/{id}")
    public void deleteImage(@PathVariable Long id)
    {
        imageService.deleteImage(id);
    }

    @GetMapping("/data/productImage/{name}")
    public ResponseEntity<Resource> serveFile(@PathVariable String name) throws IOException
    {
        FileSystemResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();
        String uploadDir = "./data/productImages/";
        Resource resource = fileSystemResourceLoader.getResource(uploadDir + name);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Path.of(name)))
                .body(resource);
    }

}
