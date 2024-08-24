package pl.ticket.cart.admin.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/admin/cart")
@AllArgsConstructor
public class AdminCartController
{
    @GetMapping
    public String firstCall()
    {
        return "Jest OK!";
    }

}
