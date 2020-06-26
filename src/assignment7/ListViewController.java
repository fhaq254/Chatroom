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

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import java.io.IOException;
import java.util.ArrayList;

/**
 * FXML Controller class
 *
 */
public class ListViewController {

    private static Integer chatNumber = 0;
    private static NewChatController chatController;
    private ChatRoom selectedChat;
    
    @FXML
    private ListView<ChatRoom> listView;

    @FXML
    private StackPane window;

    @FXML
    private Button newChatButton;

    @FXML
    private TextArea messageTextArea;

    @FXML
    private Button send;

    @FXML
    private HBox submitBox;

    private DataModel model;

    
    
    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = model;

        ObservableList<ChatRoom> list = model.getChatRoomList();
        listView.setItems(list);
        
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            model.setCurrentChatRoom(newSelection);
        });

        HBox.setHgrow(messageTextArea, Priority.ALWAYS);
        VBox.setVgrow(listView, Priority.ALWAYS);

        model.currentChatRoomProperty().addListener((obs, oldChatRoom, newChatRoom) -> {
            if (newChatRoom == null) {
                listView.getSelectionModel().clearSelection();
            } else {
                ClientMain.scene.lookup("#" + newChatRoom.getFXMLChatScrollPane()).toFront();
            }
        });
        
        listView.setCellFactory(lv -> new ListCell<ChatRoom>() {
            @Override
            public void updateItem(ChatRoom chatroom, boolean empty) {
                super.updateItem(chatroom, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(chatroom.getRoomName());
                }
            }
        });

        messageTextArea.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
            messageTextArea.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
            //use Double.MIN_VALUE to scroll to the top
        });

        messageTextArea.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    send.fire();
                    event.consume();
                }
            }
        });

        send.setOnAction(event -> {
            String messageText = messageTextArea.getText();
            selectedChat = listView.getSelectionModel().getSelectedItem();
            selectedChat.sendMessage(messageText);
            messageTextArea.setText("");
            messageTextArea.requestFocus();
            System.out.println("Message sent to port: " + selectedChat.getPort());
        });

        newChatButton.setOnAction(e -> {
            listView.getSelectionModel().select(model.getChatRoomList().get(0));
            ChatRoom selectedChat = listView.getSelectionModel().getSelectedItem();
            ArrayList clients = selectedChat.getAllClients();
            DataModel.addClients(clients);
            FXMLLoader loader;
            Parent root;
            try {
                loader = new FXMLLoader(getClass().getResource("NewChat.fxml"));
                root = loader.load();
                model.setCurrentChatRoom(listView.getSelectionModel().getSelectedItem());
                chatController = loader.getController();
                chatController.initModel(model);
                Stage stage = new Stage();
                stage.setTitle("Create A New Chat");
                stage.setScene(new Scene(root));
                stage.show();
            }
            catch (IOException er) {
                er.printStackTrace();
            }
        });
        
    }


    public void addNewChat(ChatRoom newChatRoom, DataModel model) {
        chatNumber++;
        this.model = model;
        ObservableList<ChatRoom> list = model.getChatRoomList();
        listView.setItems(list);
        ScrollPane newChatScrollPane = new ScrollPane();
        VBox newChatWindow = new VBox();
        //newChatWindow.setMinHeight(window.getHeight());
        newChatScrollPane.setId("scrollPane" + chatNumber);
        newChatWindow.setStyle("-fx-background-image: url(longhorn.png);");
        newChatWindow.setId("window" + chatNumber);
        newChatRoom.setFXMLChatWindow("window" + chatNumber);
        newChatRoom.setFXMLChatScrollPane("scrollPane" + chatNumber);
        newChatScrollPane.setContent(newChatWindow);
        window.getChildren().add(newChatScrollPane);
        model.setCurrentChatRoom(newChatRoom);
        listView.getSelectionModel().select(newChatRoom);
        ClientMain.scene.lookup("#" + newChatRoom.getFXMLChatScrollPane()).toFront();
    }
    
}
