package sender;

/*
 Контроллер для отправки сообщений

 */
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

// для добавления данных из БД используем интерфейс
//    http://qaru.site/questions/1182227/managing-a-combobox-items-in-javafx
public class Controller implements Initializable {
  @FXML
  ComboBox<String>  cb_reciever;  // имя получателя шифровки
  @FXML
  TextArea  ta_inputTxt;          // входное сообщение
  @FXML
  TextArea  ta_outputTxt;         // зашифрованное сообщение (шифрограмма)
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
   * Обработка нажатия кнопки "зашифровать"
   * @param actionEvent
   */
   public void onclickBtnEncrypt(ActionEvent actionEvent)
  {
    Model model = new Model();
    String  text = ta_inputTxt.getText(); // возьмем текст
    String  usr  = cb_reciever.getValue();  // имя получателя
    String  encrypt = model.encryptText(usr, text);
    ta_outputTxt.setText(encrypt);
  }

  public void onclickComboBox(ActionEvent actionEvent)
  {
    System.out.println("hello");
  }

}

