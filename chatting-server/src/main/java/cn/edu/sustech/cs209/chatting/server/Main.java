package cn.edu.sustech.cs209.chatting.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {


  public static void main(String[] args) throws IOException {

    System.out.println("Starting server");
    client c = new client();

    ServerSocket ss = new ServerSocket(8888);

    Runtime.getRuntime().addShutdownHook(
        new Thread() {
          public void run() {
            Socket[] sss = new Socket[c.clientlist.size()];
            for (int i = 0; i < sss.length; i++) {
              sss[i] = c.getHash().get(c.clientlist.get(i));

            }

            for (int i = 0; i < sss.length; i++) {
              PrintWriter outt = null;
              try {
                outt = new PrintWriter(sss[i].getOutputStream());
              } catch (IOException e) {
                e.printStackTrace();
              }
              String resp = "close";
              outt.println(resp);
              outt.flush();
            }
          }

        }
    );

    while (true) {

      Socket s = ss.accept();
      System.out.println("Client connected");
      service serv = new service(s, c);
      Thread t = new Thread(serv);
      t.start();
    }


  }


}

