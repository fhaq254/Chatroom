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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.Shape;
import java.io.*;
import java.net.Socket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ClientMain extends Application {

	private static ClientInitController initController;
	public static ListViewController controller;
	public static DataModel model;
	public static Scene scene;
	public static String serverIp;
	public static String myIp;
	public static String myName;
	public static Color backgroundColor;

	private static final String splitter = "!@#";
	private static final Integer defaultPort = 8000;
	public static ChatRoom defaultChatRoom;

	/* List of Notifications:
	 * message : incoming text is a message to be posted,
	 * 				last value is the string representation of the port
	 * 				whose chat we want to append to
	 * newChat : includes the name of the chat, the name of all the users involved
	 * 			 	and the last value is the port to establish a connection with
	 */

	public static void main(String[] args) { launch(args); }

	public static void startConnection(ChatRoom newChatRoom) throws Exception {

		Socket mySock = new Socket(serverIp, newChatRoom.getPort()); 
		// Establishing a connection
		InputStreamReader streamReader = new InputStreamReader(mySock.getInputStream());
		PrintWriter chatWriter = new PrintWriter(mySock.getOutputStream());
		newChatRoom.setSocketIO(new BufferedReader(streamReader), chatWriter);
		model.getChatRoomList().add(newChatRoom);
		controller.addNewChat(newChatRoom, model);
		ChatRoom publicChatRoom = ClientMain.defaultChatRoom;
		InetAddress inet = mySock.getInetAddress();
		String hostname = inet.getHostName();
		publicChatRoom.sendToServer("setName" + ChatRoom.splitter + ClientMain.myName + ChatRoom.splitter + hostname);
		System.out.println("I'm IN.");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatUI.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(root);
        this.scene = scene;

		model = new DataModel();
		controller.initModel(model);

        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Initialize the first, public chatroom here. 
        ArrayList<String> clients = new ArrayList<>();
        myIp = InetAddress.getLocalHost().getHostName();
        clients.add(myIp);
        // Make a newChatRoom with the agreed default port
        defaultChatRoom = new ChatRoom("Public Chat Room", defaultPort, clients);

        //
		Parent initRoot;
		loader = new FXMLLoader(getClass().getResource("ClientInit.fxml"));
		initRoot = loader.load();
		initController = loader.getController();
		initController.initialize();
		Stage stage = new Stage();
		stage.setTitle("Connect");
		stage.setScene(new Scene(initRoot));
		stage.show();
		
		List<? extends Stage> c1 = new ArrayList<Stage>();
		
		
		
		
		
		
		
	}
}
