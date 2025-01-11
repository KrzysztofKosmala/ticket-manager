package pl.ticket.event.admin.image.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.ticket.dto.UploadResponse;
import pl.ticket.event.admin.image.service.AdminImageService;

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

    /*TODO:
    *  - delete
    *  - get by smth
    *  - serve file*/

}
