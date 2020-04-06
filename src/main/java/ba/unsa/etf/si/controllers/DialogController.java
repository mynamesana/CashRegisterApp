package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.utility.HttpUtils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

import static ba.unsa.etf.si.App.DOMAIN;
import static ba.unsa.etf.si.controllers.PrimaryController.currentUser;

public class DialogController   {
    public JFXButton cancelReceipt;
    public TextField receiptField;
    public JFXButton revertReceipt;
    public JFXButton abort;
    public Button exitButton;
    public Label warningLabel;


    private DialogStatus dialogStatus = new DialogStatus();
    private String id = "error";
    private String text = "Kliknut je abort button!";
    private String confirmationString = "err";


    Consumer<String> callback = (String str) -> {
        System.out.println(str);
        buttonBlock(false);

        if (!str.contains("200")) {
            dialogStatus.setStatus(505);
        }
        else {
            if (str.contains("deleted!")) dialogStatus.setStatus(200);
            else dialogStatus.setStatus(201);
        }
        Platform.runLater(
                () -> {
                    Stage stage = (Stage) cancelReceipt.getScene().getWindow();
                    stage.close();
                }
        );
    };

    @FXML
    public void initialize() {

        exitButton.setOnAction(e -> {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        });

        abort.setOnAction(e -> {
            Stage stage = (Stage) abort.getScene().getWindow();
            stage.close();
        });

        cancelReceipt.setOnAction(e -> {
            dialogStatus.setCancel(true);
            sendRequest();
        });

        revertReceipt.setOnAction(e -> {
            dialogStatus.setRevert(true);
            sendRequest();
        });

        receiptField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue!=null && newValue.equals(confirmationString))   buttonBlock(false);
            else buttonBlock(true);
        });

        buttonBlock(true);

    }

    public void setId (String x) {
        String[] arr = x.split("-");
        confirmationString = arr[arr.length-1];
        id = x;
        String newString =  warningLabel.getText();
        newString=  newString.replace("rec_id", confirmationString);
        warningLabel.setText(newString);
    }

    public void buttonBlock (boolean block) {
        cancelReceipt.setDisable(block);
        revertReceipt.setDisable(block);
        if (block) {
            revertReceipt.getStyleClass().add("buttonBlocked");
            cancelReceipt.getStyleClass().add("buttonBlocked");
        }
        else {
            revertReceipt.getStyleClass().removeAll("buttonBlocked");
            cancelReceipt.getStyleClass().removeAll("buttonBlocked");
        }
    }

    public String getText () {
        return text;
    }

    public DialogStatus getStatus () {
        return dialogStatus;
    }

    private void sendRequest () {
        buttonBlock(true);
        exitButton.setDisable(true);
        HttpRequest getSuppliesData = HttpUtils.DELETE(DOMAIN + "/api/receipts/" + id, "Authorization", "Bearer " + currentUser.getToken());
        HttpUtils.send(getSuppliesData, HttpResponse.BodyHandlers.ofString(), callback, () -> {
            dialogStatus.setCancel(false);
            Stage stage = (Stage) cancelReceipt.getScene().getWindow();
            stage.close();
        });
    }
    public static class DialogStatus {
        boolean cancel, revert;
        int status; //505 - fail, 200 - success, 201 - already processed

        public DialogStatus () {
            cancel = false;
            revert = false;
            status = 505;
        }
        public DialogStatus(boolean cancel, boolean revert, int status) {
            this.cancel = cancel;
            this.revert = revert;
            this.status = status;
        }

        public boolean isCancel() {
            return cancel;
        }

        public void setCancel(boolean cancel) {
            this.cancel = cancel;
        }
        public boolean isRevert() {
            return revert;
        }

        public void setRevert(boolean revert) {
            this.revert = revert;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

}