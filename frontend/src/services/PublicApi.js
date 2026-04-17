import httpClient from "../config/AxiosHelper.js";

export const logoutUser = async () => {

    try{
        const response = await httpClient.post(`/public/logout`,{},
            {
                headers:{
                    'Content-Type': 'application/json',
                }   
            },
            { withCredentials: true }
        )

        return response.data;

    }catch(error){
        console.log("Error logging out:", error);
        throw error;
    }
}