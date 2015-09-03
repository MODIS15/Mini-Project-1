/**
 * Created by JakeMullit on 31/08/15.
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;


public class SocketUDP extends DatagramSocket {

    ArrayList<DatagramPacket> packages = new ArrayList<>();

    Random random = new Random();


    public SocketUDP() throws SocketException {
    }

    public SocketUDP(int i) throws SocketException{
        super(i);
    }

    @Override
    public void send(DatagramPacket _package)
    {
        addMessageTOQueue(_package);
        System.out.println("Packages in store: "+packages.size());

        if(random.nextBoolean())
        {


            while(!packages.isEmpty()) //All stored messages
            {

                try {sendMessage(getRandomPackage());}
                catch (IOException e) {e.printStackTrace();}
            }
        }
    }

    private void sendMessage(DatagramPacket _package) throws IOException
    {

        int action = random.nextInt(4);
        switch(action)
        {
            case 0: super.send(_package);
                break;
            case 1: super.send(_package); super.send(_package);
                break;
            case 2: super.send(distortData(_package));
                break;
            default:
                return;
        }

    }

    private DatagramPacket distortData(DatagramPacket _package)
    {
        String input = new String(_package.getData());

        String returnValue;

        if(random.nextBoolean())
            returnValue = disarrange(input);
        else
            returnValue = randomDiscard(input);

        byte[] changedData = returnValue.getBytes();

        _package.setData(changedData);
        return _package;
    }


    private String disarrange(String _input)
    {
        char[] input = _input.toCharArray();
        int swapIndex;

        for(int i = 0; i<input.length; i++)
        {
            swapIndex = random.nextInt(input.length);
            char swapTemp = input[i];
            input[i]= input[swapIndex];
            input[swapIndex] = swapTemp;
        }

        return String.valueOf(input);
    }

    private String randomDiscard(String input)
    {
        int interval = input.length();
        int substringStart = random.nextInt(interval);
        int substringEnd = random.nextInt(interval);

        if(substringStart>substringEnd)
        {
            int swapTemp = substringStart;
            substringStart = substringEnd;
            substringEnd = swapTemp;
        }
        //Avoid 100% deletion

        return input.substring(substringStart,substringEnd);
    }

    private DatagramPacket getRandomPackage()
    {
        int randomIndex = random.nextInt(packages.size());
        DatagramPacket _package = packages.get(randomIndex);
        packages.remove(randomIndex);
        return _package;
    }

    private void addMessageTOQueue(DatagramPacket _package)
    {
        packages.add(_package);

        //Maybe add duplicate
        int action = random.nextInt(100);
        if(action<30)
            packages.add(_package);
    }
}
