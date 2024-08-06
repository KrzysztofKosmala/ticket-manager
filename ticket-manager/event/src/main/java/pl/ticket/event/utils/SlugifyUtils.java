package pl.ticket.event.utils;

import com.github.slugify.Slugify;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SlugifyUtils
{
    public String slugifySlug(String slug)
    {
        final Slugify slg = Slugify.builder().customReplacement("_", "-").build();
        return slg.slugify(slug);
    }
}
