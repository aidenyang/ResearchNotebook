package org.geworkbench.components.genspace.notebook;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.XMLGregorianCalendar;

import org.geworkbench.components.genspace.GenSpaceServerFactory;
import org.geworkbench.components.genspace.server.stubs.AnalysisEvent;

public class NoteTester {
	public static void main(String[] args) {
		
		//Log in to genspace
		System.out.println("Logging in");
		GenSpaceServerFactory.userLogin("jon", "test123");
		System.out.println("Getting my notes");
		
		/**
		 * Get our events. Sort by tool by setting the second parameter to "tool"
		 * Search/filter by providing the search term as the first parameter.
		 */
		List<AnalysisEvent> events = GenSpaceServerFactory.getPrivUsageFacade().getMyNotes(null, null);
		SimpleDateFormat format = new SimpleDateFormat("F/M/yy h:mm a");
		for(AnalysisEvent e : events)
		{
			System.out.println(e.getTool().getName() + " at " + format.format(convertToDate(e.getCreatedAt())) + " Note: " + e.getNote());
		}
		events.get(0).setNote("This is a note that I'm setting on the first analysis");
		GenSpaceServerFactory.getPrivUsageFacade().saveNote(events.get(0));
		
		System.out.println("Now fetching them ordered by tool");
		
		events = GenSpaceServerFactory.getPrivUsageFacade().getMyNotes(null, "tool");
		for(AnalysisEvent e : events)
		{
			System.out.println(e.getTool().getName() + " at " + format.format(convertToDate(e.getCreatedAt())) + " Note: " + e.getNote());
		}
		
		System.out.println("Now searching for Anova only");
		events = GenSpaceServerFactory.getPrivUsageFacade().getMyNotes("anova",null);
		for(AnalysisEvent e : events)
		{
			System.out.println(e.getTool().getName() + " at " + format.format(convertToDate(e.getCreatedAt())) + " Note: " + e.getNote());
		}
		
	}
	public static Date convertToDate(XMLGregorianCalendar cal)
	{
		return DatatypeConverter.parseDateTime(cal.toXMLFormat()).getTime();
	}
}
