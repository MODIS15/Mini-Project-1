import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by rcechab12 on 04-09-2015.
 */
public class RealOpgave3 {

    public boolean waitingForMessages;
    public ArrayList<String> StorageMessages = new ArrayList();
    public SenderTread sender;
    public Thread senderThread;

    public static void main(String args[])
    {
        RealOpgave3 ro3 = new RealOpgave3(10,3,1);
    }

    public boolean getMessage(){return waitingForMessages;}

    public RealOpgave3(int DatagramSize, int NumberOfDatagrams, int IntervalBetweenTransmissions )
    {
                    DatagramSocket socket = null; //Reciever setup
                    waitingForMessages = true;
                    try
                    {
                        socket = new DatagramSocket(7007);
                        // create socket at agreed port
                        byte[] buffer = new byte[1000];

                        //Starts the sender.
                        sender = new SenderTread(socket,DatagramSize,NumberOfDatagrams,IntervalBetweenTransmissions);
                        senderThread = new Thread(sender);
                        senderThread.start();

                        while(waitingForMessages)
                        {
                            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                            socket.receive(reply);
                            String message = new String(reply.getData());
                            StorageMessages.add(message);
                            System.out.println(message);
                            System.out.println("fdfd");
                        }
                    }
                    catch (SocketException e){System.out.println("Socket: " + e.getMessage());}
                    catch (IOException e) {System.out.println("IO: " + e.getMessage());}
                    finally {if(socket != null) socket.close();}


        System.out.println("STOP!!!");

    }

    public class SenderTread implements Runnable
    {
        String IPDestination = "localhost";
        int _timeOut;
        int size;
        int number;
        ArrayList<String> messages;
        DatagramSocket _socket;

        public  SenderTread(DatagramSocket socket, int DatagramSize, int NumberOfDatagrams, int IntervalDatagram)
        {
             _timeOut = IntervalDatagram;
            size = DatagramSize;
            number = NumberOfDatagrams;
            messages = new ArrayList<>();
            _socket = socket;

            for (int i = 0; i<number;i++) messages.add(""+i);
        }

        @Override
        public void run()
        {
            int port = 7007;

            byte[] message;
            String _message;

            try {Thread.sleep(_timeOut*1000);}
            catch (InterruptedException e) {e.printStackTrace();}

            for (int i = 0; i<number; i++)
            {

                message = messages.get(i).getBytes();
                _message = messages.get(i);

                try {
                    InetAddress host = InetAddress.getByName(IPDestination);
                    DatagramPacket packet = new DatagramPacket(message, _message.length(), host, port);
                    _socket.send(packet);
                    Thread.sleep(1000 * _timeOut);

                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(_timeOut*1000);
                System.out.println("Done sending");
                waitingForMessages = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }
}
