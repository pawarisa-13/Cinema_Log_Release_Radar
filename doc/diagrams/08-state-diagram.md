# State diagram — Reminder

Implemented by the `ReminderStatus` enum (State pattern) and used by `Reminder` (`domain/entity/Reminder.java`).
Each constant decides what `send()`, `cancel()` and `reschedule()` return.

Source: [`08-state-diagram.puml`](08-state-diagram.puml) · rendered: [`08-state-diagram.svg`](08-state-diagram.svg)

![State machine diagram](08-state-diagram.svg)

| Transition | Code |
|---|---|
| create → SCHEDULED | `Reminder` constructor calls `reschedule(...)`; `ReminderServiceImpl.save()` when no reminder exists |
| SCHEDULED → SENT | `ReminderDispatchServiceImpl` → `reminder.markSent(now)` → `status.send()`; only reminders with `isDueOn(today)` (SCHEDULED and date ≤ today) are sent |
| any → SCHEDULED | `ReminderServiceImpl.save()` on an existing reminder → `existing.reschedule(offsetDays, channel)` (also recalculates the date and clears `sentAt`) |
| any → CANCELLED | `ReminderServiceImpl.cancel()` → `reminder.cancel()` (finds the reminder in any state, so cancelling twice keeps CANCELLED) |
| errors | `SENT.send()` / `CANCELLED.send()` throw `BusinessRuleException`; no transition happens |

There is no final state: any reminder can be rescheduled again, and the app deletes a reminder row only through `ON DELETE CASCADE` when its user or movie is deleted.
