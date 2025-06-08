package ru.volodin.CreditBank.service.scoring.filter.soft;

import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.service.scoring.filter.ScoringSoftFilter;

@Component
public class WorkPositionSoftScoringFilter implements ScoringSoftFilter {

    //Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2;
    //                   Топ-менеджер → ставка уменьшается на 3

}
