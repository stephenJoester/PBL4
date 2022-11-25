
package Class;
import java.net.*;
import java.util.Enumeration;

public class interfaceItem {
    private String name;
    private String ipv4;
    private String ipv6;
    
    public interfaceItem(NetworkInterface e) {
        this.name = e.getName();
        Enumeration a = e.getInetAddresses();
        int count = 0;
        for ( ; a.hasMoreElements(); count++) {
            InetAddress addr = (InetAddress) a.nextElement();
            if (count==0) {
                String temp = String.valueOf(addr);
                this.ipv6 = temp.substring(1,temp.length());
            }
            if (count == 1) {
                String temp = String.valueOf(addr);
                this.ipv4 = temp.substring(1,temp.length());
            }
        }
    }
    
    public String getName() {
        return name;
    }
    public String getIPv4() {
        return ipv4;
    }
    public String getIPv6() {
        return ipv6;
    }
}
