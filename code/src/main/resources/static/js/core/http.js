export class ApiError extends Error {
  constructor(status, body = {}) {
    const field = body.fieldErrors && body.fieldErrors[0];
    super(field ? field.message : (body.message || `Request failed (${status})`));
    this.status = status;
    this.body = body;
  }
}

export async function http(method, url, body) {
  const res = await fetch(url, {
    method,
    credentials: 'same-origin',
    headers: body !== undefined ? { 'Content-Type': 'application/json', Accept: 'application/json' } : { Accept: 'application/json' },
    body: body !== undefined ? JSON.stringify(body) : undefined
  });
  if (res.status === 401) {
    location.href = '/login';
    throw new ApiError(401, { message: 'Please log in first.' });
  }
  if (!res.ok) {
    let data = {};
    try { data = await res.json(); } catch (e) { /* not JSON */ }
    throw new ApiError(res.status, data);
  }
  if (res.status === 204) return null;
  const type = res.headers.get('content-type') || '';
  return type.includes('json') ? res.json() : null;
}

export const qs = o => {
  const p = new URLSearchParams();
  Object.entries(o || {}).forEach(([k, v]) => { if (v !== '' && v != null) p.set(k, v); });
  const s = p.toString();
  return s ? '?' + s : '';
};
