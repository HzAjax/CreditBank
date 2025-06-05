package ru.volodin.CreditBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.volodin.CreditBank.service.OffersService;

@RestController
@RequestMapping("/calculator/offers")
@RequiredArgsConstructor
public class OffersController {

    private final OffersService offersService;

}
