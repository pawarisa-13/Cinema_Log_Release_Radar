import { $, esc } from './dom.js';
import { ic, ICONS } from './doodles.js';

/* ---------- toast ---------- */
export function toast(msg, icon = 'check') {
  let box = $('#toasts');
  if (!box) { box = document.createElement('div'); box.id = 'toasts'; box.className = 'toasts'; box.setAttribute('aria-live', 'polite'); document.body.append(box); }
  const t = document.createElement('div');
  t.className = 'toast';
  t.innerHTML = `${ic(icon)}<span>${esc(msg)}</span>`;
  box.append(t);
  setTimeout(() => { t.classList.add('out'); setTimeout(() => t.remove(), 320); }, 2800);
}

/* ---------- modal (bottom sheet on phones via CSS) ---------- */
let lastFocus = null;
export function modal(html, { cls = '', onMount } = {}) {
  closeModal(); closePop();
  lastFocus = document.activeElement;
  const s = document.createElement('div');
  s.className = 'scrim';
  s.innerHTML = `<div class="sheet ${cls}" role="dialog" aria-modal="true"><button class="x" data-act="close" aria-label="Close">${ic('x')}</button>${html}</div>`;
  document.body.append(s);
  s.addEventListener('mousedown', e => { if (e.target === s) closeModal(); });
  const sheet = s.firstElementChild;
  onMount?.(sheet);
  (sheet.querySelector('[autofocus]') || sheet.querySelector('input:not([type=hidden]):not([type=radio]):not([type=checkbox]),textarea,button:not(.x)'))?.focus({ preventScroll: true });
  return sheet;
}
export function closeModal() {
  const s = $('.scrim');
  if (s) { s.remove(); lastFocus?.focus?.({ preventScroll: true }); }
}
export const isModalOpen = () => !!$('.scrim');

/* ---------- popover ---------- */
let openPop = null;
export function popover(anchor, html, cls = '') {
  if (openPop && openPop.anchor === anchor) { closePop(); return null; }
  closePop();
  const p = document.createElement('div');
  p.className = 'pop ' + cls;
  p.innerHTML = html;
  document.body.append(p);
  const r = anchor.getBoundingClientRect(), pw = p.offsetWidth, ph = p.offsetHeight;
  const left = Math.min(Math.max(12, r.right - pw), innerWidth - 12 - pw);
  let top = r.bottom + 8;
  if (top + ph > innerHeight - 12 && r.top - ph - 8 > 12) top = r.top - ph - 8;
  p.style.left = left + 'px';
  p.style.top = Math.max(12, top) + 'px';
  anchor.setAttribute('aria-expanded', 'true');
  openPop = { el: p, anchor };
  p.querySelector('button,input,a')?.focus({ preventScroll: true });
  return p;
}
export function closePop() {
  if (!openPop) return;
  openPop.el.remove();
  openPop.anchor.setAttribute('aria-expanded', 'false');
  openPop = null;
}
export const currentPop = () => openPop;

/* ---------- small pieces ---------- */
export const starsTxt = n => n
  ? `<span class="stars" aria-label="${n} of 5 stars">${'★'.repeat(n)}<i>${'★'.repeat(5 - n)}</i></span>`
  : '<span class="muted">no rating</span>';

export function starsInput(name, val = 0) {
  let s = `<div class="stars-in" role="radiogroup" aria-label="Rating">`;
  for (let i = 5; i >= 1; i--) {
    s += `<input type="radio" id="${name}-${i}" name="${name}" value="${i}"${+val === i ? ' checked' : ''}><label for="${name}-${i}" title="${i} star${i > 1 ? 's' : ''}"><svg viewBox="0 0 24 24">${ICONS.star}</svg><span class="sr">${i} stars</span></label>`;
  }
  return s + '</div>';
}

export function secHead(title, { note = '', doodle = '', more = '', arrows = '', id = '' } = {}) {
  return `<div class="sec-head">${doodle}<div class="t"><h2 class="h2"${id ? ` id="${id}"` : ''}>${title}</h2>${note ? `<span class="note">${note}</span>` : ''}</div>
    <div class="tools">${arrows ? `<button class="round-btn" data-act="scroll" data-target="${arrows}" data-dir="-1" aria-label="Scroll left">${ic('chevL')}</button><button class="round-btn" data-act="scroll" data-target="${arrows}" data-dir="1" aria-label="Scroll right">${ic('chevR')}</button>` : ''}${more}</div></div>`;
}
export const strip = (id, items, cls = '') => `<div class="strip ${cls}" id="${id}">${items}</div>`;
export const emptyState = (doodle, title, text, btn = '') =>
  `<div class="empty-st">${doodle}<h3 class="h2">${title}</h3>${text ? `<p>${text}</p>` : ''}${btn}</div>`;
export const loading = (label = 'loading…') => `<p class="loading">${label}</p>`;
export const skeletonRow = (n = 6) => `<div class="skeleton-row">${'<span></span>'.repeat(n)}</div>`;
export const showError = (el, err) => { el.textContent = err.message; el.hidden = false; };
