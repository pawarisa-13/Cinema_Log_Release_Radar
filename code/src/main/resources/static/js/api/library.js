import { http, qs } from '../core/http.js';

export const library = {
  state: () => http('GET', '/api/v1/users/me/library'),
  stats: () => http('GET', '/api/v1/users/me/stats')
};
export const diary = {
  page: (page = 0, size = 60, sort = 'watchedDate,desc') => http('GET', '/api/v1/users/me/diary' + qs({ page, size, sort })),
  month: month => http('GET', `/api/v1/users/me/diary/calendar?month=${month}`),
  day: date => http('GET', `/api/v1/users/me/diary/day?date=${date}`),
  get: id => http('GET', `/api/v1/users/me/diary/${id}`),
  create: body => http('POST', '/api/v1/users/me/diary', body),
  update: (id, body) => http('PUT', `/api/v1/users/me/diary/${id}`, body),
  remove: id => http('DELETE', `/api/v1/users/me/diary/${id}`)
};
export const watchlist = {
  list: () => http('GET', '/api/v1/users/me/watchlist'),
  add: id => http('PUT', `/api/v1/users/me/watchlist/${id}`),
  remove: id => http('DELETE', `/api/v1/users/me/watchlist/${id}`)
};
export const likes = {
  list: () => http('GET', '/api/v1/users/me/likes'),
  add: id => http('PUT', `/api/v1/users/me/likes/${id}`),
  remove: id => http('DELETE', `/api/v1/users/me/likes/${id}`)
};
export const collections = {
  list: () => http('GET', '/api/v1/users/me/collections'),
  get: id => http('GET', `/api/v1/users/me/collections/${id}`),
  create: body => http('POST', '/api/v1/users/me/collections', body),
  update: (id, body) => http('PUT', `/api/v1/users/me/collections/${id}`, body),
  remove: id => http('DELETE', `/api/v1/users/me/collections/${id}`),
  addMovie: (id, movieId) => http('PUT', `/api/v1/users/me/collections/${id}/movies/${movieId}`),
  removeMovie: (id, movieId) => http('DELETE', `/api/v1/users/me/collections/${id}/movies/${movieId}`)
};
export const reviews = {
  forMovie: movieId => http('GET', `/api/v1/movies/${movieId}/reviews`)
};
