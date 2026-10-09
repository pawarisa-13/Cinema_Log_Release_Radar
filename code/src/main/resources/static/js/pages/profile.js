import { boot } from '../app.js';
import { $, esc, fmtMD } from '../core/dom.js';
import { D, R, ic, posterHtml, avatar } from '../core/doodles.js';
import { secHead, strip, emptyState } from '../core/ui.js';
import { cache, session } from '../core/state.js';
import { reminders } from '../api/account.js';
import { library, diary, watchlist, likes, collections } from '../api/library.js';
import { card } from '../components/movie-card.js';
import { seenCard, colCard } from './library-parts.js';

function genreChart(rows) {
  if (!rows.length) return emptyState(D.kid('calendar'), 'no films yet', 'Log a few movies to see your mix.');
  const max = Math.max(...rows.map(r => r.count)), rowH = 46, X0 = 132, BW = 330;
  const fills = ['fy', 'fk', 'fb', 'fg', 'fl', 'fy'];
  let s = `<defs><pattern id="hatch" patternUnits="userSpaceOnUse" width="7" height="7" patternTransform="rotate(40)"><line x1="0" y1="0" x2="0" y2="7" stroke="currentColor" stroke-width="1.1" opacity=".55"/></pattern></defs>`;
  rows.forEach(({ genre, count }, i) => {
    const y = 12 + i * rowH, w = Math.max(14, BW * count / max);
    s += `<text class="lbl" x="0" y="${y + 21}">${esc(genre)}</text>`;
    s += `<path class="${fills[i]}" stroke="none" d="${R.rect(X0, y + 2, w, 24, 200 + i, 1.4)}"/>`;
    s += `<path fill="url(#hatch)" stroke="currentColor" stroke-width="1.6" d="${R.rect(X0, y + 2, w, 24, 300 + i, 1.4)}"/>`;
    s += `<text class="val" x="${X0 + w + 10}" y="${y + 19}">${count} film${count > 1 ? 's' : ''}</text>`;
  });
  return `<svg viewBox="0 0 540 ${rows.length * rowH + 16}" role="img" aria-label="Genres watched most: ${rows.map(r => `${r.genre} ${r.count}`).join(', ')}" fill="none" stroke-linecap="round" style="color:var(--ink)">${s}</svg>`;
}

function radarRow(r) {
  const m = cache(r.movie);
  const when = r.offsetDays === 0 ? 'on release day' : r.offsetDays === 7 ? '1 week before' : `${r.offsetDays} day${r.offsetDays > 1 ? 's' : ''} before`;
  return `<div class="rd"><a class="poster" href="/movies/${m.id}" aria-label="${esc(m.title)}">${posterHtml(m)}</a>
    <div style="min-width:0"><b>${esc(m.title)}</b><div class="meta">${fmtMD(m.releaseDate)} · remind ${when} · ${r.channel === 'EMAIL' ? 'email' : 'in-app'}</div>
      <div style="display:flex;gap:8px;align-items:center;margin-top:6px;flex-wrap:wrap"><span class="status ${r.status.toLowerCase()}">${r.status.toLowerCase()}</span><button class="link" style="font-size:.8rem" data-act="remind" data-id="${m.id}">edit</button></div></div>
    <div class="when">${r.daysLeft === 1 ? 'tomorrow' : `in ${r.daysLeft} days`}</div></div>`;
}

async function render() {
  $('#prof-av').innerHTML = avatar({ avatarStyle: session.avatarStyle, avatarColor: session.avatarColor }, 138);
  const [stats, rems, seen, wl, cols, liked] = await Promise.all([
    library.stats(), reminders.list(), diary.page(0, 12), watchlist.list(), collections.list(), likes.list()
  ]);
  $('#stats').innerHTML = `
    <a class="stat" href="/watched"><b>${stats.moviesWatched}</b><span>Movies watched</span></a>
    <a class="stat" href="/watched"><b>${stats.reviews}</b><span>Reviews</span></a>
    <a class="stat" href="/watchlist"><b>${stats.watchlist}</b><span>Watchlist</span></a>
    <a class="stat" href="/collections"><b>${stats.collections}</b><span>Collections</span></a>
    <a class="stat" href="/liked"><b>${stats.liked}</b><span>Liked</span></a>`;
  const upcoming = rems.filter(r => r.daysLeft > 0);
  const more = (href, n) => `<a class="more" href="${href}">see all ${n} ${ic('arrowR')}</a>`;
  $('#page').innerHTML = `
    <div class="two-col">
      <section class="sec" aria-labelledby="h-gen">
        ${secHead('what I watch most', { id: 'h-gen', note: stats.diaryEntries ? `${stats.diaryEntries} diary entries · ${stats.cinemaVisits} at the cinema · avg ${stats.averageRating.toFixed(1)}★` : 'log a few films to see your mix' })}
        <div class="chart">${genreChart(stats.topGenres)}</div>
      </section>
      <section class="sec" id="radar" aria-labelledby="h-radar">
        ${secHead('release radar', { id: 'h-radar', note: 'coming soon... don’t forget to remind yourself!', doodle: D.bell('happy') })}
        ${upcoming.length ? `<div class="radar">${upcoming.map(radarRow).join('')}</div>`
          : emptyState('', 'no reminders yet', 'Tap “Remind me” on any upcoming movie.', '<a class="btn" href="/films#h-soon">See what’s coming</a>')}
      </section>
    </div>
    <section class="sec">${secHead('movies I’ve seen', { arrows: seen.content.length ? 's-seen' : '', more: seen.content.length ? more('/watched', seen.totalElements) : '' })}
      ${seen.content.length ? strip('s-seen', seen.content.map(seenCard).join(''))
        : emptyState(D.kid('calendar'), 'your movie memories start here.', '', `<button class="btn" data-act="add-diary">${ic('plus')}Add a Movie</button>`)}</section>
    <section class="sec">${secHead('things I want to watch', { arrows: wl.length ? 's-wl' : '', more: wl.length ? more('/watchlist', wl.length) : '' })}
      ${wl.length ? strip('s-wl', wl.slice(0, 12).map(card).join(''))
        : emptyState(D.shelf(), 'nothing here yet...', 'Let’s find something to watch.', '<a class="btn" href="/films">Explore Movies</a>')}</section>
    <section class="sec">${secHead('my collections', { more: `<a class="more" href="/collections">all collections ${ic('arrowR')}</a>` })}
      <div class="cgrid">${cols.slice(0, 3).map(colCard).join('')}<button class="ccard new" data-act="new-collection"><div class="fan"><span>${ic('plus')}Create Collection</span></div></button></div></section>
    <section class="sec">${secHead('movies I liked', { note: 'hearts, no strings attached', arrows: liked.length ? 's-liked' : '', more: liked.length ? more('/liked', liked.length) : '' })}
      ${liked.length ? strip('s-liked', liked.slice(0, 12).map(card).join(''))
        : emptyState(D.ghost(), 'no hearts yet', 'Tap ♡ on any film to keep it here.')}</section>`;
  if (location.hash === '#radar') $('#radar')?.scrollIntoView();
}

boot(render, ['*']);
