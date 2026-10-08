import { $, esc, fmtMD, ago, releaseWhen, maskEmail } from '../core/dom.js';
import { D, ic, avatar } from '../core/doodles.js';
import { popover } from '../core/ui.js';
import { registerActions } from '../core/actions.js';
import { st, session } from '../core/state.js';
import { reminders, notifications } from '../api/account.js';

const ICON_FOR = { RELEASE_REMINDER: 'bell', NOW_IN_THEATERS: 'film', REVIEW_ADDED: 'pencil' };

export function paintNav() {
  const dot = $('#bell-dot');
  if (dot) { dot.hidden = !st.unread; dot.textContent = st.unread > 9 ? '9+' : (st.unread || ''); }
  const av = $('#nav-av');
  if (av) av.innerHTML = avatar({ avatarStyle: session.avatarStyle, avatarColor: session.avatarColor }, 34);
}
document.addEventListener('library:changed', paintNav);

async function openNotifications(anchor) {
  const [rems, notes] = await Promise.all([reminders.list(), notifications.list(8)]);
  const upcoming = rems.filter(r => r.daysLeft > 0).slice(0, 4);
  const p = popover(anchor, `
    <div class="np-head">${D.bell('happy')}<div><h3 class="h3">Release Radar</h3><span class="hint">${upcoming.length ? `${upcoming.length} upcoming reminder${upcoming.length > 1 ? 's' : ''}` : 'nothing on the radar yet'}</span></div></div>
    <div class="np-list">
      ${upcoming.map(r => `<div class="np-item radar"><span class="ico">${ic('film')}</span><div><b>${esc(r.movie.title)}</b><span>${releaseWhen(r.daysLeft)} · ${fmtMD(r.movie.releaseDate)}</span><a class="link" style="font-size:.8rem;margin-top:3px" href="/movies/${r.movie.id}">View movie</a></div></div>`).join('')
        || '<p class="hint" style="padding:6px">Tap “Remind me” on any upcoming movie and it shows up here.</p>'}
      <p class="np-sub">notifications</p>
      ${notes.map(n => `<div class="np-item${n.read ? '' : ' unread'}"><span class="ico">${ic(ICON_FOR[n.type] || 'bell')}</span><div><span style="color:var(--ink)">${esc(n.message)}</span><time>${ago(n.createdAt)}${n.channel === 'EMAIL' ? ' · email' : ''}</time>${n.movieId ? ` · <a class="link" style="font-size:.76rem" href="/movies/${n.movieId}">view</a>` : ''}</div></div>`).join('')
        || '<p class="hint" style="padding:6px">All quiet for now.</p>'}
    </div>
    <div class="menu-sep"></div><a class="menu-i" href="/profile#radar">${ic('user')}Release radar in your profile<small>${ic('arrowR')}</small></a>`, 'notif');
  if (p && st.unread) {
    await notifications.readAll();
    st.unread = 0;
    paintNav();
  }
}

function openAccount(anchor) {
  const b = session;
  popover(anchor, `<div class="acct-head">${avatar({ avatarStyle: b.avatarStyle, avatarColor: b.avatarColor }, 40)}<div><b>${esc(b.userName || '')}</b><small>${esc(maskEmail(st.email))}</small></div></div><div class="menu-sep"></div>
    <a class="menu-i" href="/profile">${ic('user')}Profile</a>
    <a class="menu-i" href="/diary">${ic('cal')}Diary</a>
    <a class="menu-i" href="/watched">${ic('eye')}Movies I’ve seen</a>
    <a class="menu-i" href="/watchlist">${ic('bookmark')}Watchlist</a>
    <a class="menu-i" href="/collections">${ic('folder')}Collections</a>
    <a class="menu-i" href="/liked">${ic('heart')}Movies I liked</a>
    <button class="menu-i" data-act="edit-profile">${ic('pencil')}Edit profile</button>
    <div class="menu-sep"></div>
    <a class="menu-i" href="/swagger-ui.html" target="_blank" rel="noopener">${ic('list')}API docs (Swagger)</a>
    <button class="menu-i" data-act="logout">${ic('logout')}Log out</button>`, 'menu');
}

registerActions({
  notif: (_, el) => openNotifications(el),
  account: (_, el) => openAccount(el),
  logout: () => $('#logout-form').submit()
});
