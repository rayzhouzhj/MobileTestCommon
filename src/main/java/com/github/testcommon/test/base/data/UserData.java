package com.github.testcommon.test.base.data;

public class UserData
{
	private String username;
	private String password;
	private ShareData shareData;
	
	public String getUserName() {
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
	public void setShareData(ShareData shareData)
	{
		this.shareData = shareData;
	}
}
