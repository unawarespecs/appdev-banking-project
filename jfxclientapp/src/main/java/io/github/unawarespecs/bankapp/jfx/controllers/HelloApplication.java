package io.github.unawarespecs.bankapp.jfx.controllers;

import io.github.unawarespecs.bankapp.jfx.SceneUtils;
import io.github.unawarespecs.bankapp.service.BankInterface;
import io.github.unawarespecs.bankdb.BankdbApplication;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    private ConfigurableApplicationContext springContext;
    private BankInterface bankService;

    @Override
    public void init() throws Exception{
        this.springContext = new SpringApplicationBuilder()
            .sources(BankdbApplication.class)
            .run();

        this.bankService = springContext.getBean(io.github.unawarespecs.bankdb.serviceimpl.BankServiceImpl.class);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("login.fxml"));

        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == io.github.unawarespecs.bankdb.controllers.LoginController.class) {
                io.github.unawarespecs.bankdb.controllers.LoginController controller =
                        new io.github.unawarespecs.bankdb.controllers.LoginController(bankService);

                // Wire the redirection behavior safely here using SceneUtils
                controller.setOnSuccessfulLogin(() -> {
                    try {
                        SceneUtils.changeStage(
                                stage,
                                "/io/github/unawarespecs/bankapp/jfx/controllers/menu.fxml",
                                "Bank label- Dashboard",
                                bankService
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                controller.setOnAdminLogin(() -> {
                    try {
                        SceneUtils.changeStage(
                                stage,
                                "/io/github/unawarespecs/bankapp/jfx/controllers/adminmenu.fxml",
                                "Bank label- Dashboard",
                                bankService
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                return controller;
            }
            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Scene scene = new Scene(fxmlLoader.load());
        scene.setUserAgentStylesheet(
                Objects.requireNonNull(
                        getClass().getResource("/assets/css/fluent-override.css")
                ).toExternalForm()
        );
        stage.setMaximized(true);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
