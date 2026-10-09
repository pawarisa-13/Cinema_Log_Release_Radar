import { toast } from '../core/ui.js';
import { registerActions } from '../core/actions.js';
import { st, MOVIES, afterChange } from '../core/state.js';
import { likes, watchlist } from '../api/library.js';

const title = id => MOVIES.get(+id)?.title || 'this movie';

registerActions({
  async like({ id }) {
    const on = st.liked.has(+id);
    await (on ? likes.remove(id) : likes.add(id));
    toast(on ? `Removed ${title(id)} from liked` : `Liked ${title(id)} ♡`, 'heart');
    await afterChange(+id, 'like');
  },
  async wl({ id }) {
    const on = st.watchlist.has(+id);
    await (on ? watchlist.remove(id) : watchlist.add(id));
    toast(on ? `Removed ${title(id)} from your watchlist` : `Added ${title(id)} to your watchlist`, on ? 'x' : 'bookmark');
    await afterChange(+id, 'watchlist');
  }
});
