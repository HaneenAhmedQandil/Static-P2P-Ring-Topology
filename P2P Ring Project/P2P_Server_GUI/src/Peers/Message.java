package Peers;

import java.util.UUID;

public class Message {

    private String id;
    private String sender;
    private String recipient;
    private String msg;
    private boolean isPrivate;

    public Message(String sender, String recipient, String content, boolean isPrivate) 
    {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.recipient = recipient;
        this.msg = content;
        this.isPrivate = isPrivate;
    }

    public Message(String messageString) 
    {
        String[] parts = messageString.split(",");
        this.id = parts[0];
        this.sender = parts[1];
        this.recipient = parts[2];
        this.msg = parts[3];
        this.isPrivate = Boolean.parseBoolean(parts[4]);
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return msg;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setContent(String content) {
        this.msg = content;
    }

    @Override
    public String toString() {
        return id + "," + sender + "," + recipient + "," + msg + "," + isPrivate;
    }
}
