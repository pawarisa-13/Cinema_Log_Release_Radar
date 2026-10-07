import { http } from '../core/http.js';

export const auth = {
  register: body => http('POST', '/api/v1/auth/register', body)
};
export const profile = {
  get: () => http('GET', '/api/v1/users/me'),
  update: body => http('PUT', '/api/v1/users/me', body),
  favoriteGenres: genreIds => http('PUT', '/api/v1/users/me/favorite-genres', { genreIds })
};
export const reminders = {
  list: () => http('GET', '/api/v1/users/me/reminders'),
  save: (movieId, offsetDays, channel) => http('PUT', `/api/v1/users/me/reminders/${movieId}`, { offsetDays, channel }),
  remove: movieId => http('DELETE', `/api/v1/users/me/reminders/${movieId}`)
};
export const notifications = {
  list: (limit = 8) => http('GET', `/api/v1/users/me/notifications?limit=${limit}`),
  readAll: () => http('POST', '/api/v1/users/me/notifications/read-all')
};
