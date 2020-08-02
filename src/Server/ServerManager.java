package Server;

import DataBase.DataBaseGetterClientSide;
import DataBase.Lock;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerManager {

    private ServerSocket socketHearing = null;
    private Socket connectionSocket = null;
    private final Lock CommonLock = new Lock(0);
    private ArrayList<ClientHandler> allConnectedClients = null;
    int connectedClients = 0;
    DataBaseGetterClientSide DataBase = new DataBaseGetterClientSide();
    boolean StopServer = false;

    public ServerManager(int port){

        setServerToListenToPort(port);
        assignClientHandlersToClients();
    }

    //server management
    public void setServerToListenToPort(int port){
        try {
            socketHearing = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assignClientHandlersToClients(){

        allConnectedClients = new ArrayList<>();

        //create Client Handler threads until all clients are connected
        while (!StopServer) {
            System.out.println("Waiting  for Clients to connect. Currently connected : " + connectedClients);

            try {
                checkStatesOfCHandlers();

                connectionSocket = socketHearing.accept();
                connectedClients++;

                System.out.println("Client connected to the server application :" + connectedClients);
                DataInputStream dis = new DataInputStream(connectionSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(connectionSocket.getOutputStream());

                allConnectedClients.add(new ClientHandler(dis, dos, connectionSocket, CommonLock, DataBase));

            } catch (IOException e) {
                try {
                    connectionSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        System.out.println("Server Application Terminated");
    }

    public void checkStatesOfCHandlers(){
        int i = 0;

        while(i < allConnectedClients.size()){

            if(allConnectedClients.get(i).getState() == Thread.State.TERMINATED) {
                allConnectedClients.remove(i);
                connectedClients--;
                System.out.println("Waiting  for Clients to connect. Currently connected : " + connectedClients);
             }
            if(allConnectedClients.get(i).getState() == Thread.State.NEW) {
                allConnectedClients.get(i).start();
                i++;
            }
        }

    }

    public static void main(String[] args) {

        ServerManager serverManager = new ServerManager(5055);
    }

}
