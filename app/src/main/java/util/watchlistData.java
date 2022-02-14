package util;

public class watchlistData {

    String ID;
    String name;
    int position;

    public watchlistData(String ID, String name, int position)
    {
        this.ID = ID;
        this.name = name;
        this.position = position;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
