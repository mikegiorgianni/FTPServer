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
 */
public class Server {
    
    class ListeningThread extends Thread {
        public void run(){
            listentoIncomingRequests();
        }
    }

    class ClientCommunicator extends Thread {
        ClientInfo clientinfo;
        PrintWriter out;
        BufferedReader in;
        public ClientCommunicator(ClientInfo clientinfo){
            this.clientinfo = clientinfo;
            try {
                out = new PrintWriter(clientinfo.client.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in = new BufferedReader(
                        new InputStreamReader(clientinfo.client.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.println("Welcome!!");
            pos.println("Welcome!!");
            out.println("Port=" + clientinfo.port);
            pos.println("Port=" + clientinfo.port);
        }
        public void run(){
            startClientCommunicatorThread(clientinfo, in, out);
        }
    }

    int portnum = 2076;
    ServerSocket serverSocket;
    HashMap<String, String> map = new HashMap<>();
    PrintStream pos;
    
    public Server() {
        String fi_path = "C:\\Users\\mike\\Documents\\SChool\\CS472\\serverlog.txt";
        try {
            pos = new PrintStream(new BufferedOutputStream(new FileOutputStream(fi_path, true), 1024), true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        String path = "C:\\Users\\mike\\Documents\\SChool\\CS472\\serverconfig.txt";
        readConfigFile(path);
        String port = map.get("port_mode");
        String pasv = map.get("pasv_mode");
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
    class ClientInfo {
        Socket client;
        int port;
        PrintWriter out;
        BufferedReader in;
        public ClientInfo(Socket client, int port) {
            this.client = client;
            this.port = port;
        }
        
    }
    
    ArrayList<ClientInfo> clients = new ArrayList<>();
    int currentPort = 4000;
    
    public void listentoIncomingRequests() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                clients.add(new ClientInfo(clientSocket, currentPort++));
                Thread th = new ClientCommunicator(clients.get(clients.size()-1));
                th.start();
            }catch(IOException ex) {

            }

            }

    }

    public void startClientCommunicatorThread(ClientInfo clientinfo, BufferedReader in, PrintWriter out){
        while(true){
            String str = "";
            String delims = "[ ]+";
            try {
                str = in.readLine();
                String[] tokens = str.split(delims);
                if (tokens.length == 2) {
                    out.println(str);
                    pos.println(str);
                    try {
                            FileWriter fi = new FileWriter("Userfile.txt");
                            String user = tokens[1];
                            out.println("Please enter a password.");
                            pos.println("Please enter a password.");
                            String password = in.readLine();
                            String userpass = user.concat(password);
                            String credentials = userpass.concat("\r\n");
                            fi.write(credentials);
                            fi.close();
                            out.println("Your credentials were stored!");
                            pos.println("Your credentials were stored!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                } else {
                    if (tokens[0].equals("USER")) {
                        try {
                            out.println("Please enter a username. ");
                            pos.println("Please enter a username. ");
                            FileWriter fi = new FileWriter("Userfile.txt");
                            String user = in.readLine();
                            out.println("Please enter a password.");
                            pos.println("Please enter a password.");
                            String password = in.readLine();
                            String userpass = user.concat(" " + password);
                            String credentials = userpass.concat("\r\n");
                            fi.write(credentials);
                            fi.close();
                            out.println("Your credentials were stored!");
                            pos.println("Your credentials were stored!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    //port if statement
                    } else if (tokens[0].equals("PORT")) {
                            String path = "C:\\Users\\mike\\Documents\\SChool\\CS472\\serverconfig.txt";
                            readConfigFile(path);
                            String port = map.get("port_mode");
                            if (port.equals("YES")) {
                                out.println("PORT is enabled!");
                                pos.println("PORT is enabled!");
                            } else if (port.equals("NO")) {
                                out.println("PORT is disabled!");
                                pos.println("PORT is disabled!");
                            }
                        //pasv if statement
                        } else if (tokens[0].equals("PASV")) {
                            String path = "C:\\Users\\mike\\Documents\\SChool\\CS472\\serverconfig.txt";
                            readConfigFile(path);
                            String pasv = map.get("pasv_mode");
                            if (pasv.equals("YES")) {
                                out.println("PASV is enabled!");
                                pos.println("PASV is enabled!");
                            } else if (pasv.equals("NO")) {
                                out.println("PASV is disabled!");
                                pos.println("PASV is disabled!");
                            }
                        }
                    }
                } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
                

            //out.println(str);
//            switch (state){
//                case 1:
//                    break;
//                default:
//                    //This is unrecognized command
//                    break;
//            }
        }
    }
    public static void main(String [] args){
        Server s = new Server();

    }
}
