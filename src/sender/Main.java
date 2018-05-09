package sender;
/*
 Отправка зашифрованного сообщения в адрес получателя
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.security.SecureRandom;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception{
    Parent root = FXMLLoader.load(getClass().getResource("sender.fxml"));
    primaryStage.setTitle("Crypto Sender");
    primaryStage.setScene(new Scene(root, 800, 400));
    primaryStage.getScene().getStylesheets().add("css/JMetroLightTheme.css");
    primaryStage.show();
  }


  public static void main(String[] args) {
    launch(args);
  }

} // end class
