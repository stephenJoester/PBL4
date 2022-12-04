package View;
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
import Class.Piechart.*;

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
    static boolean isStarted;
    static Thread task;
    private Process proc;
    private PieChart piechart;
    private JPanel panelChart;
    private Vector<String> listProtocol = new Vector<String>();
    private ModelPieChart tcp, icmp, udp, ip, arp;
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
        scrollPane1.setBounds(10,265,721,295);
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
        btnConfig.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        // event listener
        btnConfig.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                config();
        	}
        });
        btnConfig.setBounds(10, 162, 89, 23);
        
        panel.add(btnConfig);
        
        JButton btnEditRules = new JButton("Edit Rules");
        
        btnEditRules.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnEditRules.setBounds(128, 162, 89, 23);
        panel.add(btnEditRules);
        
        JButton btnRun = new JButton("Run");
        btnRun.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnRun.setBounds(10, 218, 89, 23);
        
        frame.getContentPane().add(btnRun);
        
        JButton btnStop = new JButton("Stop");
        btnStop.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnStop.setEnabled(false);
        btnStop.setBounds(139, 218, 89, 23);
        
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

        // Start process 
        btnRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    createTable(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                btnRun.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });

        // Stop process
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    createTable(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                btnRun.setEnabled(true);
                btnStop.setEnabled(false);
            }
        });

        // edit rules
        btnEditRules.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                ruleEdit re = new ruleEdit();
                re.createFrame();
        	}
        });

        frame.setVisible(true);
	}
    public void createTable(boolean isStarted) throws Exception {
        id = 1;

        // test 
        Thread t = new Thread() {
            public void run() {
                // processbuilder
                try {
                    // if true -> start process
                    if (isStarted) {
                        ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","sudo snort -q -l /var/log/snort/ -i ens33 -A console -c /etc/snort/snort.conf");
                        proc = builder.start();

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
                                model.addRow(row);
                                table.setModel(model);
                                id++;
                            }
                            
                        }
                    }
                    else {
                        proc.destroy();

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
}

