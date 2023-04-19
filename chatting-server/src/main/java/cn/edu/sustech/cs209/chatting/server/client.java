package cn.edu.sustech.cs209.chatting.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class client {

  List<String> clientlist;
  HashMap<String, Socket> hash;

  public client() {
    clientlist = new ArrayList<>();
    hash = new HashMap<>();
  }

  public synchronized void addclient(String s) {
    this.clientlist.add(s);
  }

  public synchronized void removeclient(String s) {

    clientlist.removeIf(t -> t.equals(s));
  }

  public synchronized void putsocke(String s, Socket sock) {
    hash.put(s, sock);
  }


  public synchronized void removesocke(String s, Socket sock) {
    hash.remove(s, sock);
  }


  public List<String> getClientlist() {
    return clientlist;
  }

  public HashMap<String, Socket> getHash() {
    return hash;
  }

}
