package com.pokemanager.quangminh.backend;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;


public class PokemonController {
	private static PokemonController instance = null;
	public static PokemonController getInstance(){
		if (Objects.isNull(instance)){
			instance = new PokemonController();
		}
		return instance;
	}


	public List<Pokemon> getPokemonsFromSpecies(List<Pokemon> pokemons, PokemonId id) {
		List<Pokemon> result = pokemons.stream()
				.filter(pokemon -> pokemon.getPokemonId().toString().equals(id.toString()))
				.collect(Collectors.toList());
		return result;
	}

	public Pokemon getHighestIvPokemon(List<Pokemon> pokemons){
		Pokemon result = pokemons.get(0);
		for(int i = 1; i < pokemons.size(); i++){
			if (pokemons.get(i).getIvRatio() >= result.getIvRatio()){
				result = pokemons.get(i);
			}
		}
		return result;
	}

	public Pokemon getHighestCpPokemon(List<Pokemon> pokemons){
		Pokemon result = pokemons.get(0);
		for(int i = 1; i < pokemons.size(); i++){
			if (pokemons.get(i).getCp() >= result.getCp()){
				result = pokemons.get(i);
			}
		}
		return result;
	}

	public List<PokemonId> getAllSpecies(List<Pokemon> pokemons) {
		List<PokemonId> result = new ArrayList<PokemonId>();
		pokemons.forEach(pokemon -> {
			if (!result.contains(pokemon.getPokemonId())) {
				result.add(pokemon.getPokemonId());
			}
		});
		return result;
	}

	public HashMap<PokemonId, Pokemon> filterHighestCp(List<Pokemon> pokemons){
		HashMap<PokemonId, Pokemon> result = new HashMap<PokemonId, Pokemon>();
		pokemons.forEach(pokemon -> {
			if (!result.containsKey(pokemon.getPokemonId())){
				result.put(pokemon.getPokemonId(), pokemon);
			}else {
				if (result.get(pokemon.getPokemonId()).getCp() < pokemon.getCp()){
					result.put(pokemon.getPokemonId(), pokemon);
				}
			}
		});
		return result;
	}

	public HashMap<PokemonId, Pokemon> filterHighestIv(List<Pokemon> pokemons){
		HashMap<PokemonId, Pokemon> result = new HashMap<PokemonId, Pokemon>();
		pokemons.forEach(pokemon -> {
			if (!result.containsKey(pokemon.getPokemonId())){
				result.put(pokemon.getPokemonId(), pokemon);
			}else {
				if (result.get(pokemon.getPokemonId()).getIvRatio() < pokemon.getIvRatio()){
					result.put(pokemon.getPokemonId(), pokemon);
				}
			}
		});
		return result;
	}

	public void transfer(List<Pokemon> pokemons) {
		pokemons.forEach(pokemon -> {
			try {
				pokemon.transferPokemon();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void powerUp(List<Pokemon> pokemons){
		pokemons.forEach(pokemon -> {
			try {
				pokemon.powerUp();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	public void evolve(List<Pokemon> pokemons){
		pokemons.forEach(pokemon -> {
			try {
				pokemon.evolve();
			} catch (LoginFailedException e) {
				e.printStackTrace();
			} catch (RemoteServerException e) {
				e.printStackTrace();
			}
		});
	}

	//Testing purpose
	public void printPokemons(List<Pokemon> pokemons){
		pokemons.forEach(pokemon -> {
			System.out.println(pokemon.getPokemonId().toString() + " has CP: " +pokemon.getCp());
		});
	}
}
