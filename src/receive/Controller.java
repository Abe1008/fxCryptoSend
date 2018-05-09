package receive;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
  @FXML
  ComboBox<String> cb_reciever;
  @FXML
  TextArea  ta_inputTxt;
  @FXML
  TextArea  ta_outputTxt;
  @FXML
  Button btn_Encrypt;

  /**
   * Вызывается при инициализации root объекта, будем заполнять ComboBox данными из БД
   * @param location
   * @param resources
   */
  @Override
  public void initialize(URL location, ResourceBundle resources)
  {
    Model model = new Model();
    String[] usrs = model.getUsrKeys();
    cb_reciever.getItems().removeAll(cb_reciever.getItems()); // очистить список комбо-бокса
    for(int i=0; i < usrs.length; i++) {
      cb_reciever.getItems().add(usrs[i]);
    }
    cb_reciever.getSelectionModel().select(0);  // выбрать первый элемент
  }

  /**
   * Обработка нажатия кнопки "расшифровать"
   * @param actionEvent
   */
  public void onclickBtnDecrypt(ActionEvent actionEvent)
  {
    Model model = new Model();
    String  text = ta_inputTxt.getText(); // возьмем текст
    String  usr  = cb_reciever.getValue();  // имя получателя
    String  decrypt = model.decryptText(usr, text);
    ta_outputTxt.setText(decrypt);
  }

}
