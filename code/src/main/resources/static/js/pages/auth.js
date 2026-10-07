import { $ } from '../core/dom.js';
import { D, hydrateDoodles, genreIcon, GENRES, ic } from '../core/doodles.js';
import { showError } from '../core/ui.js';
import { auth, profile } from '../api/account.js';

hydrateDoodles();
const page = $('#main').dataset.page;

if (page === 'register') {
  $('#reg-form').addEventListener('submit', async e => {
    e.preventDefault();
    const err = $('#rg-err');
    const displayName = $('#rg-name').value.trim(), email = $('#rg-email').value.trim(), password = $('#rg-pass').value;
    try {
      await auth.register({ displayName, email, password });
      const login = $('#auto-login');
      login.username.value = email;
      login.password.value = password;
      login.submit();
    } catch (x) { showError(err, x); }
  });
}

if (page === 'onboarding') {
  $('#genre-grid').innerHTML = GENRES.map(g =>
    `<label class="gtile"><input type="checkbox" name="g" value="${g.id}"><span>${genreIcon(g.name)}${g.name}</span></label>`).join('');
  $('#genre-form').addEventListener('submit', async e => {
    e.preventDefault();
    const ids = new FormData(e.target).getAll('g').map(Number);
    if (!ids.length) return showError($('#gp-err'), { message: 'Pick at least one genre.' });
    try {
      const me = await profile.favoriteGenres(ids);
      showPersonality(me.favoriteGenres.map(g => g.name));
    } catch (x) { showError($('#gp-err'), x); }
  });
}

const ADJ = { Horror: 'spooky', Animation: 'cozy', Fantasy: 'dreamy', Comedy: 'sunny', Romance: 'tender', Action: 'restless', 'Sci-Fi': 'starry-eyed', Drama: 'thoughtful', Thriller: 'sharp-eyed', Documentary: 'curious' };
const NOUN = { Horror: 'night owl', Animation: 'doodler', Fantasy: 'wanderer', Comedy: 'giggler', Romance: 'softie', Action: 'thrill seeker', 'Sci-Fi': 'stargazer', Drama: 'feeler', Thriller: 'detective', Documentary: 'explorer' };

function showPersonality(names) {
  const picked = GENRES.map(g => g.name).filter(n => names.includes(n));
  const label = `the ${ADJ[picked[1] || picked[0]] || 'curious'} ${NOUN[picked[0]] || 'moviegoer'}`;
  $('.auth-card').innerHTML = `
    <div style="width:150px;margin:0 auto">${D.popcorn()}</div>
    <p class="note" style="margin-top:12px">your movie personality is ready!</p>
    <h1 class="personality">${label}</h1>
    <div class="tags" style="justify-content:center;margin-bottom:26px">${picked.map(n => `<span class="tag">${genreIcon(n).replace('class="doodle gicon"', 'class="doodle gicon" style="width:20px;height:20px"')}${n}</span>`).join('')}</div>
    <a class="btn solid" href="/films" style="padding:12px 28px">Start exploring ${ic('arrowR')}</a>`;
}
