package net.fortuna.ical4j.transform.recurrence;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Recur.Frequency;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.*;
import java.util.function.Function;

import static net.fortuna.ical4j.model.Recur.Frequency.MONTHLY;
import static net.fortuna.ical4j.model.Recur.Frequency.YEARLY;

/**
 * Applies BYMONTHDAY rules specified in this Recur instance to the specified date list. If no BYMONTHDAY rules are
 * specified the date list is returned unmodified.
 */
public class ByMonthDayRule extends AbstractDateExpansionRule {

    private transient Logger log = LoggerFactory.getLogger(ByMonthDayRule.class);

    private final NumberList monthDayList;

    public ByMonthDayRule(NumberList monthDayList, Frequency frequency) {
        super(frequency);
        this.monthDayList = monthDayList;
    }

    public ByMonthDayRule(NumberList monthDayList, Frequency frequency, Optional<WeekDay.Day> weekStartDay) {
        super(frequency, weekStartDay);
        this.monthDayList = monthDayList;
    }

    @Override
    public DateList transform(DateList dates) {
        if (monthDayList.isEmpty()) {
            return dates;
        }
        final DateList monthDayDates = Dates.getDateListInstance(dates);
        for (final Date date : dates) {
            if (EnumSet.of(MONTHLY, YEARLY).contains(getFrequency())) {
                monthDayDates.addAll(new ExpansionFilter(monthDayDates.getType()).apply(date));
            } else {
                Optional<Date> limit = new LimitFilter().apply(date);
                if (limit.isPresent()) {
                    monthDayDates.add(limit.get());
                }
            }
        }
        return monthDayDates;
    }

    private class LimitFilter implements Function<Date, Optional<Date>> {

        @Override
        public Optional<Date> apply(Date date) {
            final Calendar cal = getCalendarInstance(date, true);
            if (monthDayList.contains(cal.get(Calendar.DAY_OF_MONTH))) {
                return Optional.of(date);
            }
            return Optional.empty();
        }
    }

    private class ExpansionFilter implements Function<Date, List<Date>> {

        private final Value type;

        public ExpansionFilter(Value type) {
            this.type = type;
        }

        @Override
        public List<Date> apply(Date date) {
            List<Date> retVal = new ArrayList<>();
            final Calendar cal = getCalendarInstance(date, false);
            // construct a list of possible month days..
            for (final int monthDay : monthDayList) {
                if (monthDay == 0 || monthDay < -Dates.MAX_DAYS_PER_MONTH || monthDay > Dates.MAX_DAYS_PER_MONTH) {
                    if (log.isTraceEnabled()) {
                        log.trace("Invalid day of month: " + monthDay);
                    }
                    continue;
                }
                final int numDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (monthDay > 0) {
                    if (numDaysInMonth < monthDay) {
                        continue;
                    }
                    cal.set(Calendar.DAY_OF_MONTH, monthDay);
                } else {
                    if (numDaysInMonth < -monthDay) {
                        continue;
                    }
                    cal.set(Calendar.DAY_OF_MONTH, numDaysInMonth);
                    cal.add(Calendar.DAY_OF_MONTH, monthDay + 1);
                }
                retVal.add(Dates.getInstance(cal.getTime(), type));
            };
            return retVal;
        }
    }

    /**
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        log = LoggerFactory.getLogger(Recur.class);
    }
}
