package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseGetterClientSide {

    public Connection connectionToDataBase;

    public DataBaseGetterClientSide(){

        setConnectionToDataBase();
    }

    //major methods

    public void setConnectionToDataBase(){

        String host = "jdbc:mysql://localhost:3306/mybase?useSSL=false";
        String uName = "root";
        String uPass = "Jakub.123@!o0";

        try {
            connectionToDataBase = DriverManager.getConnection(host, uName, uPass);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void closeConnectionToDataBase(){
        try {
            connectionToDataBase.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //worker methods

    public Connection getConnectionToDataBase(){
        return connectionToDataBase;
    }

}
