package com.lendico.finance.util;

import com.lendico.finance.constant.GlobalConstant;
import org.decimal4j.util.DoubleRounder;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public class CommonUtil {

    /**
     * This method returns true if the given object is not null
     *
     * @param value
     * @return
     */
    public static boolean isNotNull(final Object value) {

        return value != null;
    }

    /**
     * This method returns the double value after applying rounding off function.
     *
     * @param inputValue
     * @return
     */
    public static Double round(final Double inputValue) {

        return DoubleRounder.round(inputValue, GlobalConstant.NO_OF_PRECISION);
    }

    /**
     *
     * This method returns the LocalDateTime by adding numberOfMonth with inputDate.
     * Additionally it has logic to ensure that LocalDateTime is what we have expected. This will help in deriving the loan repayment days.
     *
     * Example: we want to get the 30th day from every month starting from 30-Dec-2019 to Apr-2020
     *
     * addMonth(2019-12-30, 1, 30) = 2020-01-30
     * addMonth(2020-01-30, 1, 30) = 2020-02-29
     * addMonth(2020-02-29, 1, 30) = 2020-03-30
     * addMonth(2020-03-30, 1, 30) = 2020-03-30
     *
     * @param inputDate
     * @param numberOfMonth
     * @param dayExpected
     * @return
     */
    public static LocalDateTime addMonth(final LocalDateTime inputDate, final Integer numberOfMonth,
                                         final Integer dayExpected) {

        LocalDateTime localDateTime = inputDate.plusMonths(numberOfMonth);

        return isLastDayOfMonth(localDateTime) ? localDateTime : localDateTime.plusDays(dayExpected - localDateTime.getDayOfMonth());
    }

    /**
     * This method returns true if the given date is last day of the month.
     *
     * @param inputDate
     * @return
     */
    public static Boolean isLastDayOfMonth(final LocalDateTime inputDate) {

        return inputDate.toLocalDate().isEqual(inputDate.toLocalDate().with(TemporalAdjusters.lastDayOfMonth()));
    }
}
