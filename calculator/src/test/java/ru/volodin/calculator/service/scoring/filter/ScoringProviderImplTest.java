package ru.volodin.calculator.service.scoring.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.volodin.calculator.entity.dto.api.request.ScoringDataDto;
import ru.volodin.calculator.entity.dto.internal.SimpleScoringInfoDto;
import ru.volodin.calculator.service.scoring.ScoringProviderImpl;
import ru.volodin.calculator.service.scoring.filter.soft.GenderAndAgeSoftScoringFilter;
import ru.volodin.calculator.service.scoring.filter.soft.InsuranceSoftScoringFilter;
import ru.volodin.calculator.service.scoring.filter.soft.MaritalStatusSoftScoringFilter;
import ru.volodin.calculator.service.scoring.filter.soft.SalaryClientSoftScoringFilter;
import ru.volodin.calculator.service.scoring.filter.soft.WorkPositionSoftScoringFilter;
import ru.volodin.calculator.service.scoring.filter.soft.WorkStatusSoftScoringFilter;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoringProviderImplTest {

    @Mock
    private InsuranceSoftScoringFilter insuranceFilter;
    @Mock
    private SalaryClientSoftScoringFilter salaryFilter;
    @Mock
    private GenderAndAgeSoftScoringFilter genderFilter;
    @Mock
    private MaritalStatusSoftScoringFilter maritalStatusFilter;
    @Mock
    private WorkPositionSoftScoringFilter workPositionFilter;
    @Mock
    private WorkStatusSoftScoringFilter workStatusFilter;

    @InjectMocks
    private ScoringProviderImpl scoringProvider;

    @BeforeEach
    void init() {
        scoringProvider = new ScoringProviderImpl(List.of(
                insuranceFilter, salaryFilter, genderFilter,
                maritalStatusFilter, workPositionFilter, workStatusFilter
        ));
        ReflectionTestUtils.setField(scoringProvider, "rate", BigDecimal.TEN);
    }

    @Test
    void fullScoring_shouldCallAllFilters_andSumValues() {
        ScoringDataDto dto = new ScoringDataDto();

        when(insuranceFilter.rateDelta(dto)).thenReturn(BigDecimal.ONE);
        when(insuranceFilter.insuranceDelta(dto)).thenReturn(BigDecimal.ONE);

        when(salaryFilter.rateDelta(dto)).thenReturn(BigDecimal.ONE);
        when(salaryFilter.insuranceDelta(dto)).thenReturn(BigDecimal.ONE);

        when(genderFilter.rateDelta(dto)).thenReturn(BigDecimal.ONE);
        when(genderFilter.insuranceDelta(dto)).thenReturn(BigDecimal.ONE);

        when(maritalStatusFilter.rateDelta(dto)).thenReturn(BigDecimal.ONE);
        when(maritalStatusFilter.insuranceDelta(dto)).thenReturn(BigDecimal.ONE);

        when(workPositionFilter.rateDelta(dto)).thenReturn(BigDecimal.ONE);
        when(workPositionFilter.insuranceDelta(dto)).thenReturn(BigDecimal.ONE);

        when(workStatusFilter.rateDelta(dto)).thenReturn(BigDecimal.ONE);
        when(workStatusFilter.insuranceDelta(dto)).thenReturn(BigDecimal.ONE);

        BigDecimal[] result = scoringProvider.fullScoring(dto);

        assertEquals(BigDecimal.valueOf(16), result[0]);
        assertEquals(BigDecimal.valueOf(6), result[1]);

        verify(insuranceFilter).rateDelta(dto);
        verify(insuranceFilter).insuranceDelta(dto);
        verify(salaryFilter).rateDelta(dto);
        verify(salaryFilter).insuranceDelta(dto);
        verify(genderFilter).rateDelta(dto);
        verify(genderFilter).insuranceDelta(dto);
        verify(maritalStatusFilter).rateDelta(dto);
        verify(maritalStatusFilter).insuranceDelta(dto);
        verify(workPositionFilter).rateDelta(dto);
        verify(workPositionFilter).insuranceDelta(dto);
        verify(workStatusFilter).rateDelta(dto);
        verify(workStatusFilter).insuranceDelta(dto);
    }

    @Test
    void simpleScoring_shouldOnlyUseInsuranceAndSalaryFilters() {
        when(insuranceFilter.rateDelta(any())).thenReturn(BigDecimal.ONE);
        when(insuranceFilter.insuranceDelta(any())).thenReturn(BigDecimal.TEN);

        when(salaryFilter.rateDelta(any())).thenReturn(BigDecimal.valueOf(2));
        when(salaryFilter.insuranceDelta(any())).thenReturn(BigDecimal.valueOf(5));

        List<SimpleScoringInfoDto> list = scoringProvider.simpleScoring();

        assertEquals(4, list.size());

        for (SimpleScoringInfoDto info : list) {
            assertEquals(BigDecimal.TEN.add(BigDecimal.ONE).add(BigDecimal.valueOf(2)), info.getNewRate());
            assertEquals(BigDecimal.TEN.add(BigDecimal.valueOf(5)), info.getInsurance());
        }

        verify(insuranceFilter, times(4)).rateDelta(any());
        verify(insuranceFilter, times(4)).insuranceDelta(any());

        verify(salaryFilter, times(4)).rateDelta(any());
        verify(salaryFilter, times(4)).insuranceDelta(any());

        verifyNoInteractions(genderFilter, maritalStatusFilter, workPositionFilter, workStatusFilter);
    }
}
