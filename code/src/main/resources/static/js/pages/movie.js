import { boot } from '../app.js';
import { $, esc, fmtLong, fmtMD, fmtShort, runtimeTxt, todayIso } from '../core/dom.js';
import { D, ic, avatar, posterHtml } from '../core/doodles.js';
import { secHead, strip, emptyState, starsTxt, starsInput, toast, showError } from '../core/ui.js';
import { st, cache, afterChange } from '../core/state.js';
import { movies } from '../api/movies.js';
import { reviews, diary } from '../api/library.js';
import { likeBtn, wlBtn, postcard } from '../components/movie-card.js';

const LANG = { en: 'English', ja: 'Japanese', ko: 'Korean', fr: 'French', es: 'Spanish', th: 'Thai', zh: 'Chinese', cn: 'Cantonese', lv: 'Latvian' };
const PLACE = { CINEMA: 'Cinema', HOME: 'Home', OTHER: 'Other' };
const movieId = +$('#main').dataset.movieId;

async function render() {
    const [m, rv, similar] = await Promise.all([movies.get(movieId), reviews.forMovie(movieId), movies.similar(movieId, 12)]);
    cache(m);
    document.title = `${m.title}  · Poppy Night`;
    const entry = st.watched.get(m.id), rem = st.reminders.get(m.id), out = m.released;
    const mine = rv.reviews.find(r => r.mine);
    $('#page').innerHTML = `
    <article class="detail">
      <div class="detail-poster"><span class="tape"></span><div class="poster">${posterHtml(m)}</div></div>
      <div class="detail-info">
        <p class="eyebrow">${esc(m.genres.join(' · '))}</p>
        <h1 class="h1">${esc(m.title)}</h1>
        ${m.originalTitle ? `<p class="orig">original title: <b>${esc(m.originalTitle)}</b></p>` : ''}
        <div class="facts"><span><b>${m.year || 'TBA'}</b></span><span>${runtimeTxt(m.runtime)}</span><span>${esc(LANG[m.language] || m.language || '')}</span>
          ${out ? `<span class="tmdb" title="TMDB user score">TMDB ★ ${Number(m.rating).toFixed(1)}</span>` : `<span class="tmdb">out ${fmtMD(m.releaseDate)} · in ${m.daysUntilRelease} day${m.daysUntilRelease > 1 ? 's' : ''}</span>`}</div>
        <p class="synopsis">${esc(m.overview || 'No synopsis yet.')}</p>
        <dl class="credits">
          <dt>Directed by</dt><dd>${esc(m.director || '—')}</dd>
          <dt>Starring</dt><dd>${esc(m.cast.join(', ') || '—')}</dd>
          <dt>Release</dt><dd>${m.releaseDate ? fmtLong(m.releaseDate) : 'TBA'}</dd>
        </dl>
        <div class="actions">
          ${likeBtn(m, 'act')}${wlBtn(m, 'act')}
          <button class="act ${entry ? 'on' : ''} ${out ? '' : 'off'}" data-act="watched" data-id="${m.id}">${ic('check')}${entry ? `Watched · ${fmtShort(entry.watchedDate)}` : 'Mark as Watched'}</button>
          <button class="act ${out ? '' : 'off'}" data-act="rate" data-id="${m.id}">${ic('star')}${entry?.rating ? `Rated ${entry.rating}★` : 'Rate'}</button>
          <button class="act ${out ? '' : 'off'}" data-act="review" data-id="${m.id}">${ic('pencil')}${entry?.reviewed ? 'Edit Review' : 'Write Review'}</button>
          <button class="act ${rem ? 'on' : ''} ${out ? 'off' : ''}" data-act="remind" data-id="${m.id}">${ic('bell')}${rem ? `Reminder · ${rem.offsetDays ? rem.offsetDays + 'd before' : 'release day'}` : 'Remind Me'}</button>
          <button class="act" data-act="collect" data-id="${m.id}">${ic('folder')}Add to Collection</button>
        </div>
        ${entry ? await myEntry(entry) : ''}
      </div>
    </article>

    <section class="sec" aria-labelledby="h-rev">
      ${secHead('what people thought', { id: 'h-rev', note: out ? `${rv.reviews.length} review${rv.reviews.length === 1 ? '' : 's'}` : 'no one has seen it yet' })}
      <div class="reviews">
        <div>${rv.reviews.length ? rv.reviews.map(reviewRow).join('')
            : emptyState(D.ghost(), 'quiet in here…', out ? 'Be the first to say something.' : `Reviews open on ${fmtMD(m.releaseDate)}. Set a reminder so you don’t miss it.`,
                out ? '' : `<button class="btn" data-act="remind" data-id="${m.id}">${ic('bell')}Remind me</button>`)}</div>
        ${out && st.loggedIn ? quickReviewForm(rv, mine, entry) : ''}
      </div>
    </section>

    ${similar.length ? `<section class="sec" aria-labelledby="h-sim">${secHead('you might also like...', { id: 'h-sim', note: 'little postcards from similar films', arrows: 's-sim' })}${strip('s-sim', similar.map(postcard).join(''), 'postcards')}</section>` : ''}`;
    bindQuickReview(entry);
}

async function myEntry(marker) {
    const e = await diary.get(marker.entryId);
    return `<div class="my-entry">${D.reel()}<div><p class="eyebrow">your diary · ${fmtLong(e.watchedDate)} · ${PLACE[e.place]}</p>
    <div style="margin:4px 0">${starsTxt(e.rating)}</div>
    ${e.review ? `<p class="quote">“${esc(e.review)}”</p>` : '<p class="note">watched it? tell us what you thought!</p>'}
    <button class="link" style="margin-top:8px" data-act="edit-entry" data-entry="${e.id}">${ic('pencil')}edit entry</button></div></div>`;
}

const reviewRow = r => `<div class="rv${r.mine ? ' mine' : ''}">${avatar(r, 44)}<div>
  <div class="who"><b>${esc(r.displayName)}</b>${r.mine ? '<span class="you">you</span>' : ''}${starsTxt(r.rating)}<time datetime="${r.watchedDate}">${fmtShort(r.watchedDate)}</time></div>
  ${r.review ? `<p>${esc(r.review)}</p>` : '<p class="muted">rated, no words yet.</p>'}</div></div>`;

const quickReviewForm = (rv, mine, entry) => `<form class="review-form form" id="quick-review" novalidate>
  ${rv.ratingCount ? `<div class="avg"><b>${rv.average.toFixed(1)}</b><span class="muted">average from ${rv.ratingCount} rating${rv.ratingCount > 1 ? 's' : ''}</span></div>` : ''}
  <h3 class="h3">${mine ? 'update your review' : 'add your review'}</h3>
  <fieldset class="fld"><legend>your rating</legend>${starsInput('qr-rating', mine?.rating || 0)}</fieldset>
  <label class="fld"><span>review</span><textarea id="qr-text" rows="4" maxlength="1000" placeholder="what stayed with you?">${esc(mine?.review || '')}</textarea></label>
  <p class="err" id="qr-err" hidden></p>
  <div class="form-actions"><span class="hint spacer">${entry ? `saves to your ${fmtShort(entry.watchedDate)} diary entry` : 'also logs it in your diary for today'}</span><button class="btn solid" type="submit">Save Review</button></div>
</form>`;

function bindQuickReview(entry) {
    const form = $('#quick-review');
    if (!form) return;
    form.addEventListener('submit', async e => {
        e.preventDefault();
        const rating = +(new FormData(form).get('qr-rating') || 0) || null;
        const review = $('#qr-text').value.trim();
        const err = $('#qr-err');
        if (!rating && !review) return showError(err, { message: 'Add a star rating or a few words first.' });
        try {
            if (entry) {
                const cur = await diary.get(entry.entryId);
                await diary.update(cur.id, { watchedDate: cur.watchedDate, rating: rating || cur.rating, review, place: cur.place });
            } else {
                await diary.create({ movieId, watchedDate: todayIso(), rating, review, place: 'HOME' });
            }
            toast(entry ? 'Review updated.' : 'Review saved and logged in your diary.');
            await afterChange(movieId, 'diary');
        } catch (x) { showError(err, x); }
    });
}

boot(render, ['diary', 'remind']);