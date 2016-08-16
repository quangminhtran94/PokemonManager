package com.pokemanager.quangminh.ui.view;

import java.util.Objects;

import com.pokegoapi.api.PokemonGo;
import com.pokemanager.quangminh.backend.AuthController;
import com.pokemanager.quangminh.ui.MainApp;
import com.pokemanager.quangminh.utilities.Constant;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LogInController<T> {
	@FXML
	private Label usernameLabel;

	@FXML
	private TextField usernameTextField;

	@FXML
	private Label passwordLabel;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Label authServiceLabel;

	@FXML
	private ChoiceBox authChoiceBox;

	@FXML
	private Button logInButton;



	private MainApp mainApp;

	public LogInController(){

	}

	@FXML
    private void initialize() {
		this.authChoiceBox.setItems(FXCollections.observableArrayList(Constant.PTC_AUTH, Constant.GOOGLE_AUTH));
		this.authChoiceBox.getSelectionModel().select(0);
		this.authChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				switch(newValue.intValue()){
				case 0:
					usernameTextField.setDisable(false);
					passwordField.setDisable(false);
					break;
				case 1:
					usernameTextField.setDisable(true);
					passwordField.setDisable(true);
					break;
				default:
					break;
				}
			}
		});
		this.logInButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				AuthController authController = AuthController.getInstace();
				String username = usernameTextField.getText();
				String password = passwordField.getText();
				String authMethod = authChoiceBox.getSelectionModel().getSelectedItem().toString();
				PokemonGo go = authController.login(username, password, authMethod, mainApp);
				if (Objects.nonNull(go)){
					mainApp.setUserInfo(go);
					mainApp.setAuthMethod(authMethod);
					mainApp.showMainPokemonView();

				} else{
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle(Constant.LOGIN_DIALOG_TITLE);
					alert.setContentText(Constant.LOGIN_FAILED);
					alert.showAndWait();
				}
			}

		});
    }

	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
