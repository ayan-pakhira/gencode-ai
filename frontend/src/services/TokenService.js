let accessToken = null;

export const setToken = (token) => {
    accessToken = token;
    localStorage.setItem("authToken", token);
};

export const getToken = () => {
    return accessToken || localStorage.getItem("authToken");
};

export const clearToken = () => {
    accessToken = null;
    localStorage.removeItem("authToken");
}