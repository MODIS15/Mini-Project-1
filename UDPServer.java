import java.net.*;
import java.io.*;
public class UDPServer
{
    private static int messageHashCode;
    private static String[] dividedMessage;
    private static DatagramPacket request;
    private static DatagramSocket aSocket = null;
    private static DatagramPacket handledRequest;


    public static void main(String args)
	{
        try
        {
            aSocket = new DatagramSocket(7007);
            // create socket at agreed port
            byte[] buffer = new byte[1000];
            while(true)
            {
                request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                handledRequest = new DatagramPacket(request.getData(), request.getLength(),
                        request.getAddress(), request.getPort());
                dividedMessage = handledRequest.getData().toString().split("¤");

                if (requestIsValid()) {
                    messageHashCode = Integer.parseInt(dividedMessage[1]);
                    handleValidMessage();
                }
                else
                    aSocket.send(new DatagramPacket(new byte[]{0}, 1, request.getAddress(), request.getPort()));
            }
        }
        catch (SocketException e){System.out.println("Socket: " + e.getMessage()); }
        catch (IOException e) {System.out.println("IO: " + e.getMessage());}
        finally {if(aSocket != null) aSocket.close();}
    }


    private static boolean requestIsValid ()
    {
        int dividedMessageCode = Integer.parseInt(dividedMessage[1]);
        return dividedMessage[0].hashCode() == dividedMessageCode
                    && dividedMessageCode != messageHashCode;
    }

    private static void handleValidMessage()
    {
        try {
            System.out.println("UDP packet from: " + new String(request.getAddress().toString()));
            System.out.println("Message: " + new String(handledRequest.getData()));
            System.out.println("Packet:" + handledRequest);
            aSocket.send(new DatagramPacket(new byte[]{1}, 1, request.getAddress(), request.getPort()));
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("");
        }
    }
}
