import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTime {

    public static Date addDurationToDate(Date date, int hours, int mins) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, mins);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
    
    public static Date initDate(String stringDate){
        try {
        if(stringDate.split(" ").length==1){
                SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy");  
                return formatter1.parse(stringDate);
        }
        SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
        return formatter1.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
