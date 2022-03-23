package main.java.com.dev.objects;

import java.util.List;

public class UserObject {
    private String username;
    private String password;
    private String token;
    private List<MessageObject> message;



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void addMessage (String message) {
    }
    public List<MessageObject> getMessage() {
        return message;
    }
    public void setPosts(List<MessageObject> message) {
       this.message = message;
    }
}
