package pl.ticket.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ImageDto
{
    private Long id;

    private String name;

    private String desc;

    private String thumbImage;
}
