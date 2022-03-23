package main.java.com.dev.objects;

import org.joda.time.DateTime;

import java.sql.Date;

public class MessageObject {
    private int message_id;
    private String sender;
    private String subject;
    private String content;
    private String time;
    private String read_time;
    private int read;


   // public MessageObject(){}
    public MessageObject(int id, String sender, String subject, String content, String dispatch){
        this.message_id=id;
        this.sender=sender;
        this.subject=subject;
        this.content=content;
        this.time=dispatch;

    }

    public int getId() {
        return message_id;
    }

    public String getSender() { return sender;}

    public String getDispatch() {
        return time;
    }

    public String getContent() {
        return content;
    }
    public String getSubject() {
        return subject;
    }
    public int isRead() {
        return read;
    }

    public void setId(int id) {

        this.message_id = id;
    }

    public void setSender(String sender) {

        this.sender = sender;
    }

    public void setDispatch(String dispatch) {

        this.time = dispatch;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public void setSubject(String subject) {

        this.subject = subject;
    }

    public void setRead(int read) {

        this.read = read;
    }
}