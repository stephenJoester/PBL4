import java.io.RandomAccessFile;

public class test {
    public static void main(String[] args) {
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","sudo chmod a+rw /etc/snort/snort.conf");
            Process proc = builder.start();
            proc.waitFor();

            RandomAccessFile raf = new RandomAccessFile("/etc/snort/snort.conf", "r");
			String line = raf.readLine();
			while (line != null) {
				if (line.contains("ipvar HOME_NET")) {
					System.out.println(line);

				}
				line = raf.readLine();
			}
			raf.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
