package pl.ticket.event.admin.event.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventCreationDto {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Integer capacity;

    @NotNull
    private String slug;

    //kategoria

    @NotNull
    private EventType eventType; // na bazie tego będzie wybierany strategia do budodwania occurrences
    //jak event type będzie okazjonalny to pobiera z jsona konkretne daty z pola occurrences np
/*      "occurrences": [
    {
        "date": "2024-08-15",
            "time": "19:00"
    },
    {
        "date": "2024-08-16",
            "time": "19:00"
    }*/
//jesli typ regular to w occurances już nie będzie listy
// w occurances dni tygodnia (pon,czw,niedz,) godziny (10:00, 12:00), data od, data do, s
    //lista biletow z ceną  maksymalna iloscia i typem
}
