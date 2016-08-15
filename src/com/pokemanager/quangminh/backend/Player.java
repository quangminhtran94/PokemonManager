package com.pokemanager.quangminh.backend;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;

import com.pokegoapi.api.inventory.Inventories;
import com.pokegoapi.api.inventory.PokeBank;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.api.pokemon.Pokemon;


public class Player {
	private static PokemonController pokemonController;
	private Inventories inventories;
	private PlayerProfile profile;

	public Player(Inventories inventories, PlayerProfile profile) {
		super();
		this.inventories = inventories;
		this.profile = profile;
		this.pokemonController = PokemonController.getInstance();
	}
	public Inventories getInventories() {
		return inventories;
	}
	public void setInventories(Inventories inventories) {
		this.inventories = inventories;
	}
	public PlayerProfile getProfile() {
		return profile;
	}
	public void setProfile(PlayerProfile profile) {
		this.profile = profile;
	}

	public List<Pokemon> getPokemons(){
		return this.inventories.getPokebank().getPokemons();
	}

	public Pokemon getHighestIvPokemon(PokemonId id){
		List<Pokemon> pokemons = this.getPokemons();
		List<Pokemon> sameSpeciesPokemons = pokemonController.getPokemonsFromSpecies(pokemons, id);
		return pokemonController.getHighestIvPokemon(sameSpeciesPokemons);
	}

	public List<Pokemon> getLowCpAndIvPokemons(){
		HashMap<PokemonId, Pokemon> highestCpPokemons = pokemonController.filterHighestCp(this.getPokemons());
		HashMap<PokemonId, Pokemon> highestIvPokemons = pokemonController.filterHighestIv(this.getPokemons());
		List<Pokemon> keepPokemons = new ArrayList<>(highestCpPokemons.values());
		keepPokemons.addAll(new ArrayList<>(highestIvPokemons.values()));
		List<Pokemon> transferPokemons = new ArrayList<>(this.getPokemons());
		transferPokemons.removeAll(keepPokemons);
		return transferPokemons;
	}

}
