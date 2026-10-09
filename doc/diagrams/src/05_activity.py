"""Draws 05-activity-diagram.svg (UML activity diagram with Member / System swimlanes).
Run from doc/diagrams:  python3 src/05_activity.py"""
import os, sys
sys.path.insert(0, os.path.dirname(__file__))
from svgkit import Svg, tw

W, H = 1210, 2460
ML, MR, SR = 20, 520, 1190          # Member lane [ML, MR], System lane [MR, SR]
MX, SX, SXL, SXR = 340, 790, 690, 905   # column centres
s = Svg(W, H, "Activity Diagram — From opening a movie page to a diary entry")

# swimlanes
s.rect(ML, 50, SR - ML, H - 70, fill="none")
s.line(MR, 50, MR, H - 20)
s.line(ML, 85, SR, 85)
s.text((ML + MR) / 2, 73, "Member", size=15, bold=True)
s.text((MR + SR) / 2, 73, "System", size=15, bold=True)

N = {}
def act(k, cx, cy, *lines):
    w = max(tw(l) for l in lines) + 26; h = 16 * len(lines) + 16
    N[k] = dict(cx=cx, cy=cy, w=w, h=h, kind="act", lines=lines)
def dia(k, cx, cy, w=46, h=34, *lines):
    N[k] = dict(cx=cx, cy=cy, w=w, h=h, kind="dia", lines=lines)
def L(k): n = N[k]; return (n["cx"] - n["w"] / 2, n["cy"])
def R(k): n = N[k]; return (n["cx"] + n["w"] / 2, n["cy"])
def T(k): n = N[k]; return (n["cx"], n["cy"] - n["h"] / 2)
def B(k): n = N[k]; return (n["cx"], n["cy"] + n["h"] / 2)
def dia_bottom_at(k, x):
    n = N[k]; return (x, n["cy"] + n["h"] / 2 * (1 - abs(x - n["cx"]) / (n["w"] / 2)))
def dia_top_at(k, x):
    n = N[k]; return (x, n["cy"] - n["h"] / 2 * (1 - abs(x - n["cx"]) / (n["w"] / 2)))

# ---------- nodes ----------
act("open", MX, 175, "Open movie page /movies/:id")
act("get", SX, 175, "GET /api/v1/movies/:id", "(MovieQueryServiceImpl.getDetail)")
dia("exists", SX, 255, 110, 46, "movie exists?")
act("e404", 1040, 255, "Return 404,", "show error")
act("show", SX, 335, "Show details, reviews", "and similar movies")
dia("rel", SX, 415, 120, 46, "movie released?")
act("en1", SXL, 505, "Enabled: Like, Watchlist,", "Add to collection, Remind me", "Disabled: Watched / Rate / Review")
act("en2", 1000, 505, "Enabled: Like, Watchlist,", "Add to collection,", "Watched / Rate / Review,", "edit or delete own entry", "Disabled: Remind me")
dia("mgrel", SX, 600)
dia("action", 155, 660, 110, 50, "action?")
act("like", SX, 740, "Toggle like", "(PUT / DELETE /users/me/likes/:movieId)")
act("wl", SX, 815, "Toggle watchlist", "(PUT / DELETE /users/me/watchlist/:movieId)")
act("tick", MX, 890, "Tick collections", "or create a new one")
act("coll", SX, 890, "Add / remove movie", "(PUT / DELETE /collections/:id/movies/:movieId)")
dia("rm", MX, 975, 120, 46, "save or remove?")
act("cancel", SX, 975, "Cancel reminder, status CANCELLED", "(DELETE /users/me/reminders/:movieId,", "ReminderServiceImpl.cancel)")
act("choose", MX, 1065, "Choose 7 / 3 / 1 / 0 days", "and IN_APP or EMAIL")
act("saverem", SX, 1065, "Save reminder, status SCHEDULED", "(ReminderServiceImpl.save)")
dia("due", SX, 1145, 210, 56, "reminder date already", "reached?")
act("sendnow", SXL, 1225, "Send notification now,", "status SENT (dispatchIfDue)")
act("wait", SXR, 1225, "Wait for hourly", "ReminderJob")
dia("mgdue", SX, 1295)
dia("mgloop", MX, 1370)
act("fill", MX, 1440, "Fill date, stars,", "review, place")
dia("valid", SX, 1440, 190, 76, "date <= today,", "movie released,", "rating 1-5?")
act("err", 1060, 1440, "Show error,", "stay in the form")
act("savediary", SX, 1535, "Save diary entry", "(DiaryServiceImpl.create)")
act("rmwl", SX, 1605, "Remove movie from watchlist")
dia("review", SX, 1675, 130, 46, "review written?")
act("notif1", SXL, 1750, "In-app notification", "\"review added\"")
dia("mgreview", SX, 1820)
act("poster", SX, 1885, "Poster appears on", "the calendar day")
dia("ed", MX, 1970, 120, 46, "edit or delete?")
act("delete", SX, 1970, "Delete entry", "(DiaryServiceImpl.delete)")
act("change", MX, 2055, "Change date, stars,", "review, place")
act("update", SX, 2055, "Check date rules, update entry", "(DiaryServiceImpl.update)")
dia("first", SX, 2135, 150, 46, "first review added?")
act("notif2", SXL, 2210, "In-app notification", "\"review added\"")
dia("mgfirst", SX, 2280)
dia("mgend", 1110, 2345, 120, 44)

# ---------- draw nodes ----------
for k, n in N.items():
    if n["kind"] == "act":
        s.rect(n["cx"] - n["w"] / 2, n["cy"] - n["h"] / 2, n["w"], n["h"], rx=10)
        s.lines(n["cx"], n["cy"], n["lines"])
    else:
        s.diamond(n["cx"], n["cy"], n["w"], n["h"])
        if n["lines"]:
            s.lines(n["cx"], n["cy"], n["lines"], size=12, lh=14)
s.circle(MX, 115, 11)                                   # initial node
def final(x, y): s.circle(x, y, 12, fill="#fff"); s.circle(x, y, 7)
final(1040, 325); final(1110, 2410)

def e(pts, label=None, lx=None, ly=None, anchor="start"):
    s.poly(pts)
    if label:
        for i, t in enumerate(label.split("\n")):
            s.text(lx, ly + i * 14, t, size=12, anchor=anchor)

# ---------- edges ----------
e([(MX, 126), T("open")])
e([R("open"), L("get")])
e([B("get"), T("exists")])
e([R("exists"), L("e404")], "No", R("exists")[0] + 6, 248)
e([B("e404"), (1040, 313)])
e([B("exists"), T("show")], "Yes", SX + 6, 290)
e([B("show"), T("rel")])
e([L("rel"), (SXL, 415), T("en1")], "No", SXL + 6, 408)
e([R("rel"), (1000, 415), T("en2")], "Yes", 1006, 408)
e([B("en1"), (SXL, 600), L("mgrel")])
e([B("en2"), (1000, 600), R("mgrel")])
e([B("mgrel"), (SX, 660), R("action")])

# fan-out of action? (one edge per guard, separate vertical lines, no crossings)
fan = [("like", 185, "Like"), ("wl", 170, "Watchlist"), ("tick", 155, "Add to collection"),
       ("rm", 140, "Remind me [not released]"), ("mgloop", 125, "Watched / Rate / Review [released]"),
       ("ed", 110, "Edit or delete entry [own entry]")]
for k, x, g in fan:
    y = N[k]["cy"]
    e([dia_bottom_at("action", x), (x, y), L(k)], g, x + 8, y - 6)

e([R("tick"), L("coll")])
e([R("rm"), L("cancel")], "remove [reminder exists]", R("rm")[0] + 6, 968)
e([B("rm"), T("choose")], "save / update", MX + 6, 1012)
e([R("choose"), L("saverem")])
e([B("saverem"), T("due")])
e([L("due"), (SXL, 1145), T("sendnow")], "Yes", SXL + 6, 1138)
e([R("due"), (SXR, 1145), T("wait")], "No", SXR + 6, 1138)
e([B("sendnow"), (SXL, 1295), L("mgdue")])
e([B("wait"), (SXR, 1295), R("mgdue")])
e([B("mgloop"), T("fill")])
e([R("fill"), L("valid")])
e([R("valid"), L("err")], "No", R("valid")[0] + 5, 1433)
e([T("err"), (1060, 1370), R("mgloop")])
e([B("valid"), T("savediary")], "Yes", SX + 6, 1492)
e([B("savediary"), T("rmwl")])
e([B("rmwl"), T("review")])
e([L("review"), (SXL, 1675), T("notif1")], "Yes", SXL + 6, 1668)
e([R("review"), (SXR, 1675), (SXR, 1820), R("mgreview")], "No", SXR + 6, 1668)
e([B("notif1"), (SXL, 1820), L("mgreview")])
e([B("mgreview"), T("poster")])
e([R("ed"), L("delete")], "delete", R("ed")[0] + 6, 1963)
e([B("ed"), T("change")], "edit", MX + 6, 2008)
e([R("change"), L("update")])
e([B("update"), T("first")])
e([L("first"), (SXL, 2135), T("notif2")], "Yes", SXL + 6, 2128)
e([R("first"), (SXR, 2135), (SXR, 2280), R("mgfirst")], "No", SXR + 6, 2128)
e([B("notif2"), (SXL, 2280), L("mgfirst")])

# fan-in to the final merge (separate vertical lines, earlier rows further right)
exits = [(R("like"), 1162), (R("wl"), 1150), (R("coll"), 1138), (R("cancel"), 1126), (R("mgdue"), 1114),
         (R("poster"), 1102), (R("delete"), 1090), (R("mgfirst"), 1078)]
for p, x in exits:
    e([p, (x, p[1]), dia_top_at("mgend", x)])
e([B("mgend"), (1110, 2397)])

s.save("05-activity-diagram.svg")
print("ok")
