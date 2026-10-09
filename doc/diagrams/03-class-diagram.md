# Class Diagram (เลเยอร์ + ตำแหน่งของ Design Pattern)

แสดงแต่ละโมดูลเป็นแนวตั้งผ่านทุกเลเยอร์ พร้อมคลาสที่ใช้ design pattern
แสดงเฉพาะแอตทริบิวต์/เมธอดที่สำคัญ รายละเอียดของแต่ละ pattern ดูที่ [`../design-patterns.md`](../design-patterns.md)
อ้างอิงจากโค้ดจริง: `code/src/main/java/com/cinemalog/**`

| Pattern | กลุ่ม | อยู่ที่ไหน (คลาส / ไฟล์) |
|---|---|---|
| Strategy | Behavioral | `NotificationSender` (interface) ← `InAppNotificationSender`, `EmailNotificationSender` — `service/notification/` |
| Template Method | Behavioral | `AbstractNotificationSender.send()` เป็น `final` คลาสลูก implement `deliver()` และ override `recordText()` ได้ |
| Observer | Behavioral | `DiaryServiceImpl`, `ReminderDispatchServiceImpl` publish `DiaryEntryLoggedEvent` / `ReminderDueEvent` ผ่าน `ApplicationEventPublisher` ของ Spring; `NotificationEventListener` รับ event ด้วย `@EventListener` |
| State | Behavioral | enum `ReminderStatus` — แต่ละค่าคงที่ implement `send()` เอง; ถูกใช้โดย `Reminder.markSent()`, `cancel()`, `reschedule()` |
| Factory | Creational | `NotificationSenderFactory.forChannel(channel)` |
| Builder | Creational | `MovieSpecificationBuilder` (`create().text().genre()…build()`) ใช้ใน `MovieQueryServiceImpl.search()` |
| Singleton | Creational | เมธอด `@Bean` ได้แก่ `ClockConfig.clock()`, `TmdbClientConfig.tmdbRestClient()`, `SecurityConfig.passwordEncoder()` (scope แบบ singleton ของ Spring) |
| Adapter | Structural | `MovieCatalogSource` (target) ← `TmdbMovieCatalogAdapter` (adapter) → `TmdbClient` (adaptee) |

แผนภาพเต็มถูกแบ่งเป็นสามมุมมองเพื่อให้ตัวหนังสืออ่านออก ทุกคลาสและทุกความสัมพันธ์จะปรากฏอย่างน้อยในหนึ่งมุมมอง
คลาสที่เชื่อมสองมุมมองเข้าด้วยกัน (เช่น `ReminderDueEvent`, `ApplicationEventPublisher`, `CurrentUserProvider`, `MovieRepository`) จะแสดงซ้ำในทั้งสองภาพ

### 3a — แคตตาล็อกหนัง (Adapter, Builder)

ไฟล์ต้นฉบับ: [`03a-class-movie-catalog.puml`](03a-class-movie-catalog.puml) · รูปที่ render แล้ว: [`03a-class-movie-catalog.svg`](03a-class-movie-catalog.svg)

![Class diagram 1/3](03a-class-movie-catalog.svg)

### 3b — Diary, คอลเลกชัน และการแจ้งเตือน (Observer, Factory, Strategy, Template Method)

ไฟล์ต้นฉบับ: [`03b-class-diary-notification.puml`](03b-class-diary-notification.puml) · รูปที่ render แล้ว: [`03b-class-diary-notification.svg`](03b-class-diary-notification.svg)

![Class diagram 2/3](03b-class-diary-notification.svg)

### 3c — Reminder (State) และคลาสที่ใช้ร่วมกันทั้งระบบ (Singleton bean, exception handler)

ไฟล์ต้นฉบับ: [`03c-class-reminder-config.puml`](03c-class-reminder-config.puml) · รูปที่ render แล้ว: [`03c-class-reminder-config.svg`](03c-class-reminder-config.svg)

![Class diagram 3/3](03c-class-reminder-config.svg)

### ภาพรวม (ทุกคลาสในรูปเดียว)

ไฟล์ต้นฉบับ: [`03-class-diagram-overview.puml`](03-class-diagram-overview.puml) · รูปที่ render แล้ว: [`03-class-diagram-overview.svg`](03-class-diagram-overview.svg) — เปิดไฟล์ SVG แล้วซูมดู ถ้าจะอ่านรายละเอียดให้ใช้ 3a–3c

![Class diagram overview](03-class-diagram-overview.svg)

`GlobalExceptionHandler` (`@RestControllerAdvice` สำหรับ `controller.api`) แปลงคลาสลูกของ `ApiException` ทุกตัวให้เป็น JSON `ErrorResponse` แบบเดียวกัน: `ResourceNotFoundException` 404, `BusinessRuleException` 400, `DuplicateResourceException` 409, `UnauthorizedException` 401, `ExternalServiceException` 503
