import com.sun.deploy.util.StringUtils;

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
		packet = setPacket();

		if (packet != null) send(packet);
		else {
			System.out.println("Something went wrong...");
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
		if (scanner.hasNext()) {
			String input = scanner.nextLine();
			String[] packetData = input.split("\\|");


			if (isInputValid(packetData)) {
				for (int i = 2; i < packetData.length; i++) {
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

	private boolean isInputValid(String[] packetData){
		String address = packetData[0];
		String port = packetData[1];

		int addressPeriodsCount = address.length() - address.replace(".", "").length();
		int portLength = port.length();

		return (addressPeriodsCount == 3 ||addressPeriodsCount == 2 ) && (portLength > 0 || portLength <= 5);
	}


	private void send(String[] inputData){
		DatagramSocket aSocket = null;
		String address = inputData[0];
		int port = Integer.getInteger(inputData[1]);

		for(int i = 2; i < inputData.length ; i++){
			System.out.println("Sending..." );

			String message = inputData[i];
			boolean isConnectionRecieve = false;

			try {
				aSocket = new DatagramSocket();
				byte[] m = message.getBytes();
				InetAddress aHost = InetAddress.getByName(address);
				int serverPort = port;
				DatagramPacket request =
						new DatagramPacket(m, message.length(), aHost, serverPort);
				aSocket.send(request);

				//Check if server has received datagram
				checkReceive(aSocket);




			} catch (SocketException e) {
				System.out.println("Socket: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("IO: " + e.getMessage());
			} finally {
				if (aSocket != null) aSocket.close();
			}

		}

	}

	private boolean checkReceive(DatagramSocket aSocket) throws IOException {
		byte[] buffer = new byte[1000];
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		while(reply.getAddress() == null)
		{
			aSocket.receive(reply);
		}

	}

}
