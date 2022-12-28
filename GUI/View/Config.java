package View;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.util.Scanner;
import java.util.Vector;

import javax.annotation.processing.FilerException;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import Class.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.ProcessBuilder.Redirect;
import java.awt.event.ActionEvent;
public class Config extends JFrame {

	private JPanel contentPane;
	private static JTextField txtHomeNet;
	private static JTextField txtExternalNet;
	private static long homenetPos;
	private static long externalnetPos;
	/**
	 * Launch the application.
	 */
	public static void createFrame() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					Config frame = new Config();
                    frame.setName("Config");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Config() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 478, 293);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("HOME_NET : ");
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblNewLabel_1.setBounds(30, 47, 108, 14);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel("Network interface : ");
		lblNewLabel_1_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblNewLabel_1_1.setBounds(30, 126, 115, 14);
		contentPane.add(lblNewLabel_1_1);
		
		JLabel lblNewLabel_1_2 = new JLabel("EXTERNAL_NET : ");
		lblNewLabel_1_2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblNewLabel_1_2.setBounds(30, 88, 108, 14);
		contentPane.add(lblNewLabel_1_2);
		
		JButton btnSave = new JButton("Save");
		btnSave.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnSave.setBounds(102, 199, 89, 23);
		contentPane.add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnCancel.setBounds(264, 199, 89, 23);
		contentPane.add(btnCancel);
		
		txtHomeNet = new JTextField();
		txtHomeNet.setBounds(177, 44, 204, 23);
		contentPane.add(txtHomeNet);
		txtHomeNet.setColumns(10);
		
		txtExternalNet = new JTextField();
		txtExternalNet.setColumns(10);
		txtExternalNet.setBounds(177, 85, 204, 23);
		contentPane.add(txtExternalNet);
		
		initiConfig();

		JComboBox cbbInterface = new JComboBox();
		cbbInterface.setBounds(177, 123, 204, 22);
        Vector<interfaceItem> list = new listInterfaces().getList();
        cbbInterface.addItem(new comboItem("Chọn", ""));
        for (int i=0;i<list.size();i++) {
            cbbInterface.addItem(new comboItem(list.elementAt(i).getName(),String.valueOf(i)));
        }
		contentPane.add(cbbInterface);

        // save
        btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                saveConfig(txtHomeNet.getText(), txtExternalNet.getText());
				btnSave.setEnabled(false);
			}
		});

        // cancel
        btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                dispose();
			}
		});
	}

	private void saveConfig(String homenet, String externalnet) {
		try {
			RandomAccessFile raf = new RandomAccessFile("/etc/snort/snort.conf", "rw");
			raf.seek(homenetPos);
			String str = "ipvar HOME_NET " + homenet + "\n";
			raf.write(str.getBytes());
			raf.seek(externalnetPos);
			str = "ipvar EXTERNAL_NET " + externalnet + "\n";
			raf.write(str.getBytes());
			raf.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

    private void initiConfig() {
        // change permission
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","sudo chmod a+rw /etc/snort/snort.conf");
            Process proc = builder.start();
            proc.waitFor();

            RandomAccessFile raf = new RandomAccessFile("/etc/snort/snort.conf", "r");
			long previousPointer= 0, pointer = 0;
			String line = raf.readLine();
			while (line != null) {
				// System.out.println("Previous pointer : " + previousPointer);
				pointer = raf.getFilePointer();
				// System.out.println("Current pointer : " + pointer);
				if (line.contains("ipvar HOME_NET")) {
					String[] words = line.split("\\s+");
					txtHomeNet.setText(words[2]);
					homenetPos = previousPointer;
				}
				if (line.contains("ipvar EXTERNAL_NET")) {
					String[] words = line.split("\\s+");
					txtExternalNet.setText(words[2]);
					externalnetPos = previousPointer;
					break;
				}
				previousPointer = pointer;
				line = raf.readLine();
			}
			raf.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
