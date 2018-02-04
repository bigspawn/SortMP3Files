package com.bigspawn;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;

/**
 * GUI for app
 */
public class AppGUI extends JFrame implements ActionListener {

  private final JLabel label = new JLabel("Выберите папки ");
  private final JLabel labelStart = new JLabel("Запуск приложения ");
  private JPanel panel;
  private JButton inputButton;
  private JButton outputButton;
  private JButton start;
  private JTextArea textArea;

  public AppGUI() throws HeadlessException {
    super("SortMP3Files");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    initPanel();
    setLocationRelativeTo(null);
    setSize(500 + getInsets().left + getInsets().right, 300 + getInsets().top + getInsets().bottom);
    setVisible(true);
  }

  public JTextArea getTextArea() {
    return textArea;
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new MigLayout());

    addLabel(label);
    addButtons();
    initTextAria();

    getContentPane().add(panel);
    pack();
  }

  private void initTextAria() {
    textArea = new JTextArea(20, 45);
    JScrollPane jScrollPane = new JScrollPane(textArea);
    textArea.setEditable(false);
    panel.add(jScrollPane, "span");
  }

  private void addButtons() {
    inputButton = new JButton("Input");
    outputButton = new JButton("Output");
    addChooseButton(inputButton, panel);
    addChooseButton(outputButton, panel);
    panel.add(new JLabel(), "wrap");
    addLabel(labelStart);
    addButton();
  }

  private void addLabel(JLabel labelStart) {
    labelStart.setAlignmentX(CENTER_ALIGNMENT);
    panel.add(labelStart);
  }

  private void addButton() {
    start = new JButton("Start");
    start.setAlignmentX(CENTER_ALIGNMENT);
    start.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        App.start();
      }
    });
    panel.add(start, "wrap");
  }

  private void addChooseButton(JButton button, JPanel panel) {
    button.setAlignmentX(CENTER_ALIGNMENT);
    button.addActionListener(this);
    panel.add(button);
  }

  private String chooseDirectory() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int ret = fileChooser.showDialog(null, "Открыть папку");
    if (ret == JFileChooser.APPROVE_OPTION) {
      return fileChooser.getSelectedFile().getAbsolutePath();
    }
    return null;
  }

  public void actionPerformed(ActionEvent e) {
    String path = chooseDirectory();
    if (path != null && !path.isEmpty()) {
      if (e.getSource() == inputButton) {
        App.FROM_FOLDER = path;
      } else {
        App.TO_FOLDER = path;
      }
      textArea.append(path + "\n");
    }
  }
}
