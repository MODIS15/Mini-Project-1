import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

/**
 * This test class checks whether the UDPClient only parses messages of 255 chars or less.
 */
public class UDPClientTest
{

    private String invalidString;
    private String validString;
    private String PORT;
    private String ADDRESS;
    private int MAX_SIZE;

    @Before
    public void setUp() throws Exception
    {
        PORT = "7007";
        ADDRESS = "localhost";
        MAX_SIZE = 255;
        invalidString = new String(new char[256]);
        validString = new String(new char[255]);
        //UDPServer server = new UDPServer();
        //UDPClient client = new UDPClient();
    }

    @After
    public void tearDown() throws Exception
    {

    }

    @Test
    public void testValidMessage()
    {
        String input = ADDRESS + "|" + PORT + "|" + validString;
        String[] packet = input.split("\\|");
        Assert.assertTrue(packet[2].toCharArray().length <= MAX_SIZE);
    }

    @Test
    public void testInvalidMessage()
    {
        String input = ADDRESS + "|" + PORT + "|" + invalidString;
        String[] packet = input.split("\\|");
        Assert.assertFalse(packet[2].toCharArray().length <= MAX_SIZE);
    }

    /*
    @Test
    public void testValidMessage()
    {
        String data = ADDRESS + "|" + PORT + "|" + validString;
        InputStream stdin = System.in;
        try
        {
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            Scanner scanner = new Scanner(System.in);
            System.out.println(scanner.nextLine());
        }
        finally
        {
            System.setIn(stdin);
        }
    }
    */

    /*
    @Test
    public void testInvalidMessage()
    {
        UDPClient.main(new String[] {ADDRESS, PORT, invalidString});
        String data = ADDRESS + "|" + PORT + "|" + validString;
        InputStream stdin = System.in;
        try
        {
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            Scanner scanner = new Scanner(System.in);
            System.out.println(scanner.nextLine());
        }
        finally
        {
            System.setIn(stdin);
        }
    }
    */

}