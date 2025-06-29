package ru.volodin.deal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.volodin.deal.entity.CreditEntity;
import ru.volodin.deal.entity.dto.api.CreditDto;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "creditId", ignore = true)
    @Mapping(target = "insuranceEnabled", source = "isInsuranceEnabled")
    @Mapping(target = "salaryClient", source = "isSalaryClient")
    CreditEntity toCredit(CreditDto creditDto);
}