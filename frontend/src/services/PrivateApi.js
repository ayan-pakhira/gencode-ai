import axios from "axios";
import httpClient from "../config/AxiosHelper";
import { setToken, getToken} from "./TokenService.js";

export const baseURL = "http://localhost:8080";

const privateApi = axios.create({
     baseURL : baseURL,
        headers: {
            'Content-Type': 'application/json',
        },
        withCredentials: true,
})

privateApi.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('authToken');
        if(token){
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    }, 
    (error) => Promise.reject(error)
);


let isRefreshing = false;
let refreshSubscribers = [];

const onRefreshed = (newToken) => {
    refreshSubscribers.forEach((callback) => callback(newToken));
    refreshSubscribers = [];
};

const addSubscriber = (callback) => {
    refreshSubscribers.push(callback);
}

privateApi.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        //if token expired and we are not already refreshing
        if(error.response?.status === 401 && !originalRequest._retry){
            if(isRefreshing){
                //queue the request until the token is refreshed
                return new Promise((resolve) => {
                    addSubscriber((token) => {
                        originalRequest.headers.Authorization = `Bearer ${token}`;
                        resolve(privateApi(originalRequest));
                    });
                });
            }

            originalRequest._retry = true;
            isRefreshing = true;
            
            try{

                const response = await httpClient.post(`/public/refresh-token`, {}, {
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    withCredentials: true,
                })

                const newAccessToken = response.data.token;
                setToken(newAccessToken);
                
                privateApi.defaults.headers.Authorization = `Bearer ${newAccessToken}`;
                originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                onRefreshed(newAccessToken);

                return privateApi(originalRequest);

            }catch(error){
                console.error("Error refreshing token:", error);

                localStorage.removeItem('authToken');
                window.location.href = "/login";
                return Promise.reject(error);
            }finally{
                isRefreshing = false;
            }
        }
        return Promise.reject(error);
    }
)

export default privateApi;