import java.util.Scanner;
import java.io.*;

public class BookClient {
    public static void main(String[] args) {
        String hostAddress;
        int tcpPort;
        int udpPort;
        int clientId;

        if (args.length != 2) {
            System.out.println("ERROR: Provide 2 arguments: command-file, clientId");
            System.out.println("\t(1) command-file: file with commands to the server");
            System.out.println("\t(2) clientId: an integer between 1..9");
            System.exit(-1);
        }

        String commandFile = args[0];
        clientId = Integer.parseInt(args[1]);
        hostAddress = "localhost";
        tcpPort = 7000;// hardcoded -- must match the server's tcp port
        udpPort = 8000;// hardcoded -- must match the server's udp port
        boolean usingTCP = false;
        TCPClient tcpClient = new TCPClient(hostAddress, tcpPort, clientId);
        UDPClient udpClient = new UDPClient(hostAddress, udpPort, clientId);

        try {
            Scanner sc = new Scanner(new FileReader(commandFile));
            PrintWriter outFile = new PrintWriter("out_" + clientId + ".txt");

            while (sc.hasNextLine()) {
                String cmd = sc.nextLine();
                String[] tokens = cmd.split(" ");

                if (tokens[0].equals("set-mode")) {
                    // Set the mode of communication for sending commands to the server
                    if (tokens[1].equals("t")){
                        usingTCP = true;
                        tcpClient.send(cmd);
                        tcpClient.out(outFile);
                    } else {
                        usingTCP = false;
                        udpClient.send(cmd);
                        udpClient.out(outFile);
                    }
                
                } else if (tokens[0].equals("begin-loan")) {
                    if (usingTCP){
                        tcpClient.send(cmd);
                        tcpClient.out(outFile);
                    } else {
                        udpClient.send(cmd);
                        udpClient.out(outFile);
                    }

                } else if (tokens[0].equals("end-loan")) {
                    if (usingTCP){
                        tcpClient.send(cmd);
                        tcpClient.out(outFile);
                    } else {
                        udpClient.send(cmd);
                        udpClient.out(outFile);
                    }

                } else if (tokens[0].equals("get-loans")) {
                    if (usingTCP){
                        tcpClient.send(cmd);
                        tcpClient.out(outFile);
                    } else {
                        udpClient.send(cmd);
                        udpClient.out(outFile);
                    }

                } else if (tokens[0].equals("get-inventory")) {
                    if (usingTCP){
                        tcpClient.send(cmd);
                        tcpClient.out(outFile);
                    } else {
                        udpClient.send(cmd);
                        udpClient.out(outFile);
                    }
                } else if (tokens[0].equals("exit")) {
                    if (usingTCP){
                        tcpClient.send(cmd);
                    } else {
                        udpClient.send(cmd);
                    }
                    udpClient.close();
                    tcpClient.close();
                    outFile.close();
                } else {
                    System.out.println("ERROR: No such command");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}