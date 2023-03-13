import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    DatagramSocket socket;
    InetAddress iPAddress;
    DatagramPacket sendPacket;
    DatagramPacket receivePacket;
    byte[] sendData;
    byte[] receiveData;
    int udpPort;
    int clientID;

    public UDPClient (String hostAddress, int udpPort, int clientID){
        try {
            receiveData = new byte[4096];
            socket = new DatagramSocket();
            iPAddress = InetAddress.getByName(hostAddress);
            this.udpPort = udpPort;
            this.clientID = clientID;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send (String msg) {
        try {
            msg = clientID + "|" + msg;
            sendData = msg.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, iPAddress, udpPort);
            socket.send(sendPacket);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void out (PrintWriter outfile) {
        try {
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            while (true) {
                socket.receive(receivePacket);
                String received = new String(receivePacket.getData(), 0, receivePacket.getLength());
                String[] response  = received.split("\\|");
                if (response[0].equals(Integer.toString(clientID))){
                    for(int i = 1; i < response.length; i++){
                        outfile.println(response[i]);
                    }
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } 
    }

    public void close () {
        socket.close();
    }
}
