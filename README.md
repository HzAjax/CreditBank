# CreditBank

**CreditBank** — распределённая микросервисная система для автоматизации кредитного конвейера: от прескоринга и расчёта условий до уведомлений клиента и единой точки входа через API-Gateway.

## Состав проекта

### Микросервисы

1. **calculator** — расчётная логика

* Генерация кредитных предложений: `/calculator/offers`
* Полный расчёт параметров кредита с учётом скоринга: `/calculator/calc`
* Swagger UI: `http://localhost:8080/swagger-ui.html`

2. **deal** — управление сделкой

* Предварительный расчёт условий: `/deal/statement`
* Подтверждение выбранного предложения: `/deal/offer/select`
* Финальный расчёт с привлечением `calculator`: `/deal/calculate/{statementId}`
* Liquibase-скрипты для БД, логи, Swagger, 100% unit-тесты (Mockito).
* Swagger UI: `http://localhost:8081/swagger-ui.html`&#x20;

3. **statement** — точка входа и прескоринг

* Прескоринг + запрос в `deal` на расчёт предложений
* Фиксация выбора клиента и пересылка в `deal`
* Swagger UI: `http://localhost:8082/swagger-ui.html`&#x20;

4. **dossier** — уведомления клиенту

* Обработка Kafka-событий этапов сделки: `finish-registration`, `create-documents`, `send-documents`, `send-ses`, `credit-issued`, `statement-denied`
* Письма на HTML-шаблонах, генерация PDF, отправка email/SES

5. **gateway** — API-шлюз

* Единая точка входа и маршрутизация к `deal` и `statement`
* Circuit Breaker + fallback-обработчики
* Агрегация OpenAPI для Swagger UI
* Swagger UI: `http://localhost:8084/webjars/swagger-ui/index.html`&#x20;

---

## Общая библиотека

Репозиторий: `MyLib` (папки `logging`, `error-handling`, `my-lib-starter`) — свой Gradle-проект со **стартером**.
Назначение:

* **logging** — единообразное логирование входящих/исходящих запросов, корреляция.
* **error-handling** — универсальный `ControllerExceptionHandler` + `ErrorMessageDto` для согласованных ошибок.
* **my-lib-starter** — автоконфигурация Spring Boot для быстрого подключения логирования и обработчика ошибок к любому сервису.

### Подключение (пример Gradle)

```groovy
dependencies {
    implementation("com.your-org:mylib-starter:<version>")
}
```

> После подключения стартер подтянет `logging` и `error-handling` и включит автоконфигурацию без дополнительного кода.

---

## Быстрый старт (локально)

1. Создайте базу данных `creditbank`.
2. Для Kafka необходимо создать 6 топиков: `finish-registration`, `create-documents`, `send-documents`, `send-ses`, `credit-issued`, `statement-denied`.
3. Поднимите сервисы в порядке: `calculator` → `deal` → `statement` → `dossier` → `gateway`. 
4. Проверьте Swagger UI каждого сервиса (см. адреса выше). 
5. Рекомендуемый пользовательский поток:

    * POST в `statement` для прескоринга → получить 4 оффера&#x20;
    * Выбрать оффер → передать в `deal` `/deal/offer/select`&#x20;
    * Завершить регистрацию и финальный расчёт через `deal` `/deal/calculate/{statementId}`&#x20;
    * Отслеживать уведомления в `dossier` по событиям Kafka&#x20;
    * Использовать единый вход через `gateway` для клиентских запросов&#x20;

---

## Технологии и практики

* **Java 17+, Spring Boot 3**, Spring Cloud Gateway, Kafka, Liquibase
* **OpenAPI/Swagger** во всех сервисах&#x20;
* Unit-тесты: **JUnit + Mockito**, целевое покрытие 100% сервисной логики
* Единый подход к логированию и обработке ошибок через библиотеку **MyLib** (стартер)

---

## Эндпоинты Swagger

* calculator: `http://localhost:8080/swagger-ui.html`&#x20;
* deal: `http://localhost:8081/swagger-ui.html`&#x20;
* statement: `http://localhost:8082/swagger-ui.html`&#x20;
* gateway: `http://localhost:8084/webjars/swagger-ui/index.html`&#x20;

---

## Диаграмма

```
[Client] → [Gateway] → [Statement] → [Deal] ↔ [Calculator]
                               ↓
                           [Dossier] ← Kafka events
```

## 📊 Покрытие тестами

| Микросервис | Покрытие                                                                                  |
|-------------|-------------------------------------------------------------------------------------------|
| calculator  | ![Coverage](https://codecov.io/gh/HzAjax/CreditBank/branch/main/graph/badge.svg?flag=calculator) |
| deal        | ![Coverage](https://codecov.io/gh/HzAjax/CreditBank/branch/main/graph/badge.svg?flag=deal) |
| statement   | ![Coverage](https://codecov.io/gh/HzAjax/CreditBank/branch/main/graph/badge.svg?flag=statement) |
| dossier     | ![Coverage](https://codecov.io/gh/HzAjax/CreditBank/branch/main/graph/badge.svg?flag=dossier) |

