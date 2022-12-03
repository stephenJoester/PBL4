package Class;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class analyzeLine {
    static String ip;
    static Vector<String> list;
    private String input;
    public analyzeLine(String input) {
        this.input = input;
    }
    public static void main(String[] args) {
        
    }
    public Vector<String> getList() {
        // local host address
        // try(final DatagramSocket socket = new DatagramSocket()) {
        //     socket.connect(new InetSocketAddress("google.com", 80));
        //     ip = socket.getLocalAddress().toString();
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }
        // System.out.println("HOME_NET = " + ip);


        // String input = "11/01-22:44:50.185450  [**] [1:10001:1] ICMP Ping Detected [**] [Priority: 0] {ICMP} 192.168.1.7 -> 192.168.1.25"; // icmp 
        // String input = "11/02-07:18:37.941044  [**] [1:10002:1] UDP DDOS [**] [Priority: 0] {UDP} 192.168.40.2:53 -> 192.168.40.128:35534"; // udp

        list = new Vector<>();
        boolean foundTime = false;
        int bracketCount = 0;
        for (int i=0;i<input.length();i++) {
            // Tim timestamp
            if (input.charAt(i) == '.' && !foundTime) {
                String temp = input.substring(0, i);
                list.add(temp);
                foundTime = true;
            }

            // Tim msg
            if (input.charAt(i) == ']' && bracketCount < 2) {
                bracketCount++;
            }
            if (bracketCount == 2) {
                for (int j=i;j<input.length();j++) {
                    if (input.charAt(j) == '[') {
                        String temp = input.substring(i+2, j);
                        list.add(temp);
                        break;
                    }
                }
                bracketCount++;
            }
            
            // Tim protocol
            if (input.charAt(i) == '{') {
                for (int j=i;j<input.length();j++) {
                    if (input.charAt(j) == '}') {
                        String temp = input.substring(i+1, j);
                        list.add(temp);
                        break;
                    }
                }
            }
        }

        // Tim source
        String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        String inputNew = "";
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String temp = matcher.group();
            list.add(temp);
            inputNew = input.replaceFirst(temp, "");
        } 
        // System.out.println(inputNew);
        // Tim destination
        matcher = pattern.matcher(inputNew);
        if (matcher.find()) {
            String temp = matcher.group();
            list.add(temp);
        }

        // in ra list
        // for (String t : list) {
        //     System.out.println(t);
        // }
        return list;
    }
}
