package DataBase;

import java.util.Objects;

public class Lock{

    private int id;
    public Lock(int iD ){
        this.id = iD;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        else return false;
    }

}