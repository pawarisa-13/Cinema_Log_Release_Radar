"""สร้าง 01-use-case.md (ตาราง) และ src/01-use-case-doc.html (ไฟล์ต้นฉบับ A4 สำหรับพิมพ์เป็น 01-use-case.pdf)
รันจาก doc/diagrams:  python3 src/01_use_case_doc.py"""
import os, re, sys, html
sys.path.insert(0, os.path.dirname(__file__))
from use_cases import UC

ACTORS = [
 ("Visitor", "ผู้ที่ยังไม่ได้ล็อกอิน เข้าได้เฉพาะ `/login`, `/register` และ public API (`SecurityConfig`)"),
 ("Member", "Visitor ที่ล็อกอินแล้ว (generalization) ทุกหน้าและ `/api/v1/users/me/**` ต้องมี session"),
 ("Scheduler", "งาน Spring `@Scheduled` คือ `MovieSyncJob` และ `ReminderJob` แต่ละงานจะรันหนึ่งครั้งตอน `ApplicationReadyEvent` ด้วย"),
 ("TMDB API, SMTP server", "ระบบภายนอก การส่งอีเมลเป็นตัวเลือกเสริม (`spring.mail.host` ปล่อยว่างได้)"),
]
REL = [
 ("UC01 «include» UC02", "หลังสมัครสมาชิกสำเร็จ หน้าเว็บจะส่งฟอร์มล็อกอินที่ซ่อนไว้ทุกครั้ง", "`templates/auth/register.html` (`#auto-login`), `pages/auth.js`"),
 ("UC04 «extend» UC02", "เฉพาะเมื่อโปรไฟล์ยังไม่มีแนวหนังที่ชอบ", "`OnboardingAwareSuccessHandler` → `UserProfileService.needsOnboarding()`"),
 ("UC07 «extend» UC06", "เฉพาะการค้นหาด้วยข้อความยาว 2 ตัวอักษรขึ้นไป ที่หน้า 0 และมี TMDB key", "`MovieQueryServiceImpl.search()` → `MovieSyncService.importSearchResults()`"),
 ("UC08 «include» UC10", "หน้าหนังจะโหลดรีวิวของหนังเรื่องนั้นทุกครั้ง", "`pages/movie.js` → `GET /api/v1/movies/{id}/reviews`"),
 ("UC09 «extend» UC08", "เฉพาะเมื่อรายละเอียดยังไม่มีหรือเก่าเกินไป และตั้งค่า key แล้ว", "`MovieQueryServiceImpl.getDetail()` → `Movie.needsDetails()` → `refreshDetails()`"),
 ("UC23 «include» UC24", "การเตือนทุกรายการที่ถึงกำหนดจะถูกส่ง", "`ReminderDispatchServiceImpl.dispatchDueReminders()`"),
 ("UC24 «extend» UC18", "เฉพาะเมื่อถึงวันเตือนแล้ว", "`ReminderServiceImpl.save()` → `dispatchIfDue()`"),
]

def md_cell(t): return t.replace("|", "\\|")
def lst_md(items, numbered):
    if items == ["—"]: return "—"
    return "<br>".join((f"{i+1}. " if numbered else "• ") + md_cell(x) for i, x in enumerate(items))

# ---------- Markdown ----------
md = ["# Use case diagram และคำอธิบาย Use Case", "",
      "UML use case diagram (PlantUML) ไฟล์ต้นฉบับ: [`01-use-case.puml`](01-use-case.puml) · ภาพที่ render แล้ว: [`01-use-case.svg`](01-use-case.svg) · "
      "เอกสาร A4 สำหรับพิมพ์ (แผนภาพ + ตารางคำอธิบาย): [`01-use-case.pdf`](01-use-case.pdf)", "",
      "![Use case diagram](01-use-case.svg)", "", "## Actor", ""]
md += [f"- **{a}**: {d}" for a, d in ACTORS]
md += ["", "## ความสัมพันธ์ «include» / «extend» ที่ใช้ในแผนภาพ", "", "| ความสัมพันธ์ (Relation) | เหตุผล | โค้ดที่เกี่ยวข้อง |", "|---|---|---|"]
md += [f"| {a} | {b} | {c} |" for a, b, c in REL]
md += ["", "## คำอธิบาย Use Case", ""]
for u in UC:
    md += [f"### {u['id']} — {u['name']}", "", "| หัวข้อ | รายละเอียด |", "|---|---|",
           f"| รหัส (Use Case ID) | {u['id']} |", f"| ชื่อ Use Case | {md_cell(u['name'])} |", f"| Actor หลัก | {md_cell(u['actor'])} |",
           f"| คำอธิบาย | {md_cell(u['desc'])} |", f"| เงื่อนไขก่อนเริ่ม (Pre-conditions) | {lst_md(u['pre'], False)} |",
           f"| ผลลัพธ์หลังจบ (Post-conditions) | {lst_md(u['post'], False)} |", f"| ขั้นตอนหลัก (Main Flow) | {lst_md(u['main'], True)} |",
           f"| ขั้นตอนทางเลือก (Alternative Flow) | {lst_md(u['alt'], False)} |", ""]
open("01-use-case.md", "w", encoding="utf-8").write("\n".join(md))

# ---------- HTML for A4 PDF ----------
def inline(t):
    t = html.escape(t)
    return re.sub(r"`([^`]+)`", r"<code>\1</code>", t)
def lst_html(items, numbered):
    if items == ["—"]: return "—"
    tag = "ol" if numbered else "ul"
    return f"<{tag}>" + "".join(f"<li>{inline(x)}</li>" for x in items) + f"</{tag}>"
svg = '<img src="../01-use-case.svg" alt="Use case diagram">'
rows = []
for u in UC:
    rows.append(f"""<table class="uc">
<tr><th>รหัส <span class="nw">(Use Case ID)</span></th><td><b>{u['id']}</b></td></tr>
<tr><th>ชื่อ Use Case</th><td>{inline(u['name'])}</td></tr>
<tr><th>Actor หลัก</th><td>{inline(u['actor'])}</td></tr>
<tr><th>คำอธิบาย</th><td>{inline(u['desc'])}</td></tr>
<tr><th>เงื่อนไขก่อนเริ่ม <span class="nw">(Pre-conditions)</span></th><td>{lst_html(u['pre'], False)}</td></tr>
<tr><th>ผลลัพธ์หลังจบ <span class="nw">(Post-conditions)</span></th><td>{lst_html(u['post'], False)}</td></tr>
<tr><th>ขั้นตอนหลัก <span class="nw">(Main Flow)</span></th><td>{lst_html(u['main'], True)}</td></tr>
<tr><th>ขั้นตอนทางเลือก <span class="nw">(Alternative Flow)</span></th><td>{lst_html(u['alt'], False)}</td></tr>
</table>""")
doc = f"""<!doctype html><html lang="th"><head><meta charset="utf-8"><title>Use Case Diagram และคำอธิบาย Use Case</title>
<style>
@page {{ size: A4; margin: 16mm 15mm 16mm 15mm; }}
body {{ font-family: "Sarabun", "TH Sarabun New", "Tahoma", sans-serif; font-size: 10.5pt; line-height: 1.35; color: #000; }}
h1 {{ font-size: 16pt; margin: 0 0 2mm; }} h2 {{ font-size: 12.5pt; margin: 6mm 0 2mm; border-bottom: 1px solid #000; padding-bottom: 1mm; }}
p.sub {{ margin: 0 0 4mm; font-size: 9.5pt; }}
.fig {{ text-align: center; }} .fig img {{ max-width: 100%; max-height: 222mm; }}
.cap {{ text-align: center; font-size: 9pt; margin-top: 2mm; }}
table {{ border-collapse: collapse; width: 100%; }}
th, td {{ border: 1px solid #000; padding: 1.4mm 2mm; vertical-align: top; text-align: left; }}
table.uc {{ margin: 0 0 5mm; page-break-inside: avoid; }}
table.uc th {{ width: 40mm; font-weight: bold; background: #fff; }}
table.rel th {{ background: #fff; }}
ul, ol {{ margin: 0; padding-left: 5mm; }} li {{ margin: 0.3mm 0; }}
td.code {{ overflow-wrap: anywhere; }}
code {{ font-family: "Courier New", monospace; font-size: 8.8pt; }}
.pb {{ page-break-before: always; }}
.nw {{ white-space: nowrap; }}
</style></head><body>
<h1>Cinema Log &amp; Release Radar — Use Case Model</h1>
<p class="sub">CP353002 · Section 03 · ไฟล์ต้นฉบับ: <code>doc/diagrams/01-use-case.puml</code></p>
<h2>1. แผนภาพ Use Case (Use Case Diagram)</h2>
<div class="fig">{svg}</div>
<div class="cap">รูปที่ 1 — Use case diagram</div>
<h2 class="pb">2. Actor</h2>
<table class="rel"><tr><th style="width:42mm">Actor</th><th>คำอธิบาย</th></tr>
{''.join(f'<tr><td><b>{inline(a)}</b></td><td>{inline(d)}</td></tr>' for a, d in ACTORS)}</table>
<h2>3. ความสัมพันธ์ «include» / «extend»</h2>
<table class="rel"><tr><th style="width:38mm">ความสัมพันธ์ (Relation)</th><th style="width:62mm">เหตุผล</th><th>โค้ดที่เกี่ยวข้อง</th></tr>
{''.join(f'<tr><td>{inline(a)}</td><td>{inline(b)}</td><td class="code">{inline(c)}</td></tr>' for a, b, c in REL)}</table>
<h2>4. คำอธิบาย Use Case</h2>
{''.join(rows)}
</body></html>"""
open(os.path.join("src", "01-use-case-doc.html"), "w", encoding="utf-8").write(doc)
print("ok")
