# SOLID analysis

Where each principle shows up in the code, with the reason. Line numbers refer to the files as committed.

> Rule check: no field injection (`@Autowired` on fields) anywhere in `code/`. Every class receives dependencies through its constructor.

## S — Single Responsibility

| Where | What it shows | Owner |
|---|---|---|
| `code/src/main/java/com/cinemalog/service/external/tmdb/TmdbClient.java` L18 | TmdbClient only does HTTP calls to TMDB. Converting TMDB JSON into our model is a different job, done by TmdbMovieCatalogAdapter. | Prayfon |
| `code/src/main/java/com/cinemalog/service/impl/DiaryServiceImpl.java` L118 | Business rules (no future dates, movie must be out) live in the service. Field validation is in the DTO annotations (DiaryEntryRequest), and saving is in WatchedMovieRepository. | Nunthaporn |
| `code/src/main/java/com/cinemalog/service/notification/NotificationEventListener.java` L13 | The listener only turns events into notifications. The reminder code does not know how messages are delivered. | Pawarisa |
| `code/src/main/java/com/cinemalog/mapper/MovieMapper.java` L19 | Entity → DTO conversion has its own class, so controllers and services do not build JSON shapes. | Prayfon |

## O — Open/Closed

| Where | What it shows | Owner |
|---|---|---|
| `code/src/main/java/com/cinemalog/service/notification/NotificationSender.java` L5 | A new channel (e.g. LINE) is a new class implementing NotificationSender. The factory picks it up automatically, with no edits to existing code. | Pawarisa |
| `code/src/main/java/com/cinemalog/service/notification/NotificationSenderFactory.java` L16 | The factory receives every sender bean from Spring, so it has no switch or if/else on channels. | Pawarisa |
| `code/src/main/java/com/cinemalog/exception/ApiException.java` L5 | Each error type carries its own HTTP status. Adding a new error class needs no change in GlobalExceptionHandler. | Pawarisa |
| `code/src/main/java/com/cinemalog/domain/enums/MovieSortOption.java` L7 | Each sort option carries its own Sort. Adding an option means adding one enum constant, with no switch in the service. | Prayfon |

## L — Liskov Substitution

| Where | What it shows | Owner |
|---|---|---|
| `code/src/main/java/com/cinemalog/service/notification/AbstractNotificationSender.java` L25 | InAppNotificationSender and EmailNotificationSender can replace each other anywhere a NotificationSender is expected. Both always save a record, and neither throws UnsupportedOperationException. | Pawarisa |
| `code/src/main/java/com/cinemalog/service/external/MovieCatalogSource.java` L10 | Any catalog source (TMDB adapter, a mock in tests) honors the same contract: isAvailable() is false means "no data", never a crash. | Prayfon |
| `code/src/main/java/com/cinemalog/domain/enums/ReminderStatus.java` L43 | Every state implements the same methods. Invalid moves report a BusinessRuleException (normal 400 error), not UnsupportedOperationException. | Pawarisa |

## I — Interface Segregation

| Where | What it shows | Owner |
|---|---|---|
| `code/src/main/java/com/cinemalog/service/DiaryQueryService.java` L12 | Reading the diary and changing it are separate interfaces (DiaryQueryService / DiaryCommandService), so read-only clients don't see write methods. | Nunthaporn |
| `code/src/main/java/com/cinemalog/service/ReminderDispatchService.java` L5 | Sending due reminders is apart from ReminderService (what users do). Only the scheduler needs it, and no controller sees it. | Pawarisa |
| `code/src/main/java/com/cinemalog/service/AuthService.java` L6 | Registration (AuthService) is separate from profile editing (UserProfileService). | Pawarisa |
| `code/src/main/java/com/cinemalog/service/MovieDiscoveryService.java` L8 | Shelves and recommendations are separate from search/detail (MovieQueryService) and from TMDB syncing (MovieSyncService). | Prayfon |

## D — Dependency Inversion

| Where | What it shows | Owner |
|---|---|---|
| `code/src/main/java/com/cinemalog/service/impl/MovieQueryServiceImpl.java` L38 | The service depends on the MovieCatalogSource and MovieSyncService interfaces, not on TmdbClient. Everything is injected through the constructor only. | Prayfon |
| `code/src/main/java/com/cinemalog/controller/api/DiaryController.java` L47 | Controllers depend on service interfaces and on CurrentUserProvider (not on Spring Security classes), using constructor injection. | Nunthaporn |
| `code/src/main/java/com/cinemalog/config/ClockConfig.java` L13 | Services depend on an injected Clock instead of calling LocalDate.now(), so tests can fix the date. | Pawarisa |
| `code/src/main/java/com/cinemalog/service/impl/LibraryServiceImpl.java` L36 | Library state uses the ReminderService and NotificationService interfaces from another module instead of their implementations. | Nunthaporn |
