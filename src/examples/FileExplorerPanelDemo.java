import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.ricardojlrufino.jexplorer.JExplorerPanel;
import com.ricardojlrufino.jexplorer.utils.AcceptAllFileFilter;

public class FileExplorerPanelDemo extends JFrame {

    public FileExplorerPanelDemo() {

        Preferences prefs = Preferences.userNodeForPackage(FileExplorerPanelDemo.class);
        
        String root = prefs.get("lastFolder", System.getProperty("user.home"));
        JExplorerPanel fileExplorerPanel = new JExplorerPanel(new File(root));

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JExplorerPanel demo");
        this.setSize(500, 500);
        this.setLayout(new BorderLayout());
        this.add(fileExplorerPanel);

        // Add File Filter
        JTextField filterTextField=new JTextField();
        filterTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                String text=filterTextField.getText();
                if(text==null||text.trim().isEmpty()){
                    fileExplorerPanel.setFileFilter(new AcceptAllFileFilter());
                } else {
                    fileExplorerPanel.setFileFilter(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return pathname.getName().contains(text);
                        }
                    });
                }
            }
        });
        this.add(filterTextField, BorderLayout.NORTH);

        // Add Buton to change...
        JButton button = new JButton("Browse");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(FileExplorerPanelDemo.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    fileExplorerPanel.replaceWorkingDirectory(selectedFile);
                    prefs.put("lastFolder", selectedFile.getAbsolutePath());
                }
            }
        });
        this.add(button, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {

      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      new FileExplorerPanelDemo().setVisible(true);
    }

}
