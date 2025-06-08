package ru.volodin.CreditBank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.volodin.CreditBank.service.CalculatorService;

@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculatorService calculatorService;

}
