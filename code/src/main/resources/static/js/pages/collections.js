import { boot } from '../app.js';
import { $, esc } from '../core/dom.js';
import { D, ic } from '../core/doodles.js';
import { secHead, emptyState, toast, modal, closeModal, showError } from '../core/ui.js';
import { registerActions } from '../core/actions.js';
import { collections } from '../api/library.js';
import { card } from '../components/movie-card.js';
import { colCard } from './library-parts.js';

const collectionId = $('#main').dataset.collectionId;

async function renderAll() {
  const cols = await collections.list();
  $('#page').innerHTML = `${secHead('my collections', { note: 'little shelves for every mood', doodle: D.kid('box'), more: `<button class="btn" data-act="new-collection">${ic('plus')}Create Collection</button>` })}
    <div class="cgrid" style="margin-top:24px">${cols.map(colCard).join('')}<button class="ccard new" data-act="new-collection"><div class="fan"><span>${ic('plus')}Create Collection</span></div><span class="c-name muted">new shelf</span></button></div>`;
}

async function renderOne() {
  let c;
  try { c = await collections.get(collectionId); } catch (e) {
    $('#page').innerHTML = emptyState(D.kid('box'), 'that collection is gone', 'It may have been deleted.', '<a class="btn" href="/collections">All collections</a>');
    return;
  }
  document.title = `${c.name} 	· Poppy Night`;
  $('#page').innerHTML = `${secHead(esc(c.name), { note: esc(c.description || `${c.movies.length} films`), more: `<button class="btn ghost sm" data-act="edit-collection" data-name="${esc(c.name)}" data-desc="${esc(c.description || '')}">${ic('pencil')}Rename</button><button class="btn ghost danger sm" data-act="del-collection">${ic('trash')}Delete collection</button>` })}
    ${c.movies.length ? `<p class="hint" style="margin-top:4px">${c.movies.length} films · add more from any movie’s ⋯ menu</p>
      <div class="grid" style="margin-top:20px">${c.movies.map(m => `<div>${card(m)}<button class="link" style="margin-top:4px;font-size:.8rem" data-act="col-remove" data-id="${m.id}">${ic('x')}remove</button></div>`).join('')}</div>`
      : emptyState(D.kid('box'), 'this collection is waiting for its movies.', 'Open any film’s ⋯ menu and choose “Add to collection”.', '<a class="btn" href="/films">Explore Movies</a>')}`;
}

registerActions({
  async 'col-remove'({ id }) {
    const c = await collections.removeMovie(collectionId, id);
    toast(`Removed from “${c.name}”`, 'folder');
    renderOne();
  },
  async 'del-collection'(_, el) {
    if (el.dataset.confirm !== '1') { el.dataset.confirm = '1'; el.innerHTML = `${ic('trash')}Tap again to delete`; return; }
    await collections.remove(collectionId);
    location.href = '/collections';
  },
  'edit-collection'({ name, desc }) {
    modal(`<div class="sheet-head">${D.kid('box')}<div><h2 class="h2">rename collection</h2></div></div>
      <form class="form" id="ec-form" novalidate>
        <label class="fld"><span>name</span><input type="text" id="ec-name" maxlength="60" value="${esc(name)}" autofocus></label>
        <label class="fld"><span>description <i>(optional)</i></span><input type="text" id="ec-desc" maxlength="200" value="${esc(desc)}"></label>
        <p class="err" id="ec-err" hidden></p>
        <div class="form-actions"><button type="button" class="btn ghost" data-act="close">Cancel</button><button class="btn solid" type="submit">Save</button></div>
      </form>`, {
      onMount(sh) {
        sh.querySelector('#ec-form').addEventListener('submit', async e => {
          e.preventDefault();
          try {
            await collections.update(collectionId, { name: sh.querySelector('#ec-name').value, description: sh.querySelector('#ec-desc').value });
            closeModal(); toast('Collection saved.', 'folder'); renderOne();
          } catch (x) { showError(sh.querySelector('#ec-err'), x); }
        });
      }
    });
  }
});

boot(collectionId ? renderOne : renderAll, ['collection', 'like', 'watchlist']);
