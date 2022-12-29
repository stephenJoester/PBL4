package View;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JTable;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import Class.*;

public class DetailLog extends JFrame {

	private JPanel contentPane;
	private JTable table;
    private int lineMarker;
    private DefaultTableModel model;
	private static int id;
	private TimerTask fileWatcher;
	private Timer timer;

	/**
	 * Launch the application.
	 */
	public static void createFrame(int marker) {
    
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DetailLog frame = new DetailLog(marker);
                    frame.setName("Detail Log");
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
	public DetailLog(int marker) {
        lineMarker = marker;

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 749, 434);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
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
		JScrollPane scrollPane1 = new JScrollPane((Component) null);
		contentPane.add(scrollPane1);

		// component listener - scroll to the bottom of table
        table.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                table.scrollRectToVisible(table.getCellRect(table.getRowCount()-1, 0, true));
            }
        });
		
		scrollPane1.setViewportView(table);
        initTable();
		monitoring();
	}

    public void initTable() {
        try {
            id = 1;
            File alertLog = new File("/var/log/snort/snort.alert.fast");
            int count = 0;
            Scanner sc = new Scanner(alertLog);
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

    public void monitoring() {
		
		fileWatcher = new FileWatcher( new File("/var/log/snort/snort.alert.fast") ) {
            protected void onChange( File file ) {
                // action on change
                // System.out.println( "File "+ file.getName() +" have change !" );
                clearTable();
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
        id++;
		model.addRow(row);
	}

	public void clearTable() {
		id = 1;
		model.setRowCount(0);
	}
}
