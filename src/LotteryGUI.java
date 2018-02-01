import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
/**
 * GUI for the lottery program
 * @author Sabrina Hyunmi Ko
 *
 */
public class LotteryGUI{

	private static JFrame frame;
	private static JTextField textField;
	private static JTextField textField_1;
	private static FileFindListener wtwListener;
	private static FileFind2Listener semListener;
        private static JLabel jLabel1;
        private static JScrollPane jScrollPane1;
        private static JTextArea jTextArea1;
        
	/**
	 * Launch the application.
         * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
                        @Override
			public void run() {
				try {
                                    LotteryGUI window = new LotteryGUI();
                                    LotteryGUI.frame.setVisible(true);
				} catch (Exception e) {
                                    
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LotteryGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
                JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("5CLIR Lottery");
		frame.setBackground(new Color(240, 240, 240));
		frame.getContentPane().setBackground(new Color(240, 240, 240));
		frame.getContentPane().setFont(new Font("Helvetica", Font.BOLD, 14));
		frame.setBounds(500, 250, 500, 250);
                frame.setMaximumSize(new Dimension(500, 250));
                frame.setMinimumSize(new Dimension(500, 250));
                frame.setPreferredSize(new Dimension(500, 250));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
                jLabel1 = new JLabel();
		jLabel1.setFont(new java.awt.Font("Helvetica", 0, 18)); // NOI18N
                jLabel1.setForeground(new Color(0, 102, 51));
                jLabel1.setText("5 College Learning in Retirement Lottery Program");
                frame.add(jLabel1);
                
                jScrollPane1 = new JScrollPane();
                jScrollPane1.setFont(new java.awt.Font("Helvetica", 0, 12)); // NOI18N
                jScrollPane1.setPreferredSize(new Dimension(490, 80));
                jScrollPane1.setBorder(null);
                
                jTextArea1 = new JTextArea();
                jTextArea1.setBackground(new Color(240, 240, 240));
                jTextArea1.setEditable(false);
                jTextArea1.setColumns(20);
                jTextArea1.setFont(new java.awt.Font("Helvetica", 0, 14)); // NOI18N
                jTextArea1.setLineWrap(true);
                jTextArea1.setRows(3);
                jTextArea1.setText("Check the readme.txt file in the Lottery Program folder for information on how to format the files you are inputting into this program/information on how the files that will be outputted will be formatted and found.");
                jTextArea1.setWrapStyleWord(true);
                jTextArea1.setAutoscrolls(false);
                jTextArea1.setFocusTraversalKeysEnabled(false);
                jTextArea1.setFocusable(false);
                jTextArea1.setSelectionColor(new java.awt.Color(0, 153, 102));
                jTextArea1.setVerifyInputWhenFocusTarget(false);
                jScrollPane1.setViewportView(jTextArea1);
                frame.add(jScrollPane1);
		
		JPanel panel = new JPanel();
		panel.setBorder(null);
		panel.setForeground(new Color(240, 240, 240));
		panel.setBackground(new Color(240, 240, 240));
		frame.getContentPane().add(panel);
		panel.setLayout(new GridLayout(2, 6, 0, 0));
		
		JLabel whoTakesWhatLabel = new JLabel("Who Takes What File:");
		whoTakesWhatLabel.setFont(new Font("Helvetica", Font.PLAIN, 14));
		panel.add(whoTakesWhatLabel);
		
		textField = new JTextField();
                textField.setFont(new Font("Helvetica", Font.PLAIN, 14));
		panel.add(textField);
		textField.setColumns(10);
		
		JButton wtwBut = new JButton("Find File");
		wtwBut.setFont(new Font("Helvetica", Font.PLAIN, 14));
		panel.add(wtwBut);
		wtwListener = new FileFindListener();
		wtwBut.addActionListener(wtwListener);
		
		JLabel lblSeminarInfoFile = new JLabel("Seminar Info FIle:");
		lblSeminarInfoFile.setFont(new Font("Helvetica", Font.PLAIN, 14));
		panel.add(lblSeminarInfoFile);
		
		textField_1 = new JTextField();
                textField_1.setFont(new Font("Helvetica", Font.PLAIN, 14));
		panel.add(textField_1);
		textField_1.setColumns(10);
		
		JButton semBut = new JButton("Find File");
		semBut.setFont(new Font("Helvetica", Font.PLAIN, 14));
		panel.add(semBut);
		semListener = new FileFind2Listener();
		semBut.addActionListener(semListener);
		
		JButton btnRunProgram = new JButton("Start");
		btnRunProgram.setFont(new Font("Helvetica", Font.PLAIN, 16));
		frame.getContentPane().add(btnRunProgram);
		btnRunProgram.addActionListener(new StartListener());
	}
        
    /** Event handler for WTW File Find button */
    private static class FileFindListener implements ActionListener {
    	private static File wtwFile; 
    	
    	public File getFile() {
    		return wtwFile;
    	}
            @Override
		public void actionPerformed(ActionEvent e) {
			File selectedFile = null;
    		JFrame fileFrame = new JFrame("File Chooser");
    		JFileChooser fileChooser = new JFileChooser();
    		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    		int result = fileChooser.showOpenDialog(fileFrame);
    		if (result == JFileChooser.APPROVE_OPTION) {
    			selectedFile = fileChooser.getSelectedFile();
    		} else {
                    JOptionPane.showMessageDialog(frame,
                            "Error: No file chosen",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
    		}
    		
    		wtwFile = selectedFile;
    		textField.setText(selectedFile.getName());
    		frame.repaint();
    	}
    }
    
    /** Event handler for Seminar File Find button */
    private static class FileFind2Listener implements ActionListener {
    	private static File semFile; 
    	
    	public File getFile() {
    		return semFile;
    	}
    	
            @Override
            public void actionPerformed(ActionEvent e) {
                    File selectedSemFile = null;
            JFrame fileFrame = new JFrame("File Chooser");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(fileFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                    selectedSemFile = fileChooser.getSelectedFile();
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Error: No file chosen",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            semFile = selectedSemFile;
            textField_1.setText(selectedSemFile.getName());
            frame.repaint();
    	}
    }
    
    /** Event handler for Start button */
    private static class StartListener implements ActionListener {

                @Override
		public void actionPerformed(ActionEvent e) {
                    try {
                        String[] arguments = new String[] {wtwListener.getFile().getAbsolutePath(), semListener.getFile().getAbsolutePath()};
                        LotteryAlgorithm.main(arguments);

                        JOptionPane.showMessageDialog(frame,
                        "Lottery program has finished. Please check your desktop for the output files.");
                    } catch (IOException exception) {
                        JOptionPane.showMessageDialog(frame,
                        "Error: Something went wrong when running the lottery program",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    }
    	}
    }
}
