package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class Controller implements Initializable {

    public Label currentOnlineCnt;
    public TextArea inputArea;
    @FXML
    ListView<Message> chatContentList;
    @FXML
    ListView<String> chatList;
    @FXML
    Label currentUsername;

    String username;
    String chatwit;
    Socket s;
    Scanner in ;
    PrintWriter out ;
    List<String> pricha=new ArrayList<>();

    HashMap<String, List<Message>> has=new HashMap<>();
    HashMap<String, String> grou=new HashMap<>();
    HashMap<Integer,char[]> emoj=new HashMap<>();
    boolean lis=false;
    String lisresp;

    Thread rece=new Thread(()->
    {
        while (true)
        {


            if(in.hasNext()) {
                String s=in.next();
                if(s.equals("list"))
                {

                    lisresp=in.next();

                    lis=true;
                }
               else if (s.equals("receive")) {

                    long time = Long.parseLong(in.next());
                    String sentb = in.next();
                    String sendt = in.next();
                    String dat = in.next();
                    dat=dat.replace('|','\n');
                    StringBuilder st=new StringBuilder();
                    for(int i=0;i<dat.length();i++)
                    {
                        if(dat.charAt(i)=='#')
                        {
                            int ll=dat.charAt(i+1)-48;
                           if(ll==1)
                           {
                               int m=dat.charAt(i+2)-48;
                               st.append(emoj.get(m));
                               i++;
                               i++;
                           }
                           else
                           {
                               int m=dat.charAt(i+2)-48;
                               int mm=dat.charAt(i+3)-48;
                               int l=10*m+mm;
                               st.append(emoj.get(l));
                               i++;
                               i++;
                               i++;

                           }


                        }
                        else
                        {
                            st.append(dat.charAt(i));
                        }
                    }
                    dat=st.toString();

                    Message m = new Message(time, sentb, sendt, dat);
                    List<Message> mm=new ArrayList<>();
                    if(has.containsKey(sentb))
                    {
                        mm=has.get(sentb);
                    }

                    mm.add(m);
                    has.put(sentb,mm);
                    ObservableList<Message> obst = FXCollections.observableArrayList(mm);

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if(sentb.equals(chatwit)){chatContentList.setItems(obst);}
                            if(!pricha.contains(sentb)) {
                                pricha.add(sentb);
                                ObservableList<String> obstt = FXCollections.observableArrayList(pricha);
                                chatList.setItems(obstt);

                            }

                        }
                    });


                }

               else if(s.equals("grourece"))
                {
                    long time = Long.parseLong(in.next());
                    String sentb = in.next();

                    String dat = in.next();
                    String memb=in.next();
                    String ss = in.nextLine();
                    StringBuilder sendtt=new StringBuilder();
                    dat=dat.replace('|','\n');
                    StringBuilder stt=new StringBuilder();
                    for(int i=0;i<dat.length();i++)
                    {
                        if(dat.charAt(i)=='#')
                        {
                            int ll=dat.charAt(i+1)-48;
                            if(ll==1)
                            {
                                int m=dat.charAt(i+2)-48;
                                stt.append(emoj.get(m));
                                i++;
                                i++;
                            }
                            else
                            {
                                int m=dat.charAt(i+2)-48;
                                int mm=dat.charAt(i+3)-48;
                                int l=10*m+mm;
                                stt.append(emoj.get(l));
                                i++;
                                i++;
                                i++;

                            }


                        }
                        else
                        {
                            stt.append(dat.charAt(i));
                        }
                    }
                    dat=stt.toString();
                    for(int i=2;i<ss.length();i++)
                    {
                        sendtt.append(ss.charAt(i));
                    }
                    String sendt=sendtt.toString();

                    Message m = new Message(time, sentb, sendt, dat);
                    List<Message> mm=new ArrayList<>();
                    if(has.containsKey(sendt))
                    {
                        mm=has.get(sendt);
                    }

                    mm.add(m);
                    has.put(sendt,mm);
                    ObservableList<Message> obst = FXCollections.observableArrayList(mm);
                    if(!grou.containsKey(sendt))
                    {
                        List<String> groumem= Arrays.asList(memb.split(","));
                        List<String> gruomemm=groumem.stream().filter((t)->!t.equals(username)).collect(
                            Collectors.toList());
                        StringBuilder st=new StringBuilder();
                        for(int i=0;i<gruomemm.size();i++)
                        {
                            st.append(gruomemm.get(i));
                            st.append(",");
                        }
                        st.append(sentb);
                        grou.put(sendt,st.toString());
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if(sendt.equals(chatwit)){chatContentList.setItems(obst);}
                            if(!pricha.contains(sendt)) {
                                pricha.add(sendt);
                                ObservableList<String> obstt = FXCollections.observableArrayList(pricha);
                                chatList.setItems(obstt);

                            }

                        }
                    });
                }
               else if(s.equals("new"))
                {
                    int l=in.nextInt();

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            currentOnlineCnt.setText(String.valueOf(l));

                            }

                        }
                    );
                }
                else if(s.equals("quit"))
                {
                    int l=in.nextInt();
                    String na=in.next();
                    boolean b=false;
                    for(int i=0;i<pricha.size();i++)
                    {
                        if(pricha.get(i).equals(na))
                        {
                            b=true;
                        }
                    }

                    boolean finalB = b;
                    Platform.runLater(new Runnable() {
                                          @Override
                                          public void run() {
                                              if(finalB) {
                                                  Alert ale = new Alert(AlertType.INFORMATION);
                                                  ale.setTitle("Dialog");
                                                  ale.setHeaderText(null);
                                                  ale.setContentText(
                                                      na+" is offline");
                                                  ale.showAndWait();
                                              }
                                              currentOnlineCnt.setText(String.valueOf(l));

                                          }

                                      }
                    );
                }


            }
        }
    });


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for(int i=0;i<50;i++)
        {

            emoj.put(i,Character.toChars(0x1f600+i));
        }


        chatList.getSelectionModel().selectedItemProperty().addListener((obs,oldv,newv)->{chatwit=newv;

            ObservableList<Message> obst=null;
            if(has.containsKey(newv)){obst = FXCollections.observableArrayList(has.get(newv));

            }

            ObservableList<Message> finalObst = obst;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {


                        chatContentList.setItems(finalObst);


                }
            });



        });
        Dialog<String> dialog = new TextInputDialog();
        dialog.setTitle("Login");
        dialog.setHeaderText(null);
        dialog.setContentText("Username:");

        Optional<String> input = dialog.showAndWait();
        if (input.isPresent() && !input.get().isEmpty()) {
            /*
               TODO: Check if there is a user with the same name among the currently logged-in users,
                     if so, ask the user to change the username
             */
            try {


                    Socket s = new Socket("localhost", 8888);
                    InputStream inp = s.getInputStream();
                    OutputStream outp = s.getOutputStream();
                    Scanner in = new Scanner(inp);
                    PrintWriter out = new PrintWriter(outp);
                    this.s=s;
                    this.in=in;
                    this.out=out;
                    while (true) {

                        username = input.get();
                        String command = "add " + username;
                        out.println(command);
                        out.flush();
                        String resp = in.next();
                        if (!resp.equals("false")) {
                            currentUsername.setText("Current User: "+username);
                            rece.start();


                                break;
                        }
                        Alert ale=new Alert(AlertType.INFORMATION);
                        ale.setTitle("Dialog");
                        ale.setHeaderText(null);
                        ale.setContentText("The name exist, please try again");
                        ale.showAndWait();


                        input = dialog.showAndWait();
                    }

            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            System.out.println("Invalid username " + input + ", exiting");
            Platform.exit();
        }

        chatContentList.setCellFactory(new MessageCellFactory());
    }

    @FXML
    public void createPrivateChat() {



        // FIXME: get the user list from server, the current user's name should be filtered out


        Thread taskThread = new Thread(new Runnable() {
            @Override
            public void run() {



                String command = "getlist";
                out.println(command);
                out.flush();
                AtomicBoolean b= new AtomicBoolean(true);

                while (true)
                {
                    System.out.print("");


                    if(lis)
                    {

                        break;
                    }
                }
                lis=false;

                List<String> cl= Arrays.asList(lisresp.split(","));



                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            AtomicReference<String> user = new AtomicReference<>();

                            Stage stage = new Stage();
                            ComboBox<String> userSel = new ComboBox<>();
                            userSel.getItems().addAll(cl);
                            Button okBtn = new Button("OK");
                            okBtn.setOnAction(e -> {
                                user.set(userSel.getSelectionModel().getSelectedItem());
                                stage.close();
                            });
                            HBox box = new HBox(10);
                            box.setAlignment(Pos.CENTER);
                            box.setPadding(new Insets(20, 20, 20, 20));
                            box.getChildren().addAll(userSel, okBtn);
                            stage.setScene(new Scene(box));
                            stage.setOnCloseRequest(l->{
                                b.set(false);});

                            stage.showAndWait();
                       if(b.get()) {
                           if (!cl.get(0).equals("null")) {
                               chatwit = user.get();
                           }

                           if (!pricha.contains(user.get()) && !cl.get(0).equals("null")) {
                               pricha.add(user.get());
                               ObservableList<String> obst = FXCollections.observableArrayList(
                                   pricha);
                               chatList.setItems(obst);

                           }
                          else if (pricha.contains(user.get()) && !cl.get(0).equals("null")) {
                               List<Message> mm=new ArrayList<>();
                               if(has.containsKey(chatwit))
                               {
                                   mm=has.get(chatwit);
                               }



                               ObservableList<Message> obst = FXCollections.observableArrayList(mm);
                               chatContentList.setItems(obst);



                           }
                       }
                        }
                    });


            }
        });

        taskThread.start();










        // TODO: if the current user already chatted with the selected user, just open the chat with that user
        // TODO: otherwise, create a new chat item in the left panel, the title should be the selected user's name
    }

    /**
     * A new dialog should contain a multi-select list, showing all user's name.
     * You can select several users that will be joined in the group chat, including yourself.
     * <p>
     * The naming rule for group chats is similar to WeChat:
     * If there are > 3 users: display the first three usernames, sorted in lexicographic order, then use ellipsis with the number of users, for example:
     * UserA, UserB, UserC... (10)
     * If there are <= 3 users: do not display the ellipsis, for example:
     * UserA, UserB (2)
     */
    @FXML
    public void createGroupChat() {

        Thread taskThread = new Thread(new Runnable() {
            @Override
            public void run() {



                String command = "getlist";
                out.println(command);
                out.flush();

                while (true)
                {

                  System.out.print("");
                    if(lis)
                    {

                        break;
                    }
                }
                lis=false;

                List<String> cl= Arrays.asList(lisresp.split(","));




                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        List<String> user = new ArrayList<>();
                        Stage stage = new Stage();
                      List<CheckBox> c=new ArrayList<>();
                      AtomicBoolean b= new AtomicBoolean(true);


                        Button okBtn = new Button("OK");
                        for(int i=0;i<cl.size();i++)
                        {
                            c.add(new CheckBox(cl.get(i)));
                        }
                        okBtn.setOnAction(e -> {
                            for(int i=0;i<c.size();i++)
                            {
                                if(c.get(i).isSelected())
                                {
                                    user.add(cl.get(i));
                                }
                            }
                            stage.close();

                        });
                        VBox box = new VBox(10);
                        box.setAlignment(Pos.CENTER);
                        box.setPadding(new Insets(20, 20, 20, 20));

                        for(int i=0;i<c.size();i++) {
                            box.getChildren().addAll(c.get(i));
                        }
                        box.getChildren().addAll(okBtn);
                        stage.setScene(new Scene(box));
                        stage.setOnCloseRequest(l-> b.set(false));

                        stage.showAndWait();





                                if(b.get()) {

                                    StringBuilder s = new StringBuilder();
                                    StringBuilder ss = new StringBuilder();
                                    if (user.size() + 1 <= 3) {
                                        s.append(username + ", ");
                                        for (int i = 0; i < user.size(); i++) {
                                            if (i != user.size() - 1) {
                                                s.append(user.get(i) + ", ");
                                                ss.append(user.get(i) + ",");
                                            } else {
                                                s.append(user.get(i) + " ");
                                                ss.append(user.get(i));
                                            }
                                        }
                                        int usersiz = user.size() + 1;
                                        s.append("(" + usersiz + ")");


                                    } else {

                                        for (int i = 0; i < user.size(); i++) {
                                            if (i != user.size() - 1) {

                                                ss.append(user.get(i) + ",");
                                            } else {

                                                ss.append(user.get(i));
                                            }
                                        }
                                        List<String> l = new ArrayList<>();
                                        for (int i = 0; i < user.size(); i++) {
                                            l.add(user.get(i));
                                        }
                                        l.add(username);
                                        l = l.stream().sorted().collect(Collectors.toList());
                                        for (int i = 0; i < 3; i++) {
                                            if (i != 2) {
                                                s.append(l.get(i) + ", ");
                                            } else {
                                                s.append(l.get(i));
                                            }
                                        }
                                        s.append("... ");
                                        s.append("(" + l.size() + ")");
                                    }

                                    if (!pricha.contains(s.toString()) && !cl.get(0)
                                        .equals("null")) {
                                        pricha.add(s.toString());
                                        ObservableList<String> obst = FXCollections.observableArrayList(
                                            pricha);
                                        chatList.setItems(obst);
                                        grou.put(s.toString(), ss.toString());
                                    }
                                    if (!cl.get(0).equals("null")) {
                                        chatwit = s.toString();
                                    }
                                }

                    }
                });


            }
        });

        taskThread.start();

    }

    /**
     * Sends the message to the <b>currently selected</b> chat.
     * <p>
     * Blank messages are not allowed.
     * After sending the message, you should clear the text input field.
     */
    @FXML
    public void doSendMessage() {
        // TODO
        long time=System.currentTimeMillis();
        String sentb=username;
        String sendt=chatwit;
        String dat=inputArea.getText();
        Message m=new Message(time,sentb,sendt,dat);
        List<Message> mm=new ArrayList<>();
        if(!dat.isEmpty()) {
            if (has.containsKey(sendt)) {
                mm = has.get(sendt);
            }

            mm.add(m);
            has.put(sendt, mm);
            ObservableList<Message> obst = FXCollections.observableArrayList(mm);
            chatContentList.setItems(obst);
            inputArea.setText(null);
            String command;
            dat=dat.replace('\n','|');
            StringBuilder st=new StringBuilder();
            for(int i=0;i<dat.length();i++)
            {
                if(dat.charAt(i)==55357)
                {
                    st.append("#");
                    int l=(int)dat.charAt(i+1)-56832;
                    if(l<10)
                    {
                        st.append(1);
                    }
                    else
                    {
                        st.append(2);
                    }
                    st.append(l);
                    i++;
                }
                else
                {
                    st.append(dat.charAt(i));
                }
            }
            dat=st.toString();
            if (!sendt.contains(",")) {

                command = "send " + time + " " + sentb + " " + sendt + " " + dat;
            } else {
                String s = grou.get(sendt);
                command = "grou " + time + " " + sentb + " " + s + " " + dat + " " + sendt;
            }

            out.println(command);
            out.flush();
        }


    }

    /**
     * You may change the cell factory if you changed the design of {@code Message} model.
     * Hint: you may also define a cell factory for the chats displayed in the left panel, or simply override the toString method.
     */
    private class MessageCellFactory implements Callback<ListView<Message>, ListCell<Message>> {
        @Override
        public ListCell<Message> call(ListView<Message> param) {
            return new ListCell<Message>() {

                @Override
                public void updateItem(Message msg, boolean empty) {
                    super.getChildren().clear();
                    super.updateItem(msg, empty);
                    if (empty || Objects.isNull(msg)) {
                        return;
                    }

                    HBox wrapper = new HBox();
                    Label nameLabel = new Label(msg.getSentBy());
                    Label msgLabel = new Label(msg.getData());

                    nameLabel.setPrefSize(50, 20);
                    nameLabel.setWrapText(true);
                    nameLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

                    if (username.equals(msg.getSentBy())) {
                        wrapper.setAlignment(Pos.TOP_RIGHT);
                        wrapper.getChildren().addAll(msgLabel, nameLabel);
                        msgLabel.setPadding(new Insets(0, 20, 0, 0));
                    } else {
                        wrapper.setAlignment(Pos.TOP_LEFT);
                        wrapper.getChildren().addAll(nameLabel, msgLabel);
                        msgLabel.setPadding(new Insets(0, 0, 0, 20));
                    }

                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(wrapper);
                }
            };
        }
    }
}
