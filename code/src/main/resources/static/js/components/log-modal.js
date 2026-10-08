import { esc, fmtMD, fmtShort, todayIso, daysFromToday } from '../core/dom.js';
import { D, ic, posterHtml } from '../core/doodles.js';
import { modal, closeModal, toast, starsInput, showError } from '../core/ui.js';
import { registerActions } from '../core/actions.js';
import { st, MOVIES, cache, afterChange } from '../core/state.js';
import { diary, watchlist } from '../api/library.js';
import { movies } from '../api/movies.js';

const pickRow = m => { cache(m); return `<button type="button" class="pick" data-id="${m.id}"><span class="poster">${posterHtml(m)}</span><span>${esc(m.title)}<small>${m.year || 'TBA'}</small></span></button>`; };
const movieRow = m => `<div class="mrow"><span class="poster">${posterHtml(m)}</span><span style="flex:1"><b>${esc(m.title)}</b><small>${m.year || 'TBA'}</small></span></div>`;

export async function openLog(movieId, mode = 'watched', { entryId = null, date = null } = {}) {
  let entry = null;
  if (entryId) entry = await diary.get(entryId);
  else if (movieId && (mode === 'rate' || mode === 'review') && st.watched.has(+movieId)) entry = await diary.get(st.watched.get(+movieId).entryId);
  let m = entry ? cache(entry.movie) : movieId ? (MOVIES.get(+movieId) || cache(await movies.get(movieId))) : null;
  if (m && !m.released) { toast(`You can log ${m.title} once it’s out on ${fmtMD(m.releaseDate)}.`, 'bell'); return; }

  const isEdit = !!entry;
  const title = { watched: st.watched.has(m?.id) ? 'logging a rewatch?' : 'you watched it!', rate: 'how was it?', review: 'write a little review', diary: 'add to diary', edit: 'edit your entry' }[mode];
  const sub = isEdit ? `updating your ${fmtShort(entry.watchedDate)} entry` : { watched: 'save the date, the stars and a thought.', rate: 'tap the stars.', review: 'a line is plenty.', diary: 'search a movie and when you saw it.', edit: '' }[mode];
  const suggest = mode === 'diary' && !m ? (await watchlist.list()).filter(x => x.released).slice(0, 4) : [];
  const place = entry?.place || (m && daysFromToday(m.releaseDate) > -60 ? 'CINEMA' : 'HOME');
  const picker = `<div class="fld"><span>movie</span><div id="lf-picked"></div><div id="lf-picker">
      <label class="search sm"><span class="sr">Search movie</span>${ic('search')}<input id="lf-search" placeholder="search a movie…" autofocus></label>
      <div class="pick-list" id="pick-list">${suggest.length ? `<div class="pick-h">from your watchlist</div>${suggest.map(pickRow).join('')}` : ''}</div></div></div>`;

  modal(`<div class="sheet-head">${mode === 'diary' || mode === 'edit' ? D.reel() : D.kid('ticket')}<div><h2 class="h2">${title}</h2><p class="note">${sub}</p></div></div>
    <form id="log-form" class="form" novalidate>
      ${m ? movieRow(m) : picker}
      <div class="row2">
        <label class="fld"><span>date watched</span><input type="date" id="lf-date" name="date" value="${date || (isEdit ? entry.watchedDate : todayIso())}" max="${todayIso()}" required></label>
        <fieldset class="fld"><legend>rating</legend>${starsInput('lf-rating', isEdit ? entry.rating : 0)}</fieldset>
      </div>
      <label class="fld"><span>${mode === 'review' ? 'your review' : 'short review'} <i>(optional)</i></span><textarea id="lf-review" name="review" rows="4" maxlength="1000" placeholder="what stayed with you?"${mode === 'review' ? ' autofocus' : ''}>${esc(isEdit ? entry.review || '' : '')}</textarea></label>
      <fieldset class="fld"><legend>where did you watch it?</legend><div class="chips">${[['CINEMA', 'Cinema'], ['HOME', 'Home'], ['OTHER', 'Other']].map(([v, l]) => `<label class="chip"><input type="radio" name="place" value="${v}"${v === place ? ' checked' : ''}><span>${l}</span></label>`).join('')}</div></fieldset>
      ${!isEdit && (mode === 'rate' || mode === 'review') ? '<p class="hint">This also marks it as watched and adds it to your diary.</p>' : ''}
      <p class="err" id="lf-err" hidden></p>
      <div class="form-actions">${isEdit ? `<button type="button" class="btn ghost danger spacer" data-act="del-entry" data-entry="${entry.id}">${ic('trash')}Delete entry</button>` : ''}
        <button type="button" class="btn ghost" data-act="close">Cancel</button>
        <button class="btn solid" type="submit">${mode === 'review' ? 'Save Review' : mode === 'rate' ? 'Save Rating' : isEdit ? 'Save changes' : 'Save to Diary'}</button></div>
    </form>`, {
    onMount(sh) {
      let picked = m ? m.id : null;
      const search = sh.querySelector('#lf-search');
      if (search) {
        const list = sh.querySelector('#pick-list');
        let timer;
        search.addEventListener('input', () => {
          clearTimeout(timer);
          timer = setTimeout(async () => {
            const q = search.value.trim();
            if (q.length < 2) { list.innerHTML = ''; return; }
            const res = await movies.search({ q, size: 8 });
            const items = res.content.filter(x => x.released);
            list.innerHTML = items.length ? items.map(pickRow).join('') : '<p class="hint" style="padding:8px">No released movie matches that. Try another title.</p>';
          }, 250);
        });
        list.addEventListener('click', e => {
          const b = e.target.closest('.pick'); if (!b) return;
          picked = +b.dataset.id;
          const pm = MOVIES.get(picked);
          sh.querySelector('#lf-picker').hidden = true;
          sh.querySelector('#lf-picked').innerHTML = movieRow(pm).replace('</div>', '<button type="button" class="link" id="lf-change">change</button></div>');
          sh.querySelector('#lf-change').onclick = () => { picked = null; sh.querySelector('#lf-picked').innerHTML = ''; sh.querySelector('#lf-picker').hidden = false; search.focus(); };
          if (daysFromToday(pm.releaseDate) > -60) sh.querySelector('input[name=place][value=CINEMA]').checked = true;
        });
      }
      sh.querySelector('#log-form').addEventListener('submit', async e => {
        e.preventDefault();
        const fd = new FormData(e.target), err = sh.querySelector('#lf-err');
        const body = { watchedDate: fd.get('date'), rating: fd.get('lf-rating') ? +fd.get('lf-rating') : null, review: (fd.get('review') || '').toString(), place: fd.get('place') || 'HOME' };
        if (!picked) return showError(err, { message: 'Pick a movie first.' });
        if (mode === 'rate' && !body.rating) return showError(err, { message: 'Tap a star to rate it.' });
        try {
          const saved = isEdit ? await diary.update(entry.id, body) : await diary.create({ movieId: picked, ...body });
          closeModal();
          document.dispatchEvent(new CustomEvent('diary:saved', { detail: saved.watchedDate }));
          toast(isEdit ? 'Diary entry updated.' : `${saved.movie.title} saved to your diary · ${fmtShort(saved.watchedDate)}`);
          await afterChange(picked, 'diary');
        } catch (x) { showError(err, x); }
      });
    }
  });
}

registerActions({
  watched: ({ id }) => openLog(id, 'watched'),
  rate: ({ id }) => openLog(id, 'rate'),
  review: ({ id }) => openLog(id, 'review'),
  'edit-entry': ({ entry }) => openLog(null, 'edit', { entryId: entry }),
  'add-diary': ({ date }) => openLog(null, 'diary', { date: date || null }),
  async 'del-entry'({ entry }, el) {
    if (el.dataset.confirm !== '1') { el.dataset.confirm = '1'; el.innerHTML = `${ic('trash')}Tap again to delete`; return; }
    await diary.remove(entry);
    closeModal();
    toast('Entry deleted.', 'trash');
    await afterChange(null, 'diary');
  }
});
