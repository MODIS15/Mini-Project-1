import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient
{

	private String[] packet;

    public void main(String args[])
	{
		UDPClient client = new UDPClient();
			}

	private UDPClient(){
		initialize();
	}

	private void initialize(){
		try
		{
			packet = setPacket();
			if (packet != null)
			{
				send(packet);
			}
			else
			{
				System.out.println("Something went wrong...");
				initialize();
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			initialize();
		}


	}

	/**
	 * Get user input, and create a packet with message and hashcode
	 * @return array with messages
	 */
	private String[] setPacket() throws Exception, IllegalAccessException {

		System.out.println("Please specify reciever address, port and message. Seperate the input with |  " +
				"\n Eg: 192.0.0.1|8080|message1|message2 " +
				"\n To use default values just press enter without any input. ");

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
					String messageWithHashCode = new String(tempMessage + "¤" + tempMessage.hashCode());
					packetData[i] = messageWithHashCode;
				}
				return packetData;
			}
			else throw new IllegalArgumentException("Entered port or address is invalid.");
		}
		else return null;
	}

	/**
	 * Check if address and port is valid
	 * @param packetData
	 * @return a boolean regarding port and address is valid
	 */
	private boolean isUserInputValid(String[] packetData){
		String address = packetData[0];
		String port = packetData[1];

		int addressPeriodsCount = address.length() - address.replace(".", "").length();
		int portLength = port.length();

		return (addressPeriodsCount == 3 ||addressPeriodsCount == 2 ) && (portLength > 0 || portLength <= 5);
	}


	/**
	 *
	 * @param inputData
	 */
	private void send(String[] inputData){
		DatagramSocket aSocket = null;
		String address = inputData[0];
		int port = Integer.getInteger(inputData[1]);
		int tryCount = 3;

		for(int i = 2; i < inputData.length ; i++) {
			System.out.println("Sending message "+ i+"..." );

			String message = inputData[i];
			try
			{
				aSocket = new DatagramSocket();
				byte[] m = message.getBytes();
				InetAddress aHost = InetAddress.getByName(address);
				int serverPort = port;
				DatagramPacket request = new DatagramPacket(m, message.length(), aHost, serverPort);

				aSocket.send(request);


				//Check if server has received datagram
				System.out.println("Waiting for host...");
				byte[] buffer = new byte[1000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				boolean packetOK = false;

				//Checking until timeout
				long endTimeMillis = System.currentTimeMillis() + 5000; //Timeout after 5 seconds
				while (System.currentTimeMillis() < endTimeMillis){
					if(checkReceive(aSocket,reply))
					{
						packetOK = true;
						break;
					}
				}

				//Resending or skipping message after a number of tries
				if(!packetOK && tryCount > 0)
				{
					System.out.println("Timeout... Trying again.");
					i--;
					tryCount--;
				}

				else if (!packetOK && tryCount >= 0)
				{
					if(inputData.length < 4)
						System.out.println("Message could not be sendt.");
					else
						System.out.println("Message could not be sendt. Skipping to next message.");
				}

				else System.out.println("Success.");


			} catch (SocketException e) {
				System.out.println("Socket: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("IO: " + e.getMessage());
			} finally {
				if (aSocket != null) aSocket.close();
			}

			//Resetting client
			initialize();

		}
	}

	private boolean checkReceive(DatagramSocket aSocket, DatagramPacket reply) throws IOException {
		aSocket.receive(reply);

		if(reply.getAddress() == null) return false;

		int validationNumber = Integer.getInteger(new String(reply.getData()));
		if(validationNumber == 0) System.out.println("Sent message was corrupt.");
		return validationNumber == 1;


	}

}
