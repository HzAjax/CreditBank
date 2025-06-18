package ru.volodin.calculator.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "scoring.filters")
@Getter
@Setter
public class ScoringFilterProperties {


    private Soft soft;
    private Hard hard;

    @Getter
    @Setter
    public static class Soft {
        private ChangeRateHolder salaryClient;
        private ChangeRateHolder insurance;
        private WorkStatus workStatus;
        private WorkPosition workPosition;
        private MaritalStatus maritalStatus;
        private Gender gender;
    }

    @Getter
    @Setter
    public static class Hard {
        private int countSalary;
        private Age age;
        private Experience experience;
    }

    @Getter
    @Setter
    public static class ChangeRateHolder {
        private int changeRate;
    }

    @Getter
    @Setter
    public static class WorkStatus {
        private ChangeRateHolder selfEmployed;
        private ChangeRateHolder businessman;
    }

    @Getter
    @Setter
    public static class WorkPosition {
        private ChangeRateHolder middleManager;
        private ChangeRateHolder topManager;
    }

    @Getter
    @Setter
    public static class MaritalStatus {
        private ChangeRateHolder married;
        private ChangeRateHolder single;
    }

    @Getter
    @Setter
    public static class Gender {
        private ChangeRateHolder notBinary;
        private int changeRate;
        private Age maleAge;
        private Age femaleAge;
    }

    @Getter
    @Setter
    public static class Age {
        private int min;
        private int max;
    }

    @Getter
    @Setter
    public static class Experience {
        private int total;
        private int current;
    }

}
