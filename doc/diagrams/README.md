# Diagrams (แผนภาพ)

แผนภาพทั้งหมดเป็น UML แบบขาวดำธรรมดา แต่ละภาพมีไฟล์ต้นฉบับแบบข้อความ และไฟล์ `.svg` ที่ render แล้วซึ่ง GitHub แสดงได้ทันที ส่วนไฟล์ `.md` มีตารางและหมายเหตุที่โยงแต่ละแผนภาพเข้ากับโค้ด

| # | แผนภาพ | อ่าน | ไฟล์ต้นฉบับ | รูปภาพ |
|---|---|---|---|---|
| 01 | Use case + คำอธิบาย use case | [01-use-case.md](01-use-case.md), A4: [01-use-case.pdf](01-use-case.pdf) | `01-use-case.puml`, `src/use_cases.py` | `01-use-case.svg` |
| 02 | Domain model | [02-domain-model.md](02-domain-model.md) | `02-domain-model.puml` | `02-domain-model.svg` |
| 03 | Class diagram (3 มุมมอง + ภาพรวม) | [03-class-diagram.md](03-class-diagram.md) | `03a-…`, `03b-…`, `03c-…`, `03-class-diagram-overview.puml` | `.svg` ชื่อเดียวกัน |
| 04 | Sequence diagram (3 สถานการณ์, 4 รูป) | [04-sequence-diagrams.md](04-sequence-diagrams.md) | `04a-…` ถึง `04d-….puml` | `.svg` ชื่อเดียวกัน |
| 05 | Activity diagram (แบบ swimlane) | [05-activity-diagram.md](05-activity-diagram.md) | `src/05_activity.py` | `05-activity-diagram.svg` |
| 06 | ER diagram: schema แบบ crow's foot + มุมมองแบบ Chen | [06-er-diagram.md](06-er-diagram.md) | `06-er-diagram.puml`, `06-er-diagram-chen.puml` | `.svg` ชื่อเดียวกัน |
| 07 | Component + deployment | [07-component-deployment.md](07-component-deployment.md) | `07-component.puml`, `07-deployment.puml` | `.svg` ชื่อเดียวกัน |
| 08 | State machine (Reminder) | [08-state-diagram.md](08-state-diagram.md) | `08-state-diagram.puml` | `08-state-diagram.svg` |

`_style.iuml` เก็บสไตล์ PlantUML ที่ใช้ร่วมกัน (พื้นหลังขาว เส้นดำ ไม่มีสี)

## การสร้างรูปใหม่

ให้รันคำสั่งเหล่านี้ในโฟลเดอร์นี้ ต้องมี Java, `plantuml.jar` (เวอร์ชัน 1.2025 ขึ้นไป ซึ่งรองรับ ER แบบ Chen), Graphviz และ Python 3 ที่ติดตั้ง Pillow

```bash
java -jar plantuml.jar -tsvg *.puml            # all PlantUML diagrams
python3 src/05_activity.py                     # activity diagram
python3 src/01_use_case_doc.py                 # 01-use-case.md + src/01-use-case-doc.html
chrome --headless --no-pdf-header-footer --print-to-pdf=01-use-case.pdf src/01-use-case-doc.html
```
