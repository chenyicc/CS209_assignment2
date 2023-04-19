package cn.edu.sustech.cs209.chatting.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class service implements Runnable{
  private Socket s;
  private client c;

  String name;



  private Scanner in;
  private PrintWriter out;
  public service(Socket s,client c)
  {
    this.s=s;
    this.c=c;





  }

  @Override
  public void run() {
    try {

      try {
        in = new Scanner(s.getInputStream());
        out = new PrintWriter(s.getOutputStream());

        doservice();
      } finally {
        c.removeclient(name);
        Socket[] sss=new Socket[c.clientlist.size()];
        for(int i=0;i<sss.length;i++) {
          sss[i]= c.getHash().get(c.clientlist.get(i));

        }

        for(int i=0;i<sss.length;i++) {
          PrintWriter outt = new PrintWriter(sss[i].getOutputStream());
          String resp = "quit "+c.clientlist.size()+" "+name;
          outt.println(resp);
          outt.flush();
        }

        s.close();
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }


  }

  public void doservice() throws IOException {
    while (true) {
      if(!in.hasNext())
      {
        return;
      }


      String command=in.next();

      if(command.equals("add"))
      {
        String name=in.next();

        if(!c.getClientlist().contains(name)) {
          c.addclient(name);
          c.putsocke(name,s);
         this.name=name;
          out.println("true");
          out.flush();

          Socket[] sss=new Socket[c.clientlist.size()];
          for(int i=0;i<sss.length;i++) {
            sss[i]= c.getHash().get(c.clientlist.get(i));

          }

          for(int i=0;i<sss.length;i++) {
            PrintWriter outt = new PrintWriter(sss[i].getOutputStream());
            String resp = "new "+c.clientlist.size();
            outt.println(resp);
            outt.flush();
          }





        }
        else
        {
          out.println("false");
          out.flush();
        }
      }
      else if(command.equals("getlist"))
      {
        List<String> clien=c.getClientlist().stream().filter(s->!s.equals(name)).collect(Collectors.toList());
        StringBuilder cl=new StringBuilder();
        cl.append("list ");
        for(int i=0;i<clien.size();i++)
        {

            cl.append(clien.get(i));
            if(i!=clien.size()-1) {
              cl.append(",");
            }

        }
        if(clien.size()==0)
        {
          cl.append("null");
        }

        out.println(cl);
        out.flush();

      }
      else if(command.equals("send"))
      {

        long time=Long.parseLong(in.next());
        String sentb=in.next();
        String sendt=in.next();
        String dat=in.next();

        if(c.getClientlist().contains(sendt)) {
          Socket s = c.getHash().get(sendt);
          PrintWriter outt = new PrintWriter(s.getOutputStream());
          String resp = "receive " + time + " " + sentb + " " + sendt + " " + dat;
          outt.println(resp);
          outt.flush();
        }
      }
      else if(command.equals("grou"))
      {

        long time=Long.parseLong(in.next());
        String sentb=in.next();
        String sendt=in.next();
        String dat=in.next();
        String grounam=in.nextLine();
        String[] ss=sendt.split(",");
        List<Socket> sss=new ArrayList<>();
        for(int i=0;i<ss.length;i++) {
          if(c.getClientlist().contains(ss[i])) {
            sss.add(c.getHash().get(ss[i]));
          }
        }
        for(int i=0;i<sss.size();i++) {
          PrintWriter outt = new PrintWriter(sss.get(i).getOutputStream());
          String resp = "grourece " + time + " " + sentb +  " " + dat+" " +sendt+" "+grounam;
          outt.println(resp);
          outt.flush();
        }
      }

    }
  }
}
