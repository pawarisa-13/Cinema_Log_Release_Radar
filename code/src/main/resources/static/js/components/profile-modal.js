import { esc } from '../core/dom.js';
import { D, avatar, genreIcon, GENRES, AV_COLORS, AV_STYLES } from '../core/doodles.js';
import { modal, closeModal, toast, showError } from '../core/ui.js';
import { registerActions } from '../core/actions.js';
import { profile } from '../api/account.js';

export async function openEditProfile() {
  const u = await profile.get();
  const fav = new Set(u.favoriteGenres.map(g => g.id));
  const av = { avatarStyle: u.avatarStyle, avatarColor: u.avatarColor };
  modal(`<div class="sheet-head">${D.kid('wave')}<div><h2 class="h2">edit profile</h2><p class="note">a little about you.</p></div></div>
    <form class="form" id="ep-form" novalidate>
      <div class="fld"><span>profile picture</span><div style="display:flex;gap:16px;align-items:center;flex-wrap:wrap"><span id="ep-av">${avatar(av, 72)}</span>
        <div style="display:grid;gap:8px">
          <div class="chips">${AV_STYLES.map(k => `<label class="chip"><input type="radio" name="avatarStyle" value="${k}"${av.avatarStyle === k ? ' checked' : ''}><span>${k.toLowerCase()}</span></label>`).join('')}</div>
          <div class="chips">${Object.keys(AV_COLORS).map(c => `<label class="chip"><input type="radio" name="avatarColor" value="${c}"${av.avatarColor === c ? ' checked' : ''}><span><svg width="14" height="14" viewBox="0 0 14 14" class="doodle" aria-hidden="true"><circle cx="7" cy="7" r="6" class="${AV_COLORS[c]}" stroke="currentColor" stroke-width="1.3"/></svg>${c.toLowerCase()}</span></label>`).join('')}</div>
        </div></div></div>
      <div class="row2"><label class="fld"><span>name</span><input type="text" id="ep-name" value="${esc(u.displayName)}" maxlength="60"></label>
        <label class="fld"><span>email</span><input type="email" id="ep-email" value="${esc(u.email)}"></label></div>
      <label class="fld"><span>bio</span><textarea id="ep-bio" rows="2" maxlength="200">${esc(u.bio || '')}</textarea></label>
      <fieldset class="fld"><legend>favorite genres</legend><div class="chips">${GENRES.map(g => `<label class="chip"><input type="checkbox" name="g" value="${g.id}"${fav.has(g.id) ? ' checked' : ''}><span>${genreIcon(g.name)}${g.name}</span></label>`).join('')}</div></fieldset>
      <p class="err" id="ep-err" hidden></p>
      <div class="form-actions"><button type="button" class="btn ghost" data-act="close">Cancel</button><button class="btn solid" type="submit">Save profile</button></div>
    </form>`, {
    cls: 'wide',
    onMount(sh) {
      const f = sh.querySelector('#ep-form');
      f.addEventListener('change', e => {
        if (e.target.name === 'avatarStyle' || e.target.name === 'avatarColor') {
          av[e.target.name] = e.target.value;
          sh.querySelector('#ep-av').innerHTML = avatar(av, 72);
        }
      });
      f.addEventListener('submit', async e => {
        e.preventDefault();
        try {
          await profile.update({
            displayName: sh.querySelector('#ep-name').value,
            email: sh.querySelector('#ep-email').value,
            bio: sh.querySelector('#ep-bio').value,
            avatarStyle: av.avatarStyle,
            avatarColor: av.avatarColor,
            favoriteGenreIds: new FormData(f).getAll('g').map(Number)
          });
          closeModal();
          toast('Profile saved.');
          setTimeout(() => location.reload(), 500);   // nav + profile card are drawn by the server
        } catch (x) { showError(sh.querySelector('#ep-err'), x); }
      });
    }
  });
}

registerActions({ 'edit-profile': () => openEditProfile() });
