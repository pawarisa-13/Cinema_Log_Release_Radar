# Domain Model (Class Diagram เชิงแนวคิด)

มุมมองเชิงแนวคิดของ object ทางธุรกิจ แสดงแค่ชื่อ แอตทริบิวต์หลัก และ multiplicity
คอลัมน์ทางเทคนิค (id, `created_at`, `updated_at`) อยู่ใน ER diagram ([`06-er-diagram.md`](06-er-diagram.md))
อ้างอิงจากโค้ดจริง: `code/src/main/java/com/cinemalog/domain/entity/*` และ `domain/enums/*`

ไฟล์ต้นฉบับ: [`02-domain-model.puml`](02-domain-model.puml) · รูปที่ render แล้ว: [`02-domain-model.svg`](02-domain-model.svg)

![Domain model](02-domain-model.svg)

หมายเหตุ
- `CollectionMovie` แยกเป็น concept ของตัวเอง (ไม่ใช่ many-to-many ธรรมดา) เพราะต้องเก็บว่าเพิ่มหนังเข้ามา**เมื่อไร** (`addedAt`)
- ผู้ใช้บันทึกหนังเรื่องเดิมได้มากกว่าหนึ่งครั้ง (ดูซ้ำ) ดังนั้น `WatchedMovie` จึงไม่มี unique บน user + movie ส่วน watchlist, like, หนังในคอลเลกชัน และ reminder จะ unique ต่อ user + movie (ดู ER diagram)
- `NotificationType.NOW_IN_THEATERS` ประกาศไว้ใน enum แต่ยังไม่มีโค้ดส่วนไหนสร้างค่านี้ ตอนนี้มีแค่ `RELEASE_REMINDER` (`NotificationEventListener.onReminderDue`) และ `REVIEW_ADDED` (`onDiaryEntryLogged`) ที่ถูกสร้างจริง
- ความสัมพันธ์ `Notification` → `Movie` เป็นแบบไม่บังคับ: foreign key เป็น nullable และจะถูกตั้งเป็น `NULL` ถ้าหนังถูกลบ
