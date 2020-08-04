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

import static java.lang.Thread.sleep;

public class CHandlerGet {


    DataInputStream dataInputS;
    DataOutputStream dataOutputS;
    Socket connectionSocket;
    DataBaseGetterClientSide DataBaseRef;
    Lock lock;
    String SqlRequest;
    ResultSet resultSet;
    String exceptionMessage;

    public CHandlerGet(DataInputStream dataInputS, DataOutputStream dataOutputS, Socket connectionSocket, DataBaseGetterClientSide dataBaseRef, Lock lock, String request) {
        this.dataInputS = dataInputS;
        this.dataOutputS = dataOutputS;
        this.connectionSocket = connectionSocket;
        DataBaseRef = dataBaseRef;
        this.lock = lock;
        this.SqlRequest = request;
    }

    //this method runs everything

    public void executeHandler(){   //CHandlerGet has acquired SQL request from ClientHandler
            askDataBase();
            sendResultToClient();
    }

    //major methods

    public void askDataBase(){
        try {
            synchronized (DataBaseRef) {
                Statement statement = DataBaseRef.connectionToDataBase.createStatement();
                resultSet = statement.executeQuery(SqlRequest);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            exceptionMessage = e.getMessage();
            resultSet = null;
        }
    }

    public void sendResultToClient(){

        try {
            synchronized (connectionSocket) {

                if(resultSet != null) sendDataThroughSocket();    //throws SQLException, IOException
                else dataOutputS.writeUTF(exceptionMessage);

                dataOutputS.writeUTF("EndOfTransmission");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    //worker methods

    public void sendDataThroughSocket() throws SQLException, IOException {

        StringBuffer rowInformation = new StringBuffer();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columns = metaData.getColumnCount();

        while (resultSet.next()) {

            for (int i = 1; i <= columns; i++) {
                rowInformation.append(resultSet.getString(i));
                rowInformation.append("###");
            }
            dataOutputS.writeUTF(new String(rowInformation));
            rowInformation.delete(0, rowInformation.length());
        }

    }

    public void sleepFor2000ms() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

    }


}
