package com.pokemanager.quangminh.ui.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.annimon.stream.Objects;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemanager.quangminh.backend.AuthController;
import com.pokemanager.quangminh.backend.PokemonController;
import com.pokemanager.quangminh.ui.MainApp;
import com.pokemanager.quangminh.ui.alert.ConfirmationAlert;
import com.pokemanager.quangminh.utilities.Constant;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MainPokemonViewController {
	@FXML
	private TableView<Pokemon> pokemonTable;

	@FXML
	private TableColumn<Pokemon, String> nickNameColumn;

	@FXML
	private TableColumn<Pokemon, String> speciesColumn;

	@FXML
	private TableColumn<Pokemon, String> cpColumn;

	@FXML
	private TableColumn<Pokemon, String> ivColumn;

	@FXML
	private TableColumn<Pokemon, String> candyColumn;

	@FXML
	private Button transferBtn;

	@FXML
	private Button evolveBtn;

	@FXML
	private Button powerUpBtn;

	@FXML
	private Button optimizeAllBtn;

	@FXML
	private Button refreshBtn;

	private MainApp mainApp;

	private PokemonController pokemonController;

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		this.pokemonController = PokemonController.getInstance();
		pokemonTable.setItems(FXCollections.observableArrayList(mainApp.getPlayer().getPokemons()));
	}

	private void refreshPokemonList(){
		AuthController authController = AuthController.getInstace();
		if (mainApp.getAuthMethod().equals(Constant.GOOGLE_AUTH)){
			if (authController.isTokenExpired(this.mainApp)){
				try {
					authController.refreshGoogleToken(this.mainApp);
				} catch (LoginFailedException e) {
					e.printStackTrace();
				} catch (RemoteServerException e) {
					e.printStackTrace();
				}
			}
		}
		mainApp.refreshPokemonList();
		pokemonTable.getColumns().get(4).setVisible(false);
		pokemonTable.getColumns().get(4).setVisible(true);
		pokemonTable.setItems(FXCollections.observableArrayList(mainApp.getPlayer().getPokemons()));
	}

	// TODO: Candy column is not refreshed when power up
	@FXML
	private void initialize(){
		pokemonTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		nickNameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getNickname()));
		speciesColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPokemonId().toString()));
		cpColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getCp())));
		ivColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getIvRatio())));
		candyColumn.setCellValueFactory(cellData -> {
			try {
				return new ReadOnlyStringWrapper(String.valueOf(cellData.getValue().getCandy()));
			} catch (LoginFailedException e1) {
				// TODO Auto-generated catch block
				mainApp.showNetworkErrorAlert();
				e1.printStackTrace();
			} catch (RemoteServerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		});

		refreshBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				mainApp.showLoadingDialog();
				final Task task = new Task(){
					@Override
					protected Object call() throws Exception {
						Platform.runLater(() -> {
							refreshPokemonList();
							mainApp.hideLoadingDialog();
						});
						return null;
					}

				};
				new Thread(task).start();
			}

		});

		transferBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				List<Pokemon> selectedPokemons = pokemonTable.getSelectionModel().getSelectedItems();
				ConfirmationAlert confirmationAlert = new ConfirmationAlert(selectedPokemons, Constant.TRANSFER_WARNING);
				Optional<ButtonType> result = confirmationAlert.showAndWait();
				if (Objects.equals(result.get(), ButtonType.OK)){
					mainApp.showLoadingDialog();
					final Task task = new Task(){
						@Override
						protected Object call() throws Exception {
							pokemonController.transfer(selectedPokemons);
							Platform.runLater(() -> {
								refreshPokemonList();
								mainApp.hideLoadingDialog();
							});
							return null;
						}

					};
					new Thread(task).start();
				}

			}

		});

		evolveBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				List<Pokemon> selectedPokemons = pokemonTable.getSelectionModel().getSelectedItems();
				ConfirmationAlert confirmationAlert = new ConfirmationAlert(selectedPokemons, Constant.EVOLVE_WARNING);
				Optional<ButtonType> result = confirmationAlert.showAndWait();
				if (Objects.equals(result.get(), ButtonType.OK)){
					mainApp.showLoadingDialog();
					final Task task = new Task(){
						@Override
						protected Object call() throws Exception {
							pokemonController.evolve(selectedPokemons);
							Platform.runLater(() -> {
								refreshPokemonList();
								mainApp.hideLoadingDialog();
							});
							return null;
						}

					};
					new Thread(task).start();
				}
			}
		});

		powerUpBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				List<Pokemon> selectedPokemons = pokemonTable.getSelectionModel().getSelectedItems();
				ConfirmationAlert confirmationAlert = new ConfirmationAlert(selectedPokemons, Constant.POWERUP_WARNING);
				Optional<ButtonType> result = confirmationAlert.showAndWait();
				if (Objects.equals(result.get(), ButtonType.OK)){
					mainApp.showLoadingDialog();
					final Task task = new Task(){
						@Override
						protected Object call() throws Exception {
							pokemonController.powerUp(selectedPokemons);
							Platform.runLater(() -> {
								refreshPokemonList();
								mainApp.hideLoadingDialog();
							});
							return null;
						}

					};
					new Thread(task).start();
				}
			}
		});

		optimizeAllBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				List<Pokemon> selectedPokemons = mainApp.getPlayer().getLowCpAndIvPokemons();
				ConfirmationAlert confirmationAlert = new ConfirmationAlert(selectedPokemons, Constant.TRANSFER_WARNING);
				Optional<ButtonType> result = confirmationAlert.showAndWait();
				if (Objects.equals(result.get(), ButtonType.OK)){
					mainApp.showLoadingDialog();
					final Task task = new Task(){
						@Override
						protected Object call() throws Exception {
							pokemonController.transfer(selectedPokemons);
							Platform.runLater(() -> {
								refreshPokemonList();
								mainApp.hideLoadingDialog();
							});
							return null;
						}

					};
					new Thread(task).start();
				}
			}
		});

	}
}
