# Diagrams

All diagrams are plain black-and-white UML. Each one has a text source and a rendered `.svg` that GitHub shows directly. The `.md` files add tables and notes that link each diagram to the code.

| # | Diagram | Read | Source | Picture |
|---|---|---|---|---|
| 01 | Use case + use case descriptions | [01-use-case.md](01-use-case.md), A4: [01-use-case.pdf](01-use-case.pdf) | `01-use-case.puml`, `src/use_cases.py` | `01-use-case.svg` |
| 02 | Domain model | [02-domain-model.md](02-domain-model.md) | `02-domain-model.puml` | `02-domain-model.svg` |
| 03 | Class diagram (3 views + overview) | [03-class-diagram.md](03-class-diagram.md) | `03a-…`, `03b-…`, `03c-…`, `03-class-diagram-overview.puml` | matching `.svg` |
| 04 | Sequence diagrams (3 scenarios, 4 pictures) | [04-sequence-diagrams.md](04-sequence-diagrams.md) | `04a-…` to `04d-….puml` | matching `.svg` |
| 05 | Activity diagram (swimlanes) | [05-activity-diagram.md](05-activity-diagram.md) | `src/05_activity.py` | `05-activity-diagram.svg` |
| 06 | ER diagram: crow's foot schema + Chen view | [06-er-diagram.md](06-er-diagram.md) | `06-er-diagram.puml`, `06-er-diagram-chen.puml` | matching `.svg` |
| 07 | Component + deployment | [07-component-deployment.md](07-component-deployment.md) | `07-component.puml`, `07-deployment.puml` | matching `.svg` |
| 08 | State machine (Reminder) | [08-state-diagram.md](08-state-diagram.md) | `08-state-diagram.puml` | `08-state-diagram.svg` |

`_style.iuml` holds the shared PlantUML style (white background, black lines, no colour).

## Regenerating the pictures

Run these from this folder. You need Java, `plantuml.jar` (1.2025 or newer, which includes Chen ER support), Graphviz, and Python 3 with Pillow.

```bash
java -jar plantuml.jar -tsvg *.puml            # all PlantUML diagrams
python3 src/05_activity.py                     # activity diagram
python3 src/01_use_case_doc.py                 # 01-use-case.md + src/01-use-case-doc.html
chrome --headless --no-pdf-header-footer --print-to-pdf=01-use-case.pdf src/01-use-case-doc.html
```
