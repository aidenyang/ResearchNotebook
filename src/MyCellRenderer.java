import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.XMLGregorianCalendar;

import org.geworkbench.components.genspace.server.stubs.AnalysisEvent;

public class MyCellRenderer extends JPanel implements TableCellRenderer{


	private int rows;
	private int lines;

	public void getRows(int rows)
	{
		this.rows = rows;
	}

	private Date convertToDate(XMLGregorianCalendar cal) {
		return DatatypeConverter.parseDateTime(cal.toXMLFormat()).getTime();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{
		SimpleDateFormat format = new SimpleDateFormat("F/M/yy h:mm a");
		JPanel panel = new JPanel();
		TableModel lm = table.getModel();
		AnalysisEvent e = (AnalysisEvent) lm.getValueAt(row, 0); 
		panel.setLayout(new BorderLayout());
		JTextArea noteInfo = new JTextArea(e.getTool().getName() + " at " + format.format(convertToDate(e.getCreatedAt())));
		noteInfo.setEditable(false);
		JTextArea noteText = new JTextArea(e.getNote());
		noteText.setEditable(true);
		noteText.setLineWrap(true);
		noteText.setWrapStyleWord(true);
		noteText.setSize(table.getWidth(), 0);
		table.setRowHeight(row, noteText.getFontMetrics(noteText.getFont()).getHeight() * (NotebookView.countLines(noteText)+2));
		panel.add(noteInfo, BorderLayout.NORTH);  
		panel.add(noteText, BorderLayout.CENTER);
		panel.setBackground(Color.white);
		return panel;
	}

	public void setLines(int lines)
	{
		this.lines = lines;
	}

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

}


