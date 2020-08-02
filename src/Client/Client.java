package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private Socket connectionSocket = null;
    private DataInputStream dataInputS;
    private DataOutputStream dataOutputS;
    private int PortNumb = 5055;
    private String ipAddress;
    private boolean StopApp = false;
    public User myUser;
    public String UserRequest;
    public ArrayList<String> requestResource = new ArrayList<>();

    //this function run everything

    public Client(String ip){

        ipAddress = new String(ip);
        connectToServerSocket();
        Scanner sc = new Scanner(System.in);

        while(!StopApp) {

            setUserRequest("select * from branch");
            sendRequestToHandler();
            getResourceFromHandler();

            StopApp = sc.nextBoolean();
        }
        closeConnection();
    }

    //worker methods

    public  void connectToServerSocket(){

        //connect to local host to correct port number
        try {
            connectionSocket = new Socket(ipAddress, PortNumb);

            dataInputS = new DataInputStream(connectionSocket.getInputStream());
            dataOutputS = new DataOutputStream(connectionSocket.getOutputStream());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequestToHandler(){
        try {
            dataOutputS.writeUTF(UserRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void getResourceFromHandler(){

        String buffer;
        try {
            while(!(buffer = dataInputS.readUTF()).equals("EndOfTransmission"))
                requestResource.add(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            dataOutputS.close();
            dataInputS.close();
            connectionSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setUserRequest(String userRequest) {
        UserRequest = userRequest;
    }


    public static void main(String[] args) {
        Client cl = new Client("127.0.0.1");
    }
}