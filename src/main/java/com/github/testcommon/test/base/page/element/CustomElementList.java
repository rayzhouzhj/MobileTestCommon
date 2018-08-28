package com.github.testcommon.test.base.page.element;

import java.lang.reflect.Field;
import java.util.List;

import org.openqa.selenium.support.PageFactory;

import com.rayzhou.framework.context.TestingDevice;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public abstract class CustomElementList extends AbstractElement
{
	/*	
	 * Example of how to define CustomElementList with child element
	 * You should define: 
	 * root 			Optional
	 * elementList
	 * childElementName
	 * 
	 * public CustomElementList ArticleIndexList = new CustomElementList()
	{
		@CacheLookup
		@AndroidFindBy(id = "id")
		@iOSFindBy(accessibility = "id")
		public MobileElement root;

		@CacheLookup
		@AndroidFindBy(className = "id")
		@iOSFindBy(accessibility = "id")
		public List<ChildElement> elementList;

		@AndroidFindBy(id = "id")
		@iOSFindBy(accessibility = "id")
		public ChildElement childElementName;
	};*/

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
	 * @return List<MobileElement>
	 */
	public MobileElement getRoot()
	{
		try 
		{
			initElement();

			Field element = this.getClass().getDeclaredField("root");
			element.setAccessible(true);
			return (MobileElement) element.get(this);
		} 
		catch (Exception e) 
		{
			System.out.println("[CustomElementList] No root element defined for current list");
		}

		return null;
	}

	/**
	 * Use reflection to lookup custom element defined in actual page object
	 * Initialize the element if current element is not yet initialized
	 * 
	 * @return List<MobileElement>
	 */
	public List<MobileElement> getList()
	{
		try 
		{
			initElement();

			MobileElement root = getRoot();
			if(root != null)
			{
				return root.findElements(this.getBy("elementList"));
			}
			else
			{
				return this.driver.findElements(this.getBy("elementList"));
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return null;
	}

	public MobileElement getElementByName(String name)
	{
		List<MobileElement> list = this.getList();
		for(MobileElement element : list)
		{
			if(element.getText().equalsIgnoreCase(name))
			{
				return element;
			}
		}

		return null;
	}

	public MobileElement getElementByChildValue(String childElementName, String childElementValue)
	{
		List<MobileElement> list = this.getList();
		for(MobileElement element : list)
		{
			if(childElementValue.equals(element.findElement(this.getBy(childElementName)).getText()))
			{
				return element;
			}
		}

		return null;
	}
}
