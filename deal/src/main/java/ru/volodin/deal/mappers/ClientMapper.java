package ru.volodin.deal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.volodin.deal.entity.ClientEntity;
import ru.volodin.deal.entity.dto.api.LoanStatementRequestDto;
import ru.volodin.deal.entity.dto.api.ScoringDataDto;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "maritalStatus", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "employment", ignore = true)
    @Mapping(target = "dependentAmount", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "passport.series", source = "passportSeries")
    @Mapping(target = "passport.number", source = "passportNumber")
    ClientEntity toClient(LoanStatementRequestDto loanStatement);

    @Mapping(target = "passport.series", source = "passportSeries")
    @Mapping(target = "passport.number", source = "passportNumber")
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "maritalStatus", source = "maritalStatus")
    @Mapping(target = "dependentAmount", source = "dependentAmount")
    @Mapping(target = "client.passport.issueDate", source = "passportIssueDate")
    @Mapping(target = "client.passport.issueBranch", source = "passportIssueBranch")
    @Mapping(target = "employment", source = "employment")
    @Mapping(target = "client.employment.employerInn", source = "employment.employerINN")
    @Mapping(target = "client.employment.status", source = "employment.employmentStatus")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "employment.employmentUUID", ignore = true)
    @Mapping(target = "employment.workExperienceTotalInMonths", ignore = true)
    @Mapping(target = "employment.workExperienceCurrentInMonths", ignore = true)
    void updateClientFromScoringData(@MappingTarget ClientEntity client, ScoringDataDto scoringDataDto);
}
