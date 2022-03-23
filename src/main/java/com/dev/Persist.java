package main.java.com.dev;

import com.dev.objects.MessageObject;
import com.dev.objects.UserObject;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class Persist {
    private Connection connection;

    @PostConstruct
    public void createConnectionToDatabase() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/messengertask", "root", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
/////
    public Integer getUserIdByToken(String token) {
        Integer id = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT id FROM users WHERE token = ?");
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;

    }

    public void resetLoginAttempts(String username){
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "UPDATE users SET loginAttempts=0 WHERE username = ?");
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public String getTokenByUsernameAndPassword(String username, String password) {
        String token = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT token FROM users WHERE username = ? AND password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                token = resultSet.getString("token");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;
    }

    public String getUsernameByToken(String token) {
        String username = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT username FROM users WHERE token=?");
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                username = resultSet.getString("username");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }
      //*home page*//
    public void loginAttemptsCounter(String username) {//increase login attempts by one  in every failed try
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "UPDATE users SET loginAttempts=loginAttempts + 1 WHERE username = ?");
            preparedStatement.setString(1, username);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int loginFails(String username)  {//login fails amount
        int failed = 0;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT loginAttempts  FROM users WHERE username =?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                failed = resultSet.getInt("loginAttempts");

            }
        } catch (SQLException e) {
            e.printStackTrace();

                }

              return failed;

    }



    public boolean LoginAttempt(String username, String password) {//return true if username and password matches
        boolean passwordIsValid = false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT password FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getString("password").equals(password))
                    passwordIsValid = true;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passwordIsValid;
    }

    public boolean userNameValidation(String username) {
        boolean usernameUsed = false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT username FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                usernameUsed = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usernameUsed;

    }

    public boolean createAccount(UserObject userObject) {
        boolean success = false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO users ( username, password , token) VALUE ( ?, ?, ? )");
            preparedStatement.setString(1, userObject.getUsername());
            preparedStatement.setString(2, userObject.getPassword());
            preparedStatement.setString(3, userObject.getToken());
            if (!this.userNameValidation(userObject.getUsername())) {
                preparedStatement.executeUpdate();
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }
        //*messenger*//


    public boolean sendMessage(String senderToken, String addressee_id, String subject, String content) {
        boolean success = false;
        try {
            Integer sender_id = this.getUserIdByToken(senderToken);
            Integer addressee = this.getUserIdByUsername(addressee_id);
            if (sender_id != null && addressee_id != null) {
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "INSERT INTO messages ( sender_id , addressee_id , subject ,  content , dispatch_time) VALUES ( ?, ? , ? , ? , NOW() )");
                preparedStatement.setInt(1, sender_id);
                preparedStatement.setInt(2, addressee);
                preparedStatement.setString(3, subject);
                preparedStatement.setString(4, content);
                preparedStatement.executeUpdate();
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }


    public Integer getUserNameById(int userId){
        Integer id = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT username FROM users WHERE id = ?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public Integer getUserIdByUsername(String username){
        Integer id = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT id FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public List<MessageObject> getMessages (String token) {
        List<MessageObject> messageObjects = new ArrayList<>();
        try {
            Integer userId = getUserIdByToken(token);
            if(userId != null) {
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "SELECT * FROM messages m" +
                                " JOIN users u ON m.sender_id = u.id " +
                                "WHERE addressee_id = ? " +
                                "ORDER BY dispatch_time DESC");
                preparedStatement.setInt(1, userId);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        messageObjects.add(new MessageObject(
                                resultSet.getInt("message_id"),
                                resultSet.getString("u.username"),
                                resultSet.getString("subject"),
                                resultSet.getString("content"),
                                resultSet.getString("dispatch_time")

                        ));

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageObjects;
    }


    public boolean deleteMessage(int message_id ) {
        boolean success = false;
        try{
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "DELETE FROM messages WHERE  message_id=?");
            preparedStatement.setInt(1,message_id);
            preparedStatement.executeUpdate();
            success = true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return success;
    }

    public boolean messageRead(int message_id) {
        boolean success = false;
        try{
            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(
                    "UPDATE messages SET `read`= 1,read_time=NOW() WHERE message_id = ?");
            preparedStatement.setInt(1, message_id);
                preparedStatement.executeUpdate();
                success = true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }
}






