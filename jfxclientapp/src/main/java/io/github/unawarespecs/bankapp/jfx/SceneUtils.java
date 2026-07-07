package io.github.unawarespecs.bankapp.jfx;

import io.github.unawarespecs.bankapp.service.BankInterface;
import io.github.unawarespecs.bankdb.controllers.AccountManagerController;import io.github.unawarespecs.bankdb.controllers.AdminMenuController;
import io.github.unawarespecs.bankdb.controllers.LoanManagerController;
import io.github.unawarespecs.bankdb.controllers.MenuController;
import io.github.unawarespecs.bankdb.controllers.LoginController;
import io.github.unawarespecs.bankdb.controllers.TransferFundsController;
import io.github.unawarespecs.bankdb.controllers.CustomerSettingsController;
import io.github.unawarespecs.bankdb.serviceimpl.BankServiceImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.Objects;

public class SceneUtils {
    //commonly used change stages here
    private static void logout(Stage stage, BankInterface bankService){
        try {
            SceneUtils.changeStage(stage, "/io/github/unawarespecs/bankapp/jfx/controllers/login.fxml", "Login", bankService);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void admindashboard(Stage stage, BankInterface bankService){
        try {
            SceneUtils.changeStage(stage, "/io/github/unawarespecs/bankapp/jfx/controllers/adminmenu.fxml", "Bank Label - Dashboard", bankService);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void dashboard(Stage stage, BankInterface bankService){
        try {
            SceneUtils.changeStage(stage, "/io/github/unawarespecs/bankapp/jfx/controllers/menu.fxml", "Bank Label - Dashboard", bankService);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void changeStage(Stage stage, String fxml, String title, BankInterface bankService) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneUtils.class.getClassLoader().getResource(fxml.startsWith("/") ? fxml.substring(1) : fxml));

        fxmlLoader.setControllerFactory(param -> {

            if (param == LoginController.class) {
                LoginController controller = new LoginController(bankService);
                controller.setOnSuccessfulLogin(() -> {
                    dashboard(stage, bankService);
                });
                controller.setOnAdminLogin(() -> {
                    admindashboard(stage, bankService);
                });
                return controller;
            }
            if (param == MenuController.class) {
                MenuController controller = new MenuController(bankService);
                controller.setOnLogoutRequested((currentStage) -> {
                    logout(currentStage, bankService);
                });
                controller.setOnTransferFundsRequested((currentStage) -> {
                    try {
                        popUpStage("io/github/unawarespecs/bankapp/jfx/controllers/transfer.fxml", "Transfer Funds", bankService, currentStage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                controller.setOnSettingsRequested((currentStage) -> {
                    try {
                        popUpStage("io/github/unawarespecs/bankapp/jfx/controllers/settings.fxml", "Customer Settings", bankService, currentStage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                controller.setOnDepositRequested((currentStage) -> {
                    try {
                        popUpStage("io/github/unawarespecs/bankapp/jfx/controllers/deposit.fxml", "Deposit", bankService, currentStage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                controller.setOnWithdrawRequested((currentStage) -> {
                    try {
                        popUpStage("io/github/unawarespecs/bankapp/jfx/controllers/withdraw.fxml", "Deposit", bankService, currentStage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                controller.setOnTransactHistoryRequested((currentStage) -> {
                    try {
                        popUpStage("io/github/unawarespecs/bankapp/jfx/controllers/transaction.fxml", "Transaction History", bankService, currentStage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                controller.setOnApplyForLoanRequested((currentStage) -> {
                    try {
                        popUpStage("io/github/unawarespecs/bankapp/jfx/controllers/loan.fxml", "Deposit", bankService, currentStage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return controller;
            }
            if (param == AdminMenuController.class) {
                AdminMenuController controller = getAdminMenuController(stage, bankService);
                return controller;
            }
            if (param == AccountManagerController.class) {
                AccountManagerController controller = new AccountManagerController(bankService);
                controller.setOnBackRequested((currentStage) -> {
                    admindashboard(currentStage, bankService);
                });
                return controller;
            }

            if (param == LoanManagerController.class) {
                LoanManagerController controller = new LoanManagerController(bankService);
                controller.setOnBackRequested((currentStage) -> {
                    admindashboard(currentStage, bankService);
                });
                return controller;
            }



            // add controllers here

            try {
                return param.getConstructor(BankInterface.class).newInstance(bankService);
            } catch (NoSuchMethodException e) {
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to instantiate controller: " + param.getName(), ex);
                }
            } catch (Exception e){
                throw new RuntimeException("Dependency injection failed for controller: " + param.getName(), e);
            }
        });

        Scene scene = new Scene(fxmlLoader.load());
        scene.setUserAgentStylesheet(
                Objects.requireNonNull(
                        SceneUtils.class.getResource("/assets/css/fluent-override.css")
                ).toExternalForm()
        );
        stage.setMaximized(true);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private static @NonNull AdminMenuController getAdminMenuController(Stage stage, BankInterface bankService) {
        AdminMenuController controller = new AdminMenuController(bankService);
        controller.setOnLogoutRequested((currentStage) -> {
            logout(currentStage, bankService);
        });
        controller.setOnAccManagerRequested((currentStage) -> {
            try {
                SceneUtils.changeStage(stage, "/io/github/unawarespecs/bankapp/jfx/controllers/account_manager.fxml", "Bank Label - Account Manager", bankService);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        controller.setOnLoanManagerRequested((currentStage) -> {
            try {
                SceneUtils.changeStage(stage, "/io/github/unawarespecs/bankapp/jfx/controllers/loan_manager.fxml", "Bank Label - Loan Manager", bankService);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return controller;
    }

    public static void popUpStage(String fxml, String title, BankInterface bankService, Stage owner) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SceneUtils.class.getClassLoader().getResource(fxml.startsWith("/") ? fxml.substring(1) : fxml));

        fxmlLoader.setControllerFactory(param -> {
            if (param == TransferFundsController.class) {
                return new TransferFundsController(bankService);
            }
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
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);

        // allow controllers to register callbacks (e.g. settings controller wants to navigate back to login)
        Object controller = fxmlLoader.getController();
        if (controller instanceof CustomerSettingsController) {
            CustomerSettingsController c = (CustomerSettingsController) controller;
            c.setOnDeleteComplete(() -> {
                try {
                    // close the popup
                    stage.close();
                    // change the owning stage to the login screen
                    SceneUtils.changeStage(owner, "/io/github/unawarespecs/bankapp/jfx/controllers/login.fxml", "Login", bankService);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}