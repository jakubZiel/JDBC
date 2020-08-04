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
    final private int PortNumb = 6969;
    final private String ipAddress;
    private boolean StopApp = false;
    public User myUser;
    public String UserRequest;
    public ArrayList<String> requestResource = new ArrayList<>();

    public Client(String ip) {

        ipAddress = new String(ip);
        executeClient();
    }

    //this method run everything

    public  void  executeClient(){

        connectToServerSocket();
        Scanner sc = new Scanner(System.in);

        while(!StopApp) {

            setUserRequest("select * from branch");

            sendRequestToHandler();

            decideAboutTypeOfHandler();

            StopApp = sc.nextBoolean();
        }

        TellHandlerToEndConnection();
        closeConnection();

    }

    //major methods

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

    public void setUserRequest(String userRequest) {
        UserRequest = userRequest;
    }

    public void sendRequestToHandler(){
        try {
            dataOutputS.writeUTF(UserRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decideAboutTypeOfHandler(){

        String requestId = UserRequest.substring(0,6);

        if(requestId.equals("select")) {
            getResourceFromHandlerGet();
            printReceivedResource();
            clearBufferedResource();
        }else if(requestId.equals("delete") || requestId.equals("update") || requestId.equals("insert")) {
            getResourceFromHandlerUpdate();
        }else System.out.println("this type of operation hasn't been implemented yet");
        System.out.println("Do you want to stop the app?");

    }
    //TODO
    public void TellHandlerToEndConnection(){

    }

    //worker methods

    public  void getResourceFromHandlerGet(){

        String buffer;
        try {
            while(!(buffer = dataInputS.readUTF()).equals("EndOfTransmission"))
                requestResource.add(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printReceivedResource(){
        for(String s : requestResource) System.out.println(s);
    }

    public  void clearBufferedResource(){
        requestResource.clear();
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

    public  void getResourceFromHandlerUpdate(){
        String buffer;

        try {
            while (!(buffer = dataInputS.readUTF()).equals("EndOfTransmission")) System.out.println(buffer);
        } catch(IOException e){
            e.printStackTrace();
        }

    }



    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int i = sc.nextInt();

        Client cl = new Client("127.0.0.1");

    }
}
