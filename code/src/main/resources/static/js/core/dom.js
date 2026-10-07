export const $ = (s, el = document) => el.querySelector(s);
export const $$ = (s, el = document) => [...el.querySelectorAll(s)];
export const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
export function hash(str) { let h = 2166136261; for (const c of String(str)) { h ^= c.charCodeAt(0); h = Math.imul(h, 16777619); } return h >>> 0; }
export function rng(seed) { let s = (seed >>> 0) || 1; return () => { s = (Math.imul(s, 1664525) + 1013904223) >>> 0; return s / 4294967296; }; }

export const MONTHS = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
export const WEEKDAYS = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
export const pad = n => String(n).padStart(2, '0');
export const iso = d => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
export const parseD = s => { const [y, m, d] = s.split('-').map(Number); return new Date(y, m - 1, d); };
export const today = () => { const d = new Date(); return new Date(d.getFullYear(), d.getMonth(), d.getDate()); };
export const todayIso = () => iso(today());
export const daysFromToday = s => Math.round((parseD(s) - today()) / 864e5);
export const fmtLong = s => { const d = parseD(s); return `${d.getDate()} ${MONTHS[d.getMonth()]} ${d.getFullYear()}`; };
export const fmtShort = s => { const d = parseD(s); return `${d.getDate()} ${MONTHS[d.getMonth()].slice(0, 3)} ${d.getFullYear()}`; };
export const fmtMD = s => { const d = parseD(s); return `${MONTHS[d.getMonth()]} ${d.getDate()}`; };
export const runtimeTxt = n => n ? `${Math.floor(n / 60)}h ${n % 60}m` : '—';
export const isMobile = () => matchMedia('(max-width:640px)').matches;
export function ago(isoTime) {
  const diff = (Date.now() - new Date(isoTime)) / 1000;
  if (diff < 60) return 'just now';
  if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
  if (diff < 172800) return 'yesterday';
  return `${Math.floor(diff / 86400)} days ago`;
}
export function releaseWhen(days) {
  if (days < 0) return 'now in theaters';
  if (days === 0) return 'out today';
  if (days === 1) return 'coming tomorrow';
  return `coming in ${days} days`;
}
export const maskEmail = (e = '') => { const [u, d] = e.split('@'); return d ? `${u.slice(0, 3)}…@${d}` : e; };
