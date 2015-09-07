import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

/**
 * This class creates a UDP server using a DatagramSocket to receive packets from a UDPClient.
 */
public class UDPServer
{
	private static String[] dividedMessage;
	private static DatagramPacket request;
	private static DatagramSocket aSocket = null;
	private static HashMap<InetAddress, Integer> IPMessageMap;
	private static final int BUFFER_SIZE = 1000;
	private static boolean packetReceive; //Testing purpose

	public static void main(String[] args)
	{
		try
		{
			setPacketRecieve(false);

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
					setPacketRecieve(true);
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

	/**
	 * Checks whether the packet address is valid.
	 * @return true if address is valid.
	 */
	private static boolean requestIsValid ()
	{
		if(IPMessageMap.get(request.getAddress()) != null)
			return dividedMessage[0].hashCode() == Integer.parseInt(dividedMessage[1])
					&& IPMessageMap.get(request.getAddress()) != request.hashCode();
		else
			return dividedMessage[0].hashCode() == Integer.parseInt(dividedMessage[1]);
	}

	/**
	 * Prints out packet information (address, port number and message) if the message is valid.
	 * A message is sent back to the client if packet is received successfully.
	 */
	private static void handleValidMessage()
	{
		try
		{
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


	//Testing methods
	public static boolean isPacketRecieve(){
		return packetReceive;
	}

	private static void setPacketRecieve(boolean isReceived){
		packetReceive = isReceived;
	}
}
