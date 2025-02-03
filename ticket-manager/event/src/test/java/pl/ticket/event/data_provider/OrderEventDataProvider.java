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
                                        .productId(1111111L)
                                        .build(),
                                        OrderRowDto.builder()
                                                .productId(1111111L)
                                                .build(),
                                        OrderRowDto.builder()
                                                .productId(3333333L)
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
                                                .build())
                        )
                .build();
    }



}
