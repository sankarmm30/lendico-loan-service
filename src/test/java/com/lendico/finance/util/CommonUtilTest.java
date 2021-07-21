package com.lendico.finance.util;

import com.lendico.finance.model.GeneratePlanRequestDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDateTime;

@RunWith(JUnit4.class)
public class CommonUtilTest {

    @DisplayName("Test isNotNull method")
    @Test
    public void testIsNotNull() {

        Assert.assertTrue(CommonUtil.isNotNull(GeneratePlanRequestDto.builder()
                .loanAmount(1000.0)
                .nominalRate(1.0)
                .duration(1)
                .startDate(LocalDateTime.now())
                .build()));

        Assert.assertFalse(CommonUtil.isNotNull(null));
    }

    @DisplayName("Test round method")
    @Test
    public void testRoundOff() {

        Assert.assertEquals(2, CommonUtil.round(1.9999), 0);
        Assert.assertEquals(1.23, CommonUtil.round(1.234), 0);
        Assert.assertEquals(0.96, CommonUtil.round(0.956666), 0);
    }

    @DisplayName("Test addMonth method with valid dates")
    @Test
    public void testAddMonth() {

        // input: 2018-01-01 , expected: 2018-02-01
        Assert.assertEquals(LocalDateTime.of(2018, 2,1, 0, 0),
                CommonUtil.addMonth(LocalDateTime.of(2018, 1,1, 0, 0),
                        1, 1));

        // input: 2020-01-30 , expected: 2020-02-29
        Assert.assertEquals(LocalDateTime.of(2020, 2,29, 0, 0),
                CommonUtil.addMonth(LocalDateTime.of(2020, 1,30, 0, 0),
                        1, 30));

        // input: 2020-02-29 , expected: 2020-03-30
        Assert.assertEquals(LocalDateTime.of(2020, 3,30, 0, 0),
                CommonUtil.addMonth(LocalDateTime.of(2020, 2,29, 0, 0),
                        1, 30));
    }

    @DisplayName("Test isLastDayOfMonth method with valid dates")
    @Test
    public void testIsLastDayOfMonth() {

        // input: 2020-02-29, expected: true
        Assert.assertTrue(CommonUtil.isLastDayOfMonth(LocalDateTime.of(2020, 2,29, 0, 0)));

        // input: 2020-02-28, expected: false
        Assert.assertFalse(CommonUtil.isLastDayOfMonth(LocalDateTime.of(2020, 2,28, 0, 0)));
    }
}
