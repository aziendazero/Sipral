package com.phi.json;

import java.io.IOException;
import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Date deserializer.
 */
public class DateDeserializer extends JsonDeserializer<Date> {
	
//	JAVA 7:
//	private static final SimpleDateFormat dateFormat = DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	
    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {

//    	JAVA 7:
//      String date = jsonparser.getText();
//      try {
//          return df.parse(date);
//      } catch (ParseException e) {
//          throw new RuntimeException(e);
//      }
    	
//		Java EE:
//    	String date = jsonparser.getText();
//    	
//    	Date d = DatatypeConverter.parseDateTime(date).getTime();
    	
//		Joda Time:
    	String date = jsonparser.getText();
    	
    	if (date.startsWith("\"")) {
    		date = date.substring(1);
    	}
    	
    	if (date.endsWith("\"")) {
    		date = date.substring(0, date.length()-1);
    	}
    	
    	DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
    	Date d = fmt.parseLocalDateTime(date).toDate();
    	
    	return d;
    }
}