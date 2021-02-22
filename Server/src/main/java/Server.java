/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.NumberFormatException;
import Documents.GitHub.FTPServer.Server.src.main.java.ClientInfo;
import Documents.GitHub.FTPServer.Server.src.main.java.ClientCommunicator;

/**
 *
 * @author mike
 * 1) InputStream
 * 2) InputStreamReader
 * 3) BufferedReader
 * 
 * 
 */
public class Server {

    // class ListeningThread extends Thread {
    //     public void run(){
    //         listentoIncomingRequests();
    //     }
    // }

    int portnum = 2076;
    String fi_path = "C:\\Users\\mike\\Documents\\SChool\\CS472\\serverlog.txt";
   // ServerSocket serverSocket;
    HashMap<String, String> map = new HashMap<>();
    PrintStream pos;

    public Server(String logFileName, int portnum) {
        this.portnum = portnum;
        this.fi_path = logFileName; 
        try {
            pos = new PrintStream(new BufferedOutputStream(new FileOutputStream(fi_path, true), 1024), true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        // String path = "";//"C:\\Users\\mike\\Documents\\SChool\\CS472\\serverconfig.txt";
        // readConfigFile(path);
        // String port = "3333"; //map.get("port_mode");
        // String pasv = "YES"; //map.get("pasv_mode");
        // if (port.equals("YES") && pasv.equals("YES")){
        //     pos.println("PORT and PASV are enabled!");
        // }else if (port.equals("YES") && pasv.equals("NO")) {
        //     pos.println("PORT is enabled!");
        // }else if (port.equals("NO") && pasv.equals("YES")) {
        //     pos.println("PORT and PASV are disabled!");
        // }else /*if (port.equals("NO") && pasv.equals("NO"))*/ {
        //     pos.println("Error: PORT && PASV != NO");
        //     System.exit(-1);
        // }
        
        // try {
        //     serverSocket = new ServerSocket(this.portnum);
            
        //     /*Thread th = (new Thread(){public void run(){ListentoIncomingRequests();}});
        //     th.start();*/
        //     Thread th = new ListeningThread();
        //     th.start();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        listentoIncomingRequests();
    }

    void readConfigFile(String fullfilepath) {
        String delims = "[=]";
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(fullfilepath)));
            while(true) {
                String str = br.readLine();
                if (str == null) {
                    break;
                }else{
                    String[] tokens = str.split(delims);
                    map.put(tokens[0].trim(), tokens[1].trim());
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    ArrayList<ClientInfo> clients = new ArrayList<>();
    int currentPort = 4000;

    public void listentoIncomingRequests() {
        try (ServerSocket serverSocket = new ServerSocket(this.portnum)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientInfo client = new ClientInfo(clientSocket, currentPort++);
                clients.add(client);
                ClientCommunicator clientThread = new ClientCommunicator(client, pos);
                clientThread.start();
            }
        } catch(IOException ex) {
            System.out.println(ex);
        }
    }

    public static void main(String [] args){
        if (!Server.hasValidArgs(args)) {
            System.out.println("You must provide command line arguments: LogFileName and PortNumber");
        } else if (Server.getPortNum(args[1]) < 0) {
            System.out.println("PortNumber must be a valid integer");
        } else {
            // Might need to check filename?
            String logFileName = args[0];
            int portNum = Server.getPortNum(args[1]);
            System.out.println("started server");
            Server s = new Server(logFileName, portNum);
        }
    }

    public static boolean hasValidArgs(String [] args) {
        // must check to see if atleast two arguments are passed in, per instructions
        if (args.length < 2) {
            return false;
        }
        return true;
    }

    public static int getPortNum(String port) {
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
