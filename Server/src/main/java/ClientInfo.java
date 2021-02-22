import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class ClientInfo {
    Socket client;
    String state; // maintain state info
    int port;
    PrintWriter out;
    BufferedReader in;
    public ClientInfo(Socket client, int port) {
        this.client = client;
        this.port = port;
        System.out.println("new connection from client");
        System.out.println(client.getInetAddress());
    }
}
