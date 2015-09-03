import java.io.IOException;
import java.net.*;
import java.util.HashMap;

/*
Your programs must accept as input (a) datagram size, (b) number of datagrams sent and (c) interval between transmissions.
The program must output (1) absolute number and percentage of lost datagrams and (2) absolute number and percentage of duplicated datagrams.
 It is acceptable if your estimate cannot distinguish between losing a single reply, losing a single response, or losing both a reply and a response.
 */

public class opgave3 {


    //Minimum String memory usage (bytes) = 8 * (int) ((((no chars) * 2) + 45) / 8)
    //Simplified : Multiply the number of characters of the String by two; Then add 38;


    HashMap<String,Integer> occurrence = new HashMap<>();
    DatagramSocket sendingSocket;



    public static void main(String[] args){
        opgave3 program = new opgave3(21,15,1);
    }

    String fillerData;



    public opgave3(int datagramSize, int amountOfDatagramsSent, int transmissionInterval)
    {
        fillerData = createFillerData(datagramSize);

        try
        {
            sendingSocket = new DatagramSocket();
            Thread listenerThread = new Thread(new ReceiverThread(occurrence));
            Thread senderThread = new Thread(new SendingThread(sendingSocket, transmissionInterval,amountOfDatagramsSent));

            listenerThread.start();
            senderThread.start();
        }
        catch (SocketException e) {e.printStackTrace();}
    }



    private String createFillerData(int memorySize)
    {
        //Minimum String memory usage (bytes) = 8 * (int) ((((no chars) * 2) + 45) / 8)
        //int memorySize = 16+ ((((x) * 2) + 45) / 8);

        int x = ((memorySize-16)*8-45)/2 ;
        String _filerData = "";

        if(x>0)
        {
            char[] fillerData = new char[x];
            for(int i =0; i<fillerData.length; i++)
            {
                fillerData[i] = 'a';
            }

            _filerData = new String(fillerData);
        }

        return _filerData;
    }


    public void amountOfLostDatagrams(){}

    public void amountOfLostDatagramsInPercentage(){}

    public void amountOFDuplicateDatagram(){}

    public void amountOFDuplicateDatagramInPercentage(){}



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


        String[] messageList;
        DatagramSocket socket;
        String IPDestination = "localhost";
        int _timeOut;


        public SendingThread(DatagramSocket socket, int timeOut, int messagesToBeSent)
        {
            this.socket = socket;
            messageList  = new String[messagesToBeSent];
            _timeOut = timeOut;


            for(int i = 0; i<messageList.length; i++)
            {
                if(i>9)
                    messageList[i]=i+fillerData;  //Make sure there is two ciffers string. Important for data size
                else
                    messageList[i] ="0"+i+fillerData;
            }

        }


        @Override
        public void run()
        {
            for (int i = 0; i<messageList.length; i++)
            {

                byte[] message = messageList[i].getBytes();
                String _message = messageList[i];

                int port = 7007;

                try {
                    InetAddress host = InetAddress.getByName(IPDestination);
                    DatagramPacket packet = new DatagramPacket(message, _message.length(), host, port);
                    socket.send(packet);
                    Thread.sleep(1000*_timeOut);

                }
                catch (SocketException e) {e.printStackTrace();}
                catch (UnknownHostException e) {e.printStackTrace();}
                catch (IOException e) {e.printStackTrace();} catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {Thread.sleep(_timeOut);}
                catch (InterruptedException e) {e.printStackTrace();}
            }
        }
    }

}
