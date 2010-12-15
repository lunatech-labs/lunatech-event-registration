package controllers;

import models.Administrator;

public class EventSecurity extends Secure.Security {
	public static boolean authenticate(String user, String password){
		Administrator admin = Administrator.find("emailAddress = ? AND password = ?", user, password).first();
		return admin != null;
	}
}
