# Design Patterns ที่ใช้ในโปรเจกต์

กลุ่ม GoF ที่เลือก: **Behavioral** (กลุ่มพฤติกรรม) ได้แก่ Strategy, Observer, Template Method, State
นอกจากนี้เรายังใช้ pattern กลุ่ม Creational (กลุ่มการสร้างออบเจกต์) คือ Factory, Builder และกลุ่ม Structural (กลุ่มโครงสร้าง) คือ Adapter ในจุดที่ช่วยแก้ปัญหาได้จริง
ทุกแถวด้านล่างระบุ class ที่ใช้ pattern นั้นไว้

## 5.1 Enterprise / Architectural Patterns (บังคับ)

| Pattern | ปัญหาที่แก้ | ไฟล์ / class |
|---|---|---|
| Layered Architecture | แยกส่วน HTTP, business rule และการเข้าถึงข้อมูลออกจากกัน และไม่มีการข้ามชั้น | `controller/*` → `service/*` → `repository/*` → `domain/*` |
| MVC | หน้าเว็บถูก render ด้วย view ของ Thymeleaf ผ่าน controller | `controller/web/*PageController`, `templates/**`, model = DTO |
| Repository | เข้าถึงข้อมูลได้โดยไม่ต้องเขียน SQL ใน service | `repository/*Repository` (Spring Data JPA), `MovieRepository` + `JpaSpecificationExecutor` |
| Service Layer | รวม business rule และ transaction ไว้ที่เดียว | interface `service/*Service` + `service/impl/*` (`@Transactional`) |
| DTO + Mapper | entity ไม่หลุดออกไปนอก service layer ทำให้ API contract คงที่ | `dto/request`, `dto/response`, `mapper/*Mapper` |
| Dependency Injection | ทุก class รับ dependency ผ่าน constructor เท่านั้น | ทุก `@Service`, `@Component`, `@RestController` |

## 5.2 GoF Patterns

| Pattern | กลุ่ม | ปัญหาที่แก้ | ไฟล์ / class | ผู้รับผิดชอบ |
|---|---|---|---|---|
| **Strategy** | Behavioral | การแจ้งเตือนส่งได้ทั้งในแอปและทางอีเมล โดยฝั่งที่เรียกใช้ไม่ต้องสนใจว่าส่งช่องทางไหน | `NotificationSender` (strategy), `InAppNotificationSender`, `EmailNotificationSender` | Pawarisa |
| **Observer** | Behavioral | เมื่อถึงเวลาแจ้งเตือนหรือมีการบันทึกรีวิว ระบบต้องสร้าง notification ได้ โดยโค้ดส่วน reminder/diary ไม่ต้องพึ่งพาส่วน notification | `ReminderDispatchServiceImpl` และ `DiaryServiceImpl` publish `ReminderDueEvent` / `DiaryEntryLoggedEvent` ส่วน `NotificationEventListener` เป็นผู้ subscribe (`@EventListener`) | Pawarisa (+ Nunthaporn ฝั่ง publish) |
| **Template Method** | Behavioral | ทุกช่องทางมีขั้นตอนเหมือนกัน (โหลด user → ส่ง → บันทึก record) ต่างกันแค่วิธีส่ง | `AbstractNotificationSender.send()` เป็น `final` โดย subclass ต้อง implement `deliver()` และ override `recordText()` ได้ | Pawarisa |
| **State** | Behavioral | reminder มีสถานะ SCHEDULED, SENT หรือ CANCELLED และสิ่งที่ทำได้ขึ้นอยู่กับสถานะนั้น | enum `ReminderStatus` (แต่ละค่าคงที่ implement `send()` เอง) ถูกใช้โดย `Reminder.markSent()` / `cancel()` / `reschedule()` | Pawarisa |
| **Factory** | Creational | เลือก sender ให้ตรงกับช่องทางได้โดยไม่ต้องใช้ `if/else` | `NotificationSenderFactory.forChannel()` | Pawarisa |
| **Builder** | Creational | หน้า catalog มีตัวกรองที่ไม่บังคับถึง 7 ตัว ถ้าสร้าง query ด้วย if ซ้อนกันจะอ่านยากมาก | `MovieSpecificationBuilder` (`text().genre().year()… .build()`) ใช้ใน `MovieQueryServiceImpl.search()` | Prayfon |
| **Adapter** | Structural | ต้องไม่ให้รูปแบบ JSON ของ TMDB (snake_case, วันที่เป็น string, รายการ credits) หลุดเข้ามาใน model ของเรา | `MovieCatalogSource` (target) ← `TmdbMovieCatalogAdapter` (adapter) → `TmdbClient` (adaptee) | Prayfon |
| **Singleton** | Creational | ใช้ `Clock`, `RestClient` และ `PasswordEncoder` ร่วมกันเพียงตัวเดียว | method `@Bean` ใน `ClockConfig`, `TmdbClientConfig`, `SecurityConfig` (singleton scope ของ Spring) | Pawarisa / Prayfon |

## Class Diagram (ตำแหน่งของแต่ละ pattern)

```mermaid
classDiagram
    direction LR

    class NotificationSender {
        <<interface>>
        +channel() NotificationChannel
        +send(NotificationMessage)
    }
    class AbstractNotificationSender {
        <<abstract>>
        +send(NotificationMessage) final
        #deliver(User, NotificationMessage)*
        #recordText(NotificationMessage) String
    }
    class InAppNotificationSender
    class EmailNotificationSender
    class NotificationSenderFactory {
        +forChannel(NotificationChannel) NotificationSender
    }
    class NotificationEventListener {
        +onReminderDue(ReminderDueEvent)
        +onDiaryEntryLogged(DiaryEntryLoggedEvent)
    }
    class ReminderDispatchServiceImpl {
        +dispatchDueReminders() int
        +dispatchIfDue(Reminder) boolean
    }
    class DiaryServiceImpl
    class Reminder {
        -status ReminderStatus
        +markSent(Instant)
        +cancel()
        +reschedule(int, NotificationChannel)
    }
    class ReminderStatus {
        <<enumeration>>
        SCHEDULED
        SENT
        CANCELLED
        +send() ReminderStatus
        +cancel() ReminderStatus
    }

    NotificationSender <|.. AbstractNotificationSender : Strategy
    AbstractNotificationSender <|-- InAppNotificationSender : Template Method
    AbstractNotificationSender <|-- EmailNotificationSender : Template Method
    NotificationSenderFactory o-- NotificationSender : Factory
    NotificationEventListener --> NotificationSenderFactory
    ReminderDispatchServiceImpl ..> NotificationEventListener : ReminderDueEvent (Observer)
    DiaryServiceImpl ..> NotificationEventListener : DiaryEntryLoggedEvent (Observer)
    Reminder --> ReminderStatus : State

    class MovieCatalogSource {
        <<interface>>
        +fetchList(MovieListType, int)
        +search(String)
        +fetchDetails(long)
    }
    class TmdbMovieCatalogAdapter
    class TmdbClient
    class MovieSyncServiceImpl
    class MovieQueryServiceImpl
    class MovieSpecificationBuilder {
        +text(String)
        +genre(Integer)
        +year(Integer)
        +minRating(Double)
        +build() Specification
    }
    MovieCatalogSource <|.. TmdbMovieCatalogAdapter : Adapter
    TmdbMovieCatalogAdapter --> TmdbClient : adaptee
    MovieSyncServiceImpl --> MovieCatalogSource
    MovieQueryServiceImpl ..> MovieSpecificationBuilder : Builder
```
