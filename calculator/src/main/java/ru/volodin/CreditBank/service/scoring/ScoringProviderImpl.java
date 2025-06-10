package ru.volodin.CreditBank.service.scoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.volodin.CreditBank.entity.dto.*;
import ru.volodin.CreditBank.service.scoring.filter.ScoringHardFilter;
import ru.volodin.CreditBank.service.scoring.filter.ScoringLightFilter;
import ru.volodin.CreditBank.service.scoring.filter.ScoringSoftFilter;
import ru.volodin.CreditBank.service.scoring.filter.soft.InsuranceSoftScoringFilter;
import ru.volodin.CreditBank.service.scoring.filter.soft.SalaryClientSoftScoringFilter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScoringProviderImpl implements ScoringProvider {

    private final List<ScoringHardFilter> hardFilters;
    private final List<ScoringSoftFilter> softFilters;
    private final List<ScoringLightFilter> lightFilters;

    private static final List<OfferCombination> FILTER_COMBINATIONS = List.of(
            new OfferCombination(false, false),
            new OfferCombination(true, false),
            new OfferCombination(false, true),
            new OfferCombination(true, true)
    );

    @Value("${service.rate}")
    private BigDecimal rate;

    @Override
    public RateAndInsuredServiceDto fullScoring(ScoringDataDto scoringDataDto) {

        log.debug("Scoring data={}, rate={}", scoringDataDto, rate);

        hardScoring(scoringDataDto);

        RateAndInsuredServiceDto rateAndInsuredServiceDto = softScoring(scoringDataDto, rate);

        log.info("Result scoring data={} is {}, insurance cost={}"
                , scoringDataDto, rateAndInsuredServiceDto.getNewRate(), rateAndInsuredServiceDto.getInsuredService());

        return rateAndInsuredServiceDto;
    }

    @Override
    public void hardScoring(ScoringDataDto scoringDataDto) {
        hardFilters.stream()
                .allMatch(filter -> filter.check(scoringDataDto));
    }

    @Override
    public RateAndInsuredServiceDto softScoring(ScoringDataDto scoringDataDto, BigDecimal rate) {

        List<RateAndInsuredServiceDto> resultList = softFilters.stream()
                .map(filter -> filter.check(scoringDataDto))
                .toList();

        BigDecimal diffRate = resultList.stream()
                .map(RateAndInsuredServiceDto::getNewRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal insuredService = resultList.stream()
                .map(RateAndInsuredServiceDto::getInsuredService)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        RateAndInsuredServiceDto rateAndInsuredServiceDto = new RateAndInsuredServiceDto(rate.add(diffRate), insuredService);

        return rateAndInsuredServiceDto;
    }

    @Override
    public List<SimpleScoringInfoDto> simpleScoring() {

        List<SimpleScoringInfoDto> offers = new ArrayList<>();

        for (OfferCombination offerCombination : FILTER_COMBINATIONS) {

            List<RateAndInsuredServiceDto> filterResults = applyFilters(offerCombination);

            RateAndInsuredServiceDto totalEffect = calculateTotalEffect(filterResults);
            RateAndInsuredServiceDto finalResult = combineEffects(totalEffect);

            offers.add(new SimpleScoringInfoDto(
                    offerCombination,
                    finalResult
            ));
        }

        log.debug("Possible offers, {}", offers);

        return offers;
    }

    private List<RateAndInsuredServiceDto> applyFilters(OfferCombination offerCombination) {
        List<RateAndInsuredServiceDto> filterResults = new ArrayList<>();

        for (ScoringLightFilter filter : lightFilters) {
            boolean isActive = filter instanceof InsuranceSoftScoringFilter && offerCombination.isInsurance()
                    || filter instanceof SalaryClientSoftScoringFilter && offerCombination.isSalaryClient();

            filterResults.add(filter.check(isActive));

            log.info("{}:change rate={}:insurance cost={}", filter.getClass().getSimpleName(),
                    filter.check(isActive).getNewRate(),
                    filter.check(isActive).getInsuredService());

        }
        return filterResults;
    }

    private RateAndInsuredServiceDto calculateTotalEffect(List<RateAndInsuredServiceDto> filterResults) {
        return filterResults.stream()
                .reduce(
                        new RateAndInsuredServiceDto(BigDecimal.ZERO, BigDecimal.ZERO),
                        (x, y) -> new RateAndInsuredServiceDto(
                                x.getNewRate().add(y.getNewRate()),
                                x.getInsuredService().add(y.getInsuredService())
                        )
                );
    }

    private RateAndInsuredServiceDto combineEffects(RateAndInsuredServiceDto totalEffect) {
        return new RateAndInsuredServiceDto(
                rate.add(totalEffect.getNewRate()),
                totalEffect.getInsuredService()
        );
    }
}
