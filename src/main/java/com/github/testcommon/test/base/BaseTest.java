package com.github.testcommon.test.base;

import java.util.Random;

import org.testng.Assert;

import com.github.framework.report.ReportManager;

public class BaseTest
{
	public String getRandomNumberString(int length)
	{
		String output = "";
		Random random = new Random();

		for(int i = 0; i < length; i++)
		{
			output = output + random.nextInt(10);
		}

		return output;
	}

	public void sleep(long millis)
	{
		try 
		{
			System.out.println("[BaseTest] Wait for " + millis + " milliseconds");
			Thread.sleep(millis);
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
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
