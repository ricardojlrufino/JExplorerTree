import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import com.ricardojlrufino.jexplorer.FileOperation;
import com.ricardojlrufino.jexplorer.JExplorerPanel;

public class FileExplorerPanelLinuxDebug extends JFrame {

    private JTextArea textArea;
    private String root;

    public FileExplorerPanelLinuxDebug() {

        Preferences prefs = Preferences.userNodeForPackage(FileExplorerPanelLinuxDebug.class);
        
        root = prefs.get("lastFolder", System.getProperty("user.home"));
        
        System.out.println("Current Folder: " + root);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JExplorerPanel demo");
        this.setSize(800, 500);
        this.setLayout(new BorderLayout());
        
        textArea = new JTextArea();
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setMinimumSize(new Dimension(400, (int)getSize().getHeight()));
        
        updateDir();
        
        JExplorerPanel fileExplorerPanel = new JExplorerPanel(new File(root)){

          @Override
          protected boolean executeOperation(FileOperation operation) {
            boolean status = super.executeOperation(operation);
            updateDir();
            return status;
          }
          
          @Override
          protected void handleUndo() {
            super.handleUndo();
            updateDir();
          }
          
          @Override
          protected void initMenus() {
            super.initMenus();
            popup.remove(0);
          }
          
        };
        
        fileExplorerPanel.setMinimumSize(new Dimension(400, (int)getSize().getHeight()));
        
        this.add(fileExplorerPanel, BorderLayout.CENTER);
        this.add(textArea, BorderLayout.EAST);
        
        // Add Buton to change...
        JButton button = new JButton("Browse");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(FileExplorerPanelLinuxDebug.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    fileExplorerPanel.replaceWorkingDirectory(selectedFile);
                    prefs.put("lastFolder", selectedFile.getAbsolutePath());
                }
            }
        });
        this.add(button, BorderLayout.SOUTH);
    }

    private void updateDir() {
      try {
        String cmd = execCmd(new String[] {"bash", "-c", "cd "+root+" ; tree" });
        textArea.setText(cmd);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    public static void main( String[] args ) {
        new FileExplorerPanelLinuxDebug().setVisible(true);
    }
    
    public static String execCmd(String cmd[]) throws java.io.IOException {
      java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
      return s.hasNext() ? s.next() : "";
  }

}
