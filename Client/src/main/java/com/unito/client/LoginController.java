package com.unito.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.regex.Pattern;

public class LoginController {
    @FXML
    private TextField mailField;
    @FXML
    private Label result;
    @FXML
    protected void onLoginButtonClick() {
        String insertedMail = mailField.getText();
        if(isValidEmail(insertedMail)) {
            result.setText("L'indirizzo mail inserito è valido");
        }else {
            result.setText("La mail non è valida prova con un'altra");
            mailField.clear();
        }
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;

        String normalized = email.trim(); // rimuove spazi davanti/dietro (copi-incolla ecc.)
        if (normalized.isEmpty()) return false;

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, normalized);
    }

    public static class InboxController {
        // Da definire
    }
}

