package pl.ticket.event.admin.image.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.ticket.dto.UploadResponse;
import pl.ticket.event.admin.image.model.AdminImage;
import pl.ticket.event.admin.image.repository.AdminImageRepository;
import pl.ticket.event.utils.SlugifyUtils;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class AdminImageService
{
    @Value("${IMAGE_UPLOAD_DIR}")
    private String IMAGE_UPLOAD_DIR;
    @Value("${FETCH_IMAGE_URL}")
    private String FETCH_IMAGE_URL;
    private final AdminImageRepository imageRepository;
    private final SlugifyUtils slugifyUtils;

    public UploadResponse uploadFile(MultipartFile image, String description)
    {
        AdminImage model = new AdminImage();
        String name = slugifyUtils.slugifySlug(image.getOriginalFilename());

        Path path = Paths.get(IMAGE_UPLOAD_DIR).resolve(name);
        // Upewnij się, że katalog docelowy istnieje

        try(InputStream inputStream = image.getInputStream())
        {
            String string = path.toAbsolutePath().toString();
            OutputStream outputStream = Files.newOutputStream(path);
            inputStream.transferTo(outputStream);
        }catch (IOException e)  {
            throw new RuntimeException("Nie można zapisać pliku", e);
        }

        model.setThumbImage(FETCH_IMAGE_URL+name);
        model.setName(name);
        model.setDesc(description);
        imageRepository.save(model);

        return new UploadResponse(name, "Image uploaded successfully");
    }

    public AdminImage findById(Long id)
    {
        return imageRepository.findById(id).orElseThrow();
    }
}
