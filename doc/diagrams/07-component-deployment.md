# Component diagram & deployment diagram

Both diagrams are UML in PlantUML. GitHub does not render PlantUML, so each `.puml` source is committed together with a rendered `.svg`.

## Component diagram

Source: [`07-component.puml`](07-component.puml) · rendered: [`07-component.svg`](07-component.svg)

![Component diagram](07-component.svg)

| Component | Package / class in the code |
|---|---|
| Spring Security filter chain | `config/SecurityConfig` (session form login; unauthenticated `/api/**` calls get `401` with an empty body via `HttpStatusEntryPoint`) |
| Page controllers | `controller/web/*PageController`, `CurrentUserModelAdvice` |
| REST controllers | `controller/api/*Controller` (`/api/v1`) |
| GlobalExceptionHandler | `exception/GlobalExceptionHandler` (`@RestControllerAdvice` for `controller.api`) |
| Service layer (provided interfaces) | `service/*Service` ← `service/impl/*ServiceImpl` |
| Scheduled jobs | `service/job/MovieSyncJob`, `service/job/ReminderJob` (`@Scheduled`) |
| `MovieCatalogSource` interface / TMDB adapter | `service/external/MovieCatalogSource` ← `service/external/tmdb/TmdbMovieCatalogAdapter` → `TmdbClient` |
| Events (`ApplicationEventPublisher`) / Notification module | `domain/event/*Event` → `service/notification/NotificationEventListener` |
| `NotificationSender` interface | `service/notification/NotificationSender` ← `AbstractNotificationSender` ← `InApp…` / `Email…NotificationSender`, chosen by `NotificationSenderFactory` |
| Repositories | `repository/*Repository` (Spring Data JPA) |
| springdoc | `config/OpenApiConfig`, `/swagger-ui.html` |
| Actuator | `management.endpoints.web.exposure.include: health` in `application.yml` → `/actuator/health` |

## Deployment diagram

Source: [`07-deployment.puml`](07-deployment.puml) · rendered: [`07-deployment.svg`](07-deployment.svg)

![Deployment diagram](07-deployment.svg)

- **Current** — the app has **not** been deployed. It runs only on a developer laptop (`http://localhost:8080`) against the team's Supabase PostgreSQL database (the run log reports PostgreSQL 17.11; the JRE 21 comes from the VS Code Java extension — the project itself targets Java 17 in `pom.xml`) through the session pooler (port 5432, SSL). GitHub Actions (`.github/workflows/ci.yml`) builds and tests on push / PR to `main` and `develop`.
- **Planned** — taken from `Dockerfile` (build `maven:3.9-eclipse-temurin-17`, run `eclipse-temurin:17-jre`, `cinema-log.jar` copied to `/app/app.jar`, port 8080, `TZ=Asia/Bangkok`) and the `deploy` job in `ci.yml` (calls `RENDER_DEPLOY_HOOK` on push to `main`, skipped when the secret is not set). No public URL exists yet.
- `docker-compose.yml` (app + `postgres:16-alpine`) is an optional local setup and is not part of either view.
