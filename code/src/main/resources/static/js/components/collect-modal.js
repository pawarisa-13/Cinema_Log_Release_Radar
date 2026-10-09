import { esc } from '../core/dom.js';
import { D, ic } from '../core/doodles.js';
import { modal, closeModal, toast, showError } from '../core/ui.js';
import { registerActions } from '../core/actions.js';
import { MOVIES, cache, afterChange } from '../core/state.js';
import { collections } from '../api/library.js';
import { movies } from '../api/movies.js';

const count = n => `${n} film${n === 1 ? '' : 's'}`;

async function openCollect(movieId) {
  const m = MOVIES.get(+movieId) || cache(await movies.get(movieId));
  const paint = async sh => {
    const cols = await collections.list();
    sh.querySelector('#col-list').innerHTML = cols.map(c => {
      const on = c.movieIds.includes(m.id);
      return `<button type="button" class="col-row${on ? ' on' : ''}" data-act="col-toggle" data-col="${c.id}" data-id="${m.id}" aria-pressed="${on}"><span class="box">${ic('check')}</span><b>${esc(c.name)}</b><small>${count(c.movieCount)}</small></button>`;
    }).join('') || '<p class="hint">No collections yet. Start one below.</p>';
  };
  modal(`<div class="sheet-head">${D.kid('box')}<div><h2 class="h2">add to collection</h2><p class="note">${esc(m.title)}</p></div></div>
    <div id="col-list"></div>
    <form class="inline-new" id="col-new"><label class="sr" for="col-name">New collection name</label><input id="col-name" placeholder="+ create new collection…" maxlength="60"><button class="btn sm" type="submit">${ic('plus')}Create</button></form>
    <p class="err" id="col-err" hidden></p>
    <div class="form-actions" style="margin-top:18px"><button class="btn solid" data-act="close">Done</button></div>`, {
    onMount(sh) {
      paint(sh);
      sh.querySelector('#col-new').addEventListener('submit', async e => {
        e.preventDefault();
        const input = sh.querySelector('#col-name'), err = sh.querySelector('#col-err');
        try {
          const c = await collections.create({ name: input.value, description: null });
          await collections.addMovie(c.id, m.id);
          input.value = ''; err.hidden = true;
          toast(`Created “${c.name}” and added ${m.title}.`, 'folder');
          paint(sh);
        } catch (x) { showError(err, x); }
      });
    }
  });
}

export function openNewCollection() {
  modal(`<div class="sheet-head">${D.kid('box')}<div><h2 class="h2">a new collection</h2><p class="note">what’s the mood?</p></div></div>
    <form class="form" id="nc-form" novalidate>
      <label class="fld"><span>name</span><input type="text" id="nc-name" maxlength="60" placeholder="e.g. Rainy Sunday" autofocus></label>
      <label class="fld"><span>description <i>(optional)</i></span><input type="text" id="nc-desc" maxlength="200" placeholder="a little note for future you"></label>
      <p class="err" id="nc-err" hidden></p>
      <div class="form-actions"><button type="button" class="btn ghost" data-act="close">Cancel</button><button class="btn solid" type="submit">${ic('plus')}Create Collection</button></div>
    </form>`, {
    onMount(sh) {
      sh.querySelector('#nc-form').addEventListener('submit', async e => {
        e.preventDefault();
        try {
          const c = await collections.create({ name: sh.querySelector('#nc-name').value, description: sh.querySelector('#nc-desc').value });
          closeModal();
          location.href = `/collections/${c.id}`;
        } catch (x) { showError(sh.querySelector('#nc-err'), x); }
      });
    }
  });
}

registerActions({
  collect: ({ id }) => openCollect(id),
  'new-collection': () => openNewCollection(),
  async 'col-toggle'({ col, id }, el) {
    const on = el.classList.contains('on');
    const c = await (on ? collections.removeMovie(col, id) : collections.addMovie(col, id));
    el.classList.toggle('on', !on);
    el.setAttribute('aria-pressed', !on);
    el.querySelector('small').textContent = count(c.movies.length);
    toast(on ? `Removed from “${c.name}”` : `Added to “${c.name}”`, 'folder');
    await afterChange(+id, 'collection');
  }
});
