# Sequence Diagram (3 สถานการณ์หลัก)

แต่ละสถานการณ์เดินตามลำดับการเรียกจริง Controller → Service → Repository → Database (และ TMDB ในกรณีที่ใช้)
`@EventListener` ของ Spring ทำงาน**แบบ synchronous** ใน thread และ transaction เดียวกับผู้ publish จึงวาด event เป็นการเรียกแบบ synchronous ธรรมดา
อ้างอิงจากโค้ดจริง: คลาสที่ตั้งชื่อไว้บนแต่ละ lifeline

## 1. บันทึกหนังที่ดูแล้ว ("you watched it!")

`components/log-modal.js` → `DiaryController.create()` → `DiaryServiceImpl.create()`

ไฟล์ต้นฉบับ: [`04a-sequence-log-watched.puml`](04a-sequence-log-watched.puml) · รูปที่ render แล้ว: [`04a-sequence-log-watched.svg`](04a-sequence-log-watched.svg)

![Sequence diagram 1](04a-sequence-log-watched.svg)

## 2. ตั้งเตือนวันฉายและได้รับการแจ้งเตือน

`ReminderController.save()` → `ReminderServiceImpl.save()`; ต่อมา `ReminderJob.run()` → `ReminderDispatchServiceImpl.dispatchDueReminders()`

แบ่งเป็นสองรูปเพราะภาพกว้างเกินไป เลขขั้นตอนนับต่อกันจากส่วนที่ 1 (ขั้นที่ 1–23) ไปส่วนที่ 2 (ขั้นที่ 24–42)

**ส่วนที่ 1 — ตั้งเตือน** ไฟล์ต้นฉบับ: [`04b-sequence-reminder-set.puml`](04b-sequence-reminder-set.puml) · รูปที่ render แล้ว: [`04b-sequence-reminder-set.svg`](04b-sequence-reminder-set.svg)

![Sequence diagram 2, part 1](04b-sequence-reminder-set.svg)

**ส่วนที่ 2 — `ReminderJob` ส่งการแจ้งเตือนที่ถึงกำหนด** ไฟล์ต้นฉบับ: [`04c-sequence-reminder-dispatch.puml`](04c-sequence-reminder-dispatch.puml) · รูปที่ render แล้ว: [`04c-sequence-reminder-dispatch.svg`](04c-sequence-reminder-dispatch.svg)

![Sequence diagram 2, part 2](04c-sequence-reminder-dispatch.svg)

## 3. ค้นหาหนังในแคตตาล็อก (พร้อมนำเข้าจาก TMDB)

`pages/catalog.js` → `MovieController.search()` → `MovieQueryServiceImpl.search()`

ไฟล์ต้นฉบับ: [`04d-sequence-search.puml`](04d-sequence-search.puml) · รูปที่ render แล้ว: [`04d-sequence-search.svg`](04d-sequence-search.svg)

![Sequence diagram 3](04d-sequence-search.svg)
