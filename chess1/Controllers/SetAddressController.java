package org.example.chess1.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.example.chess1.Main;
import org.example.chess1.Server.StaticInfo;

import java.net.URL;
import java.util.ResourceBundle;

public class SetAddressController implements Initializable {
    @FXML private TextField addressTextField, portFromTextField, portToTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addressTextField.setText(StaticInfo.address);
        portFromTextField.setText(String.valueOf(StaticInfo.portFrom));
        portToTextField.setText(String.valueOf(StaticInfo.portTo));
    }

    @FXML private void onButtOkClicked() {
        if (isAddressRight()) {
            StaticInfo.setAddress(addressTextField.getText());
            StaticInfo.setPortRange(Integer.parseInt(portFromTextField.getText()), Integer.parseInt(portToTextField.getText()));
            addressTextField.setText("");
            Main.exitSetAddress();
        } else {
            Main.addError("Не корректный адрес!", "Такого адреса не существует или порты не верны.");
        }
    }

    //Add Functions
    private boolean isAddressRight() {
        String address = addressTextField.getText();
        String portFrom = portFromTextField.getText();
        String portTo = portToTextField.getText();

        if (address == null) {
            addressTextField.setText("");
            return false;
        }
        if (portFrom == null) {
            portFromTextField.setText("");
            return false;
        }
        if (portTo == null) {
            portToTextField.setText("");
            return false;
        }

        try {
            int from = Integer.parseInt(portFrom);
            int to = Integer.parseInt(portTo);

            if ((from < 3000 || from > 65535) && (to < 3000 || to > 65535) && from >= to) {
                portFromTextField.setText("");
                portToTextField.setText("");
                return false;
            }

        } catch (NumberFormatException _) {
            portFromTextField.setText("");
            portToTextField.setText("");
            return false;
        }


        if (address.equals("localhost")) {
            return true;
        }

        String[] numbers = address.split("\\.");
        if (numbers.length != 4) {
            return false;
        }
        for (String num: numbers) {
            try {
                int val = Integer.parseInt(num);
                if (val < 0 || val > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }
}
