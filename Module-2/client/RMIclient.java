package client;

import common.HostelRoomService;
import common.RoomInfo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIclient {

    public static RoomInfo fetchRoomDetails(int roomNumber) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            HostelRoomService service =
                    (HostelRoomService) registry.lookup("HostelRoomService");

            return service.getRoomDetails(roomNumber);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
