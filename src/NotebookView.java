import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.XMLGregorianCalendar;

import org.geworkbench.components.genspace.GenSpaceServerFactory;
import org.geworkbench.components.genspace.server.stubs.AnalysisEvent;
import org.geworkbench.components.genspace.server.stubs.Tool;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
public class NotebookView implements ActionListener{
	public TableCellRenderer MyCellRenderer = new MyCellRenderer();
	public TableCellEditor MyCellEditor = new MyCellEditor();
	public String searchParam = null;
	public String dropSelection;
	public String sortBy = null;
	public String firstParam = null;
	public String secondParam = null;
	int heightParameter;
	private static int lines = 0;

	public void viewNotebook() {
		// TODO Auto-generated method stub
		//Log in to genspace

		System.out.println("Logging in");
		GenSpaceServerFactory.userLogin("jon", "test123");
		System.out.println("Getting my notes");
		JFrame frame = new JFrame("My Notebook");
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainContainer = new JPanel();
		frame.add(mainContainer);
		mainContainer.setLayout(new BorderLayout(0, 9));
		List<Tool> toolStrings = GenSpaceServerFactory.getUsageOps().getAllTools();
		List<AnalysisEvent> events = GenSpaceServerFactory.getPrivUsageFacade().getMyNotes(null, null);
		final NoteListModel nlm = new NoteListModel();
		nlm.setMyList(events);
		final JTable noteList = new JTable();
		noteList.setModel(nlm);
		String [] toolNames = new String[toolStrings.size()];
		String blank = "";
		blank = toolNames[0];
		for (int i = 1; i < toolStrings.size(); i ++)
		{
			toolNames[i] = toolStrings.get(i).getName();   
		}
		System.out.println(Arrays.toString(toolNames));
		JComboBox dropdown= new JComboBox(toolNames);  // dropdown box
		dropdown.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) {

				ItemSelectable is = (ItemSelectable)e.getSource();
				setFirstParam(selectedString(is));
				List<AnalysisEvent> searchEvents = GenSpaceServerFactory.getPrivUsageFacade().getMyNotes(firstParam, secondParam);	
				nlm.setMyList(searchEvents);   
				noteList.setModel(nlm);
				noteList.revalidate();

			}

		});
		JPanel sortBy = new JPanel(new FlowLayout());
		String [] sortByStrings = {" ", "Sort by tool", "Sort by date"};
		final JComboBox sortByDropdown = new JComboBox(sortByStrings);
		sortByDropdown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (sortByDropdown.getSelectedIndex() == 1)
				{
					setSecondParam("tool");
					List<AnalysisEvent> searchEvents = GenSpaceServerFactory.getPrivUsageFacade().getMyNotes(firstParam, secondParam);	
					nlm.setMyList(searchEvents);   
					noteList.setModel(nlm);
					noteList.revalidate();
				}
				if (sortByDropdown.getSelectedIndex() == 2)
				{
					setSecondParam("date");
					List<AnalysisEvent> searchEvents = GenSpaceServerFactory.getPrivUsageFacade().getMyNotes(firstParam, secondParam);	
					nlm.setMyList(searchEvents);   
					noteList.setModel(nlm);
					noteList.revalidate();
				}

			}
		});
		sortBy.add(sortByDropdown);
		JPanel searchPanel = new JPanel(new BorderLayout());
		final JTextArea searchBox = new JTextArea("Enter your search query here or use the dropdown below");
		searchBox.setEditable(true);
		searchBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(searchBox.getText().equals("Enter your search query here or use the dropdown below"))
					searchBox.setText("");
				super.mousePressed(e);
			}
		});
		JButton searchButton = new JButton("Search");
		searchButton.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent event)
			{
				ItemSelectable searchName = (ItemSelectable)event.getSource();
				String query = searchBox.getText();
				setFirstParam(query);
				List<AnalysisEvent> searchQueryList = GenSpaceServerFactory.getPrivUsageFacade().getMyNotes(firstParam, secondParam);	 // same problem as above
				nlm.setMyList(searchQueryList);   
				noteList.setModel(nlm);
				noteList.revalidate();
			}
		});
		JLabel searchLabel = new JLabel("Filter your notes here:");
		Font f = new Font("Dialog", Font.PLAIN, 24);
		searchLabel.setFont(f);
		searchPanel.add(searchLabel, BorderLayout.NORTH);
		searchPanel.add(searchBox, BorderLayout.CENTER);
		searchPanel.add(searchButton, BorderLayout.EAST);
		searchPanel.setBackground(Color.white);
		SimpleDateFormat format = new SimpleDateFormat("F/M/yy h:mm a");
		noteList.setSize(800, 600);
		noteList.getColumnModel().getColumn(0).setCellRenderer(MyCellRenderer);
		noteList.getColumnModel().getColumn(0).setCellEditor(new MyCellEditor());
		int lines = countLines(((MyCellEditor) MyCellEditor).getNoteText());  // counts lines needed 
		System.out.println(lines);
		((MyCellRenderer) MyCellRenderer).setLines(lines);
		noteList.setTableHeader(null);
		JScrollPane notePane = new JScrollPane(noteList);
		JPanel noteArea = new JPanel(new BorderLayout()); 
		JPanel sortArea = new JPanel(new BorderLayout());   // dropdown menus
		sortArea.add(searchPanel, BorderLayout.NORTH);   // panel to hold sorting area
		sortArea.add(dropdown, BorderLayout.CENTER);
		sortArea.add(sortBy, BorderLayout.EAST);
		JLabel noteLabel = new JLabel("My Notebook:");
		noteLabel.setFont(f);
		noteArea.add(noteLabel, BorderLayout.NORTH);
		noteArea.add(notePane, BorderLayout.CENTER);
		mainContainer.add(noteArea, BorderLayout.CENTER);
		mainContainer.add(sortArea, BorderLayout.NORTH);
		mainContainer.setBackground(Color.white);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		//
	}
	// counts lines 
	public static int countLines(JTextArea textArea) {
		AttributedString text = new AttributedString(textArea.getText());
		FontRenderContext frc = textArea.getFontMetrics(textArea.getFont())
				.getFontRenderContext();

		int lines = 0;
		if (!textArea.getText().equals("") )
		{
			AttributedCharacterIterator charIt = text.getIterator();
			LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(charIt, frc);
			float formatWidth = (float) textArea.getSize().width;
			lineMeasurer.setPosition(charIt.getBeginIndex());
			while (lineMeasurer.getPosition() < charIt.getEndIndex()) {
				lineMeasurer.nextLayout(formatWidth);
				lines++;
			}
			for(int i = 0; i < textArea.getText().length();i++)
			{
				if(textArea.getText().charAt(i) == '\r' || textArea.getText().charAt(i) == '\n')
					lines++;
			}
		}
		else
		{
			lines = 1;
		}
		return lines;
	}

	public void setFirstParam(String firstParam)
	{
		this.firstParam = firstParam;
	}

	public void setSecondParam(String secondParam)
	{
		this.secondParam = secondParam;
	}

	static private String selectedString(ItemSelectable is) {
		Object selected[] = is.getSelectedObjects();
		return ((selected == null || selected.length == 0) ? "" : (String)selected[0]);
	}  




}
