package common;

import java.io.Serializable;
import java.util.List;

public class RoomInfo implements Serializable {

    private int roomNumber;
    private List<String> occupants;
    private String wardenName;
    private String wardenContact;

    public RoomInfo(int roomNumber, List<String> occupants,
                    String wardenName, String wardenContact) {
        this.roomNumber = roomNumber;
        this.occupants = occupants;
        this.wardenName = wardenName;
        this.wardenContact = wardenContact;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public List<String> getOccupants() {
        return occupants;
    }

    public String getWardenName() {
        return wardenName;
    }

    public String getWardenContact() {
        return wardenContact;
    }
}
