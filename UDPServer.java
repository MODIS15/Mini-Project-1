import javax.xml.crypto.Data;
import java.net.*;
import java.io.*;
public class UDPServer implements Runnable
{

	private DatagramSocket socket;
	private DatagramPacket request;
	private byte[] buffer;

	public void UDPServer() throws SocketException
	{
		socket = new DatagramSocket(7007);
		buffer = new byte[1000];
		request = new DatagramPacket(buffer, buffer.length);
	}

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
    			DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), 
    				request.getAddress(), request.getPort());
                System.out.println("UDP packet from: " + new String(request.getAddress().toString()));
                System.out.println("Message: " + new String(reply.getData()));
                System.out.println("Packet:" + reply);	
    			aSocket.send(reply);
    		}
		}
		catch (SocketException e){System.out.println("Socket: " + e.getMessage()); }
		catch (IOException e) {System.out.println("IO: " + e.getMessage()); }
		finally {if(aSocket != null) aSocket.close();}
    }

	public void run()
	{
		while(true)
		{

			socket = new DatagramSocket(7007);
			DatagramPacket packet = socket.receive(request);
			new Thread(new Responder(socket, packet)).start();
		}
	}

	public byte[] makeResponse(String input)
	{
		return input.getBytes();
	}

	public class Responder implements Runnable {

		private DatagramSocket socket = null;
		private DatagramPacket packet = null;

		public Responder(DatagramSocket socket, DatagramPacket packet) {
			this.socket = socket;
			this.packet = packet;
		}

		public void run() {
			byte[] data = makeResponse(); // code not shown
			DatagramPacket response = new DatagramPacket(data, data.length,
					packet.getAddress(), packet.getPort());
			try
			{
				socket.send(response);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.out.println("Unable to send response");
			}

		}
	}

}
