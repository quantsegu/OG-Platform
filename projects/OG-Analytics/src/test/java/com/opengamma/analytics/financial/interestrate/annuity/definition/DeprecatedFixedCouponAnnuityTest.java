/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.annuity.definition;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.Test;

import com.opengamma.analytics.financial.interestrate.annuity.derivative.AnnuityCouponFixed;
import com.opengamma.analytics.financial.interestrate.payments.derivative.CouponFixed;
import com.opengamma.util.money.Currency;

/**
 * @deprecated This class tests deprecated functionality
 */
@Deprecated
public class DeprecatedFixedCouponAnnuityTest {
  private static final double[] PAYMENT_TIMES = new double[] {0.5, 1, 1.5, 2, 2.5, 3};
  private static final double NOTIONAL = 1000;
  private static final double COUPON_RATE = 0.05;
  private static final String CURVE_NAME = "A";
  private static final double[] YEAR_FRACTIONS = new double[] {0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
  private static final CouponFixed[] PAYMENTS;
  private static final double DIFF = 0.02;
  private static final CouponFixed[] HIGHER;
  private static final Currency CUR = Currency.EUR;

  static {
    final int n = PAYMENT_TIMES.length;
    PAYMENTS = new CouponFixed[n];
    HIGHER = new CouponFixed[n];
    for (int i = 0; i < n; i++) {
      PAYMENTS[i] = new CouponFixed(CUR, PAYMENT_TIMES[i], CURVE_NAME, YEAR_FRACTIONS[i], NOTIONAL, COUPON_RATE);
      HIGHER[i] = new CouponFixed(CUR, PAYMENT_TIMES[i], CURVE_NAME, YEAR_FRACTIONS[i], NOTIONAL, COUPON_RATE + DIFF);
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullPaymentTimes() {
    new AnnuityCouponFixed(CUR, null, NOTIONAL, COUPON_RATE, CURVE_NAME, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testEmptyPaymentTimes() {
    new AnnuityCouponFixed(CUR, new double[0], NOTIONAL, COUPON_RATE, CURVE_NAME, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullYearFractions() {
    new AnnuityCouponFixed(CUR, PAYMENT_TIMES, NOTIONAL, COUPON_RATE, null, CURVE_NAME, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testEmptyYearFractions() {
    new AnnuityCouponFixed(CUR, PAYMENT_TIMES, NOTIONAL, COUPON_RATE, new double[0], CURVE_NAME, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullCurveName() {
    new AnnuityCouponFixed(CUR, PAYMENT_TIMES, NOTIONAL, COUPON_RATE, (String) null, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testWrongArrayLength() {
    new AnnuityCouponFixed(CUR, new double[] {1, 2, 3}, NOTIONAL, COUPON_RATE, YEAR_FRACTIONS, CURVE_NAME, true);
  }

  @Test
  public void testConstructors() {
    final AnnuityCouponFixed annuity = new AnnuityCouponFixed(PAYMENTS);
    assertFalse(annuity.equals(new AnnuityCouponFixed(CUR, PAYMENT_TIMES, COUPON_RATE, CURVE_NAME, false)));
    assertEquals(new AnnuityCouponFixed(CUR, PAYMENT_TIMES, 1, COUPON_RATE, CURVE_NAME, true), new AnnuityCouponFixed(CUR, PAYMENT_TIMES, COUPON_RATE, CURVE_NAME, true));
    assertEquals(annuity, new AnnuityCouponFixed(CUR, PAYMENT_TIMES, NOTIONAL, COUPON_RATE, CURVE_NAME, false));
    assertEquals(annuity, new AnnuityCouponFixed(CUR, PAYMENT_TIMES, NOTIONAL, COUPON_RATE, YEAR_FRACTIONS, CURVE_NAME, false));
  }
  //
  // @Test
  // public void testWithRate() {
  // final FixedCouponAnnuity annuity = new FixedCouponAnnuity(PAYMENTS);
  // assertFalse(annuity.withRate(COUPON_RATE) == annuity);
  // assertEquals(annuity.withRate(COUPON_RATE), annuity);
  // assertEquals(annuity.withRate(COUPON_RATE + DIFF), new FixedCouponAnnuity(HIGHER));
  // }
}
