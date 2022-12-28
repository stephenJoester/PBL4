package View;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

public class ruleEdit extends JFrame {

	private JPanel contentPane;
    private JList listRules;
    private DefaultListModel modelList;
    private Boolean editing = false;
    private int index = 0;
	/**
	 * Launch the application.
	 */
	public static void createFrame() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ruleEdit frame = new ruleEdit();
                    frame.setName("Rule edit");
					frame.setVisible(true);
                    frame.addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                            // Process change permission 
                            try {
                                ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","sudo chmod a-w /etc/snort/rules/local.rules");
                                Process proc = builder.start();
                                proc.waitFor();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ruleEdit() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 663, 431);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 36, 627, 154);
		listRules = new JList();
		listRules.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		listRules.setBounds(10,11,627,154);
        loadRules();
        // add list test
        // modelList = new DefaultListModel<>();
        // modelList.addElement("test1");
        // modelList.addElement("test2");
        // modelList.addElement("test3");
        // modelList.addElement("test4");
        listRules.setModel(modelList);
		scrollPane.setViewportView(listRules);
		contentPane.add(scrollPane);
		
		JLabel lblNewLabel = new JLabel("Local rules : ");
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblNewLabel.setBounds(10, 11, 344, 14);
		contentPane.add(lblNewLabel);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 276, 627, 46);
		contentPane.add(scrollPane_1);
		
		JTextArea textArea = new JTextArea();
		scrollPane_1.setViewportView(textArea);
		
		JLabel lblNhpRuleMi = new JLabel("Nh\u1EADp rule m\u1EDBi : ");
		lblNhpRuleMi.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblNhpRuleMi.setBounds(10, 251, 344, 14);
		contentPane.add(lblNhpRuleMi);
		
		JButton btnDel = new JButton("Delete");
		
		btnDel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnDel.setBounds(10, 201, 89, 23);
		contentPane.add(btnDel);
		
		JButton btnAdd = new JButton("Add");
		
		btnAdd.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnAdd.setBounds(10, 341, 89, 23);
		contentPane.add(btnAdd);
		
		JButton btnEdit = new JButton("Edit");
		btnEdit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnEdit.setBounds(125, 201, 89, 23);
		contentPane.add(btnEdit);
		
		JButton btnApply = new JButton("Apply");
		btnApply.setEnabled(false);
		btnApply.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnApply.setBounds(246, 201, 89, 23);
		contentPane.add(btnApply);
		
        // edit
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                textArea.setText(listRules.getSelectedValue().toString());
                editing = true;
                index = listRules.getSelectedIndex();
			}
		});
		
        // del
		btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                modelList.remove(listRules.getSelectedIndex());
                btnApply.setEnabled(true);
			}
		});
		
        // add/edit
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                String input = textArea.getText();
                Boolean isDuplicated = false;
                // check xem trung lap
                for (int i=0;i<modelList.size();i++) {
                    if (input.equals(modelList.getElementAt(i).toString())) {
                        isDuplicated = true;
                        editing = false;
                        break;
                    }
                }

                // neu edit
                if (!isDuplicated && editing) {
                    modelList.remove(index);
                    modelList.add(index, input);
                    btnApply.setEnabled(true);
                }

                // neu add
                else if (!isDuplicated) {
                    modelList.addElement(input);
                    btnApply.setEnabled(true);
                }

			}
		});
		
        // apply changes
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                writeRules();
                btnApply.setEnabled(false);
			}
		});
	
	}

    private void loadRules() {
        modelList = new DefaultListModel<>();
        // Process change permission 
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","sudo chmod a+rw /etc/snort/rules/local.rules");
            Process proc = builder.start();
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        

        try {
            String path = "/etc/snort/rules/local.rules";
            FileReader file = new FileReader(path);
            BufferedReader br = new BufferedReader(file);

            String line;
            while ((line = br.readLine())!=null) {
                modelList.addElement(line);
            }
            file.close();
            br.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void writeRules() {
        try {
            String path = "/etc/snort/rules/local.rules";            
            File f = new File(path);
            System.out.print("Can write: " + f.canWrite());
            FileWriter fw = new FileWriter(f);

            List<String> lines = new ArrayList<String>();
            for (int i = 0;i<modelList.size();i++) {
                lines.add(modelList.getElementAt(i).toString());
            }

            BufferedWriter bw = new BufferedWriter(fw);
            for (String s : lines) {
                // System.out.println(s);
                bw.write(s + "\n");
            }
            bw.flush();
            bw.close();
            fw.close();

        }  
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
