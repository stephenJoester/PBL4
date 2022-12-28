package Class;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DecimalFormat;

public class sensor {
    private DecimalFormat df = new DecimalFormat("#.##");
    private double cpu_utilized;
    private double totalMemory;
    private double availableMemory;
    private double usedDisk;
    private String usedDiskPercentage;
    private String osVer;
    private String snortVer;
    public static String formatFileSize(double size, String type) {
        String hrSize = null;
    
        double k = size;
        double m = size/1024.0;
        double g = ((size/1024.0)/1024.0);
        DecimalFormat dec = new DecimalFormat("0.00");
        if (type.equals("k")) {
            hrSize = dec.format(k).concat(" KB");
        }
        if (type.equals("m")) {
            hrSize = dec.format(m).concat(" MB");
        }
        if (type.equals("g")) {
            hrSize = dec.format(g).concat(" GB");
        }
    
        return hrSize;
    }
    public sensor () {
        CPU();
        Memory();
        Disk();
        OS();
        Snort();
    }
    public void CPU() {
        try {
            Process proc = new ProcessBuilder("/bin/bash","-c","iostat").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String output;
            Boolean found = false;
            while ((output=reader.readLine())!=null) {
                if (found) {
                    found = false;
                    String[] words = output.split("\\s+");
                    double idle_percentage = Double.parseDouble(words[6].replace(',', '.'));
                    cpu_utilized = 100 - idle_percentage;
                    cpu_utilized = Double.parseDouble(df.format(cpu_utilized));
                    // System.out.println(cpu_utilized);
                    break;
                }
                if (output.contains("avg-cpu")) {
                    found = true;
                }
            }
            proc.waitFor();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void Memory() {
        try {
            Process proc = new ProcessBuilder("/bin/bash","-c","free").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String output;
            while ((output = reader.readLine())!=null) {
                if (output.contains("Mem")) {
                    String[] words = output.split("\\s+");
                    totalMemory = Double.parseDouble(words[1]);
                    availableMemory = Double.parseDouble(words[6]);
                    // System.out.println("Total: " + words[1]);
                    // System.out.println("Available: " + words[6]);
                }
            }
            proc.waitFor();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void Disk() {
        try {
            Process proc = new ProcessBuilder("/bin/bash","-c","df").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String output;
            while ((output = reader.readLine())!=null) {
                if (output.contains("/dev/sda3")) {
                    String[] words = output.split("\\s+");
                    usedDisk = Double.parseDouble(words[2]);    
                    usedDiskPercentage = words[4];
                }
            }
            proc.waitFor();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    } 
    public void speedTest() {
        try {
            Process proc = new ProcessBuilder("/bin/bash","-c","speedtest").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String output;
            // while (())
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void OS() {
        try {
            String[] args = new String[] {"/bin/bash", "-c", "lsb_release -r", "with", "args"};
            Process proc = new ProcessBuilder(args).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String output;
            while ((output = reader.readLine())!=null) {
                osVer = output;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void Snort() {
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("snort -V");
            Thread errorGobbler = new Thread(new StreamGobbler(proc.getErrorStream(), System.err));
            Thread outputGobbler = new Thread(new StreamGobbler(proc.getInputStream(), System.out));
            errorGobbler.start();
            outputGobbler.start();
            int exitVal = proc.waitFor();
            errorGobbler.join();
            outputGobbler.join();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public String getCPUUtilized() {
        return String.valueOf(cpu_utilized);
    }
    public String getMemoryUtilized() {
        double memoryUtilized = (totalMemory-availableMemory)/totalMemory * 100;
        return formatFileSize(totalMemory - availableMemory,"g") + " (" + df.format(memoryUtilized) + "%)";
    }
    public String getDiskUsage() {
        return formatFileSize(usedDisk, "g") + " (" + usedDiskPercentage + ")";
    }
    public String getOS() {
        return "Ubuntu " + osVer;
    }
    public String getSnortVer() {
        return snortVer;
    }
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
                    snortVer = words[4];
                }
            }
        } catch (IOException x) {
            // Handle error
        }
        }
    }
}

