# Activity diagram — from opening a movie page to a diary entry

UML activity diagram with two swimlanes (**Member** / **System**): initial node, actions, decision and merge nodes with guards, a validation loop and final nodes.
Source: [`src/05_activity.py`](src/05_activity.py) (draws the SVG with fixed coordinates so no lines cross; run `python3 src/05_activity.py` in this folder) · rendered: [`05-activity-diagram.svg`](05-activity-diagram.svg)

![Activity diagram](05-activity-diagram.svg)

| Step in the diagram | Where in the code |
|---|---|
| Load movie, 404 if unknown | `MovieQueryServiceImpl.getDetail()` → `ResourceNotFoundException` |
| Buttons enabled / disabled by release | `static/js/pages/movie.js` (Watched / Rate / Review get `off` when not released; Remind me gets `off` when released) |
| Like / Watchlist toggle | `LikeController`, `WatchlistController` (`PUT` / `DELETE`) |
| Add to collection | `components/collect-modal.js` → `CollectionController.addMovie()` / `removeMovie()` |
| Reminder, send now if due | `ReminderServiceImpl.save()` → `ReminderDispatchService.dispatchIfDue()` |
| Remove reminder | `components/remind-modal.js` ("Remove reminder", only when one exists) → `ReminderController.cancel()` → `ReminderServiceImpl.cancel()` |
| Validation loop | `DiaryEntryRequest` (`@Min(1) @Max(5)` rating) + `DiaryServiceImpl.checkDate()` (future date / not released → 400) |
| Save, remove from watchlist, notification | `DiaryServiceImpl.create()` → `WatchlistService.remove()` → `DiaryEntryLoggedEvent` → `NotificationEventListener.onDiaryEntryLogged()` |
| Edit / delete entry | `DiaryServiceImpl.update()` (notifies only when the first review is added) / `delete()` |
