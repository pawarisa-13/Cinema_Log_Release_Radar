import { esc } from '../core/dom.js';
import { ic, posterHtml } from '../core/doodles.js';
import { popover } from '../core/ui.js';
import { registerActions } from '../core/actions.js';
import { movies } from '../api/movies.js';
import { filtersToUrl } from './movie-card.js';

const row = m => `<a class="pick" href="/movies/${m.id}"><span class="poster">${posterHtml(m)}</span><span>${esc(m.title)}<small>${m.year || 'TBA'}</small></span></a>`;

function openSearch(anchor) {
    const p = popover(anchor, `<label class="search sm"><span class="sr">Search movies</span>${ic('search')}<input id="qs-in" placeholder="search a title, director or actor…" autocomplete="off"></label>
    <div class="pick-list" id="qs-list"><p class="hint" style="padding:8px">Try “ghibli”, “Villeneuve” or “ghost”.</p></div>`, 'search-pop');
    if (!p) return;
    const input = p.querySelector('#qs-in'), list = p.querySelector('#qs-list');
    let timer;
    input.focus();
    input.addEventListener('input', () => {
        clearTimeout(timer);
        timer = setTimeout(async () => {
            const q = input.value.trim();
            if (q.length < 2) { list.innerHTML = ''; return; }
            const res = await movies.search({ q, size: 6 });
            list.innerHTML = (res.content.length ? res.content.map(row).join('') : '<p class="hint" style="padding:8px">No matches yet.</p>')
                + `<button class="menu-i" data-act="search-all">${ic('search')}See all results for “${esc(q)}”<small>${res.totalElements}</small></button>`;
        }, 250);
    });
    input.addEventListener('keydown', e => { if (e.key === 'Enter') { e.preventDefault(); goToResults(input.value); } });
}
const goToResults = q => { location.href = filtersToUrl({ q: q.trim(), page: 1, sort: 'POPULARITY' }); };

registerActions({
    'search-open': (_, el) => openSearch(el),
    'search-all': () => goToResults(document.querySelector('#qs-in')?.value || '')
});