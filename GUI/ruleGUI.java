import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ruleGUI extends JFrame {

	private JPanel contentPane;
	private JTextField txtSource;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ruleGUI frame = new ruleGUI();
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
	public ruleGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 663, 431);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JComboBox cbbAction = new JComboBox();
		cbbAction.setModel(new DefaultComboBoxModel(new String[] {"Action", "Alert", "Log"}));
		cbbAction.setSelectedIndex(0);
		cbbAction.setBounds(10, 37, 110, 22);
		contentPane.add(cbbAction);
		
		JComboBox cbbProtocol = new JComboBox();
		cbbProtocol.setModel(new DefaultComboBoxModel(new String[] {"Protocol", "ip", "icmp", "tcp", "udp"}));
		cbbProtocol.setSelectedIndex(0);
		cbbProtocol.setBounds(130, 37, 116, 22);
		contentPane.add(cbbProtocol);
		
		txtSource = new JTextField();
        txtSource.setText("Source ip");
		txtSource.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                if (txtSource.getText().isEmpty()) {
                    txtSource.setText("Source ip");

                }
            }
            public void focusGained(FocusEvent e) {
                if (txtSource.getText().equals("Source ip")) {
                    txtSource.setText("");
                }
            }
        });
		txtSource.setBounds(256, 38, 126, 21);
		contentPane.add(txtSource);
		txtSource.setColumns(10);
	}
}
