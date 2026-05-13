import axios from 'axios';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'https://parking-project-production-55ca.up.railway.app/api',
});

export default api;
