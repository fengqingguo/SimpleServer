package com.server.system.pojo;

public class User {

    private String username;

    private String password;
    
    private String salt;

    public String getUsername() {
        return "administrator";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

    public String getCredentialsSalt() {
        return username + salt;
    }
}