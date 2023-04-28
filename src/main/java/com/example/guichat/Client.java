package com.example.guichat;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private final Socket           soket;
    private final DataOutputStream out;
    private final DataInputStream  in;

    public Client(String host, int port) throws IOException {
        this.soket = new Socket(host, port);
        this.out = new DataOutputStream(this.soket.getOutputStream());
        this.in = new DataInputStream(this.soket.getInputStream());
    }

    public void start(TextArea chatField, VBox usersList, Button connectToServerButton) {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    JSONObject response = new JSONObject(in.readUTF());
                    if (response.has("message")) {
                        chatField.appendText(response.getString("message") + "\n");
                    }
//                    else if (response.has("onlineUsers") && !response.getJSONArray("onlineUsers").isEmpty()) {
//                        Platform.runLater(() -> {
//                            usersList.getChildren().clear();
//                            response.getJSONArray("onlineUsers").forEach(user -> {
//                                Button button = new Button();
//                                button.setPrefWidth(Double.POSITIVE_INFINITY);
//                                button.setText((String) user);
//                                usersList.getChildren().add(button);
//                            });
//                        });
//                    }
                }
            } catch (IOException e) {
                System.out.println("Соединение с сервером потеряно");
                System.out.println(e.getMessage());
                connectToServerButton.setDisable(false); // отключение в классе, а остальные в другом
            }
        });
        thread.start();
    }

    public void sendMessage(String message) throws IOException {
        JSONObject jo = new JSONObject();
        jo.put("private", false);
        jo.put("message", message);
        out.writeUTF(jo.toString());
    }
}
