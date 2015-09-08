import java.io.IOException;
import java.net.*;

/**
 * Created by JakeMullit on 02/09/15.
 */


public class Poster {


    public static void main(String[] args)
    {
        byte[] message  = args[0].getBytes();
        String _message = args[0];
        String IPDestination = "localhost";//args[1];
        DatagramSocket socket;
        int port = 7007;

        try
        {
            socket = new QuestionableDatagramSocket();
            InetAddress host = InetAddress.getByName(IPDestination);
            DatagramPacket packet = new DatagramPacket(message,_message.length(),host,port);
            socket.send(packet);


        }
        catch (SocketException e) {e.printStackTrace();}
        catch (UnknownHostException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}


    }
}
