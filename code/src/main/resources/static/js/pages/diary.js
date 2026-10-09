import { boot } from '../app.js';
import { $, esc, MONTHS, pad, iso, parseD, todayIso, today, isMobile, fmtLong } from '../core/dom.js';
import { D, ic, posterHtml } from '../core/doodles.js';
import { secHead, emptyState, starsTxt } from '../core/ui.js';
import { registerActions } from '../core/actions.js';
import { diary } from '../api/library.js';

const PLACE = { CINEMA: 'Cinema', HOME: 'Home', OTHER: 'Other' };
const initial = new URLSearchParams(location.search).get('month');
let cur = /^\d{4}-\d{2}$/.test(initial || '') ? { y: +initial.slice(0, 4), m: +initial.slice(5) - 1 } : { y: today().getFullYear(), m: today().getMonth() };

function calendar(y, m, byDate) {
  const first = new Date(y, m, 1), start = (first.getDay() + 6) % 7, dim = new Date(y, m + 1, 0).getDate();
  const cells = Math.ceil((start + dim) / 7) * 7, now = todayIso();
  const wk = isMobile() ? ['M', 'T', 'W', 'T', 'F', 'S', 'S'] : ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];
  let g = '';
  for (let i = 0; i < cells; i++) {
    const d = new Date(y, m, 1 - start + i), key = iso(d), inMonth = d.getMonth() === m;
    const list = inMonth ? (byDate[key] || []) : [];
    let inner = '';
    if (list.length) {
      const offs = list.length === 1 ? [[0, -2]] : list.length === 2 ? [[-9, -6], [9, 5]] : [[-14, -8], [0, 2], [14, 9]];
      inner = `<button class="dposts" data-act="day" data-date="${key}" data-i="0" aria-label="${fmtLong(key)}: ${list.map(e => esc(e.movie.title)).join(', ')}">${list.slice(0, 3).map((e, j) => `<span class="dpost" style="--x:${offs[j][0]}px;--r:${offs[j][1]}deg;z-index:${j + 1}">${posterHtml(e.movie)}</span>`).join('')}<span class="dtape"></span>${list.length > 1 ? `<span class="dcount">×${list.length}</span>` : ''}</button>`;
    } else if (inMonth && key <= now) {
      inner = `<button class="dadd" data-act="add-diary" data-date="${key}" aria-label="Add a movie on ${fmtLong(key)}">${ic('plus')}</button>`;
    }
    g += `<div class="day${inMonth ? '' : ' out'}${key === now ? ' today' : ''}"><span class="dnum">${key === now ? D.todayCircle() : ''}${d.getDate()}</span>${inner}</div>`;
  }
  return `<div class="cal" role="grid" aria-label="${MONTHS[m]} ${y}"><div class="cal-h">${wk.map(w => `<div>${w}</div>`).join('')}</div><div class="cal-g">${g}</div>
    <div class="cal-foot"><span>tap a poster to open that day</span><span>·</span><span>hover an empty day to log a film</span></div></div>`;
}

async function render() {
  const key = `${cur.y}-${pad(cur.m + 1)}`;
  history.replaceState(null, '', `/diary?month=${key}`);
  const entries = await diary.month(key);
  const byDate = {};
  entries.forEach(e => (byDate[e.watchedDate] ||= []).push(e));
  const cinema = entries.filter(e => e.place === 'CINEMA').length, rated = entries.filter(e => e.rating);
  const avg = rated.length ? (rated.reduce((s, e) => s + e.rating, 0) / rated.length).toFixed(1) : '–';
  const isThisMonth = cur.y === today().getFullYear() && cur.m === today().getMonth();
  const newestFirst = entries.slice().reverse();
  $('#page').innerHTML = `
    <div class="monthbar">
      <button class="round-btn" data-act="cal" data-dir="-1" aria-label="Previous month">${ic('chevL')}</button>
      <h2 class="h2" aria-live="polite">${MONTHS[cur.m]} ${cur.y}</h2>
      <button class="round-btn" data-act="cal" data-dir="1" aria-label="Next month">${ic('chevR')}</button>
      ${isThisMonth ? '' : '<button class="btn sm ghost" data-act="cal" data-dir="0">back to today</button>'}
      <div class="msum"><span><b>${entries.length}</b> films</span><span><b>${cinema}</b> at the cinema</span><span><b>${avg}</b> avg ★</span></div>
    </div>
    ${calendar(cur.y, cur.m, byDate)}
    <section class="sec" aria-labelledby="h-pages">
      ${secHead(`${MONTHS[cur.m].toLowerCase()}’s pages`, { id: 'h-pages', note: entries.length ? 'tap any day to flip through it' : '' })}
      ${entries.length ? `<div class="pages">${newestFirst.map(e => {
        const idx = byDate[e.watchedDate].indexOf(e), d = parseD(e.watchedDate);
        return `<button class="entry" data-act="day" data-date="${e.watchedDate}" data-i="${idx}"><span class="d">${d.getDate()}<small>${MONTHS[d.getMonth()].slice(0, 3).toUpperCase()}</small></span><span class="poster">${posterHtml(e.movie)}</span><span style="min-width:0"><b>${esc(e.movie.title)}</b><span class="m-s">${starsTxt(e.rating)}<span>${PLACE[e.place]}</span></span>${e.review ? `<span class="q">“${esc(e.review)}”</span>` : ''}</span></button>`;
      }).join('')}</div>` : emptyState(D.kid('calendar'), 'your movie memories start here.', `Nothing logged in ${MONTHS[cur.m]} yet.`, `<button class="btn" data-act="add-diary">${ic('plus')}Add a Movie</button>`)}
    </section>`;
}

registerActions({
  cal({ dir }) {
    if (+dir === 0) cur = { y: today().getFullYear(), m: today().getMonth() };
    else { const d = new Date(cur.y, cur.m + +dir, 1); cur = { y: d.getFullYear(), m: d.getMonth() }; }
    render();
  }
});

document.addEventListener('diary:saved', e => { const d = parseD(e.detail); cur = { y: d.getFullYear(), m: d.getMonth() }; });

boot(render, ['diary']);
