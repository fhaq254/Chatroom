/* CHAT ROOM <MyClass.java>
 * EE422C Project 7 submission by
 * Replace <...> with your actual data.
 * Fawadul Haq
 * fh5277
 * 16225
 * Drew Bernard
 * dhb653
 * 16225
 * Slip days used: 1
 * Spring 2019
 * GitHub: https://github.com/EE422C/project-7-chat-room-pr7-pair-8.git
 */

package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import animatefx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class ChatRoom {

    public static final String splitter = "!@#";
    public static String incomingMessage;
    private final StringProperty roomName = new SimpleStringProperty();
    private final Integer port;
    private final Set<String> clients;
    private BufferedReader reader;
    private PrintWriter writer;
    private String FXMLChatWindow;
    private String FXMLChatScrollPane;
    private boolean allClientsFlag = true;
    private String allClientsMail;
    private CountDownLatch latch;


    public final StringProperty roomNameProperty() {return this.roomName; }
    public final String getRoomName() { return this.roomNameProperty().get(); }
    public final void setRoomName(final String roomName) { this.roomNameProperty().set(roomName); }
    public final Integer getPort() { return port; }
    public final void setFXMLChatWindow(String id) { FXMLChatWindow = id; }
    public final String getFXMLChatWindow() { return FXMLChatWindow; }
    public final void setFXMLChatScrollPane(String id) { FXMLChatScrollPane = id; }
    public final String getFXMLChatScrollPane() { return FXMLChatScrollPane; }


    public ChatRoom(String roomName, Integer portNumber, ArrayList<String> clients){
        setRoomName(roomName);
        port = portNumber;
        this.clients = new HashSet<>(clients);
    }

    public void setSocketIO(BufferedReader reader, PrintWriter writer) {
        this.reader = reader;
        this.writer = writer;
        // Start reading messages
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
    }

    class IncomingReader implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    doNotif(message);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private synchronized void doNotif(String message) {

        System.out.println("doNotif(): message=" + message);

        // Parsing command
        String[] fullCommand = message.split(splitter); // can we make a Params like class?

        switch(fullCommand[0]) {
            case "message":
                // send text message to current chat
                if(fullCommand.length == 1) {
                    Platform.runLater(() -> {
                        postMessage("");
                    });
                }
                else {
                    Platform.runLater(() -> {
                        postMessage(message);
                    });
                }
                break;
            case "newChat":
                // make a new chat
                Platform.runLater(() -> {
                    makeChat(message);
                });
                break;
            case "addClient":
            	addClient(fullCommand[1]);
            	break;
            case "allClients":
                if(allClientsFlag == false) {
                    System.out.println("Got all clients");
                    allClientsMail = message.substring("allClients".length() + splitter.length());
                    allClientsFlag = true;
                    latch.countDown();
                }
                break;
            default :
                break;
        }
    }

    private void postMessage(String message) {
        String[] parts = message.split(splitter);
        String name = parts[1];
        String text = parts[2];
        String messageText = "";
        VBox chatWindow = (VBox) ClientMain.scene.lookup("#"+FXMLChatWindow);
        ScrollPane scrollPane = (ScrollPane) ClientMain.scene.lookup("#"+FXMLChatScrollPane);
        chatWindow.setPrefHeight(15);
        BorderPane chatEntry = new BorderPane();
        chatEntry.setPrefHeight(15);
        chatEntry.setPrefWidth(scrollPane.getWidth() - 15);
        
        // Get timestamp
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a"); 
        Date date = new Date();
        String timestamp = timeFormat.format(date);

        Label messageLbl = new Label();
        //messageLbl.getStyleClass().add("glass-grey");
        String color = String.format( "#%02X%02X%02X",
                (int)( ClientMain.backgroundColor.getRed() * 255 ),
                (int)( ClientMain.backgroundColor.getGreen() * 255 ),
                (int)( ClientMain.backgroundColor.getBlue() * 255 ));

        //messageLbl.setStyle("-fx-background-color: " + color + ", linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);");
        messageLbl.setStyle(
                "    -fx-background-color:\n" +
                        "            #c3c4c4,\n" +
                        "            linear-gradient(#d6d6d6 50%, white 100%),\n" +
                        "            radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);" +
                "    -fx-background-radius: 30;\n" +
                "    -fx-background-insets: 0,1,1;\n" +
                "    -fx-text-fill: " + color + ";\n" +
                "    -fx-padding: 2px 5px 2px 5px;\n" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 );"
        );

        if(name.equals(ClientMain.myName)) {
            messageText = "[" + timestamp + "] " + text + "\n";
            chatEntry.setRight(messageLbl);
            messageLbl.setText(messageText);
            chatWindow.getChildren().add(chatEntry);
            new ZoomInRight(messageLbl).play();
        } else {
            messageText = "[" + timestamp + "] " + name + ": " + text + "\n";
            chatEntry.setLeft(messageLbl);
            messageLbl.setText(messageText);
            chatWindow.getChildren().add(chatEntry);
            new ZoomInLeft(messageLbl).play();
        }
    }

    public void sendMessage(String message) {
        String messageToSend = "message" + splitter + ClientMain.myName + splitter + message;
        sendToServer(messageToSend);
    }

    public void sendToServer(String text) {
        writer.println(text);
        writer.flush();
    }

    public ArrayList<String> getAllClients() {
        allClientsFlag = false;
        //allClientsFlag will be set by thread reading incoming messages
        System.out.println("Getting all clients...");
        latch = new CountDownLatch(1);
        sendToServer("allClients");
        try {
            System.out.println("Waiting...");
            latch.await();
            System.out.println("Received all clients");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>(Arrays.asList(allClientsMail.split(splitter)));
        //Shouldn't return null
    }

    private void makeChat(String message) {
        String roomName = "";
        Integer portNum = 0;
        ArrayList<String> clients = new ArrayList<>();

        // Parsing message to receive information
		String[] fullCommand = message.split(splitter);
		roomName = fullCommand[1];
        portNum = Integer.valueOf(fullCommand[fullCommand.length - 1]);
		for(int i = 2; i < fullCommand.length-1; i++)
			  clients.add(fullCommand[i]);
		
		if(clients.contains(ClientMain.myName)) {
			System.out.println("Create chat room: name=" + roomName + " port=" + portNum + " clients=" + clients);
	        ChatRoom newChatRoom = new ChatRoom(roomName, portNum, clients);
	        try {
	            ClientMain.startConnection(newChatRoom);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
    }

    private void addClient(String newClient) {
        clients.add(newClient);
        System.out.println("addClient(): allClients=" + clients);
    }

}
