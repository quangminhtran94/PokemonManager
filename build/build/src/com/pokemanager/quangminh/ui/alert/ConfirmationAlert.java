package com.pokemanager.quangminh.ui.alert;

import java.util.List;

import com.pokegoapi.api.pokemon.Pokemon;

import javafx.scene.control.Alert;

public class ConfirmationAlert extends Alert{
	List<Pokemon> pokemons;

	public ConfirmationAlert(List<Pokemon> pokemons, String heading) {
		super(AlertType.CONFIRMATION);
		this.pokemons = pokemons;
		this.constructMessage();
		this.setHeaderText(heading);
	}

	public void constructMessage(){
		String listOfPokemons = "";
		for(int i = 0; i < this.pokemons.size(); i++){
			listOfPokemons = listOfPokemons.concat(pokemons.get(i).getPokemonId().toString() + " - CP: " + pokemons.get(i).getCp() + " - IV: " + String.format("%.2f", pokemons.get(i).getIvRatio()) + "\n");
		}
		this.setContentText(listOfPokemons);
	}

}
