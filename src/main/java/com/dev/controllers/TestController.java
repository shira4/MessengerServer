package main.java.com.dev.controllers;

import com.dev.Persist;
import com.dev.objects.MessageObject;
import com.dev.objects.UserObject;
import com.dev.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    private Persist persist;

    @PostConstruct
    private void init () {
    }

    @RequestMapping("sign-in")
    public String signIn (String username, String password)  {
        String token=null;
        if(!persist.userNameValidation(username)){
            token="userNotExist";
        }
        else {
            if (!persist.LoginAttempt(username, password) && (persist.loginFails(username) < 5)) {
                persist.loginAttemptsCounter(username);
                token = "invalidPassword";
            }
            if(persist.loginFails(username)>4){
                token="userBlocked";
            }
            if(persist.LoginAttempt(username,password)&&(persist.loginFails(username) < 5)){
                persist.resetLoginAttempts(username);
                token = persist.getTokenByUsernameAndPassword(username,password);

            }
        }
        return token;
    }
    @RequestMapping("create-account")
    public boolean createAccount (String username, String password) {
        boolean success = false;
        boolean alreadyExists = persist.getTokenByUsernameAndPassword(username, password) != null;
        if (!alreadyExists) {
            UserObject userObject = new UserObject();
            userObject.setUsername(username);
            userObject.setPassword(password);
            String hash = Utils.createHash(username, password);
            userObject.setToken(hash);
            success = persist.createAccount(userObject);
        }
        return success;
    }


    @RequestMapping("/get-username-by-token")
    public String getUsernameByToken(String token){

        return persist.getUsernameByToken(token);
    }

    @RequestMapping("send-message")
    public boolean sendMessage (String senderToken, String addressee_id, String subject, String content) {
        return persist.sendMessage(senderToken, addressee_id, subject, content);
    }
    @RequestMapping("get-messages")
    public List<MessageObject> getMessages (String token) {
        return persist.getMessages(token);
    }

    @RequestMapping("checkIfUserExistByUsername")
    public boolean checkIfUserExist(String username){
        return persist.userNameValidation(username);
    }

    @RequestMapping("delete-message")
    public boolean deleteMessage(int message_id){
        return persist.deleteMessage(message_id);
    }

    @RequestMapping("message-read")
    public boolean messageRead(int id){
        return persist.messageRead(id);
    }
}
