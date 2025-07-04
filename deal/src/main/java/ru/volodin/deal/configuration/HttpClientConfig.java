package ru.volodin.deal.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.volodin.deal.client.CalculatorHttpClient;
import ru.volodin.deal.configuration.props.CalculatorClientProperties;

@Configuration
@EnableConfigurationProperties(CalculatorClientProperties.class)
@RequiredArgsConstructor
public class HttpClientConfig {

    private final ConfigurableBeanFactory beanFactory;

    @Bean
    public CalculatorHttpClient calculatorHttpClient(CalculatorClientProperties properties) {

        RestClient restClient = RestClient.builder()
                .baseUrl(properties.getUrl())
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                                                .builderFor(adapter)
                                                .embeddedValueResolver(beanFactory::resolveEmbeddedValue)
                                                .build();

        return factory.createClient(CalculatorHttpClient.class);
    }
}