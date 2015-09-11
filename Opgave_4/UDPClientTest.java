package Opgave_4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class checks whether the Opgave_4.UDPClient only parses messages of 255 chars or less.
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
        validString = new String(new char[255]);
        invalidString = new String(new char[256]);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Set up new server on another thread
            }
        });


    }

    @After
    public void tearDown() throws Exception
    {

    }

    @Test
    public void testValidPacket_NO_CLIENT_SERVER_SETUP()
    {
        String input = ADDRESS + "|" + PORT + "|" + validString;
        String[] packet = input.split("\\|");
        Assert.assertTrue(packet[2].toCharArray().length <= MAX_SIZE);
    }

    @Test
    public void testInvalidPacket_NO_CLIENT_SERVER_SETUP()
    {
        String input = ADDRESS + "|" + PORT + "|" + invalidString;
        String[] packet = input.split("\\|");
        Assert.assertFalse(packet[2].toCharArray().length <= MAX_SIZE);
    }


    @Test
    public void testValidPacket()
    {
        String data = ADDRESS + "|" + PORT + "|" + validString;
        UDPClient.main(new String[] {data});

        Assert.assertTrue(UDPServer.isPacketReceive());
    }



    @Test
    public void testInvalidPacket()
    {
        UDPServer server = new UDPServer();
        String data = ADDRESS + "|" + PORT + "|" + invalidString;
        UDPClient.main(new String[]{data});

    }

}