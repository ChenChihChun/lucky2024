import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class LuckyDrawApp extends JFrame {
    private List<String> sourceList;
    private List<String> selectedList;
    private List<String> resultList;

	private JComboBox<Integer> resultCountComboBox;
    private JList<String> sourceJList;
    private JList<String> selectedJList;
    private JList<String> resultJList;
    private JButton startButton;
    private JButton stopButton;
    private JProgressBar progressBar;
    private Timer timer;
    private Random random;

    public LuckyDrawApp() {
        setTitle("Lucky Draw App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout(FlowLayout.LEADING));

		Dimension preferredSize = new Dimension(360, 700);
		setPreferredSize(preferredSize);

		add(new JLabel("資料來源："));

        sourceList = new ArrayList<>();
        selectedList = new ArrayList<>();
        resultList = new ArrayList<>();
        random = new Random();

		JPanel sourcePanel = new JPanel();
		sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.X_AXIS));
		sourcePanel.add(new JLabel("人員明細："));

//        // Populate the source list with sample names
//        for (int i = 1; i <= 18; i++) {
//            sourceList.add("Person " + i);
//        }

		ArrayList<String> orignalList = new ArrayList<>();
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(LuckyDrawApp.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					try {
						List<String> names = Files.readAllLines(selectedFile.toPath());
						sourceList.clear();
						sourceList.addAll(names);
						sourceJList.setListData(sourceList.toArray(new String[0]));

						orignalList.addAll(names);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		add(loadButton);

        sourceJList = new JList<>(sourceList.toArray(new String[0]));
		sourcePanel.add(new JScrollPane(sourceJList));
		add(sourcePanel);

		JPanel textPanel1 = new JPanel();
		textPanel1.setLayout(new BoxLayout(textPanel1, BoxLayout.X_AXIS));
		textPanel1.add(new JLabel("從名單選取參加人員，按【加入】完成參與設定："));

		JButton addBtn = new JButton("加入");
		textPanel1.add(addBtn);

		// 添加带入按钮的事件处理
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedList.clear();

				List<String> selectedItems = sourceJList.getSelectedValuesList();
				for (String tmp : selectedItems) {
					selectedList.add(tmp);
				}
				selectedJList.setListData(selectedList.toArray(new String[0]));

				startButton.setEnabled(true);

				resultList.clear();
				resultJList.setListData(resultList.toArray(new String[0]));
			}
		});

		add(textPanel1);

		JPanel selectedPanel = new JPanel();
		selectedPanel.setLayout(new BoxLayout(selectedPanel, BoxLayout.X_AXIS));
		selectedPanel.add(new JLabel("參與人員："));

		selectedJList = new JList<>();


		selectedPanel.add(new JScrollPane(selectedJList));
		add(selectedPanel);

		JPanel textPanel2 = new JPanel();
		textPanel2.setLayout(new BoxLayout(textPanel2, BoxLayout.X_AXIS));
		textPanel2.add(new JLabel("從上述名單中再取："));

		Integer[] resultCounts = { 1, 2, 3, 4, 5, 6, 7, 8 };
		resultCountComboBox = new JComboBox<>(resultCounts);
		textPanel2.add(resultCountComboBox);
		textPanel2.add(new JLabel("視為中獎"));

		add(textPanel2);

		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.X_AXIS));
		resultPanel.add(new JLabel("中獎明細："));

		resultJList = new JList<>();
		resultPanel.add(new JScrollPane(resultJList));
		add(resultPanel);

        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

				int resultCount = (int) resultCountComboBox.getSelectedItem();
				if (selectedList.size() >= resultCount) {
					startButton.setEnabled(false);
					stopButton.setEnabled(true);
					timer.start();
					progressBar.setMaximum((int) resultCountComboBox.getSelectedItem());

					// selectedList.clear();
					resultList.clear();
					resultJList.setListData(resultList.toArray(new String[0]));
				}
            }
        });
		add(startButton);

        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                timer.stop();
            }
        });
		add(stopButton);

        progressBar = new JProgressBar();
		add(progressBar);

		JPanel ansPanel = new JPanel();
		ansPanel.setLayout(new BoxLayout(ansPanel, BoxLayout.Y_AXIS));
		JLabel jl1 = new JLabel("結果：");
		jl1.setAlignmentX(Component.LEFT_ALIGNMENT);
		ansPanel.add(jl1);

		JScrollPane scrollPane = new JScrollPane();
		DefaultListModel listModel = new DefaultListModel<>();
		JList<String> listResult = new JList<>(listModel);
		scrollPane.setViewportView(listResult);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(320, 100));

		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		ansPanel.add(scrollPane);
		add(ansPanel);

        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
				int resultCount = (int) resultCountComboBox.getSelectedItem();
				if (resultList.size() < resultCount) {

					int selectedIndex = random.nextInt(selectedList.size());
					String luckyGuy = selectedList.get(selectedIndex);
					selectedList.remove(selectedIndex);
					resultList.add(luckyGuy);
					resultJList.setListData(resultList.toArray(new String[0]));

					progressBar.setValue(resultList.size());
                } else {
                    stopButton.setEnabled(false);
					startButton.setEnabled(false);
                    timer.stop();

					String result = "中獎人為：";
					for (String tmp : resultList) {
						result = result.concat(tmp).concat("、");
					}
					result = result.substring(0, result.length() - 1);
					listModel.add(0, result);

					resultList.clear();
					selectedList.clear();
					sourceList.clear();
					sourceJList.removeAll();

					for (String tmp : orignalList) {
						sourceList.add(tmp);
					}

					sourceJList.setListData(sourceList.toArray(new String[0]));
                }
            }
        });


		setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LuckyDrawApp().setVisible(true);
            }
        });
    }
}