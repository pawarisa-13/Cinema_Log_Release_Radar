# การวิเคราะห์หลัก SOLID

เอกสารนี้ระบุว่าแต่ละหลักการปรากฏอยู่ตรงไหนในโค้ด พร้อมเหตุผล เลขบรรทัดอ้างอิงตามไฟล์ที่ commit ไว้

> ตรวจตามกฎแล้ว: ไม่มีการทำ field injection (`@Autowired` บน field) ใน `code/` เลย ทุก class รับ dependency ผ่าน constructor ของตัวเอง

## S — Single Responsibility (หลักความรับผิดชอบเดียว)

| ตำแหน่ง | สิ่งที่แสดงให้เห็น | ผู้รับผิดชอบ |
|---|---|---|
| `code/src/main/java/com/cinemalog/service/external/tmdb/TmdbClient.java` L18 | TmdbClient ทำหน้าที่แค่เรียก HTTP ไปที่ TMDB เท่านั้น ส่วนการแปลง JSON ของ TMDB ให้เป็น model ของเราเป็นอีกงานหนึ่ง ซึ่ง TmdbMovieCatalogAdapter เป็นคนทำ | Prayfon |
| `code/src/main/java/com/cinemalog/service/impl/DiaryServiceImpl.java` L118 | business rule (ห้ามใส่วันที่ในอนาคต, หนังต้องออกฉายแล้ว) อยู่ใน service การ validate field อยู่ใน annotation ของ DTO (DiaryEntryRequest) และการบันทึกข้อมูลอยู่ใน WatchedMovieRepository | Nunthaporn |
| `code/src/main/java/com/cinemalog/service/notification/NotificationEventListener.java` L13 | listener มีหน้าที่แค่แปลง event ให้เป็น notification ส่วนโค้ดของ reminder ไม่ต้องรู้ว่าข้อความถูกส่งออกไปอย่างไร | Pawarisa |
| `code/src/main/java/com/cinemalog/mapper/MovieMapper.java` L19 | การแปลง Entity → DTO แยกเป็น class ของตัวเอง ทำให้ controller และ service ไม่ต้องสร้างรูปแบบ JSON เอง | Prayfon |

## O — Open/Closed (หลักเปิดให้ขยาย ปิดไม่ให้แก้ไข)

| ตำแหน่ง | สิ่งที่แสดงให้เห็น | ผู้รับผิดชอบ |
|---|---|---|
| `code/src/main/java/com/cinemalog/service/notification/NotificationSender.java` L5 | ถ้าจะเพิ่มช่องทางใหม่ (เช่น LINE) ก็แค่สร้าง class ใหม่ที่ implement NotificationSender แล้ว factory จะเลือกใช้ให้อัตโนมัติ โดยไม่ต้องแก้โค้ดเดิม | Pawarisa |
| `code/src/main/java/com/cinemalog/service/notification/NotificationSenderFactory.java` L16 | factory รับ sender bean ทุกตัวมาจาก Spring จึงไม่ต้องมี switch หรือ if/else ตามช่องทาง | Pawarisa |
| `code/src/main/java/com/cinemalog/exception/ApiException.java` L5 | error แต่ละประเภทเก็บ HTTP status ของตัวเองไว้ เวลาเพิ่ม error class ใหม่จึงไม่ต้องแก้ GlobalExceptionHandler | Pawarisa |
| `code/src/main/java/com/cinemalog/domain/enums/MovieSortOption.java` L7 | ตัวเลือกการเรียงแต่ละแบบเก็บ Sort ของตัวเองไว้ ถ้าจะเพิ่มตัวเลือกก็แค่เพิ่มค่าคงที่ใน enum หนึ่งตัว โดยไม่ต้องมี switch ใน service | Prayfon |

## L — Liskov Substitution (หลักการแทนที่ของ Liskov)

| ตำแหน่ง | สิ่งที่แสดงให้เห็น | ผู้รับผิดชอบ |
|---|---|---|
| `code/src/main/java/com/cinemalog/service/notification/AbstractNotificationSender.java` L25 | InAppNotificationSender และ EmailNotificationSender ใช้แทนกันได้ในทุกจุดที่ต้องการ NotificationSender ทั้งสองตัวบันทึก record ทุกครั้ง และไม่มีตัวไหน throw UnsupportedOperationException | Pawarisa |
| `code/src/main/java/com/cinemalog/service/external/MovieCatalogSource.java` L10 | แหล่งข้อมูล catalog ทุกตัว (TMDB adapter หรือ mock ในเทสต์) ทำตาม contract เดียวกัน คือถ้า isAvailable() เป็น false แปลว่า "ไม่มีข้อมูล" ไม่ใช่ระบบพัง | Prayfon |
| `code/src/main/java/com/cinemalog/domain/enums/ReminderStatus.java` L43 | ทุกสถานะ implement method ชุดเดียวกัน การเปลี่ยนสถานะที่ไม่ถูกต้องจะแจ้งเป็น BusinessRuleException (error 400 ปกติ) ไม่ใช่ UnsupportedOperationException | Pawarisa |

## I — Interface Segregation (หลักการแยก interface)

| ตำแหน่ง | สิ่งที่แสดงให้เห็น | ผู้รับผิดชอบ |
|---|---|---|
| `code/src/main/java/com/cinemalog/service/DiaryQueryService.java` L12 | การอ่าน diary กับการแก้ไข diary แยกเป็นคนละ interface (DiaryQueryService / DiaryCommandService) ทำให้ client ที่อ่านอย่างเดียวไม่เห็น method สำหรับเขียน | Nunthaporn |
| `code/src/main/java/com/cinemalog/service/ReminderDispatchService.java` L5 | การส่ง reminder ที่ถึงกำหนดแยกออกจาก ReminderService (สิ่งที่ผู้ใช้ทำ) มีแค่ scheduler ที่ต้องใช้ และไม่มี controller ตัวไหนเห็น | Pawarisa |
| `code/src/main/java/com/cinemalog/service/AuthService.java` L6 | การสมัครสมาชิก (AuthService) แยกออกจากการแก้ไขโปรไฟล์ (UserProfileService) | Pawarisa |
| `code/src/main/java/com/cinemalog/service/MovieDiscoveryService.java` L8 | ชั้นแสดงหนังและการแนะนำหนังแยกออกจากการค้นหา/ดูรายละเอียด (MovieQueryService) และแยกจากการ sync ข้อมูลกับ TMDB (MovieSyncService) | Prayfon |

## D — Dependency Inversion (หลักการกลับทิศทางการพึ่งพา)

| ตำแหน่ง | สิ่งที่แสดงให้เห็น | ผู้รับผิดชอบ |
|---|---|---|
| `code/src/main/java/com/cinemalog/service/impl/MovieQueryServiceImpl.java` L38 | service พึ่งพา interface MovieCatalogSource และ MovieSyncService ไม่ได้พึ่งพา TmdbClient โดยตรง และทุกอย่างถูก inject ผ่าน constructor เท่านั้น | Prayfon |
| `code/src/main/java/com/cinemalog/controller/api/DiaryController.java` L47 | controller พึ่งพา interface ของ service และ CurrentUserProvider (ไม่ได้พึ่งพา class ของ Spring Security) โดยใช้ constructor injection | Nunthaporn |
| `code/src/main/java/com/cinemalog/config/ClockConfig.java` L13 | service ใช้ Clock ที่ถูก inject เข้ามาแทนการเรียก LocalDate.now() ทำให้เทสต์สามารถกำหนดวันที่ตายตัวได้ | Pawarisa |
| `code/src/main/java/com/cinemalog/service/impl/LibraryServiceImpl.java` L36 | ส่วนจัดการสถานะของคลังหนังใช้ interface ReminderService และ NotificationService จากโมดูลอื่น แทนที่จะใช้ implementation ของมันโดยตรง | Nunthaporn |
