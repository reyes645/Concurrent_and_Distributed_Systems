import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    int clientID;

    public TCPClient (String hostAddress, int tcpPort, int clientID) {
        try {
            socket = new Socket(hostAddress, tcpPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientID = clientID;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void send (String message) {
        out.println(clientID + "|" + message);
    }

    public void out (PrintWriter outfile) {
        try {
            String received;
            while((received = in.readLine()) != null){
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
}
