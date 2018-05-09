package receive;
/*
 Прием зашифрованного сообщения для определенного получателя
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("receive.fxml"));
    primaryStage.setTitle("Crypto Receiver");
    primaryStage.setScene(new Scene(root, 800, 400));
    primaryStage.getScene().getStylesheets().add("css/JMetroLightTheme.css");
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

} // end class

