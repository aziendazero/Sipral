package com.phi.json;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Date serializer.
 */
public class DateSerializer extends JsonSerializer<Date>{

//	JAVA 7:
//	private static final SimpleDateFormat dateFormat = DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	
	@Override
	public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {

//		JAVA 7:
//		String formattedDate = dateFormat.format(date);
//		gen.writeString(formattedDate);
		
//		Java EE:
//		Calendar c = Calendar.getInstance();
//		c.setTime(date);
//		String str = DatatypeConverter.printDateTime(c);
		
		// Joda Time:		
		//DateTimeZone myZone = DateTimeZone.forID("Europe/Rome");
		//DateTimeZone.setDefault(myZone);
		
		Calendar dateCalendar = Calendar.getInstance();
		dateCalendar.setTime(date);
				
		DateTime dt = new DateTime(dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH) + 1, dateCalendar.get(Calendar.DATE), dateCalendar.get(Calendar.HOUR_OF_DAY), dateCalendar.get(Calendar.MINUTE), dateCalendar.get(Calendar.SECOND), dateCalendar.get(Calendar.MILLISECOND)); 		
		
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		fmt.withZone(DateTimeZone.getDefault());
		String str = fmt.print(dt);
		
		gen.writeString(str);		
	}

}