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

import com.jfoenix.controls.JFXColorPicker;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ClientInitController {

    @FXML
    private TextField ipBox;

    @FXML
    private TextField clientName;

    @FXML
    private Button connectButton;

    @FXML
    private JFXColorPicker colorPicker;

    public void initialize(){
        connectButton.setOnAction(event -> {
            ClientMain.serverIp = ipBox.getText();
            ClientMain.myName = clientName.getText();
            try {
                ClientMain.startConnection(ClientMain.defaultChatRoom);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // close window
            Node source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();

        });
    }

    public void setColor() {
        ClientMain.backgroundColor = colorPicker.getValue();
    }

}
