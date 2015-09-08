import java.io.IOException;
import java.net.*;
import java.util.HashSet;

public class opgave3
{
    HashSet<String> receivedMessages = new HashSet<>(); //Received messages. Used to keep count on duplicate messages
    String[] messageList; //Stores all sent messages.
    int amountOfMessageReceived;

    SendingThread sender;         //Sending thread. Splitting receiver and sender in two different threads
    String fillerData;            //Filler data used to append package data to adjust package size
    boolean SendingThreadIsStillActive = true;

    DatagramSocket sendingSocket; //Sending socket
    String IPDestination = "localhost";
    int sendingToPort = 7007;

    int listeningPort = 7007;

    public static void main(String[] args)
    {
        System.out.println("Opgave 3 start");
        System.out.println("Transmitting packages");
        opgave3 program = new opgave3(50,6,1);

        System.out.println("Amount of datagrams lost: " + program.amountOfLostDatagrams());
        System.out.println("Amount of datagrams lost in percentage: " + program.amountOfLostDatagramsInPercentage());
        System.out.println("Amount of datagrams duplicates: " + program.amountOFDuplicateDatagram());
        System.out.println("Amount of datagrams duplicates in percentage: " + program.amountOFDuplicateDatagramInPercentage());


    }





    public opgave3(int datagramSize, int amountOfDatagramsSent, int transmissionInterval)
    {
        fillerData = createFillerData(datagramSize);

        try
        {
            sendingSocket = new DatagramSocket();
            sender = new SendingThread(sendingSocket, transmissionInterval,amountOfDatagramsSent);
            Thread senderThread   = new Thread(sender);
            senderThread.start();


            //Receiving thread
            DatagramSocket socket = new DatagramSocket(listeningPort);
            socket.setSoTimeout(5000);
            byte[] buffer = new byte[1000];
            while(SendingThreadIsStillActive)
            {
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                socket.receive(reply);
                String message = new String(reply.getData());
                updateOccurrenceRatings(message);
                //System.out.println(message);
            }
        }
        catch (SocketTimeoutException e ){}
        catch (SocketException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
    }


    private void updateOccurrenceRatings(String reply)
    {
        amountOfMessageReceived++;
        receivedMessages.add(reply);
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

///Not Quite done
    public int amountOfLostDatagrams()
    {
        return messageList.length- receivedMessages.size();
    }

    public double amountOfLostDatagramsInPercentage()
    {
        if(receivedMessages.size()!=0)
            return (((double) messageList.length- (double) receivedMessages.size())/messageList.length)*100;
        else
            return 0;
    }

    public int amountOFDuplicateDatagram()
    {
        return amountOfMessageReceived- receivedMessages.size();
    }

    public double amountOFDuplicateDatagramInPercentage()
    {
        if(receivedMessages.size()!= 0)
            return (((double)amountOfMessageReceived-(double) receivedMessages.size())/ receivedMessages.size())*100;
        else
            return 0;
    }
///Not Quite done








public class SendingThread implements Runnable{

        DatagramSocket socket;
        int _timeOut;


        public SendingThread(DatagramSocket socket, int timeOut, int amountOfMessagesTOBeSent)
        {
            this.socket = socket;
            messageList  = new String[amountOfMessagesTOBeSent];
            _timeOut = timeOut;

            //Create filler data used to append package data to adjust package size
            for(int i = 0; i<messageList.length; i++)
            {
                if(i>9)
                    messageList[i]=i+fillerData;
                else
                    messageList[i] ="0"+i+fillerData;
            }

        }

        public String[] getMessageList()
        {
            return messageList;
        }


        @Override
        public void run()
        {

            try {Thread.sleep(1000);}   //Initial sleep. Gives the receiver time to start
            catch (InterruptedException e) {e.printStackTrace();}

            for (int i = 0; i<messageList.length; i++)
            {
                byte[] message = messageList[i].getBytes();
                String _message = messageList[i];



                try
                {
                    InetAddress host = InetAddress.getByName(IPDestination);
                    DatagramPacket packet = new DatagramPacket(message, _message.length(), host, sendingToPort);
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

            //All messages has been sent. Signal listener to close
            SendingThreadIsStillActive = false;

        }
    }

}
