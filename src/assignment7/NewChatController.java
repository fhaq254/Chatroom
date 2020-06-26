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

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

/**
 * FXML Controller class
 *
 */
public class NewChatController {

    private ObservableList<String> selectedClients;

    @FXML
    private TextField chatName;

    @FXML
    private CheckComboBox selectClientsCB;

    @FXML
    private Button createChat;


    private DataModel model;


    public void initModel(DataModel model) {
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = model;

        System.out.println(DataModel.getAllClients());
        selectClientsCB.getItems().addAll(DataModel.getAllClients());

        createChat.setOnAction(event -> {
            selectedClients = selectClientsCB.getCheckModel().getCheckedItems();
            String newChatName = chatName.getText();
            System.out.println("Create Chat button clicked!");
            String message = "newChat" + ChatRoom.splitter + newChatName;
            for(String client : selectedClients){
                message += ChatRoom.splitter + client;
            }
            ChatRoom selectedChat = model.getCurrentChatRoom();
            selectedChat.sendToServer(message);

            // close window
            Node source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();

        });

    }

}
