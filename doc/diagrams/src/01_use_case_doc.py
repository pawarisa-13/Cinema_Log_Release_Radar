"""Builds 01-use-case.md (tables) and src/01-use-case-doc.html (A4 print source for 01-use-case.pdf).
Run from doc/diagrams:  python3 src/01_use_case_doc.py"""
import os, re, sys, html
sys.path.insert(0, os.path.dirname(__file__))
from use_cases import UC

ACTORS = [
 ("Visitor", "Not logged in. Can only reach `/login`, `/register` and the public API (`SecurityConfig`)."),
 ("Member", "A logged-in Visitor (generalization). Every page and `/api/v1/users/me/**` needs a session."),
 ("Scheduler", "Spring `@Scheduled` jobs `MovieSyncJob` and `ReminderJob`. Each also runs once on `ApplicationReadyEvent`."),
 ("TMDB API, SMTP server", "External systems. Email is optional (`spring.mail.host` may be empty)."),
]
REL = [
 ("UC01 «include» UC02", "After a successful register the page always submits the hidden login form", "`templates/auth/register.html` (`#auto-login`), `pages/auth.js`"),
 ("UC04 «extend» UC02", "Only when the profile has no favorite genres", "`OnboardingAwareSuccessHandler` → `UserProfileService.needsOnboarding()`"),
 ("UC07 «extend» UC06", "Only for a text query of 2+ characters, on page 0, with a TMDB key", "`MovieQueryServiceImpl.search()` → `MovieSyncService.importSearchResults()`"),
 ("UC08 «include» UC10", "The movie page always loads its reviews", "`pages/movie.js` → `GET /api/v1/movies/{id}/reviews`"),
 ("UC09 «extend» UC08", "Only when details are missing or stale and a key is set", "`MovieQueryServiceImpl.getDetail()` → `Movie.needsDetails()` → `refreshDetails()`"),
 ("UC23 «include» UC24", "Every due reminder is delivered", "`ReminderDispatchServiceImpl.dispatchDueReminders()`"),
 ("UC24 «extend» UC18", "Only when the reminder date has already been reached", "`ReminderServiceImpl.save()` → `dispatchIfDue()`"),
]

def md_cell(t): return t.replace("|", "\\|")
def lst_md(items, numbered):
    if items == ["—"]: return "—"
    return "<br>".join((f"{i+1}. " if numbered else "• ") + md_cell(x) for i, x in enumerate(items))

# ---------- Markdown ----------
md = ["# Use case diagram + use case descriptions", "",
      "UML use case diagram (PlantUML). Source: [`01-use-case.puml`](01-use-case.puml) · rendered: [`01-use-case.svg`](01-use-case.svg) · "
      "printable A4 document (diagram + description tables): [`01-use-case.pdf`](01-use-case.pdf)", "",
      "![Use case diagram](01-use-case.svg)", "", "## Actors", ""]
md += [f"- **{a}**: {d}" for a, d in ACTORS]
md += ["", "## «include» / «extend» used in the diagram", "", "| Relation | Why | Code |", "|---|---|---|"]
md += [f"| {a} | {b} | {c} |" for a, b, c in REL]
md += ["", "## Use case descriptions", ""]
for u in UC:
    md += [f"### {u['id']} — {u['name']}", "", "| Field | Detail |", "|---|---|",
           f"| Use Case ID | {u['id']} |", f"| Use Case Name | {md_cell(u['name'])} |", f"| Primary Actor | {md_cell(u['actor'])} |",
           f"| Description | {md_cell(u['desc'])} |", f"| Pre-conditions | {lst_md(u['pre'], False)} |",
           f"| Post-conditions | {lst_md(u['post'], False)} |", f"| Main Flow | {lst_md(u['main'], True)} |",
           f"| Alternative Flow | {lst_md(u['alt'], False)} |", ""]
open("01-use-case.md", "w").write("\n".join(md))

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
<tr><th>Use Case ID</th><td><b>{u['id']}</b></td></tr>
<tr><th>Use Case Name</th><td>{inline(u['name'])}</td></tr>
<tr><th>Primary Actor</th><td>{inline(u['actor'])}</td></tr>
<tr><th>Description</th><td>{inline(u['desc'])}</td></tr>
<tr><th>Pre-conditions</th><td>{lst_html(u['pre'], False)}</td></tr>
<tr><th>Post-conditions</th><td>{lst_html(u['post'], False)}</td></tr>
<tr><th>Main Flow</th><td>{lst_html(u['main'], True)}</td></tr>
<tr><th>Alternative Flow</th><td>{lst_html(u['alt'], False)}</td></tr>
</table>""")
doc = f"""<!doctype html><html><head><meta charset="utf-8"><title>Use Case Diagram and Descriptions</title>
<style>
@page {{ size: A4; margin: 16mm 15mm 16mm 15mm; }}
body {{ font-family: Arial, Helvetica, sans-serif; font-size: 10pt; color: #000; }}
h1 {{ font-size: 16pt; margin: 0 0 2mm; }} h2 {{ font-size: 12.5pt; margin: 6mm 0 2mm; border-bottom: 1px solid #000; padding-bottom: 1mm; }}
p.sub {{ margin: 0 0 4mm; font-size: 9.5pt; }}
.fig {{ text-align: center; }} .fig img {{ max-width: 100%; max-height: 222mm; }}
.cap {{ text-align: center; font-size: 9pt; margin-top: 2mm; }}
table {{ border-collapse: collapse; width: 100%; }}
th, td {{ border: 1px solid #000; padding: 1.4mm 2mm; vertical-align: top; text-align: left; }}
table.uc {{ margin: 0 0 5mm; page-break-inside: avoid; }}
table.uc th {{ width: 32mm; font-weight: bold; background: #fff; }}
table.rel th {{ background: #fff; }}
ul, ol {{ margin: 0; padding-left: 5mm; }} li {{ margin: 0.3mm 0; }}
code {{ font-family: "Courier New", monospace; font-size: 8.8pt; }}
.pb {{ page-break-before: always; }}
</style></head><body>
<h1>Cinema Log &amp; Release Radar — Use Case Model</h1>
<p class="sub">CP353002 · Section 03 · source: <code>doc/diagrams/01-use-case.puml</code></p>
<h2>1. Use Case Diagram</h2>
<div class="fig">{svg}</div>
<div class="cap">Figure 1 — Use case diagram</div>
<h2 class="pb">2. Actors</h2>
<table class="rel"><tr><th style="width:42mm">Actor</th><th>Description</th></tr>
{''.join(f'<tr><td><b>{inline(a)}</b></td><td>{inline(d)}</td></tr>' for a, d in ACTORS)}</table>
<h2>3. «include» / «extend» relationships</h2>
<table class="rel"><tr><th style="width:38mm">Relation</th><th>Why</th><th>Code</th></tr>
{''.join(f'<tr><td>{inline(a)}</td><td>{inline(b)}</td><td>{inline(c)}</td></tr>' for a, b, c in REL)}</table>
<h2>4. Use Case Descriptions</h2>
{''.join(rows)}
</body></html>"""
open(os.path.join("src", "01-use-case-doc.html"), "w").write(doc)
print("ok")
