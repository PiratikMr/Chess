package org.example.chess1.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.chess1.DataBase.DataBase;
import org.example.chess1.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    @FXML private Button buttSignUp, buttSignIn;
    @FXML private TextField userNameSignInField, userNameSignUpField; //текстовые поля логина пользователя входа и регистрации
    @FXML private PasswordField passwordSignInField, passwordSignUpField, repeatPasswordSignUpField ; //текстовые поля пароля входа и регистрации

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}


    @FXML private void onButtSignUpClick() {
        if (checkParameters(true)){
            MenuController.instance.setProfile();
            Main.exitRegister();
        }
    }
    @FXML private void onButtSignInClick() {
        if (checkParameters(false)){
            MenuController.instance.setProfile();
            Main.exitRegister();
        }
    }

    //Additional func
    private boolean checkParameters(boolean isSignUp) {
        if (isSignUp){ //если регистрации
            String name = userNameSignUpField.getText(); //проверка возможных неверностей ввода данных и вывода соответсвующих ошибок
            String password = passwordSignUpField.getText();
            String repeatPassword = repeatPasswordSignUpField.getText();
            if (DataBase.isThere(name)){
                Main.addError("", "Такое имя уже занято, попробуйте другое."); //вывод ошибки
                setPromptText(userNameSignUpField, "Имя",true); //выделение поля красным
                return false;
            }
            if (name.length() > 45 || name.length() < 3){
                Main.addError("", "Имя пользователя должно находиться в пределах от 45 до 3 символов.");
                setPromptText(userNameSignUpField, "Имя",true);
                return false;
            }

            if (password.length() < 3 || password.length() > 45){
                Main.addError("", "Пароль должен быть в пределах от 45 до 3 символов.");
                setPromptText(passwordSignUpField, "Пароль",true);
                return false;
            }
            if (!repeatPassword.equals(password)){
                Main.addError("", "Пароли не совпадают.");
                setPromptText(repeatPasswordSignUpField, "Имя",true);
                return false;
            }

            DataBase.addUser(userNameSignUpField.getText(), passwordSignUpField.getText()); //при верности данных в базу данных добавляется пользователь

            setPromptText(userNameSignUpField, "Имя",false);
            setPromptText(passwordSignUpField, "Пароль",false);
            setPromptText(repeatPasswordSignUpField, "Пароль",false);
        } else { //если вход
            String name = userNameSignInField.getText();
            String password = passwordSignInField.getText();
            if (!DataBase.setPlayer(name, password)){ //поиск в базе данных пользователя
                Main.addError("", "Имя пользователя или пароль введены не верно.");
                setPromptText(userNameSignInField, "Имя",true);
                setPromptText(passwordSignInField, "Пароль",true);
                return false;
            }

            setPromptText(userNameSignInField, "Имя",false);
            setPromptText(passwordSignInField, "Пароль",false);
        }
        return true;
    }

    private void setPromptText(TextField field, String text, boolean isRed) {
        String color;
        if (isRed){
            color = "red";
        } else {
            color = "gray";
        }
        field.setStyle(STR."-fx-prompt-text-fill: \{color};");
        field.setPromptText(text);
        field.setText("");
    }
}
