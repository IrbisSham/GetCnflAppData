package ut.com.itzabota.plugins.getcnflappdata;

import org.junit.Test;
import com.itzabota.plugins.getcnflappdata.MyPluginComponent;
import com.itzabota.plugins.getcnflappdata.MyPluginComponentImpl;

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