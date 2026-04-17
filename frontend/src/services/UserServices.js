import httpClient from "../config/AxiosHelper.js";
import privateApi from "./PrivateApi.js";
import {setToken, getToken, clearToken} from "./TokenService.js";

export const createChat = async (chatName) => {
    try{

        const response = await privateApi.post(`/chat/create-chat`, {
            chatName
        },{
        headers:{
            'Content-Type': 'application/json',
        }})

        return response.data;

    }catch(error){
        console.log("Error creating chat:", error);
        throw error;
    }
}

export const fetchChatList = async () => {
    try{
        const response = await privateApi.get(`chat/get-chat`,{
            headers:{
                'Content-Type': 'application/json',
            }
        })

        return response.data;

    }catch(error){
        console.log("Error fetching chat list:", error);
        throw error;
    }
}

export const createMessage = async (chatId, messageData) => {
    try{
        const response = await privateApi.post(`/message/create-message`, {
            chatId,
            messageData
        },{
            headers:{
                'Content-Type': 'application/json',
            }
        })
        return response.data;

    }catch(error){
        console.log("Error creating message:", error);
        throw error;
    }
}

export const fetchMessages = async (chatId) => {
    try{

        const response = await privateApi.get(`/message/get-message/${chatId}`,{
            headers:{
                Authorization: `Bearer ${getToken()}`,
            }
        })
        return response.data;

    }catch(error){
        console.log("Error fetching messages:", error);
        throw error;
    }
}

export const deleteChat = async (chatId) => {
    try{
        const response = await privateApi.delete(`/chat/delete-chat/${chatId}`,{
            headers:{
                'Content-Type': 'application/json',
            }
        })
        return response.data;

    }catch(error){
        console.log("Error deleting chat:", error);
        throw error;
    }
}

export const processAI = async ({ chatId, prompt, image}) => {
    try{

        const formData = new FormData();
        formData.append("chatId", chatId);
        formData.append("userPrompt", prompt);

        if(image){
            formData.append("image", image);
        }

        const response = await privateApi.post(`/ai/process`, formData, {
            headers: {
                Authorization: `Bearer ${getToken()}`,
                'Content-Type': 'multipart/form-data',
            },
            withCredentials: true,
        })

        return response.data;

    }catch(error){
        console.log("Error processing AI response:", error);
        throw error;
    }
}

export const getStatus = async (chatId, messageId) => {
    
    try{
        const response = await privateApi.get(`/api/status`, {
            params: {
                chatId,
                messageId
            }, 
            headers: {
                Authorization: `Bearer ${getToken()}`,
                'Content-Type': 'application/json',
            },
        })
        return response.data;

    }catch(error){
        console.error("Error fetching status:", error);
        throw error;
    }
}
