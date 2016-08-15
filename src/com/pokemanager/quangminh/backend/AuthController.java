package com.pokemanager.quangminh.backend;
import java.util.Objects;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokemanager.quangminh.ui.MainApp;
import com.pokemanager.quangminh.utilities.Constant;

import okhttp3.OkHttpClient;

public class AuthController {
	private static AuthController instance = null;
	public static AuthController getInstace(){
		if (Objects.isNull(instance)){
			instance = new AuthController();
		}
		return instance;
	}
	public AuthController() {

	}
	public PokemonGo login (String username, String password, String authMethod, MainApp mainApp) {
		OkHttpClient httpClient = new OkHttpClient();
		if (authMethod == Constant.PTC_AUTH){
			try {
				PokemonGo go = new PokemonGo(new PtcCredentialProvider(httpClient,username,password),httpClient);
				return go;
			} catch (LoginFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (RemoteServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}else{
			String authCode = mainApp.showGoogleLoginDialog();
			GoogleUserCredentialProvider provider;
			try {
				provider = new GoogleUserCredentialProvider(httpClient);
				provider.login(authCode);
				PokemonGo go = new PokemonGo(provider, httpClient);
				mainApp.setGoogleProvider(provider);
				mainApp.setAuthCode(authCode);
				return go;
			} catch (LoginFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;

		}
	}

	public boolean isTokenExpired(MainApp mainApp){
		if (mainApp.getAuthMethod().equals(Constant.GOOGLE_AUTH)){
			return mainApp.getGoogleProvider().isTokenIdExpired();
		}
		return false;
	}

	public void refreshGoogleToken(MainApp mainApp) throws LoginFailedException, RemoteServerException{
		if (Objects.nonNull(mainApp.getGoogleProvider())){
			mainApp.getGoogleProvider().refreshToken(mainApp.getAuthCode());
		}
	}

}
