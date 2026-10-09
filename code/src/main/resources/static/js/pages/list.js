import { boot } from '../app.js';
import { $, esc, fmtShort, fmtMD } from '../core/dom.js';
import { D, ic } from '../core/doodles.js';
import { secHead, emptyState } from '../core/ui.js';
import { diary, watchlist, likes } from '../api/library.js';
import { card } from '../components/movie-card.js';
import { seenCard } from './library-parts.js';

const type = $('#main').dataset.listType;
let sort = 'watchedDate,desc';

function wlCard(m) {
  return `<div class="mcard">${card(m).replace(/^<article class="mcard">|<\/article>$/g, '')}
    <div class="seen-meta">${m.released ? `released ${fmtShort(m.releaseDate)}` : `out ${m.releaseDate ? fmtMD(m.releaseDate) : 'TBA'}`}</div>
    <div class="wl-acts">${m.released ? `<button class="btn sm" data-act="watched" data-id="${m.id}">${ic('check')}watched</button>` : `<button class="btn sm" data-act="remind" data-id="${m.id}">${ic('bell')}remind</button>`}
      <button class="btn sm ghost" data-act="wl" data-id="${m.id}" aria-label="Remove ${esc(m.title)} from watchlist">${ic('x')}remove</button></div></div>`;
}

const PAGES = {
  async watched() {
    const res = await diary.page(0, 100, sort);
    return `${secHead('movies I’ve seen', {
      note: `${res.totalElements} diary entries · tap a poster to edit your rating, review or date`, doodle: D.reel(),
      more: `<div class="sel"><select id="ws" aria-label="Sort watched movies"><option value="watchedDate,desc"${sort === 'watchedDate,desc' ? ' selected' : ''}>most recent</option><option value="rating,desc"${sort === 'rating,desc' ? ' selected' : ''}>my rating</option></select></div><button class="btn" data-act="add-diary">${ic('plus')}Add to Diary</button>`
    })}${res.content.length ? `<div class="grid" style="margin-top:24px">${res.content.map(seenCard).join('')}</div>`
      : emptyState(D.kid('calendar'), 'your movie memories start here.', '', `<button class="btn" data-act="add-diary">${ic('plus')}Add a Movie</button>`)}`;
  },
  async watchlist() {
    const list = await watchlist.list();
    return `${secHead('things I want to watch', { note: list.length ? `${list.length} films waiting for you` : '', doodle: D.kid('ticket') })}
      ${list.length ? `<div class="grid" style="margin-top:24px">${list.map(wlCard).join('')}</div>`
        : emptyState(D.shelf(), 'nothing here yet...', 'Let’s find something to watch.', '<a class="btn" href="/films">Explore Movies</a>')}`;
  },
  async liked() {
    const list = await likes.list();
    return `${secHead('movies I liked', { note: 'hearts are separate from your watchlist: like what you want to see, what you’ve seen, or just what you enjoyed', doodle: D.ghost() })}
      ${list.length ? `<div class="grid" style="margin-top:24px">${list.map(card).join('')}</div>`
        : emptyState(D.ghost(), 'no hearts yet', 'Tap ♡ on any film to keep it here.', '<a class="btn" href="/films">Explore Movies</a>')}`;
  }
};

async function render() {
  $('#page').innerHTML = await PAGES[type]();
  $('#ws')?.addEventListener('change', e => { sort = e.target.value; render(); });
}

boot(render, type === 'watched' ? ['diary', 'like'] : type === 'watchlist' ? ['watchlist', 'diary'] : ['like']);
