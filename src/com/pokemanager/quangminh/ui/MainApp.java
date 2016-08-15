package com.pokemanager.quangminh.ui;

import java.awt.Desktop;
import java.awt.JobAttributes.DialogType;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemanager.quangminh.backend.Player;
import com.pokemanager.quangminh.ui.view.LogInController;
import com.pokemanager.quangminh.ui.view.MainPokemonViewController;
import com.pokemanager.quangminh.utilities.Constant;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApp extends Application {
	private Stage primaryStage;
	private BorderPane rootLayout;
	private Alert loadingAlert;
	private String authMethod;
	private GoogleUserCredentialProvider googleProvider = null;
	private String authCode;

	private PokemonGo go;
	private Player player;



	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public GoogleUserCredentialProvider getGoogleProvider() {
		return googleProvider;
	}

	public void setGoogleProvider(GoogleUserCredentialProvider googleProvider) {
		this.googleProvider = googleProvider;
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Pokemon Manager");

		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		this.primaryStage.setWidth(primaryScreenBounds.getWidth() / 2);
		this.primaryStage.setHeight(primaryScreenBounds.getHeight() / 2);

		File image = new File("resources/icon.jpg");
		this.primaryStage.getIcons().add(new Image(image.toURI().toString()));

		initRootLayout();
		initComponents();
		showLoginView();
	}

	public static void main(String[] args) {
		launch(args);
	}


	public void initRootLayout() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
		try {
			rootLayout = (BorderPane) loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rootLayout.setTop(createMainMenuBar());

		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public MenuBar createMainMenuBar(){
		MenuBar menuBar = new MenuBar();
		Menu menuFile = new Menu("File");
		MenuItem closeMenuItem = new MenuItem("Close");
		closeMenuItem.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			    System.exit(0);
			}
		});
		menuFile.getItems().add(closeMenuItem);
		menuBar.getMenus().add(menuFile);

		Menu menuHelp = new Menu("Help");
		MenuItem aboutMenuItem = new MenuItem("About");
		aboutMenuItem.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				showCredit();
			}
		});
		menuHelp.getItems().add(aboutMenuItem);
		menuBar.getMenus().add(menuHelp);
		return menuBar;
	}


	public void showLoginView() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/LogIn.fxml"));
		try {
			Parent logInView = (Parent) loader.load();
			rootLayout.setCenter(null);
			rootLayout.setCenter(logInView);
			LogInController logInController = loader.getController();
			logInController.setMainApp(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showMainPokemonView(){
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/MainPokemonView.fxml"));
		try {
			Parent mainPokemonView = (Parent) loader.load();
			rootLayout.setCenter(null);
			rootLayout.setCenter(mainPokemonView);
			MainPokemonViewController mainPokemonViewController = loader.getController();
			mainPokemonViewController.setMainApp(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setUserInfo(PokemonGo go){
		this.go = go;
		try {
			this.player = new Player(go.getInventories(), go.getPlayerProfile());
		} catch (LoginFailedException e) {
			e.printStackTrace();
		} catch (RemoteServerException e) {
			e.printStackTrace();
		}
	}

	public Player getPlayer() {
		return this.player;
	}

	public void showErrorAlert(String content){
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setContentText(content);
		errorAlert.showAndWait();
	}

	public void showNetworkErrorAlert(){
		showErrorAlert("Network Error");
	}

	public void showUnableErrorAlert(){
		showErrorAlert("Cannot perform action, check resource.");
	}

	public void refreshPokemonList(){
		try {
			this.player.setInventories(this.go.getInventories());
		} catch (LoginFailedException e) {
			e.printStackTrace();
			this.showNetworkErrorAlert();
		} catch (RemoteServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.showUnableErrorAlert();
		}
	}

	public Stage getPrimaryStage(){
		return this.primaryStage;
	}

	public void showLoadingDialog(){
		loadingAlert.getButtonTypes().clear();
		loadingAlert.show();
	}

	public void hideLoadingDialog(){
		loadingAlert.getButtonTypes().add(ButtonType.CANCEL);
		loadingAlert.close();
	}

	public String showGoogleLoginDialog(){
		 Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		 if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			 try {
				 desktop.browse(URI.create(GoogleUserCredentialProvider.LOGIN_URL));
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
		 }
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(Constant.GOOGLE_CODE_DIALOG_TITLE);
		dialog.setHeaderText(Constant.GOOGLE_CODE_DIALOG_HEADER);
		dialog.setContentText(Constant.GOOGLE_CODE_PROMPT_LABEL);
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		    return result.get();
		}
		return null;
	}

	public void showCredit(){
		Alert creditInfo = new Alert(AlertType.INFORMATION);
		creditInfo.setTitle(Constant.CREDIT_TITLE);
		creditInfo.setHeaderText(Constant.CREDIT_TITLE);
		creditInfo.setContentText(Constant.CREDIT_DETAIL);
		creditInfo.showAndWait();
	}

	public void initComponents(){
		this.loadingAlert = new Alert(AlertType.INFORMATION);
		loadingAlert.setHeaderText(Constant.LOADING_LABEL);
		loadingAlert.setContentText(Constant.WAIT_LABEL);
		GridPane grid = new GridPane();
		grid.setPrefWidth(300);
		ProgressBar progressBar = new ProgressBar();
		progressBar.setPrefWidth(300);
		grid.add(progressBar, 1, 1);
		loadingAlert.getDialogPane().setContent(grid);
	}

	public String getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

}
