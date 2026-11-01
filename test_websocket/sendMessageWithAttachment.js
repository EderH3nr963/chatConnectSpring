import fs from "fs";
import path from "path";

const filePath = "./archives_for_test/imagem.png";

if (!fs.existsSync(filePath)) {
  console.error("Arquivo não encontrado:", filePath);
  process.exit(1);
}

const fileBuffer = fs.readFileSync(filePath);
const fileName = path.basename(filePath);
const contentType = "image/png";

// Instancia o WebSocket nativo do Node.js 21/22+
const ws = new WebSocket("ws://localhost:8080/ws");

// 1. Monta a mensagem STOMP
const sendMessageFrame =
  "SEND\n" +
  "destination:/app/chat.sendMessage\n" +
  "content-type:application/json\n" +
  "\n" +
  JSON.stringify({
    chatId: null,
    targetId: "d0d1eb9d-8375-4c62-80c3-0ada96eb73d9",
    messageType: "IMAGE",
    content: "Foto do documento em anexo",
    attachmentInfo: {
      fileName: fileName,
      contentType: contentType
    }
  }) +
  "\0";

// Callback Nativo: Conexão Aberta
ws.onopen = () => {
  console.log("WebSocket conectado");

  const connectFrame =
    "CONNECT\n" +
    "accept-version:1.2\n" +
    "host:localhost\n" +
    "Authorization:Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI3OGIzZDA5MC0zM2Q3LTRkOGMtYTJhNi02ZDM5NTQ1YTRmYWIiLCJ0eXBlIjoiYWNjZXNzIn0.EiOar1HG6542voTov9wnzuESbNwgG8vudiOT79KLQzg\n" +
    "\n" +
    "\0";

  ws.send(connectFrame);
};

// Callback Nativo: Recebimento de Mensagem
ws.onmessage = async (event) => {
  // Na API nativa, os dados da mensagem vêm dentro de event.data
  const message = event.data.toString();

  console.log("Recebido do servidor:");
  console.log(message);

  if (message.startsWith("CONNECTED")) {
    console.log("STOMP conectado!");

    const subscribeFrame =
      "SUBSCRIBE\n" +
      "id:sub-user-queue\n" +
      "destination:/user/queue/upload-ack\n" +
      "\n" +
      "\0";

    ws.send(subscribeFrame);
    console.log("Enviando mensagem...");
    ws.send(sendMessageFrame);
  }

  if (message.startsWith("MESSAGE")) {
    const bodyIndex = message.indexOf("\n\n");

    if (bodyIndex !== -1) {
      const bodyText = message.substring(bodyIndex + 2).replace(/\0$/, "");
      const ackData = JSON.parse(bodyText);

      console.log("ACK recebido:", ackData);

      const presignedUrl = ackData.presignedUrl || ackData.attachmentInfo?.presignedUrl;

      if (presignedUrl) {
        const uploadUrl = presignedUrl.replace("chat-connect-app.localstack:4566", "localhost:4566");
        
        console.log("Presigned URL obtida:", presignedUrl);
        
        await uploadFileToStorage(uploadUrl, fileBuffer, contentType);
        
        ws.close();
      }
    }
  }
};

// Callbacks Nativos: Tratamento de Erro e Fechamento
ws.onerror = (error) => console.error("WebSocket error:", error);
ws.onclose = () => console.log("WebSocket fechado");

// Função HTTP PUT nativa
async function uploadFileToStorage(presignedUrl, buffer, mimeType) {
  try {
    const response = await fetch(presignedUrl, {
      method: "PUT",
      headers: {
        "Content-Type": mimeType
      },
      body: buffer,
    });

    if (response.ok) {
      console.log("Upload do arquivo para o S3 realizado com sucesso!");
    } else {
      console.error("Erro no upload do arquivo. Status:", response.status, response.statusText);
    }
  } catch (error) {
    console.error("Erro na requisição PUT:", error);
  }
}