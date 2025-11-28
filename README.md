# Money Transfer Service

REST API для перевода денег с карты на карту.

## Функциональность

- Перевод денег между банковскими картами
- Валидация данных карт
- Логирование операций в файл
- Поддержка CORS для фронтенда

## Запуск приложения

```bash
./gradlew bootRun
```
## Docker запуск
### Запуск через Docker Compose:
```bash
docker-compose up
```
## Сборка и запуск образа

```bash
docker build -t money-transfer .
docker run -p 5500:5500 money-transfer
```
## Интеграция с FRONT
### API настроено на порт 5500 для совместимости с FRONT приложением из репозитория:
https://github.com/serp-ya/card-transfer

### Тестирование API через Postman:
```bash
POST http://localhost:5500/api/transfer
Content-Type: application/json

{
"cardFromNumber": "1234567812345678",
"cardFromValidTill": "12/25",
"cardFromCVV": "123",
"cardToNumber": "8765432187654321",
"amount": {
"value": 10000,
"currency": "RUB"
}
}
```
## API Endpoints

### POST /api/transfer
Выполняет перевод денег между картами.

Пример запроса:
```
{
  "cardFromNumber": "1234567812345678",
  "cardFromValidTill": "12/25",
  "cardFromCVV": "123",
  "cardToNumber": "8765432187654321",
  "amount": {
    "value": 10000,
    "currency": "RUB"
  }
}
```
Пример ответа:
```
{
"operationId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
"code": "0000",
"message": "Success"
}
```
### GET /api/health
Проверка работоспособности сервиса.
## Технологии
- Java 17
- Spring Boot 4.0.0
- Gradle
- Docker
