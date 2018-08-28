package com.github.testcommon.test.base;

import org.testng.Assert;

import com.rayzhou.framework.report.ReportManager;

public class TestLogger 
{
	public void logInfo(String message)
	{
		System.out.println("[INFO] " + message);
		ReportManager.getInstance().logInfo(message);
	}
	
	public void logInfoWithScreenShot(String message)
	{
		System.out.println("[INFO] " + message);
		ReportManager.getInstance().logInfoWithScreenShot(message);
	}
	
	public void logPass(String message)
	{
		System.out.println("[PASSED] " + message);
		ReportManager.getInstance().logPass(message);
	}
	
	public void logFail(String message)
	{
		System.err.println("[FAILED] " + message);
		ReportManager.getInstance().logFail(message);
	}
	
	public void logFatalError(String message)
	{
		System.err.println("[ERROR] " + message);
		Assert.fail(message);
	}
}
