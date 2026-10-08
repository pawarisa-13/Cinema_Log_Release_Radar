import { esc, fmtMD, daysFromToday } from '../core/dom.js';
import { ic, posterHtml, GENRES } from '../core/doodles.js';
import { st, cache } from '../core/state.js';

export const ratingTxt = m => m.released && m.rating ? `★ ${Number(m.rating).toFixed(1)}` : 'not out yet';

export function likeBtn(m, cls = 'mini') {
    const on = st.liked.has(m.id);
    return `<button class="${cls}${on ? ' on' : ''}" data-act="like" data-id="${m.id}" aria-pressed="${on}" aria-label="${on ? 'Unlike' : 'Like'} ${esc(m.title)}" title="${on ? 'Liked' : 'Like'}">${ic('heart')}${cls === 'act' ? `<span class="lbl" data-on="Liked" data-off="Like">${on ? 'Liked' : 'Like'}</span>` : ''}</button>`;
}
export function wlBtn(m, cls = 'mini') {
    const on = st.watchlist.has(m.id);
    return `<button class="${cls}${on ? ' on' : ''}" data-act="wl" data-id="${m.id}" aria-pressed="${on}" aria-label="${on ? 'Remove from' : 'Add to'} watchlist" title="${on ? 'In your watchlist' : 'Add to watchlist'}">${ic(on && cls === 'mini' ? 'check' : 'plus')}${cls === 'act' ? `<span class="lbl" data-on="In Watchlist" data-off="Add to Watchlist">${on ? 'In Watchlist' : 'Add to Watchlist'}</span>` : ''}</button>`;
}
export const dotsBtn = m => `<button class="mini" data-act="dots" data-id="${m.id}" aria-haspopup="menu" aria-label="More actions for ${esc(m.title)}">${ic('dots')}</button>`;

export function card(m) {
    cache(m);
    const seen = st.watched.has(m.id);
    return `<article class="mcard">
    <div class="poster" data-act="open" data-id="${m.id}" role="link" tabindex="0" aria-label="${esc(m.title)} (${m.year || 'TBA'})">${posterHtml(m)}
      ${seen ? `<span class="seen">${ic('check')}seen</span>` : ''}
      <span class="pov" data-act="noop"><span class="pov-t">${esc(m.title)}</span><span class="pov-m">${m.year || 'TBA'} · ${ratingTxt(m)}</span>
      <span class="pov-a">${likeBtn(m)}${wlBtn(m)}${dotsBtn(m)}</span></span>
    </div>
    <div class="m-t">${esc(m.title)}</div>
    <div class="m-s"><span>${m.year || 'TBA'}</span><span class="star">${ratingTxt(m)}</span></div>
  </article>`;
}

export function postcard(m, i) {
    cache(m);
    return `<a class="pcard" href="/movies/${m.id}" style="--r:${[-2.5, 1.8, -1, 2.6, -1.8, .8][i % 6]}deg" aria-label="${esc(m.title)}">
    <span class="stamp">★ ${Number(m.rating).toFixed(1)}</span><span class="poster">${posterHtml(m)}</span>
    <span class="pc-t">${esc(m.title)}</span><span class="pc-s">${m.year || ''} · ${esc(m.genres.slice(0, 2).join(', '))}</span></a>`;
}

export function ticket(m) {
    cache(m);
    const r = st.reminders.get(m.id), d = daysFromToday(m.releaseDate);
    return `<article class="ticket"><span class="perf"></span>
    <a class="poster" href="/movies/${m.id}" aria-label="${esc(m.title)}">${posterHtml(m)}</a>
    <div><div class="t-in">${d === 1 ? 'tomorrow' : `in ${d} days`}</div><div class="t-date">${fmtMD(m.releaseDate)}</div>
      <a class="t-title" href="/movies/${m.id}">${esc(m.title)}</a>
      <div class="t-gen">${esc(m.genres.join(' · ') || 'genre TBA')}</div>
      <button class="btn sm ${r ? 'on' : ''}" data-act="remind" data-id="${m.id}">${ic(r ? 'check' : 'bell')}${r ? 'reminder set' : 'Remind me'}</button></div>
  </article>`;
}

/* ---------- filters: UI values <-> API query parameters ---------- */
export const YEAR_OPTIONS = [['', 'any year'], ['2026', '2026'], ['2025', '2025'], ['2020s', '2020s'], ['2010s', '2010s'], ['2000s', '2000s'], ['1990s', '1990s'], ['older', 'before 1990']];
export const SORT_OPTIONS = [['POPULARITY', 'sort: popular'], ['RATING', 'sort: rating'], ['RELEASE_DATE', 'sort: release date'], ['RECENTLY_ADDED', 'sort: recently added']];

export function filtersFromUrl() {
    const p = new URLSearchParams(location.search);
    return { q: p.get('q') || '', genre: p.get('genre') || '', year: p.get('year') || '', rating: p.get('rating') || '', lang: p.get('lang') || '', sort: p.get('sort') || 'POPULARITY', page: Math.max(1, +(p.get('page') || 1)) };
}
export function filtersToUrl(f) {
    const p = new URLSearchParams();
    Object.entries(f).forEach(([k, v]) => { if (v && !(k === 'sort' && v === 'POPULARITY') && !(k === 'page' && +v === 1)) p.set(k, v); });
    const s = p.toString();
    return '/catalog' + (s ? '?' + s : '');
}
/** Maps the friendly filter values to MovieSearchCriteria fields. */
export function toCriteria(f, size = 40) {
    const c = { q: f.q, genreId: f.genre, minRating: f.rating, language: f.lang, sort: f.sort, page: f.page - 1, size };
    if (/^\d{4}$/.test(f.year)) c.year = f.year;
    else if (/^\d{4}s$/.test(f.year)) c.decade = f.year.slice(0, 4);
    else if (f.year === 'older') c.before = 1990;
    return c;
}
const sel = (name, label, opts, val) =>
    `<label class="sel"><span class="sr">${label}</span><select id="f-${name}" name="${name}" class="${val && name !== 'sort' ? 'set' : ''}">${opts.map(([v, t]) => `<option value="${v}"${String(v) === String(val) ? ' selected' : ''}>${t}</option>`).join('')}</select></label>`;

export function filtersBar(f, where) {
    return `<form class="filters" id="filters" role="search" autocomplete="off">
    <label class="search"><span class="sr">Search movie title</span>${ic('search')}<input id="f-q" name="q" value="${esc(f.q)}" placeholder="search a title, director or actor…"></label>
    ${sel('genre', 'Genre', [['', 'any genre'], ...GENRES.map(g => [g.id, g.name])], f.genre)}
    ${sel('year', 'Release year', YEAR_OPTIONS, f.year)}
    ${sel('rating', 'Rating', [['', 'any rating'], ['8', '★ 8 and up'], ['7', '★ 7 and up'], ['6', '★ 6 and up']], f.rating)}
    ${sel('lang', 'Language', [['', 'any language'], ['en', 'English'], ['ja', 'Japanese'], ['ko', 'Korean'], ['th', 'Thai'], ['fr', 'French'], ['es', 'Spanish'], ['other', 'other']], f.lang)}
    ${sel('sort', 'Sort by', SORT_OPTIONS, f.sort)}
    ${where === 'films' ? `<button class="btn solid" type="submit">${ic('search')}find</button>` : ''}
  </form>`;
}
export function readFilters(form) {
    const fd = new FormData(form);
    return { q: (fd.get('q') || '').toString().trim(), genre: fd.get('genre') || '', year: fd.get('year') || '', rating: fd.get('rating') || '', lang: fd.get('lang') || '', sort: fd.get('sort') || 'POPULARITY', page: 1 };
}

export function pager(page, totalPages) {
    if (totalPages <= 1) return '';
    const nums = [];
    for (let i = 1; i <= totalPages; i++) {
        if (i === 1 || i === totalPages || Math.abs(i - page) <= 2) nums.push(i);
        else if (nums[nums.length - 1] !== '…') nums.push('…');
    }
    return `<button class="pg wide" data-act="page" data-n="${page - 1}"${page === 1 ? ' disabled' : ''}>${ic('chevL')}Prev</button>` +
        nums.map(i => i === '…' ? '<span class="pg-gap">…</span>' : `<button class="pg${i === page ? ' on' : ''}" data-act="page" data-n="${i}"${i === page ? ' aria-current="page"' : ''}>${i}</button>`).join('') +
        `<button class="pg wide" data-act="page" data-n="${page + 1}"${page === totalPages ? ' disabled' : ''}>Next${ic('chevR')}</button>`;
}