package ru.itis.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MessageHandler {

    public static void send(Message message, Socket socket) {
        String type = message.getType();
        String content = message.getContent();

        byte[] typeBytes = new byte[4];
        byte[] rawTypeBytes = type.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(rawTypeBytes, 0, typeBytes, 0, Math.min(4, rawTypeBytes.length));

        byte[] messageBytes = content.getBytes(StandardCharsets.UTF_8);
        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(messageBytes.length).array();

        byte[] data = new byte[4 + 4 + messageBytes.length];
        System.arraycopy(typeBytes, 0, data, 0, 4);
        System.arraycopy(lengthBytes, 0, data, 4, 4);
        System.arraycopy(messageBytes, 0, data, 8, messageBytes.length);

        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            System.out.println("отправил сообщение " + message.getType() + " "+message.getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message read(Socket socket) {
        try {

            InputStream inputStream = socket.getInputStream();

            byte[] typeBytes = new byte[4];
            int bytesRead = inputStream.read(typeBytes);

            if (bytesRead < 4) {
                return null;
            }
            String type = new String(typeBytes, StandardCharsets.UTF_8).trim();

            byte[] lengthBytes = new byte[4];
            bytesRead = inputStream.read(lengthBytes);
            if (bytesRead < 4) {
                return null;
            }
            int length = ByteBuffer.wrap(lengthBytes).getInt();

            byte[] messageBytes = new byte[length];
            bytesRead = inputStream.read(messageBytes);
            if (bytesRead < length) {
                return null;
            }
            String content = new String(messageBytes, StandardCharsets.UTF_8);

            System.out.println("прочитал сообщение " + type + " " + content);

            return new Message(type, content);

        } catch (IOException e) {
            return null;
        }
    }
}
