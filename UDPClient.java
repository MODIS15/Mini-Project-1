import java.io.IOException;
import java.net.*;
import java.util.Scanner;

/**
 * This class creates a UDPClient sending user input from the terminal to a UDPServer with an agreed port.
 */
public class UDPClient
{

	private int tryCount, messageCount;
	private DatagramSocket aSocket;
	private static final int BUFFER_SIZE = 1000;
	private static final int TIMEOUT_SIZE = 5000;
	private static final int MAX_SIZE = 255; // chars

	public static void main(String args[])
	{
		UDPClient client = new UDPClient();
	}


	public UDPClient()
	{
		initialize();
	}

	/**
	 * Initialize the system
	 */
	private void initialize()
	{
		try
		{
			String[] packet = setPacket();
			if (packet != null && packet[2].toCharArray().length <= MAX_SIZE) // Check content and message size
			{
				send(packet);
			}
			else
			{
				System.out.println("Message is empty or too long...");
				System.out.println("\n---Resetting client...");
				initialize();
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println("Something went wrong...");
			System.out.println("\n---Resetting client...");

			initialize();
		}

	}

	/**
	 * Create a packet with host address, port and message (with hashcode) from user input.
	 * @return array with packet message.
	 */
	private String[] setPacket() throws IllegalAccessException
	{

		System.out.println("Please specify receiver address, port and message. Separate the input with |  " +
				"\n Eg: 192.0.0.1|8080|message1|message2 " +
				"\n Waiting for user input:");

		Scanner scanner = new Scanner(System.in);
		if (scanner.hasNext())
		{
			String input = scanner.nextLine();
			String[] packetData = input.split("\\|");

			if (isUserInputValid(packetData))
			{
				for (int i = 2; i < packetData.length; i++)
				{
					String tempMessage = packetData[i];
					String messageWithHashCode = new String(tempMessage + "¤" + tempMessage.hashCode()+"¤"+(i-2)+"¤");
					packetData[i] = messageWithHashCode;
				}
				return packetData;
			}
			else throw new IllegalArgumentException("Entered port or address is invalid.");
		}
		else return null;
	}

	/**
	 * Checks if address and port input is valid.
	 * @param packetData - array with address, port and message.
	 * @return true if address and port is valid.
	 */
	private boolean isUserInputValid(String[] packetData)
	{
		if(packetData.length < 3 ) return false;
		String address = packetData[0];
		int port = Integer.parseInt(packetData[1]);

		int addressPeriodsCount = address.length() - address.replace(".", "").length();

		if (packetData.length < 3)
		{
			return false;
		}
		else if(address.equals("localhost") && (port > 0 || port <= 65535))
		{
			return true;
		}
		else
		{
			return (addressPeriodsCount == 3 ||addressPeriodsCount == 2 ) && (port > 0 || port <= 65535);
		}
	}

	/**
	 * Sends packet to host server.
	 * @param inputData including address, port and message.
	 */
	private void send(String[] inputData)
	{
		String address = inputData[0];
		int port = Integer.parseInt(inputData[1]);
		tryCount = 3;
		int messageCount = inputData.length-2;

		for(int i = 2; i < inputData.length ; i++)
		{
			System.out.println("\nSending message " + (i-1) + "." );
			String message = inputData[i];

			Thread thread = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					sendMessage(inputData, message, address, port);
				}
			});
		}

		// Reset client
		if(messageCount < inputData.length-2)
		{
			System.out.println("\nSome messages could not be sent.");
		}
		System.out.println("\n---Resetting client...");
		System.out.println("\n");
		initialize();
	}

	/**
	 * Checking if host has received sent message and also if it was non-corrupt when it arrived
	 * @return true if valid message was received
	 * @throws IOException
	 */
	private boolean isReceived(DatagramSocket aSocket) throws IOException
	{
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		try
		{
			aSocket.setSoTimeout(TIMEOUT_SIZE); // Timeout after 5 seconds
			aSocket.receive(reply);

			if(reply.getAddress() == null) return false;

			int validationNumber = Character.getNumericValue(new String(reply.getData()).charAt(0)); //Get first char and convert to int
			if(validationNumber == 0) System.out.println("Sent message was corrupt.");

			//Packed was successfully sent if host returned 1
			return validationNumber == 1;
		}
		catch (SocketTimeoutException e)
		{
			System.out.println("Timeout.");
			return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public void checkPacket(String[] inputData) throws IOException
	{
		//Checking sent packet
		boolean packetOK = false;

		if(isReceived(aSocket)) packetOK  = true;

		if(!packetOK && tryCount > 0)
		{
			//i--;
			tryCount--;
		}

		else if (!packetOK && tryCount == 0)
		{
			if(inputData.length < 3)
			{
				messageCount--;
				System.out.println("Message could not be sent.");
			}
			/*
			else if (i < inputData.length)

			{
				messageCount--;
				tryCount = 3;
				System.out.println("\nMessage " + (i - 1) + " could not be sent. Skipping to next message.");
			}
			*/
		}

		else if(packetOK) System.out.println("Success.");
	}

	/**
	 * Sends messa
	 * @param inputData
	 * @param address
	 * @param port
	 */
	public void sendMessage(String[] inputData, String message, String address, int port)
	{
		System.out.println("Runnable running");
		try
		{
			aSocket = new DatagramSocket();
			byte[] m = inputData.toString().getBytes();
			InetAddress aHost = InetAddress.getByName(address);
			DatagramPacket request = new DatagramPacket(m, message.length(), aHost, port);
			aSocket.send(request);
			//Check if server has received datagram
			System.out.println("Waiting for host...");
			checkPacket(inputData);
		}
		catch (SocketException e) { System.out.println("Socket: " + e.getMessage()); }
		catch (IOException e) { System.out.println("IO: " + e.getMessage()); }
		finally { if (aSocket != null) aSocket.close(); }

	}

}
