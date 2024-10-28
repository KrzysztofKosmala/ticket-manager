package pl.ticket.event.data_provider;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import pl.ticket.dto.OrderEvent;
import pl.ticket.dto.OrderRowDto;

import java.util.Arrays;

@UtilityClass
public class OrderEventDataProvider
{
    private static final Faker faker = new Faker();

    public static OrderEvent correct() {
        return OrderEvent.builder()
                .orderId(111L)
                .orderRows
                        (
                                Arrays.asList(
                                        OrderRowDto.builder()
                                        .productId(111L)
                                        .quantity(10)
                                        .build(),
                                        OrderRowDto.builder()
                                                .productId(222L)
                                                .quantity(1)
                                                .build(),
                                        OrderRowDto.builder()
                                                .productId(333L)
                                                .quantity(3)
                                                .build()
                                )
                        )
                .build();
    }

    public static OrderEvent wrongId() {
        return OrderEvent.builder()
                .orderId(111L)
                .orderRows
                        (
                                Arrays.asList(
                                        OrderRowDto.builder()
                                                .productId(1L)
                                                .quantity(10)
                                                .build())
                        )
                .build();
    }



}
