//package Project3;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ClientRouter implements Runnable {
    //For those which weren't received
    ArrayList<Integer> pendingVals = new ArrayList<>();

    //chunk static
    final static int chunk = 500;
    int type = 0;
    //how much data is being sent over the network
    static byte[] send = new byte[513];
    //Contains the actual file
    private byte[] source;
    //sequence num
    static int seq = 0;
    //initdata sent
    public static boolean initSuccessful = false;
    //Completed the transfer
    public static boolean done = false;
    static int  lastChunk = 0;
    private InetAddress ip;
    private FileInputStream content = null;
    public static byte[] ackD = new byte[1];

    public String destination;
    public static String address;


    public ClientRouter(int type, String destination, byte[] source, InetAddress ip, FileInputStream content) {
        this.type = type;
        this.destination = destination;
        this.source = source;
        this.ip = ip;
        this.content = content;
    }

    public void start() {

        try {

            ByteBuffer bb = ByteBuffer.allocate(8);
            InetAddress hostAdd  = InetAddress.getLocalHost();
            address = hostAdd.getHostAddress().trim();

            //Destination
//                InetAddress ip = InetAddress.getByName(args[1]);
//                content = new FileInputStream(new File(fname));
//                source = content.readAllBytes();
            //Last chunk
            lastChunk = init(source);
            DatagramSocket ds0 = new DatagramSocket();
            bb.putInt(source.length).putInt(lastChunk);
//            bb.putInt(5).putInt(5);
            byte[] size = bb.array();
//            DatagramPacket dp = new DatagramPacket(size, size.length, ip, 63001);
//            ds0.send(dp);
            sendAndReceive.send(63001,ip,size);

            //successfully sent check introduction needed
            initSuccessful = true;
            if (initSuccessful) {
                sendingThread();
            }
//                    Thread client = new Thread(new ClientRouter(0, lastChunk, args[1]));
//                    client.start();
//                    Thread checkArrayList = new Thread(new ClientRouter(1, lastChunk, args[1]));
//                    checkArrayList.start();

            if (done)
                content.close();
//            } else {
//                System.err.println("File name or Server IP not given");
//                System.exit(1);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int init(byte[] source) {
        int len = source.length;
        int actualDivs = len / chunk;
        // Calculate what should be the chunk size for the final byte vals
        return len - chunk * actualDivs;

    }

    public void sendingThread() {
//        try {
        // Receive ack
        DatagramPacket dp1 = null;
        if (initSuccessful) {
            //Need to change
            while (seq < source.length) {
                intToBytes(send, seq, 0);
                String[] tempAdd = address.split("[.]+");
                for (int a = 4; a < 8; a++) {
                    send[a] = (byte) Integer.parseInt(tempAdd[a - 4]);
                }
                tempAdd = destination.split("[.]+");
                for (int a = 8; a < 12; a++) {
                    send[a] = (byte) Integer.parseInt(tempAdd[a - 8]);
                }
                send[12]= 0;
                int range = (seq == source.length - lastChunk ? lastChunk : chunk);
                int temp = seq;
                System.out.println(range);
                for (int i = 13; i <= range + 12; i++) {
                    send[i] = source[temp];
////                        send[i] = 1;
//                        System.out.println(send[i]+" for index "+ temp);
//                        System.out.println(seq+" i is "+ i);
                    temp++;
                }
//                    DatagramSocket ds = new DatagramSocket(63001);
//                    DatagramPacket dp = new DatagramPacket(send, send.length, ip, 63001);
//                    ds.send(dp);
//                    asd
                sendAndReceive.send(63001,ip,send);


//                    dp1 = new DatagramPacket(ackD, ackD.length);
//                    System.out.println("listening");
//                    ds.receive(dp1);
////                    System.out.println(ackD[0]);

                //new
                if (ackD[0]==1){
                    seq = temp;
                }
                if (true)
                    seq = temp;

            }
            done = true;
        }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void run() {
        //thread 1 for sending data
        if (this.type == 0) {
            start();
        }//thread 2 for checking if any packet was dropped
//        else if (this.type == 1) {
////            checkforVals();
//        }
    }


    private static void intToBytes(byte[] input, int data, int start) {
        input[start] = (byte) ((data >> 24) & 0xff);
        input[start + 1] = (byte) ((data >> 16) & 0xff);
        input[start + 2] = (byte) ((data >> 8) & 0xff);
        input[start + 3] = (byte) ((data >> 0) & 0xff);


    }
}
