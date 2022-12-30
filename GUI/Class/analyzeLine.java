package Class;

import java.util.Vector;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

public class analyzeLine {
    static String ip;
    static Vector<String> list;
    private String input;
    public analyzeLine(String input) {
        this.input = input;
    }
    // public static void main(String[] args) {
        
    // }
    public Vector<String> getList() {


        list = new Vector<>();
        boolean foundTime = false;
        int bracketCount = 0;
        String input2 = "";

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
                        input2 = input.substring(j+1, input.length());
                        break;
                    }
                }
            }
        }
        // Tim source
        // String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        // String inputNew = "";
        // Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        // Matcher matcher = pattern.matcher(input);
        // if (matcher.find()) {
        //     String temp = matcher.group();
        //     list.add(temp);
        //     inputNew = input.replaceFirst(temp, "");
        // } 
        String[] words = input2.split(" ");
        list.add(words[1]);
        list.add(words[3]);
        
        // System.out.println(inputNew);
        // Tim destination
        // matcher = pattern.matcher(inputNew);
        // if (matcher.find()) {
        //     String temp = matcher.group();
        //     list.add(temp);
        // }
        

        // in ra list
        // for (String t : list) {
        //     System.out.println(t);
        // }
        return list;
    }
}
