package ru.volodin.calculator.service.scoring.filter.soft;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.volodin.calculator.configuration.ScoringFilterProperties;
import ru.volodin.calculator.entity.dto.enums.Gender;
import ru.volodin.calculator.entity.dto.api.ScoringDataDto;
import ru.volodin.calculator.service.scoring.filter.ScoringSoftFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenderAndAgeSoftScoringFilter implements ScoringSoftFilter {

    private final ScoringFilterProperties scoringProps;

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
    public BigDecimal rateDelta(ScoringDataDto dto) {
        Gender gender = dto.getGender();
        long age = ChronoUnit.YEARS.between(dto.getBirthdate(), LocalDate.now());

        if (gender == Gender.FEMALE && age >= minAgeFemale && age <= maxAgeFemale
                || gender == Gender.MALE && age >= minAgeMale && age <= maxAgeMale) {
            return changeRateNormalGenderValue;
        }

        if (gender == Gender.OTHER) {
            return changeRateNotBinaryValue;
        }

        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal insuranceDelta(ScoringDataDto dto) {
        return BigDecimal.ZERO;
    }

}
