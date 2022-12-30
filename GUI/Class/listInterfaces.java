package Class;

import java.net.NetworkInterface;
import java.util.*;

public class listInterfaces {
    private Vector<interfaceItem> listInterface;

    public listInterfaces() {
        listInterface = new Vector<interfaceItem>();
        Enumeration<NetworkInterface> n = null;
        try {
            // get all interfaces
            n = NetworkInterface.getNetworkInterfaces();
            
            for ( ; n.hasMoreElements(); ) {
                NetworkInterface e = (NetworkInterface) n.nextElement();
                interfaceItem item = new interfaceItem(e);
                listInterface.add(item);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    } 

    public Vector<interfaceItem> getList() {
        return listInterface;
    }

}

