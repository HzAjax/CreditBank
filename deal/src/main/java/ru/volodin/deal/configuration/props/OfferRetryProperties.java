package ru.volodin.deal.configuration.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "retry.offers")
@Getter
@Setter
public class OfferRetryProperties {
    private Integer attempts;
    private Integer delay;
}
