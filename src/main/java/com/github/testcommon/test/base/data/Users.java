package com.github.testcommon.test.base.data;

import java.util.List;

public class Users
{
	private ShareData shareData;
	private List<UserData> userdata;

	public List<UserData> getUserdata() {
		// Update test with share data
		userdata.forEach(data -> data.setShareData(this.shareData));
		
		return userdata;
	}

	public void setUserdata(List<UserData> userdata) {
		this.userdata = userdata;
	}
}
