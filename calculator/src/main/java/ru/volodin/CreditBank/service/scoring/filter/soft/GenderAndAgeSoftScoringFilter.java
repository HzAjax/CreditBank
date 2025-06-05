package ru.volodin.CreditBank.service.scoring.filter.soft;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.entity.dto.RateAndInsuredServiceDto;
import ru.volodin.CreditBank.entity.dto.ScoringDataDto;
import ru.volodin.CreditBank.entity.dto.enums.Gender;
import ru.volodin.CreditBank.service.scoring.filter.ScoringSoftFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class GenderAndAgeSoftScoringFilter implements ScoringSoftFilter {

    @Value("${scoring.filters.soft.gender.femaleAge.min}")
    private Integer minAgeFemale;
    @Value("${scoring.filters.soft.gender.femaleAge.max}")
    private Integer maxAgeFemale;

    @Value("${scoring.filters.soft.gender.maleAge.min}")
    private Integer minAgeMale;
    @Value("${scoring.filters.soft.gender.maleAge.max}")
    private Integer maxAgeMale;

    @Value("${scoring.filters.soft.gender.changeRate}")
    private BigDecimal changeRateNormalGenderValue;
    @Value("${scoring.filters.soft.gender.notBinary.changeRate}")
    private BigDecimal changeRateNotBinaryValue;

    //Пол: Женщина, возраст от 32 до 60 лет → ставка уменьшается на 3;
    //     Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3;
    //     Не бинарный → ставка увеличивается на 7
    @Override
    public RateAndInsuredServiceDto check(ScoringDataDto scoringDataDto) {
        Gender gender = scoringDataDto.getGender();
        long age = ChronoUnit.YEARS.between(scoringDataDto.getBirthdate(), LocalDate.now());

        if (gender == Gender.FEMALE && (age >= minAgeFemale && age <= maxAgeFemale)
                || gender == Gender.MALE && (age >= minAgeMale && age <= maxAgeMale)) {
            return new RateAndInsuredServiceDto(changeRateNormalGenderValue, BigDecimal.ZERO);
        } else if (gender == Gender.OTHER) {
            return new RateAndInsuredServiceDto(changeRateNotBinaryValue, BigDecimal.ZERO);
        }
        return new RateAndInsuredServiceDto(BigDecimal.ZERO, BigDecimal.ZERO);
    }

}
