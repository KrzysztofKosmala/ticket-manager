package pl.ticket.event.admin.event.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event.exception.InvalidRequestedDataException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AdminEventUtilsTest
{


    AdminEventUtils underTest = new AdminEventUtils();

    private static Stream<Arguments> provideArgumentsForIsMatchingDayOfWeek()
    {

        return Stream.of(
                Arguments.of(
                        LocalDate.of(2024, 8, 18), // Sunday
                        "niedziela",
                        true
                ),
                Arguments.of(
                        LocalDate.of(2024, 8, 19), // Monday
                        "poniedziałek",
                        true
                ),
                Arguments.of(
                        LocalDate.of(2024, 8, 18), // Sunday
                        "poniedziałek",
                        false
                ),
                Arguments.of(
                        LocalDate.of(2024, 8, 20), // Tuesday
                        "wtorek",
                        true
                )
        );
    }


    @ParameterizedTest
    @MethodSource("provideArgumentsForIsMatchingDayOfWeek")
    void shouldPrepareEventRegular2Successfully(LocalDate date, String expectedDay, boolean isMatching)
    {

        boolean result = underTest.isMatchingDayOfWeek(date, expectedDay);

        assertThat(result).isEqualTo(isMatching);
    }
    @Test
    void shouldThrowExceptionWhenDateRangeExceedsOneYear() {

        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 2);


        assertThatThrownBy(() -> underTest.datesFromRange(startDate, endDate))
                .isInstanceOf(InvalidRequestedDataException.class)
                .hasMessage("Maksymalnie można stwożyć eventy na rok w przód.");
    }


}