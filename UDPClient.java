import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient
{

	private String[] packet;
    public static void main(String args[])
	{
		UDPClient client = new UDPClient();
			}

	private UDPClient(){
		initialize();
	}

	/**
	 * Initialize the system
	 */
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

		System.out.println("Please specify receiver address, port and message. Separate the input with |  " +
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
					String messageWithHashCode = new String(tempMessage + "�" + tempMessage.hashCode());
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
		int port = Integer.parseInt(packetData[1]);

		int addressPeriodsCount = address.length() - address.replace(".", "").length();;

		if (packetData.length < 3)
		{
			return false;
		}
		else if(address.equals("localhost"))
		{
			return port > 0 || port <= 65535;
		}
		else
		{
			return (addressPeriodsCount == 3 ||addressPeriodsCount == 2 ) && (port > 0 || port <= 65535);
		}
	}


	/**
	 * Sending specified messages from user input
	 * @param inputData
	 */
	private void send(String[] inputData){
		DatagramSocket aSocket = null;
		String address = inputData[0];
		int port = Integer.parseInt(inputData[1]);
		int tryCount = 3;
        int messageCount= inputData.length-2;

		for(int i = 2; i < inputData.length ; i++) {
			System.out.println("Sending message "+ (i-1)+"..." );

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



				//Checking sent packet
                boolean packetOK = false;
                if(checkReceive()) packetOK  = true;

				if(!packetOK && tryCount > 0)
				{
					i--;
					tryCount--;
				}

				else if (!packetOK && tryCount > 0)
				{
					if(inputData.length < 4) {
                        messageCount--;
                        System.out.println("Message could not be sent.");
                    }
					else{
                        messageCount--;
                        tryCount = 3;
                        System.out.println("Message could not be sent. Skipping to next message.");
                    }
				}

				else System.out.println("Success.");


			} catch (SocketException e) {
				System.out.println("Socket: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("IO: " + e.getMessage());
			} finally {
				if (aSocket != null) aSocket.close();
			}

		}
        //Resetting client
        if(messageCount == inputData.length-2) {
            System.out.println("Some messages could not be sent...");
        }

        System.out.println("\n");
        initialize();
	}

	/**
	 * Checking if host has received sent message and also if it was non-corrupt when it arrived
	 * @return
	 * @throws IOException
	 */
	private boolean checkReceive() throws IOException {


				DatagramSocket aSocket;
                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				try {
                    aSocket = new DatagramSocket();
                    aSocket.setSoTimeout(5000); // Timeout after 5 seconds
                    aSocket.receive(reply);

                    if(reply.getAddress() == null) return false;
                    int validationNumber = Integer.parseInt(new String(reply.getData()));
                    if(validationNumber == 0) System.out.println("Sent message was corrupt.");

                    //Packed was successfully sent if host returned 1
                    return validationNumber == 1;



				} catch (SocketTimeoutException e) {
                    System.out.println("Timeout... Trying again.");
                    return false;
                } catch (IOException e) {
					e.printStackTrace();
				}
        return false;
    }
 }
