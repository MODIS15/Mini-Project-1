import java.io.IOException;
import java.net.*;

/**
 * Created by JakeMullit on 02/09/15.
 */
public class ManyPosts {


    public static void main(String[] args)
    {
        String[] messages = new String[10];
        for(int i = 0; i<messages.length; i++)
        {
            messages[i] = ""+i;
        }

        DatagramSocket socket = null;
        try {
            socket = new SocketUDP();
        } catch (SocketException e) {
            e.printStackTrace();
        }


        for(int i = 0; i<messages.length; i++)
        {
            byte[] message = messages[i].getBytes();
            String _message = messages[i];
            String IPDestination = "localhost";//args[1];

            int port = 7007;

            try {

                InetAddress host = InetAddress.getByName(IPDestination);
                DatagramPacket packet = new DatagramPacket(message, _message.length(), host, port);
                socket.send(packet);


            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
