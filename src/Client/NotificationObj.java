package Client;

import java.util.UUID;

public class NotificationObj extends Thread{

    public UUID id;

    public NotificationObj() {
        this.id = UUID.randomUUID();
    }

    @Override
    public void run() {

    }
}
