package Server;

import DataBase.DataBaseGetterClientSide;
import DataBase.Lock;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ClientHandler extends Thread {

    DataInputStream dataInputS;
    DataOutputStream dataOutputS;
    Socket connectionSocket;
    DataBaseGetterClientSide DataBaseRef;
    Lock lock;
    String SqlRequest;
    ResultSet resultSet;

    //this method run everything

    public void run(){
            getClientsRequest();
            askDataBase();
            sendResultToClient();
            closeConnection();
    }

    //worker methods

    public ClientHandler(DataInputStream dis, DataOutputStream dos, Socket connectionSocketPara, Lock cLock, DataBaseGetterClientSide DataBase){
        dataInputS = dis;
        dataOutputS = dos;
        connectionSocket = connectionSocketPara;
        lock = cLock;
        DataBaseRef = DataBase;
    }

    public void sleepFor2000ms() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

    }

    public void getClientsRequest(){

        try {
            SqlRequest = dataInputS.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void askDataBase(){
        try {
            Statement statement = DataBaseRef.connectionToDataBase.createStatement();
            resultSet = statement.executeQuery(SqlRequest);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendResultToClient(){

        StringBuffer rowInformation = new StringBuffer();

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columns = metaData.getColumnCount();

            while(resultSet.next()){

                for(int i = 1 ; i <= columns ; i++){
                    rowInformation.append(resultSet.getString(i));
                    rowInformation.append("####");
                }
                dataOutputS.writeUTF(new String(rowInformation));
            }

            dataOutputS.writeUTF("EndOfTransmission");

        } catch (SQLException | IOException e) {
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
}
