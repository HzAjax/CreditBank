# MVP Level 5 — Реализация паттерна API-Gateway

## Назначение

Микросервис `gateway` реализует маршрутизацию REST-запросов к внутренним сервисам `deal` и `statement`. Он выступает в роли API-шлюза, предоставляя:

- единый вход для клиентов
- fallback-обработку при недоступности сервисов
- агрегацию OpenAPI-документации для Swagger UI

## Реализация

Маршруты конфигурируются программно в классе `RoutingConfiguration` с использованием `RouteLocatorBuilder`.

| Путь         | Целевой сервис     | CircuitBreaker | Fallback URI            |
|--------------|--------------------|----------------|--------------------------|
| `/deal/**`   | `${dealUrl}`       | `dealCircuitBreaker` | `/fallback/deal`     |
| `/statement/**` | `${statementUrl}` | `statementCircuitBreaker` | `/fallback/statement` |



Swagger UI доступен по адресу: http://localhost:8084/webjars/swagger-ui/index.html