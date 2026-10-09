"""Tiny helper for drawing plain black-and-white UML diagrams as SVG with explicit coordinates."""
from PIL import ImageFont
import html

FONT = "/usr/share/fonts/truetype/liberation/LiberationSans-Regular.ttf"
BOLD = "/usr/share/fonts/truetype/liberation/LiberationSans-Bold.ttf"
_f = {}

def tw(text, size=13, bold=False):
    key = (size, bold)
    if key not in _f:
        _f[key] = ImageFont.truetype(BOLD if bold else FONT, size)
    return _f[key].getlength(text)

class Svg:
    def __init__(self, w, h, title=None):
        self.w, self.h, self.out = w, h, []
        if title:
            self.text(w / 2, 28, title, size=17, bold=True)

    def esc(self, t):
        return html.escape(t, quote=True)

    def text(self, x, y, t, size=13, bold=False, anchor="middle", italic=False):
        self.out.append(f'<text x="{x:.1f}" y="{y:.1f}" font-family="Arial, Helvetica, sans-serif" font-size="{size}"'
                        f'{" font-weight=\"bold\"" if bold else ""}{" font-style=\"italic\"" if italic else ""}'
                        f' text-anchor="{anchor}" fill="#000">{self.esc(t)}</text>')

    def lines(self, cx, cy, lines, size=13, lh=16, anchor="middle", x=None):
        y0 = cy - (len(lines) - 1) * lh / 2 + size * 0.35
        for i, l in enumerate(lines):
            self.text(cx if x is None else x, y0 + i * lh, l, size=size, anchor=anchor)

    def rect(self, x, y, w, h, rx=0, sw=1.2, dash=None, fill="#fff"):
        d = f' stroke-dasharray="{dash}"' if dash else ""
        self.out.append(f'<rect x="{x:.1f}" y="{y:.1f}" width="{w:.1f}" height="{h:.1f}" rx="{rx}" fill="{fill}" stroke="#000" stroke-width="{sw}"{d}/>')

    def line(self, x1, y1, x2, y2, sw=1.2, dash=None):
        d = f' stroke-dasharray="{dash}"' if dash else ""
        self.out.append(f'<line x1="{x1:.1f}" y1="{y1:.1f}" x2="{x2:.1f}" y2="{y2:.1f}" stroke="#000" stroke-width="{sw}"{d}/>')

    def poly(self, pts, sw=1.2, dash=None, arrow="open"):
        d = f' stroke-dasharray="{dash}"' if dash else ""
        p = " ".join(f"{x:.1f},{y:.1f}" for x, y in pts)
        self.out.append(f'<polyline points="{p}" fill="none" stroke="#000" stroke-width="{sw}"{d}/>')
        if arrow:
            self.head(pts[-2], pts[-1], arrow)

    def head(self, a, b, kind="open", size=10):
        import math
        ang = math.atan2(b[1] - a[1], b[0] - a[0])
        l = (b[0] - size * math.cos(ang - 0.42), b[1] - size * math.sin(ang - 0.42))
        r = (b[0] - size * math.cos(ang + 0.42), b[1] - size * math.sin(ang + 0.42))
        if kind == "open":
            self.out.append(f'<polyline points="{l[0]:.1f},{l[1]:.1f} {b[0]:.1f},{b[1]:.1f} {r[0]:.1f},{r[1]:.1f}" fill="none" stroke="#000" stroke-width="1.2"/>')
        else:
            self.out.append(f'<polygon points="{l[0]:.1f},{l[1]:.1f} {b[0]:.1f},{b[1]:.1f} {r[0]:.1f},{r[1]:.1f}" fill="#000" stroke="#000"/>')

    def diamond(self, cx, cy, w, h):
        self.out.append(f'<polygon points="{cx:.1f},{cy-h/2:.1f} {cx+w/2:.1f},{cy:.1f} {cx:.1f},{cy+h/2:.1f} {cx-w/2:.1f},{cy:.1f}" fill="#fff" stroke="#000" stroke-width="1.2"/>')

    def circle(self, cx, cy, r, fill="#000", sw=1.2):
        self.out.append(f'<circle cx="{cx:.1f}" cy="{cy:.1f}" r="{r}" fill="{fill}" stroke="#000" stroke-width="{sw}"/>')

    def ellipse(self, cx, cy, rx, ry):
        self.out.append(f'<ellipse cx="{cx:.1f}" cy="{cy:.1f}" rx="{rx:.1f}" ry="{ry:.1f}" fill="#fff" stroke="#000" stroke-width="1.2"/>')

    def save(self, path):
        body = "\n".join(self.out)
        open(path, "w").write(
            f'<svg xmlns="http://www.w3.org/2000/svg" width="{self.w}" height="{self.h}" viewBox="0 0 {self.w} {self.h}">\n'
            f'<rect width="100%" height="100%" fill="#fff"/>\n{body}\n</svg>\n')
