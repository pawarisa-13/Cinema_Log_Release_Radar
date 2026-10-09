import { esc, fmtMD, parseD, WEEKDAYS } from '../core/dom.js';
import { D, ic, posterHtml } from '../core/doodles.js';
import { modal, starsTxt } from '../core/ui.js';
import { registerActions } from '../core/actions.js';
import { cache } from '../core/state.js';
import { diary } from '../api/library.js';

const PLACE = { CINEMA: 'Cinema', HOME: 'Home', OTHER: 'Other' };

export async function openDay(date, idx = 0) {
  const list = await diary.day(date);
  if (!list.length) return;
  let i = Math.min(+idx || 0, list.length - 1);
  const paint = sh => {
    const e = list[i], m = cache(e.movie);
    sh.querySelector('#day-body').innerHTML = `
      <div class="day-pop"><a class="poster" href="/movies/${m.id}" aria-label="Open ${esc(m.title)}">${posterHtml(m)}</a>
        <div style="min-width:0">${list.length > 1 ? `<p class="counter">${i + 1} / ${list.length}</p>` : ''}
          <h3 class="h2" style="font-size:1.9rem">${esc(m.title)}</h3><p class="hint">${m.year || ''}</p>
          <div style="margin:10px 0 4px;font-size:1.2rem">${starsTxt(e.rating)}</div>
          <p class="eyebrow" style="margin-top:12px">my review</p>
          ${e.review ? `<p class="quote">“${esc(e.review)}”</p>` : '<p class="note">no words yet. watched it? tell us what you thought!</p>'}
          <div class="kv"><span>watched at:</span><b>${PLACE[e.place] || e.place}</b></div>
          <div style="display:flex;gap:8px;margin-top:16px;flex-wrap:wrap"><button class="btn sm" data-act="edit-entry" data-entry="${e.id}">${ic('pencil')}Edit Review</button><a class="btn sm ghost" href="/movies/${m.id}">film page ${ic('arrowR')}</a></div>
        </div></div>
      ${list.length > 1 ? `<div class="day-nav"><button class="btn sm" id="dp-prev"${i === 0 ? ' disabled' : ''}>${ic('arrowL')}Previous</button><span class="counter">${i + 1} / ${list.length}</span><button class="btn sm" id="dp-next"${i === list.length - 1 ? ' disabled' : ''}>Next${ic('arrowR')}</button></div>` : ''}`;
    const prev = sh.querySelector('#dp-prev'), next = sh.querySelector('#dp-next');
    if (prev) prev.onclick = () => { if (i > 0) { i--; paint(sh); } };
    if (next) next.onclick = () => { if (i < list.length - 1) { i++; paint(sh); } };
  };
  modal(`<div class="sheet-head">${D.reel()}<div><p class="eyebrow">${WEEKDAYS[parseD(date).getDay()]}</p><h2 class="h2">${fmtMD(date)}</h2></div></div><div id="day-body"></div>`, {
    cls: 'wide',
    onMount(sh) {
      paint(sh);
      sh.addEventListener('keydown', e => {
        if (e.key === 'ArrowLeft') sh.querySelector('#dp-prev')?.click();
        if (e.key === 'ArrowRight') sh.querySelector('#dp-next')?.click();
      });
      let x0 = null;
      sh.addEventListener('touchstart', e => { x0 = e.touches[0].clientX; }, { passive: true });
      sh.addEventListener('touchend', e => {
        if (x0 == null) return;
        const dx = e.changedTouches[0].clientX - x0;
        if (Math.abs(dx) > 50) sh.querySelector(dx < 0 ? '#dp-next' : '#dp-prev')?.click();
        x0 = null;
      });
    }
  });
}

registerActions({ day: ({ date, i }) => openDay(date, i) });
