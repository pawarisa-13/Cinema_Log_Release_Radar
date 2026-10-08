import { boot } from '../app.js';
import { $ } from '../core/dom.js';
import { D, ic } from '../core/doodles.js';
import { secHead, strip, emptyState, skeletonRow } from '../core/ui.js';
import { st, session } from '../core/state.js';
import { movies } from '../api/movies.js';
import { card, ticket, filtersBar, readFilters, filtersToUrl } from '../components/movie-card.js';

const favNames = (session.favGenres || '').split('|').filter(Boolean);

function mountFilters() {
    $('#filters-slot').innerHTML = filtersBar({ q: '', genre: '', year: '', rating: '', lang: '', sort: 'POPULARITY' }, 'films');
    const form = $('#filters');
    const go = () => { location.href = filtersToUrl(readFilters(form)); };
    form.addEventListener('submit', e => { e.preventDefault(); go(); });
    form.addEventListener('change', e => { if (e.target.tagName === 'SELECT') go(); });
}

async function render() {
    const box = $('#sections');
    if (!box.innerHTML.trim()) box.innerHTML = `<section class="sec">${skeletonRow()}</section>`;
    const [now, love, picked, soon] = await Promise.all([
        movies.nowShowing(16), movies.topRated(16), movies.recommended(14), movies.upcoming(12)
    ]);
    const more = sort => `<a class="more" href="${filtersToUrl({ sort, page: 1 })}">More ${ic('arrowR')}</a>`;
    box.innerHTML = `
    <section class="sec" aria-labelledby="h-now">
      ${secHead('playing these days...', { id: 'h-now', note: 'on the big screen now', arrows: 's-now', more: more('RELEASE_DATE') })}
      ${now.length ? `<div class="filmstrip">${strip('s-now', now.map(card).join(''))}</div>`
            : emptyState(D.ghost(), 'the screens are dark', 'Connect a TMDB key to see what’s in theaters this week.')}
    </section>
    <section class="sec" aria-labelledby="h-love">
      ${secHead('movies people love', { id: 'h-love', note: 'the highest-rated films on the shelf', doodle: D.ghost(), arrows: 's-love', more: more('RATING') })}
      ${strip('s-love', love.map(card).join(''))}
    </section>
    <section class="sec" aria-labelledby="h-picked">
      ${secHead('picked for you', {
                id: 'h-picked',
                note: `<span class="arrow-note">psst... you might like these ${D.arrow()}</span>`,
                arrows: 's-picked',
                more: st.loggedIn ? `<button class="more" data-act="edit-profile">your genres: ${favNames.join(' · ') || 'none yet'} ${ic('pencil')}</button>` : ''
            })}
      ${picked.length ? strip('s-picked', picked.map(card).join(''))
            : emptyState(D.kid('box'), 'tell us what you like', 'Pick a few favorite genres and this shelf fills itself.', `<button class="btn" data-act="edit-profile">Choose genres</button>`)}
    </section>
    <section class="sec" aria-labelledby="h-soon">
      ${secHead('coming soon...', { id: 'h-soon', note: 'don’t forget to remind yourself!', doodle: D.bell() })}
      ${soon.length ? `<div class="tickets">${soon.map(ticket).join('')}</div>`
            : emptyState(D.bell(), 'nothing scheduled yet', 'Upcoming releases appear here after the TMDB sync.')}
    </section>`;
}

mountFilters();
boot(render, ['remind']);
