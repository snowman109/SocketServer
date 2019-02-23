

package com.tao.code;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private static ServerSocket serverSocket;
    public static HashMap<String, ServerThread> sockets = new HashMap<>();

    private Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(9977);
        server.start();
    }

    private void start() {
        try {
            while (true) {
                Socket s = serverSocket.accept();
                System.out.println("连接成功");
                // 初始化连接拿出用户名
                ObjectInputStream ois = new
                        ObjectInputStream(s.getInputStream());
                Message message = (Message) ois.readObject();
                String username = message.getMessage();


                ObjectOutputStream oos = new
                        ObjectOutputStream(s.getOutputStream());
                if (sockets.containsKey(username) || username.equals("")) {
                    oos.writeObject(new Message(MType.FALSE, "登陆失败"));
                    continue;
                } else {
                    oos.writeObject(new Message(MType.SUCCESS, "登陆成功"));
                    System.out.println(username + "登陆成功");
                }
                // 如果不传输入流会抛出异常
                ServerThread serverThread = new ServerThread(username, s, ois);
                sockets.put(username, serverThread);
                serverThread.start();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}




