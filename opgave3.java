import java.io.IOException;
import java.net.*;
import java.util.HashMap;

/**
 * Created by JakeMullit on 02/09/15.
 */
public class opgave3 {

    HashMap<String,Integer> occurrence = new HashMap<>();
    DatagramSocket sendingSocket;


    public static void main(String[] args){
        opgave3 program = new opgave3(0,0,2);
    }

    public opgave3(int datagramSize, int amountOfDatagramsSent, int transmissionInterval){

        try
        {
            sendingSocket = new DatagramSocket();
            Thread listenerThread = new Thread(new ReceiverThread(occurrence));
            Thread senderThread = new Thread(new SendingThread(sendingSocket, transmissionInterval));

            listenerThread.start();
            senderThread.start();
        }
        catch (SocketException e) {e.printStackTrace();}
    }

    public void amountOfLostDatagrams(){}

    public void amountOfLostDatagramsInPercentage(){}

    public void amountOFDuplicateDatagram(){}

    public void amountOFDuplicateDatagramInPercentage(){}





    /*
    Your programs must accept as input (a) datagram size, (b) number of datagrams sent and (c) interval between transmissions.
    The program must output (1) absolute number and percentage of lost datagrams and (2) absolute number and percentage of duplicated datagrams.
     It is acceptable if your estimate cannot distinguish between losing a single reply, losing a single response, or losing both a reply and a response.
     */










    public class ReceiverThread implements Runnable
    {
        private HashMap<String,Integer> occurrence;


        public ReceiverThread(HashMap<String,Integer> occurrence)
        {
            this.occurrence = occurrence;
        }
        @Override
        public void run()
        {
            DatagramSocket socket = null;
                try
                {
                    socket = new DatagramSocket(7007);
                    // create socket at agreed port
                    byte[] buffer = new byte[1000];
                    while(true)
                    {
                        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                        socket.receive(reply);
                        String message = new String(reply.getData());
                        updateOccurrenceRatings(message);
                        System.out.println(message);
                    }
                }
                catch (SocketException e){System.out.println("Socket: " + e.getMessage());}
                catch (IOException e) {System.out.println("IO: " + e.getMessage());}
                finally {if(socket != null) socket.close();}
        }

        /** Store data in occurrence map.
         */
        private void updateOccurrenceRatings(String reply)
        {
            if(occurrence.containsKey(reply))
                occurrence.put(reply,1+occurrence.get(reply)); //If exist, then increment
            else{occurrence.put(reply,1);} //Else create new entry
        }

        public HashMap<String,Integer> getOccurrenceData()
        {
            return occurrence;
        }
    }



    public class SendingThread implements Runnable{


        String[] messageList  = new String[20];
        DatagramSocket socket;
        String IPDestination = "localhost";
        int _timeOut;

        public SendingThread(DatagramSocket socket, int timeOut)
        {
            this.socket = socket;
            _timeOut = timeOut;

            for(int i = 0; i<messageList.length; i++)
            {
                messageList[i]=""+i;
            }
        }

        @Override
        public void run()
        {
            for (int i = 0; i<messageList.length; i++) {

                byte[] message = messageList[i].getBytes();
                String _message = messageList[i];

                int port = 7007;

                try {
                    InetAddress host = InetAddress.getByName(IPDestination);
                    DatagramPacket packet = new DatagramPacket(message, _message.length(), host, port);
                    socket.send(packet);
                    Thread.sleep(100*_timeOut);

                }
                catch (SocketException e) {e.printStackTrace();}
                catch (UnknownHostException e) {e.printStackTrace();}
                catch (IOException e) {e.printStackTrace();} catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
