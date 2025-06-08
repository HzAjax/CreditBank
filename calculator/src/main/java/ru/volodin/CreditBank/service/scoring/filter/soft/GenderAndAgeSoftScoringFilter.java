package ru.volodin.CreditBank.service.scoring.filter.soft;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.service.scoring.filter.ScoringSoftFilter;

@Slf4j
@Component
public class GenderAndAgeSoftScoringFilter implements ScoringSoftFilter {

    //Пол: Женщина, возраст от 32 до 60 лет → ставка уменьшается на 3;
    //     Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3;
    //     Не бинарный → ставка увеличивается на 7

}
