package com.example.demo.DTO;

public class MessageDTO {
    private String name;
    private String content;
    private String timestamp;

    public MessageDTO(String name, String content, String timestamp) {
        this.name = name;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getName() { return name; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
}
