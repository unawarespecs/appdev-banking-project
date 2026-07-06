package io.github.unawarespecs.bankapp.jfx;

import io.github.unawarespecs.bankapp.service.BankInterface;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class SceneUtils {

    public static void changeStage(Stage stage, String fxml, String title, BankInterface bankService) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneUtils.class.getClassLoader().getResource(fxml.startsWith("/") ? fxml.substring(1) : fxml));

        // Pass bankService to constructors requiring it
        fxmlLoader.setControllerFactory(param -> {
            try {
                return param.getConstructor(BankInterface.class).newInstance(bankService);
            } catch (Exception e) {
                try {
                    return param.getConstructor().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        Scene scene = new Scene(fxmlLoader.load());
        scene.setUserAgentStylesheet(
                Objects.requireNonNull(
                        SceneUtils.class.getResource("/assets/css/fluent-override.css")
                ).toExternalForm()
        );
        stage.setFullScreen(true);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public static void popUpStage(String fxml, String title, BankInterface bankService) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneUtils.class.getResource(fxml));

        fxmlLoader.setControllerFactory(param -> {
            try {
                return param.getConstructor(BankInterface.class).newInstance(bankService);
            } catch (Exception e) {
                try {
                    return param.getConstructor().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        Scene scene = new Scene(fxmlLoader.load());
        scene.setUserAgentStylesheet(
                Objects.requireNonNull(
                        SceneUtils.class.getResource("/assets/css/fluent-override.css")
                ).toExternalForm()
        );
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}