package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HostelRoomService extends Remote {

    // Remote Method 1
    RoomInfo getRoomDetails(int roomNumber) throws RemoteException;

    // Remote Method 2
    String getWardenContact(int roomNumber) throws RemoteException;
}
