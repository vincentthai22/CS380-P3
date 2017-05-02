import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.CRC32;

/**
 * Created by Vincent on 5/1/2017.
 */
public class Ipv4Client {

    private final static String SERVER_NAME = "codebank.xyz";
    private final static int PORT_NUMBER = 38003;
    private final int MIN_PACKET_SIZE=20;
    String serverName;
    int portNumber;
    byte[] byteArray;

    public Ipv4Client(String serverName, int portNumber){
        this.serverName = serverName;
        this.portNumber = portNumber;

        callServer();

    }

    private void callServer() {
        Integer bytes = 2;
        try (Socket socket = new Socket(serverName, portNumber)) {
            System.out.println("Connected to server");

            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            OutputStream os = socket.getOutputStream();
            PrintStream out = new PrintStream(os, true, "UTF-8");
            int i = 0;
            while(i < 12) {

                os.write(getIpv4Packet(bytes));
                String temp = br.readLine();
                System.out.println(temp);
                if (temp.equals("good")) {
                    System.out.println("Response good.");
                } else {
                    System.out.println("Response bad.");
                }
                i++;
                bytes*=2;
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private byte[] getIpv4Packet(int additionalData){
        byte[] ipv4Packet = new byte[MIN_PACKET_SIZE + additionalData];
        short length = (short) (20 + additionalData);
        int i = 0;

        ipv4Packet[i++] = 0x45; //version  i = 0

        ipv4Packet[i++] = 0;  //tos     i = 1

        ipv4Packet[i++] = (byte) ((length >> 8) & 0xff); //length \ i = 2 , 3
        ipv4Packet[i++] = (byte) (length & 0xff);


        ipv4Packet[i++] = 0; //ident \  i = 4 , 5
        ipv4Packet[i++] = 0;

        ipv4Packet[i++] = (1 << 6); //flags i = 6


        ipv4Packet[i++] = 0; //offset i = 7

        ipv4Packet[i++] = 50; //ttl assume every pckt has 50 \ i=8

        ipv4Packet[i++] = 6; //protocol i = 9

        // OKAY.. THIS i++ was supposed to be cool now im ruined.. LOL
        // HARD-CODE TIME.

        i = 12;
        while(i < 15) {    //SourceAddr of your choice 12.13.14.15
            ipv4Packet[i++] = (byte) i;
        }

        // dest addr
        ipv4Packet[16] = (byte) 52;
        ipv4Packet[17] = (byte) 37;
        ipv4Packet[18] = (byte) 88;
        ipv4Packet[19] = (byte) 154;


        // checksum
        short packetChecksum = checkSum(ipv4Packet);
        ipv4Packet[10] = (byte) ((packetChecksum >> 8) & 0xff);
        ipv4Packet[11] = (byte) (packetChecksum & 0xff);

        // data
        for (i = 20; i < ipv4Packet.length; i++) {
            ipv4Packet[i] = (byte)0;
        }


        return ipv4Packet;
    }

    public short checkSum(byte[] b) {

        Long sum = (long) 0;
        Long temp;
        int i = 0;
        while (i < b.length) {
            temp = (long) b[i++] & 0xFF;
            temp = temp << 8;
            if(i < b.length)
                temp |= b[i++] & 0xFF;
            sum += temp;
            //   System.out.println(Long.toHexString(temp & 0xFFFF).toUpperCase());
            //  System.out.println(Long.toHexString(sum & 0xFFFFFFFF).toUpperCase());
            if ((sum & 0xFFFF0000) > 0) {
                sum &= 0xFFFF;
                sum++;
                // System.out.println("carry occured " + i);
            }
        }
        return (short) ~(sum & 0xFFFF);
    }


    public static void main(String[] args) {
        new Ipv4Client(SERVER_NAME, PORT_NUMBER);
    }

}
