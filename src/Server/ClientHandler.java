package Server;

import DataBase.DataBaseGetterClientSide;
import DataBase.Lock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;

public class ClientHandler extends Thread {

    DataInputStream dataInputS;
    DataOutputStream dataOutputS;
    Socket connectionSocket;
    DataBaseGetterClientSide DataBaseRef;
    Lock lock;
    String SqlRequest;
    ResultSet resultSet;
    String exceptionMessage;
    boolean HandlerOpen = true;

    //this method runs everything

    public void run(){

        while(HandlerOpen) {
            getClientsRequest();

            String RequestId = SqlRequest.substring(0,6);
            RequestId.toLowerCase();

            if(RequestId.equals("select")) {
                CHandlerGet CHGet = new CHandlerGet(dataInputS, dataOutputS, connectionSocket, DataBaseRef, lock, SqlRequest);
                CHGet.executeHandler();
            }else if(RequestId.equals("delete") || RequestId.equals("update") || RequestId.equals("insert")){
                CHandlerUpdate CHUpdate = new CHandlerUpdate(dataInputS, dataOutputS, connectionSocket, DataBaseRef, lock, SqlRequest);
                CHUpdate.executeHandler();
            }
            System.gc();
            ClientEndsConnection();
        }

        closeConnection();
    }

    public ClientHandler(DataInputStream dis, DataOutputStream dos, Socket connectionSocketPara, Lock cLock, DataBaseGetterClientSide DataBase){
        dataInputS = dis;
        dataOutputS = dos;
        connectionSocket = connectionSocketPara;
        lock = cLock;
        DataBaseRef = DataBase;
    }

    //major methods

    public void getClientsRequest(){

            try {
                synchronized (connectionSocket) {
                    SqlRequest = dataInputS.readUTF();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void closeConnection(){
        try {
            dataInputS.close();
            dataOutputS.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //TODO
    public void ClientEndsConnection(){

    }

    //worker methods

    public void sleepFor2000ms() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

    }

    //TODO terminate client handler thread and close socket as soon as client disconnects

}
