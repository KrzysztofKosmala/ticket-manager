package pl.ticket.event.admin.event.utils;

import org.springframework.stereotype.Component;
import pl.ticket.event.admin.event.exception.InvalidRequestedDataException;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class AdminEventUtils
{
    public boolean isMatchingDayOfWeek(LocalDate date, String expectedDay) {
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pl-PL"));
        return dayOfWeek.equals(expectedDay);
    }

    public List<LocalDate> datesFromRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> datesFromRange = startDate.datesUntil(endDate)
                .collect(Collectors.toList());

        //TODO: 365 z pliku ma sie zaczytywać
        if(datesFromRange.size() > 365) {
            throw new InvalidRequestedDataException("Maksymalnie można stwożyć eventy na rok w przód.");
        }

        return datesFromRange;
    }
}
