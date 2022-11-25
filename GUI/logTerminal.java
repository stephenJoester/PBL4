// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.io.OutputStream;
// import java.io.OutputStreamWriter;
// import java.util.Arrays;
// import java.util.Scanner;

// public class logTerminal {
  
//     public logTerminal()  throws Exception {        
        
//         ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","sudo snort -q -l /var/log/snort/ -i ens33 -A console -c /etc/snort/snort.conf");
//         builder.inheritIO();
//         Process proc = builder.start();
        
//         // Read the output
//         OutputStreamWriter output = new OutputStreamWriter(proc.getOutputStream());

//         BufferedReader lineReader =  
//               new BufferedReader(new InputStreamReader(proc.getInputStream()));
        

//         String line = null;
//         while((line = lineReader.readLine()) != null) {
//             System.out.print(line + "\n");
//         }

//         proc.waitFor(); 
//     }
// }

