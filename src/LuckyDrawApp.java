import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
	private SecureRandom random;

	private int gAddTime = 0;
	private int gSelectTime = 0;
	JComboBox selectTimeComboBox;

	private int doLoopTime = 1;

	public LuckyDrawApp() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		// 设置新的 Look and Feel
//		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//			System.out.println(info.getClassName());
//		}
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

//		javax.swing.plaf.metal.MetalLookAndFeel
//		javax.swing.plaf.nimbus.NimbusLookAndFeel
//		com.sun.java.swing.plaf.motif.MotifLookAndFeel
//		com.sun.java.swing.plaf.windows.WindowsLookAndFeel
//		com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel

		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        setTitle("Lucky Draw App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout(FlowLayout.LEADING));

		Dimension preferredSize = new Dimension(360, 850);
		setPreferredSize(preferredSize);

		add(new JLabel("資料來源："));

        sourceList = new ArrayList<>();
        selectedList = new ArrayList<>();
        resultList = new ArrayList<>();
		random = new SecureRandom();

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
		JScrollPane sourceListJScrollPane = new JScrollPane(sourceJList);
		sourceListJScrollPane.setPreferredSize(new Dimension(260, 310));
		sourcePanel.add(sourceListJScrollPane);
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

				gAddTime++;
				gSelectTime = 1;
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
						// startButton.setEnabled(false);
						stopButton.setEnabled(true);
						timer.start();
						progressBar.setMaximum((int) resultCountComboBox.getSelectedItem());

//    					selectedList.clear();
						resultList.clear();
						resultJList.setListData(resultList.toArray(new String[0]));
					} else if (selectedList.size() == 0) {
					subStartMethond();
					}


            }

        });
		add(startButton);

		Integer[] selectCount = new Integer[20];
		for (int i = 0; i < 20; i++) {
			selectCount[i] = i + 1;
		}

		add(new JLabel("抽"));
		selectTimeComboBox = new JComboBox<>(selectCount);
		add(selectTimeComboBox);
		add(new JLabel("次    "));

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
		// add(stopButton);

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
				doTimerEvent(orignalList, listModel);
			}
		});

		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}

	private void subStartMethond() {
		ListModel listM = selectedJList.getModel();
		for (int i = 0; i < listM.getSize(); i++) {
			Object obj = listM.getElementAt(i);
			selectedList.add((String) obj);
		}
		gSelectTime++;
		// startButton.setEnabled(false);
		stopButton.setEnabled(true);
		timer.start();
		progressBar.setMaximum((int) resultCountComboBox.getSelectedItem());

//				selectedList.clear();
		resultList.clear();
		resultJList.setListData(resultList.toArray(new String[0]));
	}

	private void doTimerEvent(ArrayList<String> orignalList, DefaultListModel listModel) {
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
			// startButton.setEnabled(false);
			timer.stop();

			String result = String.format("第 %s 批，抽第 %s 次，中獎人為：", gAddTime, gSelectTime);
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

			if (doLoopTime < (int) selectTimeComboBox.getSelectedItem()) {
				doLoopTime++;
				subStartMethond();
				doTimerEvent(orignalList, listModel);
			} else if (doLoopTime == (int) selectTimeComboBox.getSelectedItem()) {
				doLoopTime = 1;
			}

		}
	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
				try {
					new LuckyDrawApp().setVisible(true);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }
}