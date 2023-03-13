import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class BookServer {
    static Map<String, Integer> inventory;
    static int loanID = 1;
    static Map<String, Map<Integer, String>> userLoans = new LinkedHashMap<>();
    static Map<Integer, String> loanAndUser = new LinkedHashMap<>();
    public static void main(String[] args) {
        int tcpPort;
        int udpPort;
        if (args.length != 1) {
            System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
            System.exit(-1);
        }
        String fileName = args[0];
        tcpPort = 7000;
        udpPort = 8000;
        ServerSocket tcpServerSocket;
        DatagramSocket udpServerSocket;

        // parse the inventory file
        inventory = new LinkedHashMap<>();
        try (Scanner scanner = new Scanner(new FileReader(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("(?<=\")\\s+|\\s+(?=\\d)|(?<=\\d)\\s+(?=\")");
                String bookTitle = parts[0];
                int numCopies = Integer.parseInt(parts[1]);
                inventory.put(bookTitle, numCopies);
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Inventory file not found");
            System.exit(-1);
        }


        // Create server sockets and threads to listen for each type of communciation
        try {
            // Create TCP socket
            tcpServerSocket = new ServerSocket(tcpPort);
            // Create UDP socket
            udpServerSocket = new DatagramSocket(udpPort);

            //Thread listening for TCP Communication
            new Thread(() -> {
                while (true) {
                    try {
                        Socket tcpClientSocket = tcpServerSocket.accept();
                        new Thread(() -> handleTcpConnection(tcpClientSocket)).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            //Thread listening for UDP Communication
            new Thread(() -> {
                byte[] buffer = new byte[4096];
                while (true) {
                    DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
                    try {
                        udpServerSocket.receive(udpPacket);
                        new Thread(() -> handleUdpPacket(udpServerSocket, udpPacket)).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


    private static void handleTcpConnection(Socket clientSocket) {
        // Handle the TCP connection
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String received;
            String msg;
            String clientID;
            // While commands are being received
            while((received = in.readLine()) != null){
                String[] cmd  = received.split("\\|");
                clientID = cmd[0];
                String[] tokens = cmd[1].split( "\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                // Parse commands
                if (tokens[0].equals("set-mode")) {
                    // set the mode of communication for sending commands to the server
                    if (tokens[1].equals("t")){
                        out.println(clientID + "|" + "The communication mode is set to TCP");
                    } else if (tokens[1].equals("u")){
                        out.println(clientID + "|" + "The communication mode is set to UDP");
                    }
                
                } else if (tokens[0].equals("begin-loan")) {
                    msg = beginLoan(tokens);
                    out.println(clientID + "|" + msg);

                } else if (tokens[0].equals("end-loan")) {
                    msg = endLoan(tokens);
                    out.println(clientID + "|" + msg);

                } else if (tokens[0].equals("get-loans")) {
                    ArrayList<String> loans = getLoans(tokens);
                    String joined = String.join("|", loans);
                    out.println(clientID + "|" + joined);

                } else if (tokens[0].equals("get-inventory")) {
                    ArrayList<String> inventoryList = getInventory();
                    String joined = String.join("|", inventoryList);
                    out.println(clientID + "|" + joined);
                    
                } else if (tokens[0].equals("exit")) {
                    in.close();
                    out.close();
                    clientSocket.close();
                    ArrayList<String> inventoryList = getInventory();
                    PrintWriter inventoryFile = new PrintWriter("inventory.txt");
                    for (String book : inventoryList){
                        inventoryFile.println(book);
                    }
                    inventoryFile.close();
                } else {
                    System.out.println("ERROR: No such command");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void handleUdpPacket(DatagramSocket udpServerSocket, DatagramPacket packet) {
        // Handle the UDP packet here
        byte[] sendData;
        int port = packet.getPort();
        InetAddress IPAddress = packet.getAddress();
        String clientID;
        String received = new String(packet.getData(), 0, packet.getLength());
        String[] cmd  = received.split("\\|");
        clientID = cmd[0];
        String[] tokens = cmd[1].split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        String msg;
        // Parse command
        if (tokens[0].equals("set-mode")) {
            // set the mode of communication for sending commands to the server
            if (tokens[1].equals("t")) {
                msg = clientID + "|" + "The communication mode is set to TCP";
                
            } else {
                msg = clientID + "|" + "The communication mode is set to UDP";
            }
            sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            sendPacket.setData(sendData);
            sendPacket.setLength(sendData.length);
            try {
                udpServerSocket.send(sendPacket); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (tokens[0].equals("begin-loan")) {
            msg = clientID + "|" + beginLoan(tokens);
            sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            sendPacket.setData(sendData);
            sendPacket.setLength(sendData.length);
            try {
                udpServerSocket.send(sendPacket); 
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (tokens[0].equals("end-loan")) {
            msg = clientID + "|" + endLoan(tokens);
            sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            sendPacket.setData(sendData);
            sendPacket.setLength(sendData.length);
            try {
                udpServerSocket.send(sendPacket); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (tokens[0].equals("get-loans")) {
            ArrayList<String> loans = getLoans(tokens);
            String joined = String.join("|", loans);
            msg = clientID + "|" + joined;
            sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            sendPacket.setData(sendData);
            sendPacket.setLength(sendData.length);
            try {
                udpServerSocket.send(sendPacket); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (tokens[0].equals("get-inventory")) {
            ArrayList<String> inventoryList = getInventory();
            String joined = String.join("|", inventoryList);
            msg = clientID + "|" + joined;
            sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            sendPacket.setData(sendData);
            sendPacket.setLength(sendData.length);
            try {
                udpServerSocket.send(sendPacket); 
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } else if (tokens[0].equals("exit")) {
            ArrayList<String> inventoryList = getInventory();
            try {
                PrintWriter inventoryFile = new PrintWriter("inventory.txt");
                for (String book : inventoryList){
                    inventoryFile.println(book);
                }
            inventoryFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    
        } else {
            System.out.println("ERROR: No such command " + tokens[0]);
        }
    }
    
    private synchronized static String beginLoan(String[] tokens){
        String msg;    //Empty String
        // If Library has book
        if (inventory.containsKey(tokens[2])){
            // If Library has a copy not checked out
            if (inventory.get(tokens[2]) > 0){
                inventory.compute(tokens[2], (key, value) -> value -1);     // Decrement quantity of book
                //If User does not have any previous loans make new entry in loans
                if(!userLoans.containsKey(tokens[1])){                      
                    userLoans.put(tokens[1], new LinkedHashMap<>());
                }
                userLoans.get(tokens[1]).put(loanID,  tokens[2]);     // Add loan to User's list
                loanAndUser.put(loanID, tokens[1]);
                msg = "Your request has been approved" + " " + loanID++ + " " + tokens[1] + " " + tokens[2];
            // All copies have been checked out
            } else {
                msg = "Request Failed - Book not available";
            }
        // Library does not have book
        } else {
            msg = "Request Failed - We do not have this book";
        }
        return msg;
    }

    private synchronized static String endLoan(String[] tokens){
        String msg;
        int loan = Integer.parseInt(tokens[1]);                     // Passed LoanID
        // If passed loanID is present
        if (loanAndUser.containsKey(loan)){
            String user = loanAndUser.get(loan);                    // User that has the loan
            String book = userLoans.get(user).get(loan);            // Bookname the loan refers to
            userLoans.get(user).remove(loan);                       // Remove loan from list of user's loans
            loanAndUser.remove(loan);                               // Remove loan from list of all loans
            inventory.compute(book, (key, value) -> value + 1);     // Increment quantity of book
            msg = loan + " is returned";
        // If passed loanID is not present
        } else {
            msg = tokens[1] + " not found, no such borrow record";
        }
        return msg;
    }

    private synchronized static ArrayList<String> getLoans(String[] tokens){
        ArrayList<String> loans = new ArrayList<>();
        String user = tokens[1];
        if (!userLoans.containsKey(user) || userLoans.get(user).isEmpty()){
            loans.add("No record found for " + user);
        } else {
            for (Map.Entry <Integer, String> entry : userLoans.get(user).entrySet()){
                loans.add(entry.getKey() + " " + entry.getValue());
            }
        }
        return loans;
    }

    private synchronized static ArrayList<String> getInventory(){
        ArrayList<String> inventoryList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : inventory.entrySet()){
            inventoryList.add(entry.getKey() + " " + entry.getValue());
        }
        return inventoryList;
    }
}