import { esc, fmtMD, fmtShort, daysFromToday, parseD, iso, todayIso, maskEmail, releaseWhen } from '../core/dom.js';
import { D, ic } from '../core/doodles.js';
import { modal, closeModal, toast } from '../core/ui.js';
import { registerActions } from '../core/actions.js';
import { st, MOVIES, cache, afterChange } from '../core/state.js';
import { reminders } from '../api/account.js';
import { movies } from '../api/movies.js';

const OPTIONS = [[7, '1 week before'], [3, '3 days before'], [1, '1 day before'], [0, 'On release day']];

export async function openReminder(movieId) {
  const m = MOVIES.get(+movieId) || cache(await movies.get(movieId));
  const d = daysFromToday(m.releaseDate);
  if (d <= 0) { toast(`${m.title} is already out. Go see it!`, 'film'); return; }
  const cur = st.reminders.get(m.id);
  const def = cur ? cur.offsetDays : (d > 3 ? 3 : 1);
  const fireDate = n => { const x = parseD(m.releaseDate); x.setDate(x.getDate() - n); return iso(x); };
  modal(`<div class="sheet-head">${D.bell('happy')}<div><h2 class="h2">remind me</h2><p class="note">${esc(m.title)} · out ${fmtMD(m.releaseDate)} (${d === 1 ? 'tomorrow' : `in ${d} days`})</p></div></div>
    <form class="form" id="rm-form">
      <fieldset class="fld"><legend>when?</legend><div class="chips">${OPTIONS.map(([n, l]) => {
        const f = fireDate(n);
        return `<label class="chip"><input type="radio" name="offset" value="${n}"${n === def ? ' checked' : ''}><span>${l} <small class="muted">· ${f <= todayIso() ? 'sends now' : fmtShort(f).slice(0, -5)}</small></span></label>`;
      }).join('')}</div></fieldset>
      <fieldset class="fld"><legend>how?</legend><div class="chips">
        <label class="chip"><input type="radio" name="channel" value="IN_APP"${(cur?.channel || 'IN_APP') === 'IN_APP' ? ' checked' : ''}><span>${ic('bell')}In-app notification</span></label>
        <label class="chip"><input type="radio" name="channel" value="EMAIL"${cur?.channel === 'EMAIL' ? ' checked' : ''}><span>${ic('mail')}Email · ${esc(maskEmail(st.email))}</span></label></div></fieldset>
      <div class="form-actions">${cur ? `<button type="button" class="btn ghost danger spacer" data-act="remind-off" data-id="${m.id}">${ic('trash')}Remove reminder</button>` : ''}
        <button type="button" class="btn ghost" data-act="close">Cancel</button><button class="btn solid" type="submit">${ic('bell')}${cur ? 'Update reminder' : 'Remind me'}</button></div>
    </form>`, {
    onMount(sh) {
      sh.querySelector('#rm-form').addEventListener('submit', async e => {
        e.preventDefault();
        const fd = new FormData(e.target);
        const r = await reminders.save(m.id, +fd.get('offset'), fd.get('channel'));
        closeModal();
        toast(r.status === 'SENT' ? `Reminder sent: ${m.title} is ${releaseWhen(d)}.` : `Reminder set for ${fmtMD(r.reminderDate)}${r.channel === 'EMAIL' ? ' by email' : ''}.`, 'bell');
        await afterChange(m.id, 'remind');
      });
    }
  });
}

registerActions({
  remind: ({ id }) => openReminder(id),
  async 'remind-off'({ id }) {
    await reminders.remove(id);
    closeModal();
    toast('Reminder removed.', 'bell');
    await afterChange(+id, 'remind');
  }
});
