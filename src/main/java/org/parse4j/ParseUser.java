package org.parse4j;

import org.json.JSONException;
import org.json.JSONObject;
import org.parse4j.callback.RequestPasswordResetCallback;
import org.parse4j.callback.SignUpCallback;
import org.parse4j.command.ParseGetCommand;
import org.parse4j.command.ParsePostCommand;
import org.parse4j.command.ParseResponse;
import org.parse4j.util.ParseRegister;

@ParseClassName("users")
public class ParseUser extends ParseObject {

	private String password;
	private String sessionToken;

	public ParseUser() {
		super(ParseRegister.getClassName(ParseUser.class));
		setEndPoint("users");
	}

	public void remove(String key) {
		if ("username".equals(key)) {
			throw new IllegalArgumentException("Can't remove the username key.");
		}

		remove(key);
	}
	
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public void setUsername(String username) {
		put("username", username);
	}

	public String getUsername() {
		return getString("username");
	}

	public void setPassword(String password) {
		this.password = password;
		isDirty = true;
	}

	public void setEmail(String email) {
		put("email", email);
	}

	public String getEmail() {
		return getString("email");
	}

	public String getSessionToken() {
		return sessionToken;

	}

	public static ParseUser logIn(String username, String password) throws ParseException {
		return null;
	}
	
	public boolean isAuthenticated() {
		return (this.sessionToken != null && getObjectId() != null);
	}
	
	void validateSave() {

		if (getObjectId() == null) {
			throw new IllegalArgumentException(
					"Cannot save a ParseUser until it has been signed up. Call signUp first.");
		}

		if ((!isAuthenticated()) && isDirty && getObjectId() != null) {
			throw new IllegalArgumentException(
					"Cannot save a ParseUser that is not authenticated.");
		}

	}
	
	
	
	public void signUp() throws ParseException {

		if ((getUsername() == null) || (getUsername().length() == 0)) {
			throw new IllegalArgumentException(
					"Username cannot be missing or blank");
		}

		if (password == null) {
			throw new IllegalArgumentException(
					"Password cannot be missing or blank");
		}
		
		if (getObjectId() != null) {
			throw new IllegalArgumentException(
					"Cannot sign up a user that has already signed up.");
		}
		
		ParsePostCommand command = new ParsePostCommand(getClassName());
		JSONObject parseData = getParseData();
		parseData.put("password", password);
		command.setData(parseData);
		ParseResponse response = command.perform();
		if(!response.isFailed()) {
			JSONObject jsonResponse = response.getJsonObject();
			if (jsonResponse == null) {
				throw response.getException();
			}
			try {
				setObjectId(jsonResponse.getString(ParseConstants.FIELD_OBJECT_ID));
				sessionToken = jsonResponse.getString(ParseConstants.FIELD_SESSION_TOKEN);
				String createdAt = jsonResponse.getString(ParseConstants.FIELD_CREATED_AT);
				setCreatedAt(Parse.parseDate(createdAt));
				setUpdatedAt(Parse.parseDate(createdAt));
			}catch (JSONException e) {
				throw new ParseException(
						ParseException.INVALID_JSON,
						"Although Parse reports object successfully saved, the response was invalid.",
						e);
			}
		}
		else {
			throw response.getException();
		}
		
	}
	
	public static ParseUser login(String username, String password) throws ParseException {
		
		ParseGetCommand command = new ParseGetCommand("login");
		command.addJson(false);
		command.put("username", username);
	    command.put("password", password);
		ParseResponse response = command.perform();
		if(!response.isFailed()) {
			JSONObject jsonResponse = response.getJsonObject();
			if (jsonResponse == null) {
				throw response.getException();
			}
			try {
				ParseUser parseUser = new ParseUser();
				parseUser.setObjectId(jsonResponse.getString(ParseConstants.FIELD_OBJECT_ID));
				parseUser.setSessionToken(jsonResponse.getString(ParseConstants.FIELD_SESSION_TOKEN));
				String createdAt = jsonResponse.getString(ParseConstants.FIELD_CREATED_AT);
				String updatedAt = jsonResponse.getString(ParseConstants.FIELD_UPDATED_AT);
				parseUser.setCreatedAt(Parse.parseDate(createdAt));
				parseUser.setUpdatedAt(Parse.parseDate(updatedAt));
				jsonResponse.remove(ParseConstants.FIELD_OBJECT_ID);
				jsonResponse.remove(ParseConstants.FIELD_CREATED_AT);
				jsonResponse.remove(ParseConstants.FIELD_UPDATED_AT);
				jsonResponse.remove(ParseConstants.FIELD_SESSION_TOKEN);
				parseUser.setData(jsonResponse);
				return parseUser;
				
			}catch (JSONException e) {
				throw new ParseException(
						ParseException.INVALID_JSON,
						"Although Parse reports object successfully saved, the response was invalid.",
						e);
			}
		}
		else {
			throw response.getException();
		}
		
	}
	
	public static void requestPasswordReset(String email) throws ParseException {

		ParsePostCommand command = new ParsePostCommand("requestPasswordReset");
		JSONObject data = new JSONObject();
		data.put("email", email);
		command.setData(data);
		ParseResponse response = command.perform();
		if (!response.isFailed()) {
			JSONObject jsonResponse = response.getJsonObject();
			if (jsonResponse == null) {
				throw response.getException();
			}
		} else {
			throw response.getException();
		}

	}


	public void signUpInBackground(SignUpCallback callback) {

	}
	
	public void logout() throws ParseException {

		if(!isAuthenticated()) {
			return;
		}
		
	}
	
	public static void requestPasswordResetInBackground(String email,
			RequestPasswordResetCallback callback) {

	}
	
}
