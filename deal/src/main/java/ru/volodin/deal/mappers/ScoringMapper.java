package ru.volodin.deal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.volodin.deal.entity.Statement;
import ru.volodin.deal.entity.dto.api.FinishRegistrationRequestDto;
import ru.volodin.deal.entity.dto.api.ScoringDataDto;

@Mapper(componentModel = "spring")
public interface ScoringMapper {
    @Mapping(target = "amount", source = "statement.appliedOffer.requestedAmount")
    @Mapping(target = "term", source = "statement.appliedOffer.term")
    @Mapping(target = "isInsuranceEnabled", source = "statement.appliedOffer.isInsuranceEnabled")
    @Mapping(target = "isSalaryClient", source = "statement.appliedOffer.isSalaryClient")
    @Mapping(target = "firstname", source = "statement.client.firstName")
    @Mapping(target = "lastName", source = "statement.client.lastName")
    @Mapping(target = "middleName", source = "statement.client.middleName")
    @Mapping(target = "email", source = "statement.client.email")
    @Mapping(target = "birthdate", source = "statement.client.birthdate")
    @Mapping(target = "passportSeries", source = "statement.client.passport.series")
    @Mapping(target = "passportNumber", source = "statement.client.passport.number")
    @Mapping(target = "passportIssueBranch", source = "finishRegistration.passportIssueBranch")
    ScoringDataDto toScoringDataDto(Statement statement, FinishRegistrationRequestDto finishRegistration);
}
