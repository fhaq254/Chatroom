
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

import java.util.ArrayList;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataModel {

    private final ObservableList<ChatRoom> chatRoomList = FXCollections.observableArrayList(chatroom -> 
        new Observable[] {chatroom.roomNameProperty()});
    
    private static final ObservableList<String> allClients = FXCollections.observableArrayList();

    public static void addClients(ArrayList<String> clients) { 
    	
    	for(String nameOfClient : clients) {
	    	if(!allClients.contains(nameOfClient))
	    		allClients.add(nameOfClient); 
    	}
	}

    public static ObservableList<String> getAllClients(){ return allClients; }

    private final ObjectProperty<ChatRoom> currentChatRoom = new SimpleObjectProperty<>(null);

    public ObjectProperty<ChatRoom> currentChatRoomProperty() { return currentChatRoom; }

    public final ChatRoom getCurrentChatRoom() {
        return currentChatRoomProperty().get();
    }

    public final void setCurrentChatRoom(ChatRoom chatroom) {
        currentChatRoomProperty().set(chatroom);
    }

    public ObservableList<ChatRoom> getChatRoomList() {
        return chatRoomList ;
    }
    
    public void loadFakeChatRooms() {
        chatRoomList.setAll(
            new ChatRoom("Food", 8080, new ArrayList<String>() {{add("Katie");add("John");add("Isabella");}}),
            new ChatRoom("Tech", 8081, new ArrayList<String>() {{add("Katie");add("John");add("Isabella");}}),
            new ChatRoom("News", 8082, new ArrayList<String>() {{add("Katie");add("John");add("Isabella");}})
        );
    }
    
}