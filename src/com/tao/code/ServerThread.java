package com.tao.code;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class ServerThread extends Thread {
    private String username;
    private Socket socket;
    private ObjectInputStream ois;
    private boolean notify;

    ServerThread(String username, Socket socket, ObjectInputStream ois) {
        this.username = username;
        this.ois = ois;
        this.socket = socket;
        this.notify = false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message;
                if (!notify) {
                    message = new Message(MType.LOGIN, username + "登陆成功");
                    notify = true;
                } else {
                    message = (Message) ois.readObject();
                }
                if (!socket.isClosed() && message != null) {
                    for (Map.Entry<String, ServerThread> s : Server.sockets.entrySet()) {
                        if (!s.getKey().equals(username)) {
                            ServerThread serverThread = s.getValue();
                            ObjectOutputStream objectOutputStream = new
                                    ObjectOutputStream(serverThread.socket.getOutputStream());
                            objectOutputStream.writeObject(message);
                        }
                    }
                    if (message.getType().equals(MType.LOGOUT)) {
                        Server.sockets.remove(username);
                        System.out.println("成功退出" + username);
                        return;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}


