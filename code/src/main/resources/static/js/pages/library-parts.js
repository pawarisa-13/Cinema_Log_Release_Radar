import { esc, fmtShort } from '../core/dom.js';
import { D, ic, posterHtml } from '../core/doodles.js';
import { starsTxt } from '../core/ui.js';
import { st, cache } from '../core/state.js';

export function seenCard(e) {
  const m = cache(e.movie);
  return `<article class="mcard">
    <div class="poster" data-act="edit-entry" data-entry="${e.id}" role="button" tabindex="0" aria-label="Edit your entry for ${esc(m.title)}">${posterHtml(m)}${st.liked.has(m.id) ? `<span class="liked-tag">${ic('heart')}</span>` : ''}</div>
    <div class="m-t">${esc(m.title)}</div>
    <div>${starsTxt(e.rating)}</div>
    <div class="seen-meta">Watched · ${fmtShort(e.watchedDate)}</div>
    <div><span class="rvstate ${e.review ? '' : 'none'}">${e.review ? '✎ reviewed' : 'no review yet'}</span></div>
  </article>`;
}

export function colCard(c) {
  const ps = c.previewMovies;
  const fan = ps.length
    ? ps.map((m, i) => `<span class="poster" style="left:calc(50% - ${43 - (i - (ps.length - 1) / 2) * 62}px);transform:rotate(${(i - (ps.length - 1) / 2) * 8}deg);z-index:${i === 1 ? 3 : 2}">${posterHtml(m)}</span>`).join('')
    : `<div class="empty">${D.kid('box')}</div>`;
  return `<a class="ccard" href="/collections/${c.id}"><div class="fan">${fan}</div><span class="c-name">${esc(c.name)}</span>
    <span class="c-meta">${c.movieCount} film${c.movieCount === 1 ? '' : 's'}${c.description ? ' · ' + esc(c.description) : ''}</span></a>`;
}
