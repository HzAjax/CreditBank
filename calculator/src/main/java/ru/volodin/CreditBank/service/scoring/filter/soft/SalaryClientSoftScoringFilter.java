package ru.volodin.CreditBank.service.scoring.filter.soft;

import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.service.scoring.filter.ScoringSoftFilter;

@Component
public class SalaryClientSoftScoringFilter implements ScoringSoftFilter {

    //Сумма займа больше, чем 24 зарплат → отказ

}