package Server;

import DataBase.DataBaseGetterClientSide;
import DataBase.Lock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;

public class CHandlerUpdate {

    DataInputStream dataInputS;
    DataOutputStream dataOutputS;
    Socket connectionSocket;
    DataBaseGetterClientSide DataBaseRef;
    Lock lock;
    String SqlRequest;

    String exceptionMessage = null;

    public CHandlerUpdate(DataInputStream dataInputS, DataOutputStream dataOutputS, Socket connectionSocket, DataBaseGetterClientSide dataBaseRef, Lock lock, String request) {
        this.dataInputS = dataInputS;
        this.dataOutputS = dataOutputS;
        this.connectionSocket = connectionSocket;
        DataBaseRef = dataBaseRef;
        this.lock = lock;
        this.SqlRequest = request;
    }

    //this method run everything

    public void executeHandler(){ //CHandlerGet has acquired SQL request from ClientHandler
            updateDataBase();
            sendFeedbackToClient();
    }

    //major methods

    public void updateDataBase(){
        try {
            Statement statement = DataBaseRef.connectionToDataBase.createStatement();
            statement.executeUpdate(SqlRequest);
        } catch (SQLException e) {
            e.printStackTrace();
            exceptionMessage =  e.getMessage();
        }
    }

    public void sendFeedbackToClient(){
        try {
            synchronized (connectionSocket) {
                if(exceptionMessage != null)dataOutputS.writeUTF(exceptionMessage);
                else dataOutputS.writeUTF("Operation succeeded");
                dataOutputS.writeUTF("EndOfTransmission");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
