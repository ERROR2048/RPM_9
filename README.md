# RPM_9

Учебное микросервисное приложение на Spring Cloud для работы с
пользователями, автомобилями и тест-драйвами.

## Архитектура

```text
                         ┌─────────────────┐
                         │   API Gateway   │ :8082
                         └────────┬────────┘
                                  │
       ┌──────────────────────────┼──────────────────────────┐
       │                          │                          │
┌──────▼──────┐           ┌───────▼──────┐           ┌───────▼────────┐
│ user-service│ :8084     │ car-service  │ :8083     │ test-drives    │ :8087
│             │           │              │           │ -service       │
└──────┬──────┘           └───────┬──────┘           └───────┬────────┘
       └──────────────────────────┴──────────────────────────┘
                                  │
                         ┌────────▼────────┐
                         │   PostgreSQL    │
                         └─────────────────┘

 Eureka Server :8761       Config Server :8888
```

Проект использует Java 17, Spring Boot 3.4.5 и Spring Cloud 2024.0.2.

## Структура

```text
practos_9/
├── api-gateway/                 # Единая точка входа и маршрутизация
├── car-service/                 # Модели автомобилей и карточки
├── config-server/               # Централизованная конфигурация
│   └── config-repo/
├── eureka-server/               # Регистрация и обнаружение сервисов
├── test-drives-service/         # Записи на тест-драйвы
├── user-service/                # Пользователи, роли и авторизация
├── client-service/              # Веб-интерфейс
└── README.md
```

## Реализовано

- три доменных микросервиса: `user-service`, `car-service`,
  `test-drives-service`;
- централизованная конфигурация через Config Server;
- регистрация и обнаружение сервисов через Eureka;
- маршрутизация запросов через API Gateway;
- резервные ответы Gateway при недоступности доменного сервиса;
- мониторинг через Spring Boot Actuator;
- базовое HTTP-логирование и ротация файлов логов;
- Swagger/OpenAPI для доменных сервисов;
- разграничение операций по ролям;
- CRUD для пользователей, ролей, автомобилей и тест-драйвов;
- статический клиент с авторизацией, профилем, каталогом автомобилей и
  страницей тест-драйвов;
- клиент обращается к backend через API Gateway на порту `8082`.

## Сервисы и порты

### Eureka Server

Адрес: `http://localhost:8761`

После запуска в панели должны отображаться:

- `API-GATEWAY`;
- `USER-SERVICE`;
- `CAR-SERVICE`;
- `TEST-DRIVES-SERVICE`.

### Config Server

Адрес: `http://localhost:8888`

Конфигурации находятся в `config-server/config-repo`.

Проверка:

```text
http://localhost:8888/user-service/default
http://localhost:8888/car-service/default
http://localhost:8888/test-drives-service/default
```

### API Gateway

Адрес: `http://localhost:8082`

| Префикс | Сервис |
|---|---|
| `/user-service/**` | `USER-SERVICE` |
| `/car-service/**` | `CAR-SERVICE` |
| `/test-drive-service/**` | `TEST-DRIVES-SERVICE` |

Пример маршрутизации:

```text
/test-drive-service/api/test-drives
        ↓
/api/test-drives в TEST-DRIVES-SERVICE
```

Проверка маршрутов:

```text
http://localhost:8082/actuator/gateway/routes
```

### User Service — порт 8084

Swagger: `http://localhost:8084/swagger-ui/index.html`

Основные маршруты:

| Метод | Путь |
|---|---|
| POST | `/api/auth/register` |
| POST | `/api/auth/login` |
| GET | `/api/auth/me` |
| CRUD | `/api/users`, `/api/users/{id}` |
| CRUD | `/api/roles`, `/api/roles/{id}` |

### Car Service — порт 8083

Swagger: `http://localhost:8083/swagger-ui/index.html`

Основные ресурсы:

- `/api/car-models`;
- `/api/car-complecttions`;
- `/api/cards`;
- `/api/card-details`.

### Test Drives Service — порт 8087

Swagger: `http://localhost:8087/swagger-ui/index.html`

Основные маршруты:

| Метод | Путь |
|---|---|
| GET | `/api/test-drives` |
| GET | `/api/test-drives/my` |
| GET | `/api/test-drives/{id}` |
| GET | `/api/test-drives/search?query=...` |
| POST | `/api/test-drives` |
| PUT | `/api/test-drives/{id}` |
| DELETE | `/api/test-drives/{id}` |

Пользователь с ролью `CUSTOMER` работает только со своими записями.
Пользователи с ролями `MANAGER` и `ADMIN` могут работать с общим списком
в соответствии со своими правами.

## Клиент

Клиент находится в папке `client-service` и запускается как статический сайт.
Все запросы к backend выполняются через `http://localhost:8082`.

Запуск:

```powershell
cd "C:\Users\Артём\Desktop\MPT\3 КУРС\RPM_Chibuk\practos_9\client-service"
py -m http.server 5500
```

Открыть:

```text
http://localhost:5500/index.html
```

Страница входа:

```text
http://localhost:5500/login.html
```

Перед запуском клиента должны быть доступны Eureka, Config Server,
доменные сервисы и API Gateway.

## Логирование и мониторинг

Для сервисов настроены:

- вывод сообщений в консоль;
- запись в rolling-файлы;
- ежедневная ротация;
- архивирование логов;
- HTTP-логирование метода, пути, статуса и времени выполнения.

Проверка состояния сервисов:

```text
http://localhost:8084/actuator/health
http://localhost:8083/actuator/health
http://localhost:8087/actuator/health
```

Логи создаются в каталогах соответствующих сервисов, например:

```text
test-drives-service/logs/test-drives-service.log
```

## Порядок запуска

Запускайте каждый компонент в отдельном окне PowerShell. Команды ниже рассчитаны на запуск из корневой папки проекта `RPM_9-main`.

### 1. PostgreSQL

Если база ещё не запущена, используйте существующий compose-файл:

```powershell
cd ".\\OLD PROJECT"
docker compose up -d
cd ..
```

### 2. Eureka Server

```powershell
cd ".\\eureka-server"
.\\mvnw.cmd spring-boot:run
```

Дождитесь запуска на `http://localhost:8761`, затем переходите дальше.

### 3. Config Server

```powershell
cd ".\\config-server"
.\\mvnw.cmd spring-boot:run
```

Дождитесь запуска на `http://localhost:8888`.

### 4. API Gateway

```powershell
cd ".\\api-gateway"
.\\mvnw.cmd spring-boot:run
```

Gateway использует Config Server, поэтому запускайте его после Config Server.

### 5. Доменные сервисы

Откройте отдельные окна PowerShell для каждого сервиса и запускайте после инфраструктуры:

```powershell
cd "ПУТЬ\\RPM_9-main\\user-service"
.\\mvnw.cmd spring-boot:run
```

```powershell
cd "ПУТЬ\\RPM_9-main\\car-service"
.\\mvnw.cmd spring-boot:run
```

```powershell
cd "ПУТЬ\\RPM_9-main\\test-drives-service"
.\\mvnw.cmd spring-boot:run
```

В командах замените `ПУТЬ` на фактический путь к папке проекта. У каждого сервиса должно быть своё окно PowerShell.

В конфигурации сервисов JWT-секрет берётся из переменной окружения `JWT_SECRET`, если она задана; иначе используется значение по умолчанию в `config-server/config-repo`. Для реального развёртывания задайте собственный секрет через `JWT_SECRET`.

### 6. Клиент

В отдельном окне PowerShell:

```powershell
cd "ПУТЬ\\RPM_9-main\\client-service"
py -m http.server 5500
```

Откройте `http://localhost:5500/index.html`. Для входа используйте `http://localhost:5500/login.html`.

## Проверка

Сборка и тесты сервиса тест-драйвов:

```powershell
cd ".\test-drives-service"
.\mvnw.cmd test
```

Проверяются загрузка контекста, создание и просмотр записей, а также
разграничение операций для `CUSTOMER`, `MANAGER` и `ADMIN`.


## Участник 2: news-service и test-drives-service

Реализованы/проверены исходники двух сервисов:
- `news-service`: CRUD новостей, поиск по заголовку/содержимому, JWT-фильтр и stateless Security, OpenAPI/Swagger, тесты сервиса и контроллера.
- `test-drives-service`: существующий CRUD тест-драйвов, JWT/Security, OpenAPI/Swagger и тесты сохранены.
- `config-server/config-repo/api-gateway.properties`: добавлен маршрут `/news-service/**` и CircuitBreaker fallback `/fallback/news`.
- `news-service.properties` и `test-drives-service.properties`: пароль БД и JWT secret допускают переопределение через `DB_PASSWORD` и `JWT_SECRET`.

### API
- Новости через Gateway: `/news-service/api/news` (GET/POST), `/news-service/api/news/{id}` (GET/PUT/DELETE), `/news-service/api/news/search?keyword=...`.
- Тест-драйвы через Gateway: `/test-drive-service/api/test-drives`; Swagger/OpenAPI: `/swagger-ui.html` и `/v3/api-docs` на соответствующем сервисе.
- Для защищённых `/api/**` запросов передавайте `Authorization: Bearer <JWT>`.
- Служебные эндпоинты документации и `/actuator/health` открыты без JWT.

### Тесты
В `news-service` добавлены unit-тесты `NewsServiceTest` и `NewsApiControllerTest`. В `test-drives-service` сохранены существующие `TestDriveServiceTest`, `TestDriveControllerTest` и application context test.
Запуск из каталога каждого сервиса: `mvn test` (при установленном Maven).
