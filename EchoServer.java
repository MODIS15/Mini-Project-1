import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by JakeMullit on 08/09/15.
 */
public class EchoServer {

    int incommmingPort = 7007;
    DatagramSocket dataSocket;
    byte[] buffer;

    public EchoServer()
    {

    }

    public void serverSetup()
    {
        try
        {
            dataSocket = new DatagramSocket(incommmingPort);
            buffer = new byte[1000];
        }
        catch (SocketException e) {e.printStackTrace();}
    }




}
