package ru.volodin.CreditBank.service.scoring.filter.soft;

import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.service.scoring.filter.ScoringSoftFilter;

@Component
public class WorkStatusSoftScoringFilter implements ScoringSoftFilter {

    //Рабочий статус: Самозанятый → ставка увеличивается на 2;
    //                Владелец бизнеса → ставка увеличивается на 1



}

