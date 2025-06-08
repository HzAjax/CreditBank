package ru.volodin.CreditBank.service.scoring.filter.soft;

import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.service.scoring.filter.ScoringSoftFilter;

@Component
public class MaritalStatusSoftScoringFilter implements ScoringSoftFilter {

    //Семейное положение: Замужем/женат → ставка уменьшается на 3;
    //                    Разведен → ставка увеличивается на 1

}