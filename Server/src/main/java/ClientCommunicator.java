import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileNotFoundException;

class ClientCommunicator extends Thread {
    ClientInfo clientinfo;
    PrintWriter out;
    BufferedReader in;
    PrintStream pos;
    HashMap<String, String> map = new HashMap<>();

    public ClientCommunicator(ClientInfo clientinfo, PrintStream pos){
        this.clientinfo = clientinfo;
        this.pos = pos;
        this.out = out;
        this.in = in;

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

    public void startClientCommunicatorThread(){
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

    public void run(){
        startClientCommunicatorThread();
    }
}
