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

    //this method runs everything

    public ServerManager(int port){
        setServerToListenToPort(port);
        assignClientHandlersToClients();
        closeServerManager();
    }

    //major methods

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

                CreateClientHandler();

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

    public  void closeServerManager(){
        try {
            connectionSocket.close();
            DataBase.closeConnectionToDataBase();
            socketHearing.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //worker methods

    public void CreateClientHandler(){

        try {
            System.out.println("Client connected to the server application :" + connectedClients);
            DataInputStream dis = new DataInputStream(connectionSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(connectionSocket.getOutputStream());

            ClientHandler cH = new ClientHandler(dis, dos, connectionSocket, CommonLock, DataBase);
            cH.start();
            allConnectedClients.add(cH);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void checkStatesOfCHandlers(){
        int i = 0;

        while(i < allConnectedClients.size()){

            if(allConnectedClients.get(i).getState() == Thread.State.TERMINATED) {
                allConnectedClients.remove(i);
                connectedClients--;
                System.out.println("Waiting  for Clients to connect. Currently connected : " + connectedClients);
             }else i++;
        }

    }

    public static void main(String[] args) {

        ServerManager serverManager = new ServerManager(6969);
    }

}
