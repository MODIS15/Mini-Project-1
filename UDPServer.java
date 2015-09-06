import java.net.*;
import java.io.*;
import java.util.HashMap;

public class UDPServer
{
	private static String[] dividedMessage;
	private static DatagramPacket request;
	private static DatagramSocket aSocket = null;
	private static HashMap<InetAddress, Integer> IPMessageMap;
	private static final int BUFFER_SIZE = 1000;


	public static void main(String[] args)
	{
		try
		{
			IPMessageMap = new HashMap<>();
			aSocket = new DatagramSocket(7007);
			// create socket at agreed port
			byte[] buffer = new byte[BUFFER_SIZE];
			System.out.println("Server process started...");
			while(true)
			{
				request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				dividedMessage = new String(request.getData()).split("�");

				if (requestIsValid())
				{
					handleValidMessage();
				}
				else
				{
					String failedMessage = "0";
					aSocket.send(new DatagramPacket(failedMessage.getBytes(), failedMessage.length(),
							request.getAddress(), request.getPort()));
				}
			}
		}
		catch (SocketException e){System.out.println("Socket: " + e.getMessage()); }
		catch (IOException e) {System.out.println("IO: " + e.getMessage());}
		finally {if(aSocket != null) aSocket.close();}
	}


	private static boolean requestIsValid ()
	{
		if(IPMessageMap.get(request.getAddress()) != null)
			return dividedMessage[0].hashCode() == Integer.parseInt(dividedMessage[1])
					&& IPMessageMap.get(request.getAddress()) != request.hashCode();
		else
			return dividedMessage[0].hashCode() == Integer.parseInt(dividedMessage[1]);
	}

	private static void handleValidMessage()
	{
		try {
			IPMessageMap.put(request.getAddress(), request.hashCode());
			System.out.println("UDP packet from: (Anonymous)" + new String(request.getAddress().toString()));
			System.out.println("Port:" + new String(String.valueOf(request.getPort())).toString());
			System.out.println("Message: " + new String(dividedMessage[0]));
			System.out.println("Packet:" + request);
			String approvedMessage = "1";
			aSocket.send(new DatagramPacket(approvedMessage.getBytes(), approvedMessage.length(),
					request.getAddress(), request.getPort()));
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("");
		}
	}
}
