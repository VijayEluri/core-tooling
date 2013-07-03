package core.tooling.test.validation;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import core.tooling.logging.LogFactory;
import core.tooling.logging.Logger;
import core.tooling.validation.DefaultAnnotationValidator;
import core.tooling.validation.StringValidation;
import core.tooling.validation.Validate;
import core.tooling.validation.ValidateException;
import core.tooling.validation.Validator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-tooling-context.xml")
public class TestValidate
{
    /** logger for this class */
    private Logger logger = LogFactory.getLogger(TestValidate.class);
	
    @Autowired
    private ApplicationContext context;

    @Before
    public void setup()
    {
    }

    @Test
    public void testValidate()
    {
    	// Create class to nest within another class
    	final class FruitClass
    	{
    		@StringValidation(anyOf="apple,orange,grape",message="invalidFruit")
    		private String fruit;

			public String getFruit()
			{
				return fruit;
			}

			public void setFruit(String fruit)
			{
				this.fruit = fruit;
			}
    	}
    	
    	// Validate class contains FruitClass which will get validated
    	final class ValidateClass
    	{
    		@Validate(message="invalidFruitClass")
    		private FruitClass fruitClass;

			public FruitClass getFruitClass()
			{
				return fruitClass;
			}

			public void setFruitClass(FruitClass fruitClass)
			{
				this.fruitClass = fruitClass;
			}
    	}
    	
    	ValidateClass validateClass = new ValidateClass();
    	FruitClass fruitClass = new FruitClass();
    	fruitClass.setFruit("apple");
    	validateClass.setFruitClass(fruitClass);
    	
    	Validator executor = new DefaultAnnotationValidator(new Object[] {validateClass});
    	executor.validate();
    	
    	//  set to invalid fruit to cause validation exception, messages for nested validation are concatenated with ': '
    	fruitClass.setFruit("onion");
    	try
    	{
    		executor.validate();
    	}
    	catch (ValidateException e)
    	{
    		Assert.assertEquals("invalidFruitClass: invalidFruit", e.getMessage());
    	}
    }
}
