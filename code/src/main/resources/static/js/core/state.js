import { $$ } from './dom.js';
import { ic } from './doodles.js';
import { library } from '../api/library.js';

/** Filled by the Thymeleaf layout: <span id="session" data-auth="true" data-email="…" …> */
export const session = document.getElementById('session')?.dataset || {};

export const st = {
  loggedIn: session.auth === 'true',
  email: session.email || '',
  liked: new Set(),
  watchlist: new Set(),
  watched: new Map(),     // movieId -> { entryId, watchedDate, rating, reviewed }
  reminders: new Map(),   // movieId -> { offsetDays, channel, status }
  unread: 0
};

/** Every movie the page has drawn, by id — menus and modals read titles from here. */
export const MOVIES = new Map();
export const cache = m => { (Array.isArray(m) ? m : [m]).forEach(x => x && x.id && MOVIES.set(x.id, x)); return m; };

export async function refreshLibrary() {
  if (!st.loggedIn) return;
  const s = await library.state();
  st.liked = new Set(s.likedMovieIds);
  st.watchlist = new Set(s.watchlistMovieIds);
  st.watched = new Map(s.watched.map(w => [w.movieId, w]));
  st.reminders = new Map(s.reminders.map(r => [r.movieId, r]));
  st.unread = s.unreadNotifications;
  document.dispatchEvent(new CustomEvent('library:changed'));
}

/** Flip ♡ and + buttons for one movie without redrawing the page. */
export function syncButtons(id) {
  $$(`[data-act="like"][data-id="${id}"]`).forEach(b => {
    const on = st.liked.has(+id);
    b.classList.toggle('on', on);
    b.setAttribute('aria-pressed', on);
    const l = b.querySelector('.lbl'); if (l) l.textContent = on ? l.dataset.on : l.dataset.off;
  });
  $$(`[data-act="wl"][data-id="${id}"]`).forEach(b => {
    const on = st.watchlist.has(+id);
    b.classList.toggle('on', on);
    b.setAttribute('aria-pressed', on);
    const l = b.querySelector('.lbl');
    if (l) l.textContent = on ? l.dataset.on : l.dataset.off;
    else b.querySelector('svg').outerHTML = ic(on ? 'check' : 'plus');
  });
}

/* the current page tells us how to redraw itself, and for which kinds of change */
let renderer = null;
export function setPageRenderer(fn, kinds = []) { renderer = { fn, kinds }; }

export async function afterChange(movieId, kind) {
  await refreshLibrary();
  if (movieId) syncButtons(movieId);
  if (renderer && (renderer.kinds.includes('*') || renderer.kinds.includes(kind))) {
    const y = scrollY;
    await renderer.fn();
    scrollTo(0, y);
  }
}
