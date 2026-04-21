import axios from 'axios';


export const baseURL = import.meta.env.VITE_API_URL;

const httpClient = axios.create({
    baseURL : baseURL,
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true,
})
export default httpClient;