import { esc, fmtMD, isMobile } from '../core/dom.js';
import { ic, posterHtml } from '../core/doodles.js';
import { modal, popover } from '../core/ui.js';
import { registerActions } from '../core/actions.js';
import { st, MOVIES } from '../core/state.js';
import { likeBtn, wlBtn, ratingTxt } from './movie-card.js';

function openMenu(anchor, id) {
    const m = MOVIES.get(+id);
    if (!m) { location.href = `/movies/${id}`; return; }
    const out = m.released, inWl = st.watchlist.has(m.id), rem = st.reminders.get(m.id);
    const item = (act, icon, label, disabled, hint = '') =>
        `<button class="menu-i" role="menuitem" data-act="${disabled ? 'noop' : act}" data-id="${m.id}"${disabled ? ' aria-disabled="true"' : ''}>${ic(icon)}${label}${hint ? `<small>${hint}</small>` : ''}</button>`;
    popover(anchor, `<div role="menu" aria-label="Actions for ${esc(m.title)}">
    ${item('rate', 'star', 'Rate', !out, out ? '' : 'not out yet')}
    ${item('review', 'pencil', 'Write review', !out)}
    ${item('wl', inWl ? 'check' : 'plus', inWl ? 'Remove from watchlist' : 'Add to watchlist')}
    ${item('watched', 'eye', 'Mark as watched', !out)}
    ${item('collect', 'folder', 'Add to collection')}
    <div class="menu-sep"></div>
    ${item('remind', 'bell', rem ? 'Edit release reminder' : 'Set release reminder', out, out ? 'already out' : fmtMD(m.releaseDate))}
  </div>`, 'menu');
}

function openQuickView(id) {
    const m = MOVIES.get(+id), rem = st.reminders.get(m.id);
    modal(`<div class="day-pop"><span class="poster">${posterHtml(m)}</span><div style="min-width:0">
      <p class="eyebrow">${esc(m.genres.join(' · '))}</p><h2 class="h2" style="font-size:1.8rem;margin-top:4px">${esc(m.title)}</h2>
      <p class="hint">${m.year || 'TBA'} · ${ratingTxt(m)}</p></div></div>
    <div class="actions" style="margin-top:18px">${likeBtn(m, 'act')}${wlBtn(m, 'act')}
      ${m.released ? `<button class="act" data-act="watched" data-id="${m.id}">${ic('check')}Watched</button>`
            : `<button class="act ${rem ? 'on' : ''}" data-act="remind" data-id="${m.id}">${ic('bell')}${rem ? 'Reminder set' : 'Remind me'}</button>`}
      <button class="act" data-act="dots" data-id="${m.id}" aria-label="More actions">${ic('dots')}</button></div>
    <div class="form-actions" style="margin-top:16px"><a class="btn solid" href="/movies/${m.id}">Open full page ${ic('arrowR')}</a></div>`);
}

registerActions({
    open: ({ id }) => (isMobile() && MOVIES.has(+id) ? openQuickView(id) : (location.href = `/movies/${id}`)),
    dots: ({ id }, el) => openMenu(el, id)
});
