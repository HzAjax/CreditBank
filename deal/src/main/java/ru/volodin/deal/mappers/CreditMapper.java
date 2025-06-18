package ru.volodin.deal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.volodin.deal.entity.Credit;
import ru.volodin.deal.entity.dto.api.CreditDto;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    @Mapping(target = "insuranceEnabled", source = "isInsuranceEnabled")
    @Mapping(target = "salaryClient", source = "isSalaryClient")
    Credit toCredit(CreditDto creditDto);
}