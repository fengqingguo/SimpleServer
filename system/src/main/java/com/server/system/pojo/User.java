package com.server.system.pojo;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    private String id;

    private String name;

    private String username;

    private String password;
    
    private String salt;

    private String mobile;

    private String email;

    private Date createtime;

    private Date edittime;

    private String memo;

    private Integer isdel;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
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

	public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getEdittime() {
        return edittime;
    }

    public void setEdittime(Date edittime) {
        this.edittime = edittime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

	public Integer getIsdel() {
        return isdel;
    }

    public void setIsdel(Integer isdel) {
        this.isdel = isdel;
    }
    
    public String getCredentialsSalt() {
        return username + salt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", createtime=" + createtime +
                ", edittime=" + edittime +
                ", memo='" + memo + '\'' +
                ", isdel=" + isdel +
                '}';
    }
}