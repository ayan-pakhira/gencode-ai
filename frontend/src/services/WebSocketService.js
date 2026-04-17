import {Client} from "@stomp/stompjs";

let stompClient = null;

export const connectWebSocket = (onMessageReceived) => {
    stompClient = new Client({

        brokerURL: "ws://localhost:8080/ws",
        reconnectDelay: 5000,

        onConnect: () => {
            console.log("WS connected");

            stompClient.subscribe("/topic/status", (message) => {
                const data = JSON.parse(message.body);
                onMessageReceived(data);
            });
        },

        onStompError: (frame) => {
            console.error("STOMP error:", frame);
        },
    });
    
    stompClient.activate();
};

export const disconnectWebSocket = () => {

    if(stompClient){
        stompClient.deactivate();
        console.log("WS disconnected");
    }
}