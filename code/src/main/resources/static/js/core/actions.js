import { $ } from './dom.js';
import { closeModal, closePop, currentPop, toast } from './ui.js';

const ACT = {};

export function registerActions(actions) {
  Object.assign(ACT, actions);
}

registerActions({
  noop() { },
  close: () => closeModal(),
  back: () => (history.length > 1 ? history.back() : (location.href = '/films')),
  scroll({ target, dir }) {
    const s = $('#' + target);
    s?.scrollBy({ left: +dir * s.clientWidth * 0.8, behavior: 'smooth' });
  }
});

document.addEventListener('click', e => {
  const el = e.target.closest('[data-act]');
  if (!el || el.disabled) return;
  const fn = ACT[el.dataset.act];
  if (!fn) return;
  if (el.tagName === 'A') e.preventDefault();
  e.stopPropagation();
  if (el.closest('.pop') && !['noop', 'search-all'].includes(el.dataset.act)) closePop();
  Promise.resolve(fn({ ...el.dataset }, el, e)).catch(err => toast(err.message || 'Something went wrong. Try again.', 'x'));
});

document.addEventListener('keydown', e => {
  if (e.key === 'Escape') { if (currentPop()) closePop(); else closeModal(); }
  if ((e.key === 'Enter' || e.key === ' ') && e.target.matches('[role=link][data-act],[role=button][data-act]')) {
    e.preventDefault();
    e.target.click();
  }
});
document.addEventListener('mousedown', e => {
  const p = currentPop();
  if (p && !p.el.contains(e.target) && !p.anchor.contains(e.target)) closePop();
});
addEventListener('scroll', e => {
  const p = currentPop();
  if (p && !(e.target instanceof Node && p.el.contains(e.target))) closePop();
}, true);
addEventListener('resize', () => closePop());
