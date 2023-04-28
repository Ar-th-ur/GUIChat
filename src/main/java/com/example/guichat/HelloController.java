package com.example.guichat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HelloController {
    Socket           socket;
    DataInputStream  in;
    DataOutputStream out;
    @FXML
    private TextField messageField;
    @FXML
    private TextArea  chatField;
    @FXML
    private Button    sendMessageButton;
    @FXML
    private Button    connectToServerButton;
    @FXML
    private VBox      usersList;

    @FXML
    protected void sendMessage() {
        try {
            JSONObject jsonObject = new JSONObject();
            String message = messageField.getText();
            System.out.println(message);
            chatField.appendText("Вы: " + message + "\n");
            messageField.clear();
            jsonObject.put("public", true);
            jsonObject.put("message", message);
            out.writeUTF(jsonObject.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void connect() {
        connectToServerButton.setDisable(true);
        sendMessageButton.setDisable(false);
        try {
            this.socket = new Socket("192.168.1.38", 9234);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        JSONObject jsonObject = new JSONObject(in.readUTF());
                        if (jsonObject.has("message")) {
                            chatField.appendText(jsonObject.get("message") + "\n");
                        } else if (jsonObject.has("onlineUsers")) {
                            JSONArray onlineUsers = (JSONArray) jsonObject.get("onlineUsers");
                            Platform.runLater(() -> {
                                usersList.getChildren().clear();
                                onlineUsers.forEach(user -> {
                                    Button button = new Button();
                                    button.setText(user.toString());
                                    button.setPrefWidth(Double.POSITIVE_INFINITY);
                                    usersList.getChildren().add(button);
                                });
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Потеряно соединение с сервером");
                }
            });
            thread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}