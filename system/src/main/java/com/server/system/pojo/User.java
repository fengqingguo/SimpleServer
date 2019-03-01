package com.server.system.pojo;

import java.io.Serializable;

public class User implements Serializable{
    private static final long serialVersionUID = 668457685664949231L;

    private String username;

    private String password;
    
    private String salt;

    private String credentialsSalt;

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

    public void setCredentialsSalt(String credentialsSalt) {
        this.credentialsSalt = credentialsSalt;
    }
}