package View;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import Class.*;
import Class.Piechart.*;

import javax.swing.border.EtchedBorder;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class homeGUI extends JFrame {

    private DefaultTableModel model;
    public static JTable table;
    public static JTextArea textArea;
    static int id;
    static boolean isStarted;
    private Process proc;
    private PieChart piechart;
    private JPanel panelChart;
    private Vector<String> listProtocol = new Vector<String>();
    private Vector<String> listMessage = new Vector<String>();
    private ModelPieChart tcp, icmp, udp, ip, arp;
    private Thread alertThread;
    private TimerTask fileWatcher;
    private Timer timer;
    private int lineMarker;
	/**
	 * Launch the application.
	 */
	public static void createFrame()  throws Exception {
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
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setBounds(100,100,756,642);

        model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Object[] columns = {"ID","Message","Timestamp","Source Address","Destination Address","Protocol"};
        model.setColumnIdentifiers(columns);
        
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
        btnConfig.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnConfig.setBounds(10, 162, 89, 23);
        
        panel.add(btnConfig);
        
        JButton btnEditRules = new JButton("Edit Rules");
        
        btnEditRules.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnEditRules.setBounds(109, 162, 99, 23);
        panel.add(btnEditRules);
        
        JButton btnRun = new JButton("Run");
        btnRun.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnRun.setBounds(10, 218, 89, 23);
        
        frame.getContentPane().add(btnRun);
        
        JButton btnStop = new JButton("Stop");
        btnStop.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnStop.setEnabled(false);
        btnStop.setBounds(109, 218, 89, 23);
        
        frame.getContentPane().add(btnStop);
        
        // Panel chart
        panelChart = new JPanel();
        panelChart.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        panelChart.setBounds(392, 11, 339, 243);
        // init component 
        initiComponents();
        piechart.setChartType(PieChart.PeiChartType.DONUT_CHART);

        panelChart.add(piechart);
        frame.getContentPane().add(panelChart);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(10, 265, 721, 327);
        frame.getContentPane().add(tabbedPane);
        
        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.WHITE);
        tabbedPane.addTab("Monitoring", null, panel_1, null);
        panel_1.setLayout(null);
        
        JLabel lblNewLabel_2 = new JLabel("Trạng thái :");
        lblNewLabel_2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNewLabel_2.setBounds(32, 19, 95, 14);
        panel_1.add(lblNewLabel_2);
        
        JLabel lblNewLabel_2_1 = new JLabel("CPU : ");
        lblNewLabel_2_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNewLabel_2_1.setBounds(32, 57, 95, 14);
        panel_1.add(lblNewLabel_2_1);
        
        JLabel lblNewLabel_2_2 = new JLabel("Memory :");
        lblNewLabel_2_2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNewLabel_2_2.setBounds(32, 94, 95, 14);
        panel_1.add(lblNewLabel_2_2);
        
        JLabel lblNewLabel_2_3 = new JLabel("Disk :");
        lblNewLabel_2_3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNewLabel_2_3.setBounds(32, 126, 95, 14);
        panel_1.add(lblNewLabel_2_3);
        
        JLabel lblNewLabel_2_4 = new JLabel("Receiving : ");
        lblNewLabel_2_4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNewLabel_2_4.setBounds(32, 163, 95, 14);
        panel_1.add(lblNewLabel_2_4);
        
        JLabel lblNewLabel_2_4_1 = new JLabel("Sending :");
        lblNewLabel_2_4_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNewLabel_2_4_1.setBounds(32, 200, 95, 14);
        panel_1.add(lblNewLabel_2_4_1);
        
        JLabel lbState = new JLabel(".");
        lbState.setBounds(126, 20, 144, 14);
        panel_1.add(lbState);
        
        JLabel lbCPU = new JLabel(".");
        lbCPU.setBounds(126, 58, 144, 14);
        panel_1.add(lbCPU);
        
        JLabel lbMemory = new JLabel(".");
        lbMemory.setBounds(126, 95, 144, 14);
        panel_1.add(lbMemory);
        
        JLabel lbDisk = new JLabel(".");
        lbDisk.setBounds(126, 127, 144, 14);
        panel_1.add(lbDisk);
        
        JLabel lbReceiving = new JLabel(".");
        lbReceiving.setBounds(126, 164, 144, 14);
        panel_1.add(lbReceiving);
        
        JLabel lbSending = new JLabel(".");
        lbSending.setBounds(126, 201, 144, 14);
        panel_1.add(lbSending);
        
        JLabel lblNewLabel_2_5 = new JLabel("OS :");
        lblNewLabel_2_5.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNewLabel_2_5.setBounds(404, 20, 95, 14);
        panel_1.add(lblNewLabel_2_5);
        
        JLabel lblNewLabel_2_6 = new JLabel("Snort version : ");
        lblNewLabel_2_6.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNewLabel_2_6.setBounds(404, 58, 95, 14);
        panel_1.add(lblNewLabel_2_6);
        
        JLabel lbOS = new JLabel(".");
        lbOS.setBounds(509, 20, 160, 14);
        panel_1.add(lbOS);
        
        JLabel lbVersion = new JLabel(".");
        lbVersion.setBounds(509, 58, 144, 14);
        panel_1.add(lbVersion);
        
        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Alert", null, panel_2, null);
        panel_2.setLayout(null);
        table = new JTable();
        table.setRowHeight(30);
        table.setAutoCreateRowSorter(true);
        table.setModel(model);
        
        JScrollPane scrollPane1 = new JScrollPane(table);
        scrollPane1.setBounds(0, 52, 716, 247);
        panel_2.add(scrollPane1);
        
        JButton btnDetail = new JButton("Detail");
        btnDetail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnDetail.setBounds(531, 18, 89, 23);
        panel_2.add(btnDetail);
        
        // component listener - scroll to the bottom of table
        table.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                table.scrollRectToVisible(table.getCellRect(table.getRowCount()-1, 0, true));
            }
        });

        initTable();
    
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

        // Start process 
        btnRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    isStarted = true;
                    snort();
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                    
                    Thread t1 = new Thread() {
                        public void run() {
                            sensor s = new sensor();
                            String cpu_utilized = s.getCPUUtilized();
                            lbCPU.setText(cpu_utilized + "%");
                            lbMemory.setText(s.getMemoryUtilized());
                            lbDisk.setText(s.getDiskUsage());
                            lbOS.setText(s.getOS());
                            lbVersion.setText(s.getSnortVer());
                        }
                    };
                    executorService.scheduleAtFixedRate(t1, 0, 5, TimeUnit.SECONDS);
                    
                    Thread t2 = new Thread() {
                        public void run() {
                            sensor s = new sensor();
                            s.speedTest();
                            lbReceiving.setText(s.getDonwloadSpeed());
                            lbSending.setText(s.getUploadSpeed());
                        }
                    };
                    executorService.scheduleAtFixedRate(t2, 0, 20, TimeUnit.SECONDS);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                btnRun.setEnabled(false);
                btnStop.setEnabled(true);
                lbState.setText("Running");
            }
        });

        // Stop process
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    isStarted = false;
                    Runtime rt = Runtime.getRuntime();
                    rt.exec("sudo pkill -f snort -u root");
                    fileWatcher.cancel();
                    timer.cancel();
                    // snort();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                btnRun.setEnabled(true);
                btnStop.setEnabled(false);
                lbState.setText("Stopped");
            }
        });

        // edit rules
        btnEditRules.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                ruleEdit.createFrame();
        	}
        });

        btnConfig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Config.createFrame();
            }
        });

        // Xem detail log
        btnDetail.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                DetailLog.createFrame(lineMarker);
        	}
        });
        frame.setVisible(true);
	}
    public void snort() throws Exception {

        alertThread = new Thread() {
            public void run() {
                try {
                    if (isStarted) {
                        ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","sudo snort -q -i ens33 -A fast -c /etc/snort/snort.conf");
                        proc = builder.start();
                    }
                    else {
                        proc.destroy();
                    }
                    proc.waitFor();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        alertThread.start();
        // monitor snort.alert.fast file
        fileWatcher = new FileWatcher( new File("/var/log/snort/snort.alert.fast") ) {
            protected void onChange( File file ) {
                // action on change
                // System.out.println( "File "+ file.getName() +" have change !" );
                // clearTable();
                try {
                    int count = 0;
                    Scanner sc = new Scanner(file);
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        count++;
                        if (count > lineMarker) {
                            createTable(line);
                        }


                    }
                    sc.close();
                }
                catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        };
        
        timer = new Timer();
        // repeat the check every second
        timer.schedule( fileWatcher , new Date(), 5000 );
    }

    private void initiComponents() {
        piechart = new PieChart();
        piechart.setFont(new java.awt.Font("sansserif",1,12));
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panelChart);
        panelChart.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(piechart, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(piechart, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(null);
    }

    // Tiep tuc tu line tiep theo cua session truoc
    public void initTable() {
        id = 1;
        int count = 0;
        try {
            File alertLog = new File("/var/log/snort/snort.alert.fast");
            Scanner sc = new Scanner(alertLog);
            while (sc.hasNextLine()) {
                // String line = sc.nextLine();
                // createTable(line);
                sc.nextLine();
                count++;
            }
            lineMarker = count;
            sc.close();
        } 
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    public void createTable(String line) {
        analyzeLine al = new analyzeLine(line);
        Vector<String> list = al.getList();

        Object[] row = new Object[6];
        row[0] = "#" + id; // id
        row[1] = list.elementAt(1); // message
        row[2] = list.elementAt(0); // timestamp
        row[3] = list.elementAt(3); // source
        row[4] = list.elementAt(4); // destination
        row[5] = list.elementAt(2); // protocol

        // Neu message xuat hien lan dau thi add vao list
        if (!listMessage.contains(list.elementAt(1))) {
            listMessage.addElement(list.elementAt(1));
            model.addRow(row);
            id++;
        }
        // Neu ton tai message roi thi replace row voi alert moi nhat
        else {
            Object[] updateData = new Object[4];
            for (int i=0;i<4;i++) {
                updateData[i] = row[i+2];
            }
            updateRow(list.elementAt(1), updateData);
        }

        // Same voi protocol
        if (!listProtocol.contains(list.elementAt(2))) {
            String name = list.elementAt(2);
            listProtocol.addElement(name); 
            
            switch(name) {
                case "TCP" : {
                    tcp = new ModelPieChart(name,1,new Color(23, 126, 238));
                    piechart.addData(tcp);
                    break;
                }
                case "UDP" : {
                    udp = new ModelPieChart(name,1,new Color(221, 65, 65));
                    piechart.addData(udp);
                    break;
                }
                case "ICMP" : {
                    icmp = new ModelPieChart(name,1,new Color(47, 157, 64));
                    piechart.addData(icmp);
                    break;
                }
                case "IP" : {
                    ip = new ModelPieChart(name,1,new Color(204, 51, 153));
                    piechart.addData(ip);
                    break;
                }
                case "ARP" : {
                    arp = new ModelPieChart(name,1,new Color(204, 153, 0));
                    piechart.addData(arp);
                    break;
                }
            }
            
        }   
        // +1 neu protocol ton tai
        else {
            String name = list.elementAt(2);
            switch (name) {
                case "TCP" : {
                    tcp.setValues(tcp.getValues()+1);
                    break;
                }
                case "UDP" : {
                    udp.setValues(udp.getValues()+1);
                    break;
                }
                case "ICMP" : {
                    icmp.setValues(icmp.getValues()+1);
                    break;
                }
                case "IP" : {
                    ip.setValues(ip.getValues()+1);
                    break;
                }
                case "ARP" : {
                    arp.setValues(arp.getValues()+1);
                    break;
                }
            }
        }
        // model.addRow(row);
        // table.setModel(model);
    }
    
    public void clearTable() {
        id = 1;
        model.setRowCount(0);
    }
    private void updateRow(String msg, Object[] data) {
        
        for (int i=0;i<model.getRowCount();i++) {
            if (model.getValueAt(i, 1).equals(msg)) {
                for (int j=0;j<data.length;j++) {
                    model.setValueAt(data[j], i, j+2);
                }
            }
        } 
    }
}

