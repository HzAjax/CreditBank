package ru.volodin.calculator.service.scoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.internal.SimpleScoringInfoDto;
import ru.volodin.calculator.service.scoring.filter.ScoringSoftFilter;
import ru.volodin.calculator.service.scoring.filter.soft.InsuranceSoftScoringFilter;
import ru.volodin.calculator.service.scoring.filter.soft.SalaryClientSoftScoringFilter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringProviderImpl implements ScoringProvider {

    private final List<ScoringSoftFilter> softFilters;

    @Value("${service.rate}")
    private BigDecimal rate;

    @Override
    public BigDecimal[] fullScoring(ScoringDataDto scoringDataDto) {

        log.debug("Scoring data={}, rate={}", scoringDataDto, rate);

        BigDecimal totalRateDelta = BigDecimal.ZERO;
        BigDecimal totalInsurance = BigDecimal.ZERO;

        for (ScoringSoftFilter filter : softFilters) {
            totalRateDelta = totalRateDelta.add(filter.rateDelta(scoringDataDto));
            totalInsurance = totalInsurance.add(filter.insuranceDelta(scoringDataDto));
        }

        BigDecimal[] result = new BigDecimal[] { rate.add(totalRateDelta), totalInsurance };

        log.info("Result scoring data={} is {}, insurance cost={}"
                , scoringDataDto, result[0], result[1]);

        return result;
    }

    @Override
    public List<SimpleScoringInfoDto> simpleScoring() {
        return Stream.of(true, false)
                .flatMap(isInsurance ->
                        Stream.of(true, false).map(isSalaryClient ->
                                createSimpleScoringInfo(isInsurance, isSalaryClient)
                        )
                )
                .toList();
    }

    private SimpleScoringInfoDto createSimpleScoringInfo(boolean isInsurance, boolean isSalaryClient) {
        ScoringDataDto dto = new ScoringDataDto();
        dto.setIsInsuranceEnabled(isInsurance);
        dto.setIsSalaryClient(isSalaryClient);

        log.info("Calculating scoring for combination: isInsurance={}, isSalaryClient={}", isInsurance, isSalaryClient);

        BigDecimal rateDelta = rate;
        BigDecimal insurance = BigDecimal.ZERO;

        for (ScoringSoftFilter filter : softFilters) {
            if (filter instanceof InsuranceSoftScoringFilter || filter instanceof SalaryClientSoftScoringFilter) {
                rateDelta = rateDelta.add(filter.rateDelta(dto));
                insurance = insurance.add(filter.insuranceDelta(dto));
            }
        }

        log.info("Result: finalRate={}, insurance={}", rateDelta, insurance);

        return new SimpleScoringInfoDto(isInsurance, isSalaryClient, rateDelta, insurance);
    }
}
