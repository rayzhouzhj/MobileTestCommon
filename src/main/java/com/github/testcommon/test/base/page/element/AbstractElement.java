package com.github.testcommon.test.base.page.element;

import java.lang.reflect.Field;

import org.openqa.selenium.By;

import com.github.framework.context.RunTimeContext;

import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSFindBy;

public abstract class AbstractElement
{
	public By getBy(String elementName)
	{
		By mobileBy = null;
		
		if(RunTimeContext.isAndroidPlatform())
		{
			Field child;
			try 
			{
				child = this.getClass().getDeclaredField(elementName);
				AndroidFindBy by = child.getDeclaredAnnotation(AndroidFindBy.class);
				
				if(!by.id().isEmpty())
				{
					mobileBy = By.id(by.id());
				}
				else if(!by.className().isEmpty())
				{
					mobileBy = MobileBy.className(by.className());
				}
			} 
			catch (NoSuchFieldException | SecurityException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Field child;
			try 
			{
				child = this.getClass().getDeclaredField(elementName);
				iOSFindBy by = child.getDeclaredAnnotation(iOSFindBy.class);
				
				if(!by.accessibility().isEmpty())
				{
					mobileBy = MobileBy.AccessibilityId(by.accessibility());
				}
				else if(!by.className().isEmpty())
				{
					mobileBy = MobileBy.className(by.className());
				}
			} 
			catch (NoSuchFieldException | SecurityException e)
			{
				e.printStackTrace();
			}
			
		}
		
		return mobileBy;
	}
}
