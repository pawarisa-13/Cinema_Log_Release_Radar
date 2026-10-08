import { http, qs } from '../core/http.js';

export const movies = {
    search: criteria => http('GET', '/api/v1/movies' + qs(criteria)),
    get: id => http('GET', `/api/v1/movies/${id}`),
    similar: (id, limit = 12) => http('GET', `/api/v1/movies/${id}/similar?limit=${limit}`),
    nowShowing: (limit = 16) => http('GET', `/api/v1/movies/now-showing?limit=${limit}`),
    upcoming: (limit = 12) => http('GET', `/api/v1/movies/upcoming?limit=${limit}`),
    topRated: (limit = 16) => http('GET', `/api/v1/movies/top-rated?limit=${limit}`),
    recommended: (limit = 14) => http('GET', `/api/v1/movies/recommended?limit=${limit}`)
};