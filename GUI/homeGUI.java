import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import Class.analyzeLine;
import Class.comboItem;
import Class.interfaceItem;
import Class.listInterfaces;

import javax.swing.border.EtchedBorder;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class homeGUI extends JFrame {

    private DefaultTableModel model;
    public static JTable table;
    public static JTextArea textArea;
    static int id;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)  throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					homeGUI home = new homeGUI();
                    // home.createTable();
				} catch (Exception e) {
                    e.printStackTrace();
				}
			}
		});
        
	}

	/**
	 * Create the frame.
	 */
	public homeGUI() {
		JFrame frame = new JFrame("Home");
        frame.getContentPane().setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100,100,757,610);

        model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Object[] columns = {"ID","Message","Timestamp","Source Address","Destination Address","Protocol"};
        model.setColumnIdentifiers(columns);
        table = new JTable();
        table.setRowHeight(30);
        table.setAutoCreateRowSorter(true);
        table.setModel(model);

        JScrollPane scrollPane1 = new JScrollPane(table);
        scrollPane1.setBounds(10,239,721,321);
        frame.getContentPane().add(scrollPane1);
        
        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        panel.setBounds(10, 11, 359, 196);
        frame.getContentPane().add(panel);
        panel.setLayout(null);
        
        JLabel lblNewLabel = new JLabel("Xem th\u00F4ng tin c\u00E1c Network Interface");
        lblNewLabel.setBounds(10, 11, 266, 14);
        panel.add(lblNewLabel);
        
        JLabel lblNewLabel_1 = new JLabel("Ch\u1ECDn interface :\r\n");
        lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNewLabel_1.setBounds(10, 46, 108, 14);
        panel.add(lblNewLabel_1);
        
        JComboBox cbbInterface = new JComboBox();
        cbbInterface.setBounds(128, 43, 132, 22);
        Vector<interfaceItem> list = new listInterfaces().getList();
        cbbInterface.addItem(new comboItem("Chọn", ""));
        for (int i=0;i<list.size();i++) {
            cbbInterface.addItem(new comboItem(list.elementAt(i).getName(),String.valueOf(i)));
        }


        panel.add(cbbInterface);
        
        JLabel lbIPv6 = new JLabel("IPv6 : ");
        lbIPv6.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbIPv6.setBounds(10, 84, 250, 14);
        panel.add(lbIPv6);
        
        JLabel lbIPv4 = new JLabel("IPv4 : ");
        lbIPv4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbIPv4.setBounds(10, 119, 250, 14);
        panel.add(lbIPv4);
        
        JButton btnConfig = new JButton("Config");
        // event listener
        btnConfig.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                config();
        	}
        });
        btnConfig.setBounds(10, 162, 89, 23);
        
        panel.add(btnConfig);
        
        JButton btnRun = new JButton("Run");
        btnRun.setBounds(454, 184, 89, 23);
        btnRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        frame.getContentPane().add(btnRun);
        
        JButton btnStop = new JButton("Stop");
        btnStop.setBounds(564, 184, 89, 23);
        frame.getContentPane().add(btnStop);

        Object[] row = new Object[6];
        row[0] = "ID test";
        row[1] = "message test";
        row[2] = "timestamp test";
        row[3] = "source test";
        row[4] = "destination test";
        row[5] = "protocol test";
        model.addRow(row);

        // item event combobox
        cbbInterface.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Object item = cbbInterface.getSelectedItem();
                    String value = ((comboItem)item).getValue();
                    lbIPv6.setText("IPv6 : " + list.elementAt(Integer.parseInt(value)).getIPv6());
                    lbIPv4.setText("IPv4 : " + list.elementAt(Integer.parseInt(value)).getIPv4());
                }
        	}
        });

        frame.setVisible(true);

        
	}
    public void createTable() throws Exception {
        id = 1;

        Thread t = new Thread() {
            public void run() {
                // processbuilder
                try {
                    ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","sudo snort -q -l /var/log/snort/ -i ens33 -A console -c /etc/snort/snort.conf");
                    Process proc = builder.start();

                    // Read the output

                    BufferedReader lineReader =  new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    String output;
                    while ((output = lineReader.readLine()) != null) {

                        if (output.contains("[**]")) {
                            analyzeLine al = new analyzeLine(output);
                            Vector<String> list = al.getList();
                            Object[] row = new Object[6];
                            row[0] = "#" + id; // id
                            row[1] = list.elementAt(1); // message
                            row[2] = list.elementAt(0); // timestamp
                            row[3] = list.elementAt(3); // source
                            row[4] = list.elementAt(4); // destination
                            row[5] = list.elementAt(2); // protocol
                            model.addRow(row);
                            table.setModel(model);
                            id++;
                        }
                        
                    }
                
                    proc.waitFor();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                

            }
        };
        t.start();
    }

    public void config() {
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","sudo vim /etc/snort/snort.conf");
            builder.inheritIO();
            Process proc = builder.start();
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

