package pl.ticket.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderDto
{
    private String firstname;
    private String lastname;

    private String street;

    private String zipcode;

    private String city;

    private String email;

    private String phone;

    private Long cartId;

    private Long shipmentId;

    private Long paymentId;

    @Override
    public String toString() {
        return "OrderDto {\n" +
                "  firstname='" + firstname + '\'' + "\n" +
                "  lastname='" + lastname + '\'' + "\n" +
                "  street='" + street + '\'' + "\n" +
                "  zipcode='" + zipcode + '\'' + "\n" +
                "  city='" + city + '\'' + "\n" +
                "  email='" + email + '\'' + "\n" +
                "  phone='" + phone + '\'' + "\n" +
                "  cartId=" + cartId + "\n" +
                "  shipmentId=" + shipmentId + "\n" +
                "  paymentId=" + paymentId + "\n" +
                '}';
    }
}
