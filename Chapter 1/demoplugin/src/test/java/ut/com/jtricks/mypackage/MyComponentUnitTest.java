package ut.com.jtricks.mypackage;

import org.junit.Test;
import com.jtricks.mypackage.api.MyPluginComponent;
import com.jtricks.mypackage.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}