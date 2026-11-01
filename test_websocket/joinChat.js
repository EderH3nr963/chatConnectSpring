const ws = new WebSocket("ws://localhost:8080/ws");

const sendMessage =
  "SEND\n" +
  "destination:/app/chat.sendMessage\n" +
  "content-type:application/json\n" +
  "\n" +
  "{\"chatId\": null, \"targetId\": \"d0d1eb9d-8375-4c62-80c3-0ada96eb73d9\", \"attachmentInfo\": null, \"messageType\": \"TEXT\", \"content\":\"Ola mundo!!\"}" +
  "\0";

ws.onopen = () => {
  console.log("WebSocket conectado");

  const connectFrame =
    "CONNECT\n" +
    "accept-version:1.2\n" +
    "host:localhost\n" +
    "Authorization:Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3OGIzZDA5MC0zM2Q3LTRkOGMtYTJhNi02ZDM5NTQ1YTRmYWIiLCJpYXQiOjE1MTYyMzkwMjJ9.0vVvj4qsNmlMP62Hz72Dgw34TLmtzxJgKUTz384PflE\n" +
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

    const subscribeFrame =
      "SUBSCRIBE\n" +
      "id:sub-user-queue\n" +
      "destination:/topic/chat.\n" +
      "\n" +
      "\0";

    ws.send(subscribeFrame);
  }
};

ws.onerror = (error) => {
  console.error("WebSocket error:", error);
};

ws.onclose = () => {
  console.log("WebSocket fechado");
};