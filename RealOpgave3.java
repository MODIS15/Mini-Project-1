import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Created by rcechab12 on 04-09-2015.
 */
public class RealOpgave3 {

    public boolean waitingForMessages;
    public ArrayList<String> StorageMessages = new ArrayList();

    public static void main(String args[])
    {
        RealOpgave3 ro3 = new RealOpgave3(10,10,1);

    }

    public RealOpgave3(int DatagramSize, int NumberOfDatagrams, int IntervalBetweenTransmissions )
    {
        ExecutorService service = Executors.newSingleThreadExecutor();
        try
        {
            Runnable r = new Runnable() {
                @Override
                public void run()
                {
                    DatagramSocket socket = null; //Reciever setup
                    try
                    {
                        socket = new DatagramSocket(7007);
                        // create socket at agreed port
                        byte[] buffer = new byte[1000];

                        //Starts the sender.
                        SenderTread sender = new SenderTread(socket,DatagramSize,NumberOfDatagrams,IntervalBetweenTransmissions);
                        Thread senderThread = new Thread(sender);
                        senderThread.start();

                        waitingForMessages = true;
                        while(waitingForMessages)
                        {
                            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                            socket.receive(reply);
                            String message = new String(reply.getData());
                            StorageMessages.add(message);
                            System.out.println(message);
                        }
                        System.out.println("DOES´NT REACH HERE.");
                        senderThread.interrupt(); // Stop the thread
                    }
                    catch (SocketException e){System.out.println("Socket: " + e.getMessage());}
                    catch (IOException e) {System.out.println("IO: " + e.getMessage());}
                    finally {if(socket != null) socket.close();
                    }
                }
            };

            //Call TimeOutException when throw
            Future<?> f = service.submit(r);
            f.get(4+(NumberOfDatagrams-1*IntervalBetweenTransmissions), TimeUnit.SECONDS);

        }catch (TimeoutException e){
            System.out.println("Time to calculate");
            waitingForMessages = false;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

            for (int i = 0; i<number; i++) {

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
                try {
                    Thread.sleep(_timeOut);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }




}
