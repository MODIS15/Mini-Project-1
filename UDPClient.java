import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient
{
    public static void main(String args[])
	{
		System.out.println("Please specify reciever address and message. Seperate the input with |  " +
				"\n Eg: 192.0.0.1|message1|message2 " +
				"\n To use default values just press enter without any input. " );

		Scanner scanner = new Scanner(System.in);
		if(scanner.hasNext())
		{
			String input = scanner.nextLine();
			String[] inputdata = input.split("\\|");
			send(inputdata);
		}
		else
		{
			String[] inputdata =  {"localhost","Hello"," World"," Dennis"};
			send(inputdata);
		}

	}

	private static void send(String[] inputData){
		DatagramSocket aSocket = null;
		String address = inputData[0];

		for(int i = 1; i < inputData.length ; i++){
			System.out.println("Sending..." );

			String message = inputData[i];
			boolean isConnectionRecieve = false;

			try {
				aSocket = new DatagramSocket();
				byte[] m = message.getBytes();
				InetAddress aHost = InetAddress.getByName(address);
				int serverPort = 7007;
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

	private static boolean checkReceive(DatagramSocket aSocket) throws IOException {
		byte[] buffer = new byte[1000];
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		while(reply.getAddress() == null)
		{
			aSocket.receive(reply);
		}

	}

}
