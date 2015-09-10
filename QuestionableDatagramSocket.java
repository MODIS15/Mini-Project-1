/**
 * Created by JakeMullit on 31/08/15.
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;


public class QuestionableDatagramSocket extends DatagramSocket {

    ArrayList<DatagramPacket> packages = new ArrayList<>();

    Random random = new Random();
    int transmissionInterval;

    public QuestionableDatagramSocket() throws SocketException {
    }

    public QuestionableDatagramSocket(int i, int transmissionInterval) throws SocketException{
        super(i);
        this.transmissionInterval = transmissionInterval;
    }
    @Override
    public void send(DatagramPacket _package)
    {
        addMessageTOQueue(_package);

        if(random.nextBoolean())
        {
            while(!packages.isEmpty()) //All stored messages
            {
                try {super.send(getRandomPackage());}
                catch (IOException e) {e.printStackTrace();}

                try {Thread.sleep(transmissionInterval);} //Makes sure the packages are transmitted at the same interval as specified from the sender.
                catch (InterruptedException e) {e.printStackTrace();}
            }
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
        int action = random.nextInt(100);
        if (action >= 0 && 25 >= action){packages.add(_package);}
        else if (action >= 25 && 50 >= action){packages.add(_package); packages.add(_package);}
        else if (action >= 50 && 75 >= action){packages.add(distortData(_package));}
        else if (action >= 75 && 100 >= action){ /* Discard message */}

    }
}
