import { hydrateDoodles } from './core/doodles.js';
import './core/actions.js';
import { st, refreshLibrary, setPageRenderer } from './core/state.js';
import { toast } from './core/ui.js';
import { paintNav } from './components/nav.js';
import './components/search.js';
import './components/movie-menu.js';
import './components/library-actions.js';
import './components/log-modal.js';
import './components/day-popup.js';
import './components/collect-modal.js';
import './components/remind-modal.js';
import './components/profile-modal.js';

/**
 * @param render   async function that draws the page
 * @param kinds    which changes should redraw it: 'like', 'watchlist', 'diary', 'collection', 'remind' or '*'
 */
export async function boot(render, kinds = []) {
  hydrateDoodles();
  try {
    await refreshLibrary();
  } catch (e) {
    console.warn('Could not load library state', e);
  }
  paintNav();
  if (render) {
    setPageRenderer(render, kinds);
    try {
      await render();
    } catch (e) {
      console.error(e);
      toast(e.message || 'Could not load this page.', 'x');
    }
    hydrateDoodles();
  }
}

export { st };
