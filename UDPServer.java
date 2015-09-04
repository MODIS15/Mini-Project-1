import java.net.*;
import java.io.*;
public class UDPServer
{
    public static void main(String args[])
	{
        DatagramSocket aSocket = null;
        try
        {
            aSocket = new DatagramSocket(7007);
            // create socket at agreed port
            byte[] buffer = new byte[1000];
            while(true)
            {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                DatagramPacket receivedRequest = new DatagramPacket(request.getData(), request.getLength(),
                        request.getAddress(), request.getPort());
                if (requestIsValid(receivedRequest)) {
                    System.out.println("UDP packet from: " + new String(request.getAddress().toString()));
                    System.out.println("Message: " + new String(receivedRequest.getData()));
                    System.out.println("Packet:" + receivedRequest);
                    aSocket.send(new DatagramPacket(new byte[]{1}, 1, request.getAddress(), request.getPort()));
                }
                else
                    aSocket.send(new DatagramPacket(new byte[]{0}, 1, request.getAddress(), request.getPort()));
            }
        }
        catch (SocketException e){System.out.println("Socket: " + e.getMessage()); }
        catch (IOException e) {System.out.println("IO: " + e.getMessage()); }
        finally {if(aSocket != null) aSocket.close();}
    }


    private static boolean requestIsValid (DatagramPacket packet)
    {
        String[] dividedMessage = packet.getData().toString().split("¤");

        return Integer.toString(dividedMessage[0].hashCode()).equals(dividedMessage[1]);
    }
}
