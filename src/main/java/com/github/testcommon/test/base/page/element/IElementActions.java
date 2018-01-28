package com.github.testcommon.test.base.page.element;

public interface IElementActions 
{
	public boolean waitForVisibility();
	public boolean waitForVisibility(int timeInSeconds);
	public boolean waitForDisAppear();
	public boolean waitForDisAppear(int timeInSeconds);
}
