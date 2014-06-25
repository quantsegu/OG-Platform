/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.instrument.payment;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Arrays;

import org.testng.annotations.Test;
import org.threeten.bp.Period;
import org.threeten.bp.ZonedDateTime;

import com.opengamma.analytics.financial.instrument.index.IborIndex;
import com.opengamma.analytics.financial.interestrate.payments.derivative.Coupon;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponFixed;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponIborFlatCompoundingSpread;
import com.opengamma.analytics.financial.schedule.ScheduleCalculator;
import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventions;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCounts;
import com.opengamma.timeseries.DoubleTimeSeries;
import com.opengamma.timeseries.precise.zdt.ImmutableZonedDateTimeDoubleTimeSeries;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.DateUtils;

/**
 * 
 */
public class CouponIborFlatCompoundingSpreadDefinitionTest {

  private static final Period TENOR = Period.ofMonths(1);
  private static final int SETTLEMENT_DAYS = 2;
  private static final Calendar CALENDAR = new MondayToFridayCalendar("A");
  private static final DayCount DAY_COUNT_INDEX = DayCounts.ACT_360;
  private static final BusinessDayConvention BUSINESS_DAY = BusinessDayConventions.MODIFIED_FOLLOWING;
  private static final boolean IS_EOM = true;
  private static final Currency CUR = Currency.EUR;
  private static final IborIndex INDEX = new IborIndex(CUR, TENOR, SETTLEMENT_DAYS, DAY_COUNT_INDEX, BUSINESS_DAY, IS_EOM, "Index");

  private static final int NUM_PRDS = 6;
  private static final int NUM_OBS = 5;

  private static final ZonedDateTime ACCRUAL_START_DATE = DateUtils.getUTCDate(2011, 1, 6);
  private static final ZonedDateTime ACCRUAL_END_DATE = DateUtils.getUTCDate(2011, 7, 4);
  private static final ZonedDateTime PAYMENT_DATE = DateUtils.getUTCDate(2011, 7, 6);
  // The above dates are not standard but selected for insure correct testing.
  private static final ZonedDateTime[][] FIXING_DATES = new ZonedDateTime[NUM_PRDS][NUM_OBS];
  private static final double[][] WEIGHTS = new double[NUM_PRDS][NUM_OBS];
  static {
    for (int i = 0; i < NUM_OBS; ++i) {
      for (int j = 0; j < NUM_PRDS; ++j) {
        FIXING_DATES[j][i] = DateUtils.getUTCDate(2011, j + 1, 3 + 6 * i);
        WEIGHTS[j][i] = 2. * (NUM_OBS - i) / NUM_OBS / (NUM_OBS + 1.);
      }
    }
  }

  private static final DayCount DAY_COUNT_PAYMENT = DayCounts.ACT_365;
  private static final double ACCRUAL_FACTOR = DAY_COUNT_PAYMENT.getDayCountFraction(ACCRUAL_START_DATE, ACCRUAL_END_DATE);
  private static final double[] ACCRUAL_FACTORS = new double[NUM_PRDS];
  static {
    Arrays.fill(ACCRUAL_FACTORS, ACCRUAL_FACTOR / NUM_PRDS);
  }
  private static final double NOTIONAL = 1000000;
  private static final double SPREAD = 0.02;

  private static final CouponIborFlatCompoundingSpreadDefinition DFN1 = new CouponIborFlatCompoundingSpreadDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR,
      NOTIONAL, ACCRUAL_FACTORS, INDEX, FIXING_DATES, WEIGHTS, CALENDAR, SPREAD);

  private static final ZonedDateTime REFERENCE_DATE = DateUtils.getUTCDate(2010, 12, 27);

  private static ZonedDateTime[][] EXP_START_DATES = new ZonedDateTime[NUM_PRDS][NUM_OBS];
  private static ZonedDateTime[][] EXP_END_DATES = new ZonedDateTime[NUM_PRDS][NUM_OBS];
  private static double[][] FIX_ACC_FACTORS = new double[NUM_PRDS][NUM_OBS];
  static {
    for (int i = 0; i < NUM_OBS; ++i) {
      for (int j = 0; j < NUM_PRDS; ++j) {
        EXP_START_DATES[j][i] = ScheduleCalculator.getAdjustedDate(FIXING_DATES[j][i], INDEX.getSpotLag(), CALENDAR);
        EXP_END_DATES[j][i] = ScheduleCalculator.getAdjustedDate(EXP_START_DATES[j][i], INDEX.getTenor(), INDEX.getBusinessDayConvention(), CALENDAR, INDEX.isEndOfMonth());
        FIX_ACC_FACTORS[j][i] = INDEX.getDayCount().getDayCountFraction(EXP_START_DATES[j][i], EXP_END_DATES[j][i], CALENDAR);
      }
    }
  }

  private static final CouponIborFlatCompoundingSpreadDefinition DFN2 = new CouponIborFlatCompoundingSpreadDefinition(CUR, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR,
      NOTIONAL, ACCRUAL_FACTORS, INDEX, FIXING_DATES, WEIGHTS, EXP_START_DATES, EXP_END_DATES, FIX_ACC_FACTORS, SPREAD);

  /**
   * 
   */
  @Test
  public void toDerivativeTest() {
    final ZonedDateTime[] dates1 = new ZonedDateTime[NUM_PRDS * NUM_OBS];
    final double[] rates1 = new double[NUM_PRDS * NUM_OBS];
    Arrays.fill(rates1, 0.01);
    for (int i = 0; i < NUM_PRDS; ++i) {
      for (int j = 0; j < NUM_OBS; ++j) {
        dates1[NUM_OBS * i + j] = FIXING_DATES[i][j];
      }
    }
    final DoubleTimeSeries<ZonedDateTime> fixingTS1 = ImmutableZonedDateTimeDoubleTimeSeries.ofUTC(dates1, rates1);
    final Coupon derivative1 = DFN1.toDerivative(FIXING_DATES[0][0].minusDays(10), fixingTS1);
    final Coupon derivative2 = DFN1.toDerivative(FIXING_DATES[NUM_PRDS - 1][NUM_OBS - 1].plusDays(1), fixingTS1);
    final CouponIborFlatCompoundingSpread derivative3 = (CouponIborFlatCompoundingSpread) DFN1.toDerivative(FIXING_DATES[2][3].minusDays(1), fixingTS1);

    assertTrue((derivative2 instanceof CouponFixed));
    assertTrue((derivative1 instanceof CouponIborFlatCompoundingSpread));

    assertEquals(NUM_PRDS - 2, derivative3.getFixingPeriodAccrualFactor().length);
    assertEquals(NUM_PRDS - 2, derivative3.getFixingPeriodEndTime().length);
    assertEquals(NUM_PRDS - 2, derivative3.getFixingPeriodStartTime().length);
    assertEquals(NUM_PRDS - 2, derivative3.getFixingTime().length);
    assertEquals(NUM_PRDS - 2, derivative3.getPaymentAccrualFactors().length);

    final double[] cpa = new double[2];
    double rate = 0.;
    for (int j = 0; j < NUM_OBS; ++j) {
      rate += WEIGHTS[0][j] * 0.01;
    }
    cpa[0] = (rate + SPREAD) * ACCRUAL_FACTORS[0];

    rate = 0.0;
    for (int j = 0; j < NUM_OBS; ++j) {
      rate += WEIGHTS[1][j] * 0.01;
    }
    cpa[1] = (rate + SPREAD) * ACCRUAL_FACTORS[1] + cpa[0] * rate * ACCRUAL_FACTORS[1];

    assertEquals(cpa[0] + cpa[1], derivative3.getAmountAccrued(), 1.e-14);

    try {
      DFN1.toDerivative(PAYMENT_DATE.plusDays(10), fixingTS1);
      throw new RuntimeException();
    } catch (final Exception e) {
      assertEquals("date is after payment date", e.getMessage());
    }
    try {
      DFN1.toDerivative(DateUtils.getUTCDate(2011, 5, 3), ImmutableZonedDateTimeDoubleTimeSeries.ofUTC(new ZonedDateTime[] {DateUtils.getUTCDate(2010, 2, 7) }, new double[] {0.01 }));
      throw new RuntimeException();
    } catch (final Exception e) {
      assertEquals("Could not get fixing value for date " + FIXING_DATES[0][0], e.getMessage());
    }
  }

  /**
   * 
   */
  @Test
  public void exceptionTest() {
    final ZonedDateTime afterPayment = PAYMENT_DATE.plusDays(1);
    try {
      DFN1.toDerivative(afterPayment);
      throw new RuntimeException();
    } catch (final Exception e) {
      assertEquals("date is after payment date", e.getMessage());
    }
    final ZonedDateTime afterFixing = FIXING_DATES[0][0].plusDays(1);
    try {
      DFN1.toDerivative(afterFixing);
      throw new RuntimeException();
    } catch (final Exception e) {
      assertEquals("Do not have any fixing data but are asking for a derivative at " + afterFixing + " which is after fixing date " + FIXING_DATES[0][0], e.getMessage());
    }
  }

  /**
   * 
   */
  @Test
  public void consistencyTest() {
    final CouponIborFlatCompoundingSpreadDefinition dfn1WithDouble = DFN1.withNotional(NOTIONAL * 2);

    assertEquals(DFN1.getSpread(), DFN2.getSpread());
    assertEquals(DFN1.getSpread(), dfn1WithDouble.getSpread());

    final CouponIborFlatCompoundingSpreadDefinition dfn1 = CouponIborFlatCompoundingSpreadDefinition.from(CUR, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL,
        ACCRUAL_FACTORS, INDEX, FIXING_DATES, WEIGHTS, CALENDAR, SPREAD);
    final CouponIborFlatCompoundingSpreadDefinition dfn2 = CouponIborFlatCompoundingSpreadDefinition.from(CUR, PAYMENT_DATE, ACCRUAL_START_DATE, ACCRUAL_END_DATE, ACCRUAL_FACTOR, NOTIONAL,
        ACCRUAL_FACTORS, INDEX, FIXING_DATES, WEIGHTS, EXP_START_DATES, EXP_END_DATES, FIX_ACC_FACTORS, SPREAD);

    assertTrue(DFN1.equals(dfn1));
    assertEquals(DFN1.hashCode(), dfn1.hashCode());
    assertTrue(DFN2.equals(dfn2));
    assertEquals(DFN2.hashCode(), dfn2.hashCode());

    assertFalse(DFN1.hashCode() == dfn1WithDouble.hashCode());
    assertFalse(DFN1.equals(dfn1WithDouble));

    assertTrue(DFN1.toDerivative(REFERENCE_DATE).equals(dfn1.toDerivative(REFERENCE_DATE)));

  }

}