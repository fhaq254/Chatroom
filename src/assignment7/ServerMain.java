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

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerMain {
	
	private static Map<Integer, Chat> OpenChats = Collections.synchronizedMap(new HashMap());
	// Map of hostnames to their Observer/Writer object
//	private static Map<String, ClientObserver> AllClients = Collections.synchronizedMap(new HashMap<>());
	// Put these below into a Params??
	private static Integer publicPort = 8000;
	private static Integer nextPort = 8000;
	private static final String splitter = "!@#";
	private static ArrayList<String> badwords = new ArrayList<>();
	
	/* List of Commands:
	 * message : the remaining portion of the message is text to send
	 * newChat : the remaining portion is the name of the new chat to be made,
	 * 			 the names of the users to add, and the port number of the chat
	 * addClient : the hostname of a client to be added to a chatroom
	*/
	
	public static void main(String[] args) {
		// Initialize public chat (default port 8000) to start server and run it
		new Thread((new ServerMain()).new Chat("Public Chat Room", nextPort)).start();
	}
	
	public ServerMain() {
		// Initialize badwords list to censor messages
		File badwordList = new File("src/badwords.txt");
		try {
			Scanner scan = new Scanner(badwordList);

			while (scan.hasNextLine())
				badwords.add(scan.nextLine());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
			
	}

	private class Chat extends Observable implements Runnable{
        private Map<String, ClientObserver> AllClients = Collections.synchronizedMap(new HashMap<>());
        private Map<String, String> AllNames = Collections.synchronizedMap(new HashMap<>());
		private int numClients;
		private String name;
		private int port;

		// Constructor
		public Chat(String name, int port) {
			this.port = port;
			OpenChats.put(port, this);
		}
		
		@Override
		public void run() {
			// Create a server socket
			try {
				@SuppressWarnings("resource")
				ServerSocket serverSocket = new ServerSocket(port);
				
				while(true) { // Constantly be checking for a new client connecting
					Socket clientSocket = serverSocket.accept();
					
					numClients++;
					
					// Manage observers and begin handling client
					ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
					// Get info
					InetAddress inet = clientSocket.getInetAddress();
					String hostname = inet.getHostName();
					String hostaddress = inet.getHostAddress();
					// Log all clients connected
					AllClients.put(hostname, writer);
					
					// Start Client thread
					new Thread(new ClientHandler(clientSocket, hostname, hostaddress)).start();
					this.addObserver(writer);
					System.out.println("got a connection");
					
				}
			
			} catch (IOException e) {
				System.err.println(e);
			}

		}
		
		// Inner inner class
		private class ClientHandler implements Runnable{
			private BufferedReader reader;
			private String hostname;
			private String clientIP;
			
			// Constructor
			public ClientHandler(Socket clientSocket, String name, String IP) {
				Socket sock = clientSocket;
				try {
					reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
					hostname = name;
					clientIP = IP;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
			@Override
			public void run() {
				String message;
				try {
					// Initial message notifies the client-side ChatRoom 
					// of the addition of a new client to the chat
					sendMessage("addClient" + splitter + hostname);
					
					while ((message = reader.readLine()) != null) {
						System.out.println("Received message: message=" + message + " port=" + port);
						System.out.println("Clients" + AllClients);
						doCommand(message);
					}
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			
			private void doCommand(String message) {
				// Parsing command
				String[] fullCommand = message.split(splitter);
				
				switch(fullCommand[0]) {
				case "message":
					// send text message to current chat
					sendMessage(message);
					break;
				case "newChat":
					// make a new chat
					makeChat(message.substring("newChat".length() + splitter.length()));
					break;
				case "allClients":
					// return all clients connected to server
					sendAllClients();
					break;
                case "setName":
                    setName(message);
                    break;
				default :
					break;
				}
				
			}


            private void setName(String message) {
			    System.out.println("setName(): message=" + message);
                String[] parts = message.split(splitter);
                String name = parts[1];
                String hostName = parts[2];
                AllNames.put(name,hostName);
            }

			private void sendAllClients(){
				String allClients = "allClients";
				for (String name : AllNames.keySet()) {
					allClients += splitter + name;
				}
				setChanged();
				System.out.println("sendAllClients(): " + allClients);
				notifyObservers(allClients);
			}
			
			private synchronized void makeChat(String message) {
				System.out.println("Making a new chat");

				// Separate message
				String[] parts = message.split(splitter);
				String name = parts[0];  // 

				// Make a new chat and port
				Chat newChat = new Chat(name, ++nextPort);
				new Thread(newChat).start();

				Chat publicChat = OpenChats.get(8000);

				//debugging
				System.out.println("makeChat(): publicChat=" + publicChat + " clients=" + publicChat.AllClients.keySet() + " message=" + message);

				// Notify Observers
				message = "newChat" + splitter + message;	// TODO: Undo removal of "newChat" in doCommand
				message += splitter + nextPort; // Add port number to notification
				setChanged();
				System.out.println(message);
				publicChat.notifyObservers(message);
			}
			
			private synchronized void sendMessage(String message) {
				
				// Censoring message
				String[] parseable = message.split(splitter);
				for(String badword : badwords)
					if(Arrays.stream(parseable).anyMatch(badword::equals)) {
						char[] chars = new char[badword.length()];
						Arrays.fill(chars, '*');
						String censor = new String(chars);
						
						message = message.replace(badword, censor);
					}
				//
				
				setChanged();
				notifyObservers(message);
			}
			
		}
		
	}

}
