package Client;

import java.util.Scanner;
import java.util.UUID;

public class User {

    public enum STATUS{

        USER,ADMIN,GUEST;
    }

    public UUID id;
    public String surName;
    public String name;
    public final STATUS status;

    public User() {

        UserLogin();
        status = STATUS.USER;
    }

    public User(UUID id, String surName, String name ){
        this.id = id;
        this.surName = surName;
        this.name = name;
        this.status = STATUS.USER;;
    }

    public void UserLogin(){

        Scanner scanner = new Scanner(System.in);
        System.out.println("what is your surname and name ?");
        id = UUID.randomUUID();
        surName = scanner.next();
        name = scanner.next();

        scanner.close();
    }

}
