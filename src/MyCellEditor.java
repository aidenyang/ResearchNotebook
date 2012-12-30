import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.XMLGregorianCalendar;

import org.geworkbench.components.genspace.GenSpaceServerFactory;
import org.geworkbench.components.genspace.server.stubs.AnalysisEvent;


public class MyCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	JPanel secondPanel;
	JPanel panel;
	JPanel buttonPanel;
	JTextArea noteInfo;
	JTextArea noteText;
	SimpleDateFormat format = new SimpleDateFormat("F/M/yy h:mm a");
	JScrollPane scroll;
	AnalysisEvent currentEvent;
	static int lines = 0;
	
	public MyCellEditor()
	{
		panel = new JPanel();
		secondPanel = new JPanel();
		buttonPanel = new JPanel();
		noteInfo = new JTextArea();
		noteInfo.setEditable(false);
		secondPanel.setLayout(new BorderLayout());
		panel.setLayout(new BorderLayout());
		buttonPanel.setLayout(new FlowLayout());
		noteText = new JTextArea();
		noteText.setEditable(true);
		noteText.setLineWrap(true);
		noteText.setWrapStyleWord(true);
		scroll = new JScrollPane(noteText);
		scroll.setSize(new Dimension(noteText.getPreferredSize()));	
		Button saveButton = new Button("Save Note");
		;
		saveButton.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent event)
			{
				stopCellEditing();
				JOptionPane.showMessageDialog(panel, "Note saved.");
			}
		});
		buttonPanel.add(saveButton);
		secondPanel.add(noteInfo, BorderLayout.NORTH);
		secondPanel.add(scroll, BorderLayout.CENTER);
		panel.add(secondPanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.EAST);
		panel.setBackground(Color.white);
	}
	public JTextArea getNoteText()
	{
		return noteText;
	}
	@Override
	public void addCellEditorListener(CellEditorListener listener) {
		
	}

	@Override
	public void cancelCellEditing() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getCellEditorValue()
	{
		String cellText = (noteText).getText();
		return cellText;
	}

	@Override
	public boolean isCellEditable(EventObject arg0) {
		return true;
	}

	@Override
	public void removeCellEditorListener(CellEditorListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldSelectCell(EventObject arg0) {
		return false;
	}

	@Override
	public boolean stopCellEditing() {
		currentEvent.setNote((String) getCellEditorValue());
		GenSpaceServerFactory.getPrivUsageFacade().saveNote(currentEvent);
		return true;
	}
	private Date convertToDate(XMLGregorianCalendar cal) {
		return DatatypeConverter.parseDateTime(cal.toXMLFormat()).getTime();
	}
	
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		SimpleDateFormat format = new SimpleDateFormat("F/M/yy h:mm a");
		TableModel lm = table.getModel();
		AnalysisEvent e = (AnalysisEvent) lm.getValueAt(row, 0); 
		currentEvent = e;
		noteInfo.setText(e.getTool().getName() + " at " + format.format(convertToDate(e.getCreatedAt())));
		noteText.setText(e.getNote());
		table.setRowHeight(row, noteText.getFontMetrics(noteText.getFont()).getHeight() * (NotebookView.countLines(noteText) +2));
		return panel;
	}
	
	

	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}


	
}
