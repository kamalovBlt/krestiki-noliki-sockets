package ru.itis.protocol;

public class Message {

    private final String type;
    private final String content;

    public Message(String type, String message) {
        this.type = type;
        this.content = message;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

}
