# Use case diagram + use case descriptions

UML use case diagram (PlantUML). Source: [`01-use-case.puml`](01-use-case.puml) · rendered: [`01-use-case.svg`](01-use-case.svg) · printable A4 document (diagram + description tables): [`01-use-case.pdf`](01-use-case.pdf)

![Use case diagram](01-use-case.svg)

## Actors

- **Visitor**: Not logged in. Can only reach `/login`, `/register` and the public API (`SecurityConfig`).
- **Member**: A logged-in Visitor (generalization). Every page and `/api/v1/users/me/**` needs a session.
- **Scheduler**: Spring `@Scheduled` jobs `MovieSyncJob` and `ReminderJob`. Each also runs once on `ApplicationReadyEvent`.
- **TMDB API, SMTP server**: External systems. Email is optional (`spring.mail.host` may be empty).

## «include» / «extend» used in the diagram

| Relation | Why | Code |
|---|---|---|
| UC01 «include» UC02 | After a successful register the page always submits the hidden login form | `templates/auth/register.html` (`#auto-login`), `pages/auth.js` |
| UC04 «extend» UC02 | Only when the profile has no favorite genres | `OnboardingAwareSuccessHandler` → `UserProfileService.needsOnboarding()` |
| UC07 «extend» UC06 | Only for a text query of 2+ characters, on page 0, with a TMDB key | `MovieQueryServiceImpl.search()` → `MovieSyncService.importSearchResults()` |
| UC08 «include» UC10 | The movie page always loads its reviews | `pages/movie.js` → `GET /api/v1/movies/{id}/reviews` |
| UC09 «extend» UC08 | Only when details are missing or stale and a key is set | `MovieQueryServiceImpl.getDetail()` → `Movie.needsDetails()` → `refreshDetails()` |
| UC23 «include» UC24 | Every due reminder is delivered | `ReminderDispatchServiceImpl.dispatchDueReminders()` |
| UC24 «extend» UC18 | Only when the reminder date has already been reached | `ReminderServiceImpl.save()` → `dispatchIfDue()` |

## Use case descriptions

### UC01 — Register account

| Field | Detail |
|---|---|
| Use Case ID | UC01 |
| Use Case Name | Register account |
| Primary Actor | Visitor |
| Description | A visitor creates an account and is logged in straight away. |
| Pre-conditions | • Not logged in |
| Post-conditions | • `users` + `user_profiles` rows exist; password stored as BCrypt hash |
| Main Flow | 1. Visitor enters display name, email and password.<br>2. Page sends `POST /api/v1/auth/register`.<br>3. System creates the account and profile.<br>4. Page logs the visitor in automatically (UC02). |
| Alternative Flow | • Email already used → 409.<br>• Blank name, invalid email, or password not 6–72 characters → 400 with `fieldErrors`. |

### UC02 — Log in

| Field | Detail |
|---|---|
| Use Case ID | UC02 |
| Use Case Name | Log in |
| Primary Actor | Visitor |
| Description | A visitor signs in with email and password and gets a session. |
| Pre-conditions | • Account exists |
| Post-conditions | • Session cookie set |
| Main Flow | 1. Visitor submits email + password to `/login`.<br>2. System creates a session.<br>3. System redirects to `/films`. |
| Alternative Flow | • Wrong email/password → `/login?error`.<br>• No favorite genres → `/onboarding` (UC04). |

### UC03 — Log out

| Field | Detail |
|---|---|
| Use Case ID | UC03 |
| Use Case Name | Log out |
| Primary Actor | Member |
| Description | A member ends the current session. |
| Pre-conditions | • Logged in |
| Post-conditions | • Session ended, redirect to `/login?logout` |
| Main Flow | 1. Member opens the nav menu and chooses log out (`POST /logout`). |
| Alternative Flow | — |

### UC04 — Pick favorite genres

| Field | Detail |
|---|---|
| Use Case ID | UC04 |
| Use Case Name | Pick favorite genres |
| Primary Actor | Member |
| Description | A member chooses the genres used for recommendations. |
| Pre-conditions | • Logged in |
| Post-conditions | • `user_favorite_genres` replaced |
| Main Flow | 1. Member ticks genres.<br>2. Page sends `PUT /api/v1/users/me/favorite-genres`. |
| Alternative Flow | • None ticked → 400 "Pick at least one genre".<br>• More than 10 → 400. |

### UC05 — Browse discovery shelves

| Field | Detail |
|---|---|
| Use Case ID | UC05 |
| Use Case Name | Browse discovery shelves |
| Primary Actor | Member |
| Description | A member browses the home shelves of movies. |
| Pre-conditions | • Logged in |
| Post-conditions | — |
| Main Flow | 1. Member opens `/films`.<br>2. System shows now showing, upcoming, top rated and recommended (`/api/v1/movies/now-showing`, `/upcoming`, `/top-rated`, `/recommended`). |
| Alternative Flow | • No favorite genres → recommended falls back to top rated. |

### UC06 — Search & filter catalog

| Field | Detail |
|---|---|
| Use Case ID | UC06 |
| Use Case Name | Search & filter catalog |
| Primary Actor | Member |
| Description | A member searches the catalog with text and filters. |
| Pre-conditions | • Logged in |
| Post-conditions | — |
| Main Flow | 1. Member sets text, genre, year, decade, released-before, min rating, language and sort.<br>2. Page sends `GET /api/v1/movies`.<br>3. System returns 40 results per page. |
| Alternative Flow | • No match → empty state.<br>• TMDB error → local results only (warning logged). |

### UC07 — Import search results from TMDB

| Field | Detail |
|---|---|
| Use Case ID | UC07 |
| Use Case Name | Import search results from TMDB |
| Primary Actor | System (extends UC06) |
| Description | During a search the system imports matching movies from TMDB. |
| Pre-conditions | • Text query of 2+ characters (`MovieSearchCriteria.hasText()`), page 0, TMDB key set |
| Post-conditions | • New/updated `movies` rows |
| Main Flow | 1. `TmdbMovieCatalogAdapter.search()` is called.<br>2. System upserts matches by `tmdb_id`. |
| Alternative Flow | • TMDB unreachable → `ExternalServiceException` caught, search continues. |

### UC08 — View movie details

| Field | Detail |
|---|---|
| Use Case ID | UC08 |
| Use Case Name | View movie details |
| Primary Actor | Member |
| Description | A member opens a movie page with details and actions. |
| Pre-conditions | • Logged in |
| Post-conditions | — |
| Main Flow | 1. Member opens `/movies/{id}`.<br>2. System shows details, cast, reviews (UC10) and similar movies.<br>3. Page shows buttons for like, watchlist, watched, rate, review, remind and collection. |
| Alternative Flow | • Unknown id → 404.<br>• Movie not released yet → watched/rate/review disabled, remind enabled. |

### UC09 — Refresh movie details from TMDB

| Field | Detail |
|---|---|
| Use Case ID | UC09 |
| Use Case Name | Refresh movie details from TMDB |
| Primary Actor | System (extends UC08) |
| Description | When details are missing or stale the system refreshes them from TMDB. |
| Pre-conditions | • Details missing or stale, TMDB key set |
| Post-conditions | • `details_fetched_at` updated |
| Main Flow | 1. `MovieSyncService.refreshDetails()` is called.<br>2. Runtime, director, cast and genres are updated. |
| Alternative Flow | • TMDB error → old data shown. |

### UC10 — Read movie reviews

| Field | Detail |
|---|---|
| Use Case ID | UC10 |
| Use Case Name | Read movie reviews |
| Primary Actor | Member |
| Description | A member reads the reviews of a movie. |
| Pre-conditions | • Movie exists |
| Post-conditions | — |
| Main Flow | 1. Page sends `GET /api/v1/movies/{id}/reviews`.<br>2. System returns the average rating and reviews, own review first. |
| Alternative Flow | • Unknown movie → 404. |

### UC11 — Like / unlike movie

| Field | Detail |
|---|---|
| Use Case ID | UC11 |
| Use Case Name | Like / unlike movie |
| Primary Actor | Member |
| Description | A member likes or unlikes a movie. |
| Pre-conditions | • Logged in |
| Post-conditions | • `liked_movies` row added/removed |
| Main Flow | 1. Member presses ♡ → `PUT /api/v1/users/me/likes/{movieId}`; pressing it again → `DELETE`. |
| Alternative Flow | • Already liked → no change.<br>• Unknown movie → 404. |

### UC12 — Add / remove watchlist

| Field | Detail |
|---|---|
| Use Case ID | UC12 |
| Use Case Name | Add / remove watchlist |
| Primary Actor | Member |
| Description | A member adds a movie to or removes it from the watchlist. |
| Pre-conditions | • Logged in |
| Post-conditions | • `watchlist_items` row added/removed |
| Main Flow | 1. Member presses + → `PUT /api/v1/users/me/watchlist/{movieId}`; remove → `DELETE`. |
| Alternative Flow | • Already on list → no change.<br>• Unknown movie → 404. |

### UC13 — Log watched movie

| Field | Detail |
|---|---|
| Use Case ID | UC13 |
| Use Case Name | Log watched movie |
| Primary Actor | Member |
| Description | A member records that they watched a movie, with optional rating and review. |
| Pre-conditions | • Logged in, movie released |
| Post-conditions | • `watched_movies` row added; movie removed from watchlist; if a review was written, an in-app "review added" notification |
| Main Flow | 1. Member picks date, stars, review and place.<br>2. Page sends `POST /api/v1/users/me/diary`. |
| Alternative Flow | • Date in the future or movie not out yet → 400.<br>• Rating outside 1–5 → 400.<br>• Unknown movie → 404. |

### UC14 — Edit / delete diary entry

| Field | Detail |
|---|---|
| Use Case ID | UC14 |
| Use Case Name | Edit / delete diary entry |
| Primary Actor | Member |
| Description | A member changes or deletes one of their diary entries. |
| Pre-conditions | • Entry belongs to the member |
| Post-conditions | • Entry updated (first review added → notification) or removed |
| Main Flow | 1. Member chooses edit → `PUT /api/v1/users/me/diary/{id}`, or delete → `DELETE /api/v1/users/me/diary/{id}`. |
| Alternative Flow | • Same date rules as UC13.<br>• Entry of another user → 404. |

### UC15 — Browse diary calendar

| Field | Detail |
|---|---|
| Use Case ID | UC15 |
| Use Case Name | Browse diary calendar |
| Primary Actor | Member |
| Description | A member browses their diary as a month calendar. |
| Pre-conditions | • Logged in |
| Post-conditions | — |
| Main Flow | 1. Member opens `/diary` month grid (`/diary/calendar?month=`).<br>2. Member clicks a day (`/diary/day?date=`). |
| Alternative Flow | • Empty month → empty state. |

### UC16 — Browse library lists

| Field | Detail |
|---|---|
| Use Case ID | UC16 |
| Use Case Name | Browse library lists |
| Primary Actor | Member |
| Description | A member views their watched, watchlist and liked lists. |
| Pre-conditions | • Logged in |
| Post-conditions | — |
| Main Flow | 1. Member opens the `/watched`, `/watchlist` or `/liked` page. |
| Alternative Flow | • Empty list → empty state. |

### UC17 — Manage collections

| Field | Detail |
|---|---|
| Use Case ID | UC17 |
| Use Case Name | Manage collections |
| Primary Actor | Member |
| Description | A member creates, renames and deletes collections and adds or removes movies in them. |
| Pre-conditions | • Logged in |
| Post-conditions | • `collections` / `collection_movies` changed |
| Main Flow | 1. Create, rename, delete via `/api/v1/users/me/collections`.<br>2. Add/remove movie via `PUT`/`DELETE /{id}/movies/{movieId}`. |
| Alternative Flow | • Duplicate name (case-insensitive) → 409.<br>• Blank name → 400.<br>• Removing a movie not in it → 404. |

### UC18 — Set / change release reminder

| Field | Detail |
|---|---|
| Use Case ID | UC18 |
| Use Case Name | Set / change release reminder |
| Primary Actor | Member |
| Description | A member asks to be reminded before an upcoming movie is released. |
| Pre-conditions | • Movie not released yet |
| Post-conditions | • `reminders` row SCHEDULED (or SENT) |
| Main Flow | 1. Member chooses 7 / 3 / 1 / 0 days and IN_APP or EMAIL.<br>2. Page sends `PUT /api/v1/users/me/reminders/{movieId}`. |
| Alternative Flow | • Other offset → 400.<br>• Unknown movie → 404.<br>• Movie already out or no release date → 400.<br>• Reminder already exists → rescheduled.<br>• Date already reached → delivered now (UC24). |

### UC19 — Cancel release reminder

| Field | Detail |
|---|---|
| Use Case ID | UC19 |
| Use Case Name | Cancel release reminder |
| Primary Actor | Member |
| Description | A member removes a reminder. |
| Pre-conditions | • Reminder exists |
| Post-conditions | • Status CANCELLED |
| Main Flow | 1. Page sends `DELETE /api/v1/users/me/reminders/{movieId}`. |
| Alternative Flow | • No reminder → 404. |

### UC20 — Read notifications

| Field | Detail |
|---|---|
| Use Case ID | UC20 |
| Use Case Name | Read notifications |
| Primary Actor | Member |
| Description | A member reads their notifications from the bell menu. |
| Pre-conditions | • Logged in |
| Post-conditions | • All notifications `is_read = true` |
| Main Flow | 1. Bell shows unread count + latest 10.<br>2. Opening the bell while there are unread items → `POST /notifications/read-all`. |
| Alternative Flow | — |

### UC21 — Edit profile & view stats

| Field | Detail |
|---|---|
| Use Case ID | UC21 |
| Use Case Name | Edit profile & view stats |
| Primary Actor | Member |
| Description | A member edits their profile and sees their statistics. |
| Pre-conditions | • Logged in |
| Post-conditions | • `users.email` / `user_profiles` updated |
| Main Flow | 1. Member edits name, email, bio, avatar and favorite genres → `PUT /api/v1/users/me`.<br>2. Profile page shows `/api/v1/users/me/stats`. |
| Alternative Flow | • Email used by another account → 409.<br>• Invalid fields → 400. |

### UC22 — Sync movie catalog

| Field | Detail |
|---|---|
| Use Case ID | UC22 |
| Use Case Name | Sync movie catalog |
| Primary Actor | Scheduler |
| Description | A scheduled job keeps the local catalog in step with TMDB. |
| Pre-conditions | • TMDB key set |
| Post-conditions | • Missing `genres` added (existing names kept); `movies` and `movie_genres` upserted |
| Main Flow | 1. `MovieSyncJob` runs `syncGenres()` + `syncCatalog()`.<br>2. Now playing, upcoming, popular, top rated × `sync-pages` are fetched.<br>3. Movies are upserted by `tmdb_id`. |
| Alternative Flow | • No key → skipped silently.<br>• TMDB error → WARN "TMDB sync skipped". |

### UC23 — Dispatch due reminders

| Field | Detail |
|---|---|
| Use Case ID | UC23 |
| Use Case Name | Dispatch due reminders |
| Primary Actor | Scheduler |
| Description | A scheduled job sends every reminder whose date has been reached. |
| Pre-conditions | — |
| Post-conditions | • Reminders SENT |
| Main Flow | 1. `ReminderJob` calls `dispatchDueReminders()`.<br>2. SCHEDULED reminders with date ≤ today are found.<br>3. UC24 runs for each one. |
| Alternative Flow | • Job error → logged. |

### UC24 — Deliver reminder notification

| Field | Detail |
|---|---|
| Use Case ID | UC24 |
| Use Case Name | Deliver reminder notification |
| Primary Actor | System |
| Description | The system delivers one due reminder through its channel. |
| Pre-conditions | • Reminder due |
| Post-conditions | • `notifications` row added; reminder SENT |
| Main Flow | 1. Publish `ReminderDueEvent`.<br>2. `NotificationSenderFactory.forChannel()` picks the sender.<br>3. Sender `send()` delivers it.<br>4. A `notifications` row is saved. |
| Alternative Flow | • EMAIL without mail server → email skipped (logged), record still saved. |
