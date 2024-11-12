package snippet;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class KMPFileHandlingGUI extends JFrame {

    private JTextField patternField;
    private JTextArea resultArea;
    private File selectedFile;

    public KMPFileHandlingGUI() {
        setTitle("KMP Pattern Search in CSV File");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top panel for file selection and pattern input
        JPanel topPanel = new JPanel(new FlowLayout());
        
        JButton fileButton = new JButton("Select CSV File");
        fileButton.addActionListener(new FileButtonListener());
        
        topPanel.add(fileButton);
        
        patternField = new JTextField(20);
        topPanel.add(new JLabel("Enter Pattern:"));
        topPanel.add(patternField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new SearchButtonListener());
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Save results button
        JButton saveButton = new JButton("Save Results");
        saveButton.addActionListener(new SaveButtonListener());
        add(saveButton, BorderLayout.SOUTH);
    }

    // KMP Search function
    public static boolean KMP(String text, String pattern) {
        int[] table = buildKMPTable(pattern);
        int i = 0, j = 0;
        
        while (i < text.length()) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == pattern.length()) {
                return true;  // Pattern found
            } else if (i < text.length() && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = table[j - 1];
                } else {
                    i++;
                }
            }
        }
        return false;  // Pattern not found
    }

    // Build partial match table for KMP algorithm
    public static int[] buildKMPTable(String pattern) {
        int[] table = new int[pattern.length()];
        int j = 0;
        for (int i = 1; i < pattern.length(); i++) {
            if (pattern.charAt(i) == pattern.charAt(j)) {
                j++;
                table[i] = j;
            } else {
                if (j != 0) {
                    j = table[j - 1];
                    i--;
                } else {
                    table[i] = 0;
                }
            }
        }
        return table;
    }

    // Listener for file selection button
    private class FileButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                resultArea.setText("Selected file: " + selectedFile.getAbsolutePath() + "\n");
            }
        }
    }

    // Listener for search button
    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(null, "Please select a CSV file first.");
                return;
            }
            String pattern = patternField.getText();
            if (pattern.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a search pattern.");
                return;
            }
            resultArea.append("Searching for pattern: " + pattern + "\n\n");
            performSearch(pattern);
        }
    }

    // Listener for save button
    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Results as CSV");
            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                saveResultsToFile(saveFile);
            }
        }
    }

    // Perform the KMP search and display results
    private void performSearch(String pattern) {
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            String line;
            int lineNum = 0;
            boolean found = false;

            reader.readLine();  // Skip header line
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (KMP(line, pattern)) {
                    resultArea.append("Pattern found in line " + lineNum + ": " + line + "\n");
                    found = true;
                }
            }

            if (!found) {
                resultArea.append("Pattern not found in any record.\n");
            }
        } catch (IOException ex) {
            resultArea.append("Error reading file: " + ex.getMessage() + "\n");
        }
    }

    // Save results from the text area to a CSV file
    private void saveResultsToFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(resultArea.getText());
            JOptionPane.showMessageDialog(null, "Results saved to " + file.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error saving file: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KMPFileHandlingGUI frame = new KMPFileHandlingGUI();
            frame.setVisible(true);
        });
    }
}
