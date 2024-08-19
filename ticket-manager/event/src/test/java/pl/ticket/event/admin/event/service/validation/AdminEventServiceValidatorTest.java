package pl.ticket.event.admin.event.service.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.ticket.event.admin.event.data_provider.AdminEventRegularCreationDtoProvider;
import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event.exception.InvalidRequestedDataException;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class AdminEventServiceValidatorTest
{
    private final Clock fixedClock = Clock.fixed(Instant.parse("2024-08-15T00:00:00Z"), ZoneId.of("UTC"));
    private static final AdminEventRegularCreationDtoProvider adminEventRegularCreationDtoProvider = new AdminEventRegularCreationDtoProvider();

    private static Stream<Arguments> provideAdminEventRegularCreationDtos()
    {

        return Stream.of(
                Arguments.of(
                        adminEventRegularCreationDtoProvider.startDateIsBeforeNow()
                ),
                Arguments.of(
                        adminEventRegularCreationDtoProvider.endDateIsBeforeNow()
                )
        );
    }

    @Test
    void shouldThrowWrongEventType()
    {
        AdminEventServiceValidator validator = new AdminEventServiceValidator(fixedClock);
        AdminEventRegularCreationDto dto = adminEventRegularCreationDtoProvider.wrongEventType();


        assertThatThrownBy(() -> validator.validateAdminEventRegularCreationDto(dto))
                .isInstanceOf(InvalidRequestedDataException.class)
                .hasMessage("Zły typ eventu!");

    }

    @Test
    void shouldThrowWrongRange()
    {
        AdminEventServiceValidator validator = new AdminEventServiceValidator(fixedClock);
        AdminEventRegularCreationDto dto = adminEventRegularCreationDtoProvider.endDateIsBeforeStartDate();


        assertThatThrownBy(() -> validator.validateAdminEventRegularCreationDto(dto))
                .isInstanceOf(InvalidRequestedDataException.class)
                .hasMessage("Podany został zły zakres czasowy.");

    }

    @ParameterizedTest
    @MethodSource("provideAdminEventRegularCreationDtos")
    void shouldThrowIsBefore(AdminEventRegularCreationDto dto)
    {
        AdminEventServiceValidator validator = new AdminEventServiceValidator(fixedClock);



        assertThatThrownBy(() -> validator.validateAdminEventRegularCreationDto(dto))
                .isInstanceOf(InvalidRequestedDataException.class)
                .hasMessage("Nie można stworzyć eventu w podanym zakresie czasowym.");

    }
}