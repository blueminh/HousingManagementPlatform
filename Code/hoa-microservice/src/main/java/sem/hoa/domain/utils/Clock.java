package sem.hoa.domain.utils;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;


/**
 * This utility class is a wrapper for the Calendar class and makes Testing easier.
 */
@Component
public class Clock {
    private final transient Calendar calendar;

    @Autowired
    public Clock() {
        this.calendar = Calendar.getInstance();
    }

    /**
     * Returns the current date.
     *
     * @return the current Date
     */
    public Date getCurrentDate() {
        return new Date(calendar.getTimeInMillis());
    }
}
