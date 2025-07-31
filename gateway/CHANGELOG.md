## [1.0.0] - 2025-07-31

### Added
- Первая стабильная версия микросервиса gateway.
- Реализован паттерн API Gateway на основе Spring Cloud Gateway.
- Программная маршрутизация REST-запросов к микросервисам deal и statement через `RouteLocatorBuilder`.
- Для каждого маршрута настроены CircuitBreaker-фильтры с fallback URI:
    - `/deal/**` → fallback на `/fallback/deal`
    - `/statement/**` → fallback на `/fallback/statement`
- Добавлен контроллер `FallbackController` для обработки отказов с человекочитаемыми сообщениями.
- Swagger UI агрегирует документацию от микросервисов `deal` и `statement`:
    - `/deal/v3/api-docs`
    - `/statement/v3/api-docs`
- Swagger UI доступен по адресу: http://localhost:8084/webjars/swagger-ui/index.html
- Реализовано логирование маршрутов, уровней CircuitBreaker и fallback-событий.

