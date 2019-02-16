package com.events.UserEvents.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static Date formatDate(final String date) throws Exception {
	
		
		SimpleDateFormat format = new SimpleDateFormat( "MM/dd/yyyy" );  
		
		return( format.parse(  date ) );
	}
	
	public static String getStringFormat(final Date date ) throws Exception {
		
		
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy");
		String d =  formatter.format(date);
		System.out.println("Date : " + d);
		return d;
	}
}
