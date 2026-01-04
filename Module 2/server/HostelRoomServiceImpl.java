package server;

import common.HostelRoomService;
import common.RoomInfo;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.*;

public class HostelRoomServiceImpl extends UnicastRemoteObject
        implements HostelRoomService {

    private Map<Integer, RoomInfo> roomDatabase;

    protected HostelRoomServiceImpl() throws RemoteException {
        roomDatabase = new HashMap<>();

        roomDatabase.put(104,
                new RoomInfo(104,
                        Arrays.asList("Shreya", "Padma Deepika", "J Leena Sai Sri", "Santhosshi M"),
                        "Prema Eeswaran",
                        "9876543210"));

        roomDatabase.put(118,
                new RoomInfo(118,
                        Arrays.asList("Kanishtika K", "P U Graandhikaa Sri", "Naishadha", "TS Divija"),
                        "Prema Eeswaran",
                        "9876543210"));

        roomDatabase.put(207,
                new RoomInfo(207,
                        Arrays.asList("Sanjit Rao S Chavan", "Harigaran", "Vijay N", "Sarvesh"),
                        "K Kumar",
                        "9326542630"));
    }

    @Override
    public RoomInfo getRoomDetails(int roomNumber) throws RemoteException {
        return roomDatabase.get(roomNumber);
    }

    @Override
    public String getWardenContact(int roomNumber) throws RemoteException {
        RoomInfo room = roomDatabase.get(roomNumber);
        return (room != null) ? room.getWardenContact() : "Not Found";
    }
}
