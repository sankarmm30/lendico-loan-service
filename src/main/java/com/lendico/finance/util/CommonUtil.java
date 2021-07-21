package com.lendico.finance.util;

import com.lendico.finance.constant.GlobalConstant;
import org.decimal4j.util.DoubleRounder;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
public class CommonUtil {

    public static boolean isNull(final Object value) {

        return value == null;
    }

    public static boolean isNotNull(final Object value) {

        return value != null;
    }

    public static Double round(final Double inputValue) {

        return DoubleRounder.round(inputValue, GlobalConstant.NO_OF_PRECISION);
    }

    public static LocalDateTime addMonth(final LocalDateTime inputDate, final Integer numberOfMonth,
                                         final Integer dayExpected) {

        LocalDateTime localDateTime = inputDate.plusMonths(numberOfMonth);

        return isLastDayOfMonth(localDateTime) ? localDateTime : localDateTime.plusDays(dayExpected - localDateTime.getDayOfMonth());
    }

    public static Boolean isLastDayOfMonth(final LocalDateTime inputDate) {

        return inputDate.toLocalDate().isEqual(inputDate.toLocalDate().with(TemporalAdjusters.lastDayOfMonth()));
    }
}
