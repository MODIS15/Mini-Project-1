import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class checks whether the UDPClient only parses messages of 255 chars or less.
 */
public class UDPClientTest
{

    private String invalidString;
    private String validString;
    private String PORT;

    @Before
    public void setUp() throws Exception
    {
        invalidString = new String(new char[] {256});
        validString = new String(new char[] {255});
        PORT = "7007";
        UDPServer.main(null);
    }

    @After
    public void tearDown() throws Exception
    {

    }

    @Test
    public void testValidMessage()
    {
        UDPClient.main(new String[] {"localhost", PORT, invalidString});
    }

    @Test
    public void testInvalidMessage()
    {
        UDPClient.main(new String[] {"localhost", PORT, validString});
    }
}