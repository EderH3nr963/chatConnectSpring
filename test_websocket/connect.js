const ws = new WebSocket("ws://localhost:8080/ws");

const subscribeFrame =
  "SUBSCRIBE\n" +
  "id:sub-0\n" +
  "destination:/topic/chat.6d7d55f7-07a1-4867-b9ca-ac1972e70bd7\n" +
  "\n" +
  "\0";

const sendMessage =
  "SEND\n" +
  "destination:/app/chat.editMessage\n" +
  "content-type:application/json\n" +
  "\n" +
  "{\"messageId\":\"a166ffbe-5178-42da-a28c-743b614eb077\",\"content\":\"Ola mundo!!\"}" +
  "\0";

ws.onopen = () => {
  console.log("WebSocket conectado");

  const connectFrame =
    "CONNECT\n" +
    "accept-version:1.2\n" +
    "host:localhost\n" +
    "Authorization:Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJiYmYyNjNlMy0xNzdiLTQ0OTYtOTU4Yy1jZmMyOGZjM2VhODgiLCJpYXQiOjE3ODc3ODUzNDMsImV4cCI6MTc4Nzg3MTc0M30.P9QerABtkJjUXG28t8dUdpXijqcJHZ6OYUmaeJHfmR3sla-EByEJNyaRUSgfDIzr\n" +
    "\n" +
    "\0";

  ws.send(connectFrame);
};

ws.onmessage = (event) => {
  const message = event.data.toString();

  console.log("Recebido:");
  console.log(message);

  if (message.startsWith("CONNECTED")) {
    console.log("STOMP conectado!");

    // Primeiro se inscreve no chat
    ws.send(subscribeFrame);

    console.log("Inscrito no chat!");

    // Depois envia a mensagem
    ws.send(sendMessage);
  }
};

ws.onerror = (error) => {
  console.error("WebSocket error:", error);
};

ws.onclose = () => {
  console.log("WebSocket fechado");
};