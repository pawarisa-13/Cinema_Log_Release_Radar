import { esc, hash, rng } from './dom.js';

const f1 = n => Math.round(n * 10) / 10;
export const R = {
    curve(p) {
        const n = p.length; let d = `M${f1(p[0][0])} ${f1(p[0][1])}`;
        for (let i = 0; i < n; i++) {
            const p0 = p[(i - 1 + n) % n], p1 = p[i], p2 = p[(i + 1) % n], p3 = p[(i + 2) % n];
            d += `C${f1(p1[0] + (p2[0] - p0[0]) / 6)} ${f1(p1[1] + (p2[1] - p0[1]) / 6)} ${f1(p2[0] - (p3[0] - p1[0]) / 6)} ${f1(p2[1] - (p3[1] - p1[1]) / 6)} ${f1(p2[0])} ${f1(p2[1])}`;
        }
        return d + 'Z';
    },
    ell(cx, cy, rx, ry, seed = 1, j = .06) {
        const r = rng(seed * 97 + 13), n = 10, off = r() * 6, p = [];
        for (let i = 0; i < n; i++) { const a = off + i / n * Math.PI * 2, k = 1 + (r() - .5) * j * 2; p.push([cx + Math.cos(a) * rx * k, cy + Math.sin(a) * ry * k]); }
        return R.curve(p);
    },
    line(x1, y1, x2, y2, seed = 1, bow = 1.4) {
        const r = rng(seed * 31 + 7), mx = (x1 + x2) / 2 + (r() - .5) * bow * 2, my = (y1 + y2) / 2 + (r() - .5) * bow * 2;
        return `M${f1(x1)} ${f1(y1)}Q${f1(mx)} ${f1(my)} ${f1(x2)} ${f1(y2)}`;
    },
    poly(pts, seed = 1, bow = 1.4) {
        const r = rng(seed * 53 + 3); let d = `M${f1(pts[0][0])} ${f1(pts[0][1])}`;
        for (let i = 0; i < pts.length; i++) {
            const a = pts[i], b = pts[(i + 1) % pts.length];
            d += `Q${f1((a[0] + b[0]) / 2 + (r() - .5) * bow * 2)} ${f1((a[1] + b[1]) / 2 + (r() - .5) * bow * 2)} ${f1(b[0])} ${f1(b[1])}`;
        }
        return d + 'Z';
    },
    rect(x, y, w, h, seed, bow) { return R.poly([[x, y], [x + w, y], [x + w, y + h], [x, y + h]], seed, bow); }
};
export const P = (d, cls = '', extra = '') => `<path d="${d}"${cls ? ` class="${cls}"` : ''}${extra}/>`;
export const SV = (vb, inner, cls = '', label = '') =>
    `<svg viewBox="${vb}" class="doodle ${cls}" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" ${label ? `role="img" aria-label="${esc(label)}"` : 'aria-hidden="true"'}>${inner}</svg>`;
export const spark = (x, y, r) => `<path class="fy" stroke-width="1.5" d="M${x} ${y - r}Q${x} ${y} ${x + r} ${y}Q${x} ${y} ${x} ${y + r}Q${x} ${y} ${x - r} ${y}Q${x} ${y} ${x} ${y - r}Z"/>`;

export function face(cx, cy, mood = 'smile', s = 1) {
    const e = 7 * s;
    let o = mood === 'happy'
        ? `<path d="M${cx - e - 3 * s} ${cy + 1}q${3 * s} ${-4 * s} ${6 * s} 0M${cx + e - 3 * s} ${cy + 1}q${3 * s} ${-4 * s} ${6 * s} 0" stroke-width="${1.8 * s}"/>`
        : `<ellipse class="fi" cx="${cx - e}" cy="${cy}" rx="${1.9 * s}" ry="${2.5 * s}"/><ellipse class="fi" cx="${cx + e}" cy="${cy}" rx="${1.9 * s}" ry="${2.5 * s}"/>`;
    const m = {
        smile: `M${cx - 3.5 * s} ${cy + 6 * s}q${3.5 * s} ${3.5 * s} ${7 * s} 0`,
        happy: `M${cx - 4 * s} ${cy + 5 * s}q${4 * s} ${5 * s} ${8 * s} 0`,
        flat: `M${cx - 3 * s} ${cy + 7 * s}h${6 * s}`,
        o: `M${cx - 2 * s} ${cy + 7 * s}a${2 * s} ${2.4 * s} 0 1 0 ${4 * s} 0a${2 * s} ${2.4 * s} 0 1 0 ${-4 * s} 0`
    }[mood];
    o += `<path d="${m}" stroke-width="${1.8 * s}"/>`;
    o += `<ellipse class="fk blush" cx="${cx - e - 5 * s}" cy="${cy + 5 * s}" rx="${3.4 * s}" ry="${2 * s}"/><ellipse class="fk blush" cx="${cx + e + 5 * s}" cy="${cy + 5 * s}" rx="${3.4 * s}" ry="${2 * s}"/>`;
    return o;
}

export const D = {
    /* "Pop" — a popcorn bucket who lives on the Films page */
    popcorn() {
        let s = '';
        [[50, 62, 17, 15], [75, 48, 19, 17], [101, 60, 17, 15], [63, 74, 14, 11], [90, 72, 15, 12], [120, 74, 12, 11], [36, 76, 10, 9]].forEach((c, i) => s += P(R.ell(...c, i + 2), 'fp'));
        s += P(R.poly([[34, 80], [134, 80], [120, 176], [48, 176]], 5, 2), 'fp');
        s += P(R.poly([[56, 80], [74, 80], [72, 176], [61, 176]], 6, 1), 'fk', ' stroke="none"');
        s += P(R.poly([[94, 80], [112, 80], [106, 176], [93, 176]], 7, 1), 'fk', ' stroke="none"');
        s += P(R.poly([[34, 80], [134, 80], [120, 176], [48, 176]], 5, 2));
        s += P(R.line(56, 82, 61, 174, 70, 1), '', ' stroke-width="1.2"') + P(R.line(74, 82, 72, 174, 71, 1), '', ' stroke-width="1.2"');
        s += P(R.line(94, 82, 93, 174, 72, 1), '', ' stroke-width="1.2"') + P(R.line(112, 82, 106, 174, 73, 1), '', ' stroke-width="1.2"');
        s += P(R.ell(84, 122, 23, 17, 9), 'fp');
        s += face(84, 118, 'smile', 1);
        s += P(R.line(30, 80, 138, 80, 8, 1), '', ' stroke-width="2.8"');
        s += P(R.line(37, 118, 14, 100, 10)) + P(R.ell(12, 97, 4.5, 4.5, 11), 'fp');
        s += P(R.line(128, 116, 150, 128, 12)) + P(R.ell(153, 130, 4.5, 4.5, 13), 'fp');
        s += P(R.line(70, 176, 66, 196, 14)) + P(R.line(100, 176, 104, 196, 15));
        s += P(R.ell(61, 198, 7, 3.5, 16), 'fi') + P(R.ell(109, 198, 7, 3.5, 17), 'fi');
        s += spark(152, 40, 7) + spark(22, 42, 5) + spark(140, 16, 4);
        return SV('0 0 170 206', s, 'popcorn');
    },
    /* a small round kid who carries things around the app */
    kid(prop = 'wave') {
        let s = '';
        const legs = P(R.line(60, 130, 57, 154, 22)) + P(R.line(80, 130, 84, 154, 23)) + P(R.line(57, 154, 48, 156, 24)) + P(R.line(84, 154, 93, 156, 25));
        const body = P(R.poly([[54, 72], [86, 72], [96, 130], [44, 130]], 21, 2), 'fl');
        const head = P(R.ell(70, 46, 24, 22, 26), 'fp') + `<path d="M58 27q5-10 13-4q6-9 11 3"/>` + face(70, 49, prop === 'box' ? 'o' : 'smile');
        s += legs + body + head;
        if (prop === 'wave') s += P(R.line(56, 84, 40, 108, 27)) + P(R.ell(39, 111, 4.5, 4.5, 28), 'fp') + P(R.line(84, 82, 104, 58, 29)) + P(R.ell(106, 54, 5, 5, 30), 'fp') + `<path d="M115 44q5 4 3 10M121 39q8 7 4 17" stroke-width="1.6"/>`;
        if (prop === 'ticket') s += `<g transform="rotate(-10 70 104)">${P(R.rect(40, 92, 62, 28, 31, 1), 'fy')}<circle cx="40" cy="106" r="4" class="fp"/><circle cx="102" cy="106" r="4" class="fp"/><path d="M84 95v22" stroke-dasharray="2 3" stroke-width="1.4"/><text x="47" y="110" font-size="9" class="tx">ADMIT 1</text></g>` + P(R.line(56, 84, 44, 100, 32)) + P(R.ell(43, 103, 4.5, 4.5, 33), 'fp') + P(R.line(84, 84, 98, 96, 34)) + P(R.ell(100, 99, 4.5, 4.5, 35), 'fp');
        if (prop === 'calendar') s += P(R.rect(36, 78, 68, 58, 36, 1.2), 'fp') + P(R.line(36, 92, 104, 92, 37, .6)) + `<path d="M50 73v9M90 73v9" stroke-width="2.6"/><path d="M47 104h46M47 116h46M60 96v34M74 96v34M88 96v34" stroke-width="1" opacity=".35"/>` + P(R.ell(34, 104, 4.5, 4.5, 38), 'fp') + P(R.ell(106, 104, 4.5, 4.5, 39), 'fp');
        if (prop === 'box') s += P(R.poly([[42, 104], [98, 104], [94, 142], [46, 142]], 40), 'fy') + P(R.poly([[42, 104], [28, 90], [50, 92], [58, 104]], 41), 'fy') + P(R.poly([[98, 104], [112, 90], [90, 92], [82, 104]], 42), 'fy') + P(R.ell(44, 112, 4.5, 4.5, 43), 'fp') + P(R.ell(96, 112, 4.5, 4.5, 44), 'fp') + `<text x="96" y="28" font-size="22" class="tx">?</text>`;
        return SV('0 0 140 170', s, 'kid');
    },
    /* empty watchlist: kid sitting in front of an empty shelf */
    shelf() {
        let s = P(R.line(8, 160, 252, 160, 41, 1));
        s += P(R.rect(132, 24, 108, 136, 42, 1.5), 'fp') + P(R.line(132, 70, 240, 70, 43)) + P(R.line(132, 115, 240, 115, 44));
        s += `<path d="M234 28q-9 3-11 12M228 28q-2 9-12 12M232 34l-8 4" stroke-width="1.1"/><path d="M150 64h16M190 109h22" stroke-width="1.2" stroke-dasharray="1 5"/>`;
        s += P(R.ell(68, 128, 26, 22, 45), 'fl') + P(R.ell(89, 139, 13, 12, 46), 'fp') + P(R.ell(66, 88, 21, 20, 47), 'fp');
        s += face(71, 91, 'flat', .9) + `<path d="M56 71q4-9 11-4q5-7 10 2"/>` + P(R.line(94, 151, 106, 153, 48));
        s += `<text x="98" y="60" font-size="20" class="tx">. . .</text>`;
        return SV('0 0 260 170', s, 'shelf');
    },
    /* "Reel" — the diary's film-reel buddy holding a pencil */
    reel() {
        let s = `<path d="M100 104C128 118 138 140 168 136M106 113C132 128 142 150 170 146" stroke-width="1.8"/><path d="M122 117l2 7M136 127l2 7M150 136l1 7" stroke-width="1.4"/>`;
        s += P(R.line(62, 112, 58, 134, 52)) + P(R.line(90, 112, 94, 134, 53)) + P(R.ell(54, 136, 6, 3, 54), 'fi') + P(R.ell(98, 136, 6, 3, 55), 'fi');
        s += P(R.ell(76, 70, 46, 44, 51), 'fp');
        for (let i = 0; i < 5; i++) { const a = (-90 + i * 72 + 36) * Math.PI / 180; s += P(R.ell(76 + Math.cos(a) * 30, 70 + Math.sin(a) * 29, 7, 7, 60 + i), 'fb'); }
        s += face(76, 64, 'happy', 1);
        s += P(R.line(116, 64, 132, 50, 56)) + `<g transform="rotate(-40 140 44)"><rect x="128" y="40" width="26" height="8" rx="1" class="fy"/><path d="M154 40l7 4-7 4"/></g>` + P(R.ell(132, 49, 4.5, 4.5, 57), 'fp');
        s += spark(20, 22, 6);
        return SV('0 0 176 152', s, 'reel');
    },
    /* "Bo" — a bell who guards your release reminders */
    bell(mood = 'smile') {
        let s = `<path d="M18 40q-6 6-4 14M92 40q6 6 4 14M12 34q-8 10-5 22" stroke-width="1.6"/>`;
        s += P(R.ell(55, 22, 6, 6, 81), 'fp');
        s += P('M28 86C28 46 38 28 55 28C72 28 82 46 82 86Z', 'fy');
        s += P(R.ell(55, 95, 7, 7, 82), 'fp') + P(R.line(20, 86, 90, 86, 83, 1), '', ' stroke-width="2.6"');
        s += face(55, 58, mood, 1);
        s += P(R.line(40, 98, 36, 112, 84)) + P(R.line(70, 98, 74, 112, 85));
        return SV('0 0 110 118', s, 'bell');
    },
    ghost() {
        let s = P('M28 116V56C28 22 92 22 92 56V116l-8-7-8 7-8-7-8 7-8-7-8 7-8-7z', 'fp');
        s += face(60, 60, 'o', 1) + P(R.line(28, 74, 14, 66, 91)) + P(R.line(92, 74, 106, 64, 92));
        s += `<path d="M104 30q6 2 8-4M10 40q-4-4 0-8" stroke-width="1.5"/>`;
        return SV('0 0 120 130', s, 'ghost');
    },
    arrow(flip = false) {
        return `<svg viewBox="0 0 60 28" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" aria-hidden="true"${flip ? ' style="transform:scaleX(-1)"' : ''}><path d="M3 6C14 20 34 24 54 15"/><path d="M45 10l9 5-8 7"/></svg>`;
    },
    todayCircle() { return `<svg viewBox="0 0 40 36" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" aria-hidden="true"><path d="${R.ell(20, 18, 16, 14, 7, .1)}"/></svg>`; },
    logo() {
        let s = P(R.ell(20, 20, 15, 15, 61), 'fp');
        for (let i = 0; i < 4; i++) { const a = (45 + i * 90) * Math.PI / 180; s += `<circle cx="${f1(20 + Math.cos(a) * 9)}" cy="${f1(20 + Math.sin(a) * 9)}" r="2.6" class="fb" stroke-width="1.4"/>`; }
        s += `<circle cx="20" cy="20" r="1.6" class="fi"/><path d="M30 31c4 3 7 3 9 1" stroke-width="1.8"/>`;
        return SV('0 0 42 40', s, '');
    }
};

/* genre doodles — used on the sign-up tiles and as poster motifs */
export const GI = {
    Horror: `<path class="fp" d="M13 41V22C13 9 35 9 35 22v19l-4-3-3.5 3-3.5-3-3.5 3-3.5-3z"/><ellipse class="fi" cx="20" cy="22" rx="2" ry="2.8"/><ellipse class="fi" cx="28" cy="22" rx="2" ry="2.8"/><path d="M21.5 30q2.5 2 5 0"/>`,
    Comedy: `<circle class="fy" cx="24" cy="24" r="15"/><path d="M17 20q2-3 4 0M27 20q2-3 4 0"/><path class="fp" d="M15.5 26h17c-1 6-4.5 9-8.5 9s-7.5-3-8.5-9z"/>`,
    Animation: `<path class="fy" d="M10 38l3-9L32 10l6 6-19 19z"/><path d="M13 29l6 6M29 13l6 6"/><path class="fk" d="M38 29l1.6 3.4 3.6.5-2.6 2.5.6 3.6-3.2-1.7-3.2 1.7.6-3.6-2.6-2.5 3.6-.5z"/>`,
    Fantasy: `<path class="fl" d="M10 40V20h6v5h4v-9h8v9h4v-5h6v20z"/><path d="M24 16V7l6 3-6 3"/><path class="fp" d="M21 40v-7a3 3 0 016 0v7"/>`,
    Romance: `<path class="fk" d="M24 39C18 34 9 28 9 19.5 9 14 13 11 17 11c3.5 0 5.5 2.2 7 4.5 1.5-2.3 3.5-4.5 7-4.5 4 0 8 3 8 8.5C39 28 30 34 24 39z"/><path d="M15 18q1-3 4-3"/>`,
    Action: `<path class="fy" d="M27 6L12 27h10l-3 15 17-23H25z"/>`,
    'Sci-Fi': `<circle class="fb" cx="24" cy="24" r="11"/><path d="M7 30c4 4 30-4 34-12 1.5-3-2-4-6-3.5M13 23.5C9 26 6 28.5 7 30"/><path d="M38 8v4M36 10h4"/>`,
    Drama: `<path class="fp" d="M11 12h26v12c0 9-6 15-13 15s-13-6-13-15z"/><path d="M17 21q2-2 4 0M27 21q2-2 4 0M19 31q5-4 10 0"/><path class="fb" d="M31 24q2 3 0 4.5q-2-1.5 0-4.5z"/>`,
    Thriller: `<circle class="fp" cx="21" cy="21" r="11"/><path d="M29 29l10 10" stroke-width="3.2"/><path d="M15 18q2-4 6-4"/>`,
    Documentary: `<path class="fg" d="M8 17h22v18H8z"/><path class="fp" d="M30 22l10-5v18l-10-5z"/><circle class="fp" cx="14" cy="12" r="4"/><circle class="fp" cx="23" cy="12" r="4"/>`
};
export const genreIcon = g => SV('0 0 48 48', GI[g] || GI.Drama, 'gicon');

/* UI icons — slightly wobbly strokes */
export const ICONS = {
    heart: '<path d="M12 20.3c-.5-.3-7.6-4.9-8.4-9.4C3 7.6 5.2 4.9 8 5c1.8.1 3.1 1.2 4 2.7.9-1.5 2.3-2.7 4.2-2.7 2.8 0 4.9 2.7 4.2 5.9-.9 4.5-7.9 9.1-8.4 9.4z"/>',
    plus: '<path d="M12 5.2c.1 4.5-.1 9 .1 13.6M5.3 12.1c4.4-.2 9-.1 13.4.1"/>',
    check: '<path d="M5 12.6l4.3 4.2L19.2 6.9"/>',
    dots: '<circle class="solid" cx="5.5" cy="12" r="1.5"/><circle class="solid" cx="12" cy="12.3" r="1.5"/><circle class="solid" cx="18.5" cy="11.8" r="1.5"/>',
    bell: '<path d="M6.2 16.8c.9-1 1.2-2.1 1.2-4.6 0-3.2 1.9-5.6 4.7-5.7 2.8 0 4.6 2.4 4.6 5.6 0 2.5.4 3.7 1.3 4.7z"/><path d="M5 16.9h14.1M10.3 19.3c.6 1 2.6 1.1 3.4-.1M12 4v2.4"/>',
    search: '<circle cx="10.5" cy="10.5" r="6.1"/><path d="M15 15.2l4.6 4.4"/>',
    star: '<path d="M12 4.2l2.3 4.9 5.3.6-3.9 3.6 1.1 5.3L12 15.9l-4.7 2.7 1.1-5.3-3.9-3.6 5.3-.6z"/>',
    pencil: '<path d="M5 19l1-4.2L15.8 5l3.2 3.2-9.8 9.8z"/><path d="M13.6 7.2l3.2 3.2"/>',
    x: '<path d="M6.5 6.4l11 11.2M17.6 6.3L6.4 17.5"/>',
    chevL: '<path d="M14.6 5.6L8.4 12l6.3 6.4"/>',
    chevR: '<path d="M9.4 5.6l6.2 6.4-6.3 6.4"/>',
    chevD: '<path d="M6 9.5l6 5.8 6-5.9"/>',
    arrowR: '<path d="M4.5 12.2c4.8-.3 9.6-.1 14.5-.2M14.4 7.2l4.8 4.9-4.9 4.8"/>',
    arrowL: '<path d="M19.5 12.2c-4.8-.3-9.6-.1-14.5-.2M9.6 7.2L4.8 12.1l4.9 4.8"/>',
    film: '<rect x="4" y="5" width="16" height="14" rx="1.5"/><path d="M8 5v14M16 5v14M4 9h4M4 15h4M16 9h4M16 15h4"/>',
    cal: '<rect x="4" y="5.5" width="16" height="14" rx="1.5"/><path d="M4 10h16M8.5 3.5v4M15.5 3.5v4"/>',
    user: '<circle cx="12" cy="9" r="3.8"/><path d="M5 19.5c1.2-3.6 4-5.2 7-5.2s5.8 1.6 7 5.2"/>',
    folder: '<path d="M4 7.5c0-1 .7-1.8 1.7-1.8h4l1.8 2.1h6.8c1 0 1.7.8 1.7 1.8v7.8c0 1-.7 1.8-1.7 1.8H5.7c-1 0-1.7-.8-1.7-1.8z"/>',
    trash: '<path d="M5 7h14M9.5 7V5h5v2M7 7l.9 12h8.2L17 7"/>',
    logout: '<path d="M14 5H6v14h8M10.5 12h9.5M16.5 8.5l3.5 3.5-3.5 3.5"/>',
    reset: '<path d="M5.2 12a6.8 6.8 0 1 0 2.1-4.9M5 4.5V8h3.5"/>',
    mail: '<rect x="4" y="6" width="16" height="12" rx="1.5"/><path d="M4.5 7l7.5 6 7.5-6"/>',
    eye: '<path d="M3 12c2.5-4 5.5-6 9-6s6.5 2 9 6c-2.5 4-5.5 6-9 6s-6.5-2-9-6z"/><circle cx="12" cy="12" r="2.6"/>',
    list: '<path d="M8 7h12M8 12h12M8 17h12"/><circle class="solid" cx="4.5" cy="7" r="1.1"/><circle class="solid" cx="4.5" cy="12" r="1.1"/><circle class="solid" cx="4.5" cy="17" r="1.1"/>',
    bookmark: '<path d="M7 4.5h10v15.2l-5-3.6-5 3.6z"/>'
};
export const ic = (n, cls = '') => `<svg viewBox="0 0 24 24" class="ic ic-${n} ${cls}" aria-hidden="true">${ICONS[n]}</svg>`;


/* genres the UI offers as filters / favorites (TMDB ids) */
export const GENRES = [
    { id: 27, name: 'Horror' }, { id: 35, name: 'Comedy' }, { id: 16, name: 'Animation' }, { id: 14, name: 'Fantasy' },
    { id: 10749, name: 'Romance' }, { id: 28, name: 'Action' }, { id: 878, name: 'Sci-Fi' }, { id: 18, name: 'Drama' },
    { id: 53, name: 'Thriller' }, { id: 99, name: 'Documentary' }
];

/* avatar doodle — takes the API's { avatarStyle, avatarColor } (BUN / PINK …) */
export const AV_COLORS = { PINK: 'fk', BLUE: 'fb', GREEN: 'fg', YELLOW: 'fy', LAVENDER: 'fl' };
export const AV_STYLES = ['BUN', 'BOB', 'CAP', 'CURLY'];
export function avatar(p = {}, size = 36) {
    const bg = AV_COLORS[p.avatarColor] || 'fk';
    let s = P(R.ell(30, 30, 27, 27, 101), bg) + P(R.ell(30, 34, 15, 14, 102), 'fp');
    const hair = {
        BUN: `<path class="fi" d="M14.5 32C14 19 46 19 45.5 32 41 25 19 25 14.5 32z"/><circle class="fi" cx="30" cy="15" r="5.5"/>`,
        BOB: `<path class="fi" d="M13.5 38C10 18 50 18 46.5 38L43 38C44 27 16 27 17 38z"/>`,
        CAP: `<path class="fy" d="M14 30C15 17 45 17 46 30z"/><path d="M14 30h26q8 0 12 3"/>`,
        CURLY: [[18, 26], [24, 21], [31, 19], [38, 21], [43, 26]].map(([x, y]) => `<circle class="fi" cx="${x}" cy="${y}" r="5"/>`).join('')
    }[p.avatarStyle] || '';
    s += hair + `<ellipse class="fi" cx="25" cy="36" rx="1.4" ry="1.8"/><ellipse class="fi" cx="35" cy="36" rx="1.4" ry="1.8"/><path d="M27.5 41q2.5 2 5 0" stroke-width="1.5"/>`;
    return `<svg viewBox="0 0 60 60" class="av doodle" width="${size}" height="${size}" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" aria-hidden="true">${s}</svg>`;
}

/* poster: the real TMDB image when there is one, otherwise an illustrated card */
const PAL = [
    ['#C8553D', '#F2D0A4', '#2B1B17'], ['#2F4858', '#F6AE2D', '#F4EDE1'], ['#7C9885', '#F1E3C8', '#1E2E25'], ['#3D2C4E', '#E8B4BC', '#F5EEF6'],
    ['#E9C46A', '#E76F51', '#2A1F14'], ['#264653', '#8AB17D', '#EEF2E6'], ['#A44A3F', '#F5E9DA', '#FFF6EA'], ['#5B7DB1', '#F7E1A1', '#14213D'],
    ['#D8A7B1', '#6A4C93', '#2B1B2F'], ['#1F2A44', '#E9D8A6', '#F3EEDF'], ['#B5838D', '#FFE8D6', '#2E1F24'], ['#4A6C6F', '#F4D35E', '#F7F3E3']
];
function wrapTitle(t, max) {
    const words = String(t).split(' '), lines = []; let cur = '';
    for (const w of words) { if ((cur + ' ' + w).trim().length > max && cur) { lines.push(cur); cur = w; } else cur = (cur + ' ' + w).trim(); }
    if (cur) lines.push(cur); return lines;
}
export function posterArt(m) {
    const h = hash(m.title), r = rng(h);
    const [bg, acc, ink] = PAL[h % PAL.length];
    const shape = h % 4;
    let deco = '';
    if (shape === 0) deco = `<circle cx="${f1(60 + r() * 80)}" cy="${f1(80 + r() * 30)}" r="${f1(50 + r() * 14)}" fill="${acc}" opacity=".92"/>`;
    else if (shape === 1) deco = `<path d="M34 300V122a66 66 0 01132 0v178z" fill="${acc}" opacity=".9"/>`;
    else if (shape === 2) deco = `<path d="M0 150L200 86V128L0 192Z" fill="${acc}" opacity=".9"/>`;
    else deco = `<rect x="22" y="40" width="156" height="150" fill="${acc}" opacity=".9" transform="rotate(${f1(-4 + r() * 8)} 100 115)"/>`;
    const lines = wrapTitle(m.title, 13).slice(0, 4);
    const fs = lines.length > 2 ? 21 : lines.length === 2 ? 25 : 29;
    const y0 = 286 - (lines.length - 1) * fs * .98;
    const title = lines.map((l, i) => `<text x="16" y="${f1(y0 + i * fs * .98)}" font-size="${fs}" fill="${ink}" style="font-family:var(--font-hand);font-weight:700">${esc(l)}</text>`).join('');
    const g = (m.genres && m.genres[0]) || 'Drama';
    return `<svg viewBox="0 0 200 300" preserveAspectRatio="xMidYMid slice" role="img" aria-label="Illustrated poster for ${esc(m.title)}">
  <rect width="200" height="300" fill="${bg}"/>${deco}
  <g transform="translate(${f1(100 - 24 * 2.4)} ${shape === 1 ? 108 : 70}) scale(2.4)" fill="none" stroke="#211C18" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" style="--card:#F7F1E6;--yellow:${acc};--pink:${acc};--blue:${acc};--lav:${acc};--green:${acc}">${(GI[g] || GI.Drama).replace(/class="fi"/g, 'fill="#211C18" stroke="none"')}</g>
  <text x="16" y="26" font-size="10" fill="${ink}" opacity=".85" style="font-family:var(--font-ui);font-weight:700;letter-spacing:2px">${m.year || ''}</text>${title}</svg>`;
}
export function posterHtml(m) {
    return m.posterUrl
        ? `<img src="${esc(m.posterUrl)}" alt="Poster for ${esc(m.title)}" loading="lazy">`
        : posterArt(m);
}

/* fills <span data-doodle="popcorn"></span> placeholders that Thymeleaf templates leave */
export function hydrateDoodles(root = document) {
    root.querySelectorAll('[data-doodle]').forEach(el => {
        const [name, arg] = el.dataset.doodle.split(':');
        if (D[name]) el.innerHTML = D[name](arg);
    });
}
