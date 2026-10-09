# ER Diagram (แผนภาพความสัมพันธ์ของข้อมูล)

schema จริงในฐานข้อมูลสร้างโดย Flyway `V1`–`V4` (`code/src/main/resources/db/migration/`) และ entity ใน `domain/entity/` แมปกับตารางเหล่านี้แบบ 1:1
`V5`–`V7` ใช้แค่ใส่ข้อมูลตั้งต้น (seed data) คีย์: PK = primary key, FK = foreign key, UK = unique ส่วน comment แสดง `NULL` (ไม่บังคับ) และ check

### Database schema (สัญลักษณ์แบบ crow's foot) — ใช้อ้างอิงแบบละเอียด

ไฟล์ต้นฉบับ: [`06-er-diagram.puml`](06-er-diagram.puml) · รูปที่ render แล้ว: [`06-er-diagram.svg`](06-er-diagram.svg)

![ER diagram, crow's foot](06-er-diagram.svg)

หมายเหตุของคอลัมน์อยู่ใน `[...]`: `NULL` = เป็นค่าว่างได้ (คอลัมน์อื่นทั้งหมดเป็น `NOT NULL`), ค่า default และข้อกำหนด `CHECK` ส่วน `UK(a, b)` หมายถึง unique constraint แบบหลายคอลัมน์

### สัญลักษณ์แบบ Chen — มุมมองสำหรับนำเสนอ

ไฟล์ต้นฉบับ: [`06-er-diagram-chen.puml`](06-er-diagram-chen.puml) · รูปที่ render แล้ว: [`06-er-diagram-chen.svg`](06-er-diagram-chen.svg)

entity เป็นสี่เหลี่ยม attribute เป็นวงรี (key attribute ขีดเส้นใต้) relationship เป็นสี่เหลี่ยมข้าวหลามตัด cardinality เขียนเป็น (min,max) ของการมีส่วนร่วมของแต่ละ entity
คอลัมน์ foreign key แสดงเป็น relationship แทน attribute ส่วนตารางเชื่อมล้วน ๆ สองตาราง (`movie_genres`, `user_favorite_genres`) คือ relationship แบบ many-to-many ชื่อ `TAGGED` และ `FAVORITE`
ชนิดข้อมูล ความยาว unique constraint และ index มีแค่ใน schema แบบ crow's foot ด้านบน

![ER diagram, Chen notation](06-er-diagram-chen.svg)

**ความสัมพันธ์**

| ประเภท | ตาราง | บังคับใช้อย่างไร |
|---|---|---|
| One-to-One | `users` — `user_profiles` | FK `user_profiles.user_id` + `UNIQUE (user_id)` ในฐานข้อมูลผู้ใช้หนึ่งคนมีโปรไฟล์ได้ 0 หรือ 1 อัน (แสดงเป็น `o\|`) แต่แอปจะสร้างทั้งสองอย่างพร้อมกันเสมอ (`AuthServiceImpl.register`) |
| One-to-Many | `users` → `watched_movies`, `watchlist_items`, `liked_movies`, `collections`, `reminders`, `notifications`; `collections` → `collection_movies`; `movies` → ตารางลูกชุดเดียวกัน | FK `ON DELETE CASCADE` (ยกเว้น `notifications.movie_id` → `SET NULL`) |
| Many-to-Many | `movies` — `genres` (`movie_genres`), `user_profiles` — `genres` (`user_favorite_genres`) | ตารางเชื่อมที่ใช้ composite PK |
| Many-to-Many ที่มีข้อมูลเพิ่ม | `collections` — `movies` (`collection_movies` มี `added_at`) | มี PK `id` ของตัวเอง + `UNIQUE (collection_id, movie_id)` |

**Unique constraint**: `uq_users_email`, `uq_user_profiles_user`, `uq_genres_name`, `uq_movies_tmdb`, `uq_watchlist_user_movie`, `uq_liked_user_movie`, `uq_collections_user_name`, `uq_collection_movie`, `uq_reminders_user_movie`
`watched_movies` **ไม่มี** unique บน (user, movie) เพราะการดูซ้ำนับเป็นรายการ Diary แยกกัน

**Index**: `idx_movies_release_date`, `idx_movies_popularity`, `idx_movies_vote_average`, `idx_movies_title_lower`, `idx_movie_genres_genre`, `idx_watched_user_date`, `idx_watched_movie`, `idx_collection_movies_movie`, `idx_reminders_due (status, reminder_date)`, `idx_notifications_user_created`
