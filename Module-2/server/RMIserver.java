package server;

import common.HostelRoomService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIserver {

    public static void main(String[] args) {
        try {
            HostelRoomService service = new HostelRoomServiceImpl();

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("HostelRoomService", service);

            System.out.println("RMI Server started...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
