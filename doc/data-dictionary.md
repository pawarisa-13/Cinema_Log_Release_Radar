# Data Dictionary (พจนานุกรมข้อมูล)

ใช้ PostgreSQL 17 (Supabase) โดยสร้าง schema ด้วย Flyway (`code/src/main/resources/db/migration`) เวลาทุกช่องเก็บเป็น `TIMESTAMPTZ` (UTC)

## users — บัญชีสำหรับเข้าสู่ระบบ
| คอลัมน์ | ชนิดข้อมูล | Null ได้ไหม | คีย์ / ข้อกำหนด | คำอธิบาย |
|---|---|---|---|---|
| id | BIGSERIAL | ไม่ได้ | PK | |
| email | VARCHAR(255) | ไม่ได้ | UNIQUE `uq_users_email` | อีเมลที่ใช้ล็อกอิน เก็บเป็นตัวพิมพ์เล็ก |
| password_hash | VARCHAR(100) | ไม่ได้ | | ค่า hash แบบ BCrypt |
| created_at | TIMESTAMPTZ | ไม่ได้ | default now() | |

## user_profiles — โปรไฟล์สาธารณะ (1:1 กับ users)
| คอลัมน์ | ชนิดข้อมูล | Null ได้ไหม | คีย์ / ข้อกำหนด | คำอธิบาย |
|---|---|---|---|---|
| id | BIGSERIAL | ไม่ได้ | PK | |
| user_id | BIGINT | ไม่ได้ | FK → users.id ON DELETE CASCADE, UNIQUE (ทำให้เป็น 1:1) | |
| display_name | VARCHAR(60) | ไม่ได้ | | |
| bio | VARCHAR(200) | ได้ | | |
| avatar_style | VARCHAR(20) | ไม่ได้ | default 'BUN' | BUN / BOB / CAP / CURLY |
| avatar_color | VARCHAR(20) | ไม่ได้ | default 'PINK' | PINK / BLUE / GREEN / YELLOW / LAVENDER |
| updated_at | TIMESTAMPTZ | ไม่ได้ | | |

## user_favorite_genres — โปรไฟล์กับแนวหนัง (N:M)
| คอลัมน์ | ชนิดข้อมูล | คีย์ |
|---|---|---|
| profile_id | BIGINT | PK, FK → user_profiles.id ON DELETE CASCADE |
| genre_id | INTEGER | PK, FK → genres.id ON DELETE CASCADE |

## genres — แนวหนัง
| คอลัมน์ | ชนิดข้อมูล | Null ได้ไหม | คีย์ | คำอธิบาย |
|---|---|---|---|---|
| id | INTEGER | ไม่ได้ | PK | genre id ของ TMDB (27 = Horror) |
| name | VARCHAR(50) | ไม่ได้ | UNIQUE | |

## movies — ข้อมูลหนังที่ cache ไว้จาก TMDB
| คอลัมน์ | ชนิดข้อมูล | Null ได้ไหม | คีย์ / index | คำอธิบาย |
|---|---|---|---|---|
| id | BIGSERIAL | ไม่ได้ | PK | |
| tmdb_id | BIGINT | ไม่ได้ | UNIQUE `uq_movies_tmdb` | ใช้ตอน upsert |
| title | VARCHAR(300) | ไม่ได้ | index บน lower(title) | |
| original_title | VARCHAR(300) | ได้ | | |
| overview | TEXT | ได้ | | |
| poster_path / backdrop_path | VARCHAR(200) | ได้ | | path ของรูปภาพจาก TMDB |
| release_date | DATE | ได้ | index | ใช้แบ่งหนัง "กำลังฉาย" / "เร็ว ๆ นี้" |
| runtime | INTEGER | ได้ | | ความยาวเป็นนาที ได้มาตอนเรียกข้อมูลรายละเอียด |
| vote_average | DOUBLE PRECISION | ไม่ได้ | index (desc) | คะแนนจาก TMDB 0–10 |
| popularity | DOUBLE PRECISION | ไม่ได้ | index (desc) | ใช้เรียงลำดับเป็นค่าเริ่มต้น |
| original_language | VARCHAR(10) | ได้ | | รหัส ISO 639-1 |
| director | VARCHAR(200) | ได้ | | |
| cast_names | VARCHAR(500) | ได้ | | รายชื่อนักแสดงหลัก คั่นด้วยจุลภาค |
| details_fetched_at | TIMESTAMPTZ | ได้ | | ดึงรายละเอียดใหม่เมื่อผ่านไป 7 วัน |
| created_at | TIMESTAMPTZ | ไม่ได้ | | ใช้เรียงแบบ "เพิ่มล่าสุด" |

## movie_genres — หนังกับแนวหนัง (N:M)
| คอลัมน์ | ชนิดข้อมูล | คีย์ |
|---|---|---|
| movie_id | BIGINT | PK, FK → movies.id ON DELETE CASCADE |
| genre_id | INTEGER | PK, FK → genres.id ON DELETE CASCADE, index |

## watched_movies — รายการบันทึกใน Diary (users 1:N, movies 1:N)
| คอลัมน์ | ชนิดข้อมูล | Null ได้ไหม | คีย์ / ข้อกำหนด | คำอธิบาย |
|---|---|---|---|---|
| id | BIGSERIAL | ไม่ได้ | PK | |
| user_id | BIGINT | ไม่ได้ | FK → users ON DELETE CASCADE; index (user_id, watched_date desc) | |
| movie_id | BIGINT | ไม่ได้ | FK → movies ON DELETE CASCADE; index | |
| watched_date | DATE | ไม่ได้ | | |
| rating | INTEGER | ได้ | CHECK 1–5 | |
| review | VARCHAR(1000) | ได้ | | |
| place | VARCHAR(10) | ไม่ได้ | CHECK CINEMA/HOME/OTHER | |
| created_at, updated_at | TIMESTAMPTZ | ไม่ได้ | | |

## watchlist_items / liked_movies (users 1:N)
| คอลัมน์ | ชนิดข้อมูล | คีย์ / ข้อกำหนด |
|---|---|---|
| id | BIGSERIAL | PK |
| user_id | BIGINT | FK → users ON DELETE CASCADE |
| movie_id | BIGINT | FK → movies ON DELETE CASCADE |
| added_at / liked_at | TIMESTAMPTZ | |
| | | UNIQUE (user_id, movie_id) — หนังหนึ่งเรื่องอยู่ในรายการได้แค่ครั้งเดียว |

## collections (users 1:N) และ collection_movies (collections N:M movies)
| ตาราง.คอลัมน์ | ชนิดข้อมูล | คีย์ / ข้อกำหนด |
|---|---|---|
| collections.id | BIGSERIAL | PK |
| collections.user_id | BIGINT | FK → users ON DELETE CASCADE |
| collections.name | VARCHAR(60) | UNIQUE (user_id, name) |
| collections.description | VARCHAR(200) | |
| collection_movies.id | BIGSERIAL | PK |
| collection_movies.collection_id | BIGINT | FK → collections ON DELETE CASCADE |
| collection_movies.movie_id | BIGINT | FK → movies ON DELETE CASCADE, index |
| collection_movies.added_at | TIMESTAMPTZ | UNIQUE (collection_id, movie_id) |

## reminders — การแจ้งเตือนวันฉาย (users 1:N, movies 1:N)
| คอลัมน์ | ชนิดข้อมูล | ข้อกำหนด | คำอธิบาย |
|---|---|---|---|
| id | BIGSERIAL | PK | |
| user_id / movie_id | BIGINT | FK ON DELETE CASCADE, UNIQUE (user_id, movie_id) | หนังหนึ่งเรื่องตั้งเตือนได้หนึ่งครั้ง |
| offset_days | INTEGER | CHECK IN (0,1,3,7) | จำนวนวันก่อนวันฉาย |
| reminder_date | DATE | index (status, reminder_date) | release_date − offset |
| channel | VARCHAR(10) | CHECK IN_APP/EMAIL | ใช้เป็นตัวเลือก Strategy |
| status | VARCHAR(12) | CHECK SCHEDULED/SENT/CANCELLED | ใช้กับ State pattern |
| created_at, sent_at | TIMESTAMPTZ | | |

## notifications — การแจ้งเตือนในระบบ (users 1:N)
| คอลัมน์ | ชนิดข้อมูล | ข้อกำหนด | คำอธิบาย |
|---|---|---|---|
| id | BIGSERIAL | PK | |
| user_id | BIGINT | FK → users ON DELETE CASCADE; index (user_id, created_at desc) | |
| movie_id | BIGINT | FK → movies ON DELETE SET NULL | ไม่บังคับ (ว่างได้) |
| type | VARCHAR(20) | | RELEASE_REMINDER / NOW_IN_THEATERS / REVIEW_ADDED |
| channel | VARCHAR(10) | | |
| message | VARCHAR(300) | | |
| is_read | BOOLEAN | default false | |
| created_at | TIMESTAMPTZ | | |

## การตัดสินใจเรื่อง Cascade และ Fetch
- **ON DELETE CASCADE** จาก users ไปยังทุกตารางที่ผู้ใช้เป็นเจ้าของ: เมื่อลบบัญชีจะไม่มีข้อมูลค้างอยู่ (orphan)
- **ON DELETE SET NULL** ที่ notifications.movie_id: ถึงหนังจะถูกลบ ก็ยังอ่านการแจ้งเตือนได้อยู่
- JPA: `User.profile` เป็น EAGER + cascade ALL (เพราะแสดงคู่กันเสมอ); `@ManyToOne` ทุกตัวเป็น LAZY และใช้ `@EntityGraph` กับ query ที่ต้องใช้ข้อมูลหนัง; `Movie.genres` เป็น LAZY + `@BatchSize(50)` เพื่อกันปัญหา N+1 ในหน้า catalog; `MovieCollection.items` ใช้ cascade ALL + orphanRemoval เพื่อให้เอาหนังออกจากคอลเลกชันแล้วแถวนั้นถูกลบด้วย
