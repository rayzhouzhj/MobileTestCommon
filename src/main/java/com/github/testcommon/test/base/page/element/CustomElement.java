package com.github.testcommon.test.base.page.element;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.framework.context.TestingDevice;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public abstract class CustomElement extends AbstractElement implements IElementActions
{
/*	
 * Example of how to define CustomElement with child element
 * 
 * public CustomElement customElement = new CustomElement()
	{
		@CacheLookup
		@AndroidFindBy(id = "id")
		@iOSFindBy(accessibility = "id")
		public MobileElement element;
		
		@AndroidFindBy(id = "parent-id")
		@AndroidFindBy(id = "child-id")
		@iOSFindBy(accessibility = "parent-id")
		@iOSFindBy(accessibility = "child-id")
		public MobileElement childElementName;
	};
	*/
	
	private AppiumDriver<MobileElement> driver;
	private boolean isInitialized = false;

	public void initElement()
	{
		if(!isInitialized)
		{
			this.driver = TestingDevice.get().getDriver();
			PageFactory.initElements(new AppiumFieldDecorator(driver), this);
			
			isInitialized = true;
		}
	}

	/**
	 * Use reflection to lookup custom element defined in actual page object
	 * Initialize the element if current element is not yet initialized
	 * 
	 * @return MobileElement
	 */
	public MobileElement get()
	{
		try 
		{
			initElement();
			
			Field element = this.getClass().getDeclaredField("element");
			element.setAccessible(true);
			return (MobileElement) element.get(this);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return null;
	}

	public MobileElement getChild(String childName)
	{
		try 
		{
			initElement();
			
			Field element = this.getClass().getDeclaredField(childName);
			element.setAccessible(true);
			return (MobileElement) element.get(this);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public boolean waitForVisibility() 
	{
		return waitForVisibility(5);
	}

	@Override
	public boolean waitForVisibility(int timeInSeconds) 
	{
		initElement();
		
		try
		{
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			new WebDriverWait(driver, timeInSeconds).until(ExpectedConditions.visibilityOf(this.get()));

			return true;
		}
		catch(Exception e)
		{
			return false;
		}
		finally
		{
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		}
	}

	@Override
	public boolean waitForDisAppear() 
	{
		return waitForDisAppear(5);
	}

	@Override
	public boolean waitForDisAppear(int timeInSeconds) 
	{
		initElement();
		
		try
		{
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
			new WebDriverWait(driver, timeInSeconds).until(ExpectedConditions.invisibilityOf(this.get()));

			return true;
		}
		catch(Exception e)
		{
			return false;
		}
		finally
		{
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		}
	}
}
