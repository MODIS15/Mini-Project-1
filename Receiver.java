import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by JakeMullit on 02/09/15.
 */

/**
 * Test class for objective 2.
 * Prints all received messages
 */
public class Receiver {

    public static void main(String[] args)
    {
        DatagramSocket socket = null;
        try
        {
            socket = new DatagramSocket(7007);
            // create socket at agreed port
            byte[] buffer = new byte[1000];
            while(true)
            {
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                socket.receive(reply);
                System.out.println("Message has been received "+new String(reply.getData()));


            }
        }
        catch (SocketException e){System.out.println("Socket: " + e.getMessage());}
        catch (IOException e) {System.out.println("IO: " + e.getMessage());}
        finally {if(socket != null) socket.close();}
    }


}

