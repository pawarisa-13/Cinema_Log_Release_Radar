import { boot } from '../app.js';
import { $, esc } from '../core/dom.js';
import { D } from '../core/doodles.js';
import { emptyState, loading } from '../core/ui.js';
import { registerActions } from '../core/actions.js';
import { movies } from '../api/movies.js';
import { card, filtersBar, readFilters, filtersFromUrl, filtersToUrl, toCriteria, pager } from '../components/movie-card.js';

let f = filtersFromUrl();
const filtered = () => !!(f.q || f.genre || f.year || f.rating || f.lang);

async function renderResults() {
    history.replaceState(null, '', filtersToUrl(f));
    $('#results').innerHTML = loading();
    const res = await movies.search(toCriteria(f, 40));
    $('#cat-title').textContent = filtered() ? 'search results' : 'the whole shelf';
    $('#cat-count').innerHTML = `${res.totalElements} movie${res.totalElements === 1 ? '' : 's'}${f.q ? ` matching “${esc(f.q)}”` : ''}${filtered() ? ' · <button class="link" data-act="clear-filters">clear filters</button>' : ''}`;
    $('#results').innerHTML = res.content.length
        ? `<div class="grid">${res.content.map(card).join('')}</div>`
        : emptyState(D.ghost(), 'no movies match that', 'Try a shorter search or fewer filters.', '<button class="btn" data-act="clear-filters">Clear filters</button>');
    $('#pager').innerHTML = pager(res.page + 1, res.totalPages);
}

function mountFilters() {
    $('#filters-slot').innerHTML = filtersBar(f, 'catalog');
    const form = $('#filters');
    let timer;
    const update = () => { f = { ...readFilters(form) }; renderResults(); };
    form.addEventListener('submit', e => { e.preventDefault(); update(); });
    form.addEventListener('change', e => { if (e.target.tagName === 'SELECT') { e.target.classList.toggle('set', !!e.target.value && e.target.name !== 'sort'); update(); } });
    $('#f-q').addEventListener('input', () => { clearTimeout(timer); timer = setTimeout(update, 300); });
}

registerActions({
    page({ n }) { f.page = +n; renderResults().then(() => $('#cat-title').scrollIntoView({ behavior: 'smooth', block: 'start' })); },
    'clear-filters'() { f = { q: '', genre: '', year: '', rating: '', lang: '', sort: f.sort, page: 1 }; mountFilters(); renderResults(); }
});

mountFilters();
boot(renderResults, ['remind']);