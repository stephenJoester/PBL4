import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

class StreamGobbler implements Runnable {
    private final InputStream is;
    private final PrintStream os;
   
    StreamGobbler(InputStream is, PrintStream os) {
      this.is = is;
      this.os = os;
    }
   
    public void run() {
        try {
            String output;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((output = reader.readLine()) != null) 
            {
                if (output.contains("Version")) {
                    String[] words = output.split("\\s+");
                    System.out.println(words[4]);
                }
            }
        } catch (IOException x) {
            // Handle error
        }
    }
  }
   
  public class test {
    public static void main(String[] args)
      throws IOException, InterruptedException {
   
      Runtime rt = Runtime.getRuntime();
      Process proc = rt.exec("snort -V");
   
      // Any error message?
      Thread errorGobbler
        = new Thread(new StreamGobbler(proc.getErrorStream(), System.err));
    
      // Any output?
      Thread outputGobbler
        = new Thread(new StreamGobbler(proc.getInputStream(), System.out));
   
      errorGobbler.start();
      outputGobbler.start();
   
      // Any error?
      int exitVal = proc.waitFor();
      errorGobbler.join();   // Handle condition where the
      outputGobbler.join();  // process ends before the threads finish
    }
  }