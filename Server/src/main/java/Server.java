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
    
    class ListeningThread extends Thread {
        public void run(){
            listentoIncomingRequests();
        }
    }

    int portnum = 2076;
    String fi_path = "C:\\Users\\mike\\Documents\\SChool\\CS472\\serverlog.txt";
    ServerSocket serverSocket;
    HashMap<String, String> map = new HashMap<>();
    PrintStream pos;
    
    public Server(String logFileName, String portnum) {
        this.portnum = portnum
        this.fi_path = logFileName; 
        try {
            pos = new PrintStream(new BufferedOutputStream(new FileOutputStream(fi_path, true), 1024), true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        // String path = "";//"C:\\Users\\mike\\Documents\\SChool\\CS472\\serverconfig.txt";
        // readConfigFile(path);
        String port = "3333"; //map.get("port_mode");
        String pasv = "YES"; //map.get("pasv_mode");
        if (port.equals("YES") && pasv.equals("YES")){
            pos.println("PORT and PASV are enabled!");
        }else if (port.equals("YES") && pasv.equals("NO")) {
            pos.println("PORT is enabled!");
        }else if (port.equals("NO") && pasv.equals("YES")) {
            pos.println("PORT and PASV are disabled!");
        }else /*if (port.equals("NO") && pasv.equals("NO"))*/ {
            pos.println("Error: PORT && PASV != NO");
            System.exit(-1);
        }
        
        try {
            serverSocket = new ServerSocket(portnum);
            
            /*Thread th = (new Thread(){public void run(){ListentoIncomingRequests();}});
            th.start();*/
            Thread th = new ListeningThread();
            th.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                clients.add(new ClientInfo(clientSocket, currentPort++));
                Thread th = new ClientCommunicator(clients.get(clients.size()-1), pos);
                th.start();
            }catch(IOException ex) {

            }

            }
    }

    public static void main(String [] args){
        System.out.println("started server");
        // must check to see if atleast two arguments are passed in, per instructions
        String logFileName = args[0]; // This is required as the 1st perameter per instructions
        String portNum = args[1];
        Server s = new Server(logFileName, portNum);
    }
}
