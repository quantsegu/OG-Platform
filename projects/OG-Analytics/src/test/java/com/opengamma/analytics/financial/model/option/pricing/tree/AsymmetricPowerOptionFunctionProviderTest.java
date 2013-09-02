/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.model.option.pricing.tree;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.opengamma.analytics.financial.greeks.Greek;
import com.opengamma.analytics.financial.greeks.GreekResultCollection;
import com.opengamma.analytics.math.statistics.distribution.NormalDistribution;
import com.opengamma.analytics.math.statistics.distribution.ProbabilityDistribution;

/**
 * 
 */
public class AsymmetricPowerOptionFunctionProviderTest {

  private static final ProbabilityDistribution<Double> NORMAL = new NormalDistribution(0, 1);

  private static final BinomialTreeOptionPricingModel _model = new BinomialTreeOptionPricingModel();
  private static final double SPOT = 10.;
  private static final double POWER = 2.;
  private static final double[] STRIKES = new double[] {97., 105., 105.1, 114. };
  private static final double TIME = 4.2;
  private static final double[] INTERESTS = new double[] {-0.01, 0.017, 0.05 };
  private static final double[] VOLS = new double[] {0.05, 0.1, 0.5 };
  private static final double[] DIVIDENDS = new double[] {0.005, 0.014 };

  /**
   * 
   */
  @Test
  public void priceLatticeTest() {
    final LatticeSpecification[] lattices = new LatticeSpecification[] {new CoxRossRubinsteinLatticeSpecification(), new JarrowRuddLatticeSpecification(), new TrigeorgisLatticeSpecification(),
        new JabbourKraminYoungLatticeSpecification(), new TianLatticeSpecification() };

    /**
     * Since d1, d2 in Black-Scholes formula are not relevant in the case of power option, Leisen-Reimer is poor approximation 
     */
    //    final LatticeSpecification[] lattices = new LatticeSpecification[] {new LeisenReimerLatticeSpecification() };

    final boolean[] tfSet = new boolean[] {true, false };
    for (final LatticeSpecification lattice : lattices) {
      for (final boolean isCall : tfSet) {
        for (final double strike : STRIKES) {
          for (final double interest : INTERESTS) {
            for (final double vol : VOLS) {
              final int nSteps = 331;
              for (final double dividend : DIVIDENDS) {
                final OptionFunctionProvider1D function = new AsymmetricPowerOptionFunctionProvider(strike, nSteps, isCall, POWER);
                final double exactDiv = price(SPOT, strike, TIME, vol, interest, interest - dividend, isCall, POWER);
                final double resDiv = _model.getPrice(lattice, function, SPOT, TIME, vol, interest, dividend);
                final double refDiv = Math.max(Math.abs(exactDiv), 1.) * 1.e-2;
                //                  System.out.println(exactDiv + "\t" + resDiv);
                assertEquals(resDiv, exactDiv, refDiv);
              }
            }
          }
        }
      }
    }
  }

  /**
   * The dividend is cash or proportional to asset price
   */
  @Test
  public void priceDiscreteDividendTest() {
    final LatticeSpecification[] lattices = new LatticeSpecification[] {new CoxRossRubinsteinLatticeSpecification(), new JarrowRuddLatticeSpecification(), new TrigeorgisLatticeSpecification(),
        new JabbourKraminYoungLatticeSpecification(), new TianLatticeSpecification() };
    //    final LatticeSpecification[] lattices = new LatticeSpecification[] {new LeisenReimerLatticeSpecification() };

    final double[] propDividends = new double[] {0.01, 0.01, 0.01 };
    final double[] cashDividends = new double[] {0.5, 1., 0.8 };
    final double[] dividendTimes = new double[] {TIME / 6., TIME / 3., TIME / 2. };

    final boolean[] tfSet = new boolean[] {true, false };
    for (final LatticeSpecification lattice : lattices) {
      for (final boolean isCall : tfSet) {
        for (final double strike : STRIKES) {
          for (final double interest : INTERESTS) {
            for (final double vol : VOLS) {
              final int nSteps = 731;
              final OptionFunctionProvider1D function = new AsymmetricPowerOptionFunctionProvider(strike, nSteps, isCall, POWER);
              final DividendFunctionProvider cashDividend = new CashDividendFunctionProvider(dividendTimes, cashDividends);
              final DividendFunctionProvider propDividend = new ProportionalDividendFunctionProvider(dividendTimes, propDividends);
              final double resSpot = SPOT * (1. - propDividends[0]) * (1. - propDividends[1]) * (1. - propDividends[2]);
              final double modSpot = SPOT - cashDividends[0] * Math.exp(-interest * dividendTimes[0]) - cashDividends[1] * Math.exp(-interest * dividendTimes[1]) - cashDividends[2] *
                  Math.exp(-interest * dividendTimes[2]);
              final double exactProp = price(resSpot, strike, TIME, vol, interest, interest, isCall, POWER);
              final double appCash = price(modSpot, strike, TIME, vol, interest, interest, isCall, POWER);
              final double resProp = _model.getPrice(lattice, function, SPOT, TIME, vol, interest, propDividend);
              final double refProp = Math.max(Math.abs(exactProp), 1.) * 1.e-2;
              assertEquals(resProp, exactProp, refProp);
              final double resCash = _model.getPrice(lattice, function, SPOT, TIME, vol, interest, cashDividend);
              final double refCash = Math.max(Math.abs(appCash), 1.) * 1.e-1;
              assertEquals(resCash, appCash, refCash);
            }
          }
        }
      }
    }
  }

  /**
   * 
   */
  @Test
  public void greekTest() {
    final LatticeSpecification[] lattices = new LatticeSpecification[] {new CoxRossRubinsteinLatticeSpecification(), new JarrowRuddLatticeSpecification(), new TrigeorgisLatticeSpecification(),
        new JabbourKraminYoungLatticeSpecification(), new TianLatticeSpecification() };
    //    final LatticeSpecification[] lattices = new LatticeSpecification[] {new LeisenReimerLatticeSpecification() };

    final boolean[] tfSet = new boolean[] {true, false };
    for (final LatticeSpecification lattice : lattices) {
      for (final boolean isCall : tfSet) {
        for (final double strike : STRIKES) {
          for (final double interest : INTERESTS) {
            for (final double vol : VOLS) {
              final int nSteps = 731;
              for (final double dividend : DIVIDENDS) {
                final OptionFunctionProvider1D function = new AsymmetricPowerOptionFunctionProvider(strike, nSteps, isCall, POWER);
                final GreekResultCollection resDiv = _model.getGreeks(lattice, function, SPOT, TIME, vol, interest, dividend);
                final double priceDiv = price(SPOT, strike, TIME, vol, interest, interest - dividend, isCall, POWER);
                final double refPriceDiv = Math.max(Math.abs(priceDiv), 1.) * 1.e-2;
                assertEquals(resDiv.get(Greek.FAIR_PRICE), priceDiv, refPriceDiv);
                final double deltaDiv = delta(SPOT, strike, TIME, vol, interest, interest - dividend, isCall, POWER);
                final double refDeltaDiv = Math.max(Math.abs(deltaDiv), 1.) * 1.e-2;
                assertEquals(resDiv.get(Greek.DELTA), deltaDiv, refDeltaDiv);
                final double gammaDiv = gamma(SPOT, strike, TIME, vol, interest, interest - dividend, isCall, POWER);
                final double refGammaDiv = Math.max(Math.abs(gammaDiv), 1.) * 1.e-2;
                assertEquals(resDiv.get(Greek.GAMMA), gammaDiv, refGammaDiv);
                final double thetaDiv = theta(SPOT, strike, TIME, vol, interest, interest - dividend, isCall, POWER);
                final double refThetaDiv = Math.max(Math.abs(thetaDiv), 1.) * 1.e-1;
                assertEquals(resDiv.get(Greek.THETA), thetaDiv, refThetaDiv);
              }
            }
          }
        }
      }
    }
  }

  /**
   * The dividend is cash or proportional to asset price
   */
  @Test
  public void greeksDiscreteDividendLatticeTest() {
    final LatticeSpecification[] lattices = new LatticeSpecification[] {new CoxRossRubinsteinLatticeSpecification(), new JarrowRuddLatticeSpecification(), new TrigeorgisLatticeSpecification(),
        new JabbourKraminYoungLatticeSpecification(), new TianLatticeSpecification() };
    //    final LatticeSpecification[] lattices = new LatticeSpecification[] {new LeisenReimerLatticeSpecification() };

    final double[] propDividends = new double[] {0.01, 0.02, 0.02 };
    final double[] cashDividends = new double[] {0.1, 0.4, 0.1 };
    final double[] dividendTimes = new double[] {TIME / 6., TIME / 3., TIME / 2. };

    final boolean[] tfSet = new boolean[] {true, false };
    for (final LatticeSpecification lattice : lattices) {
      for (final boolean isCall : tfSet) {
        for (final double strike : STRIKES) {
          for (final double interest : INTERESTS) {
            for (final double vol : VOLS) {
              final int nSteps = 851;
              //              final int nSteps = 8637;
              final double resSpot = SPOT * (1. - propDividends[0]) * (1. - propDividends[1]) * (1. - propDividends[2]);
              final double modSpot = SPOT - cashDividends[0] * Math.exp(-interest * dividendTimes[0]) - cashDividends[1] * Math.exp(-interest * dividendTimes[1]) - cashDividends[2] *
                  Math.exp(-interest * dividendTimes[2]);
              final double exactPriceProp = price(resSpot, strike, TIME, vol, interest, interest, isCall, POWER);
              final double exactDeltaProp = delta(resSpot, strike, TIME, vol, interest, interest, isCall, POWER);
              final double exactGammaProp = gamma(resSpot, strike, TIME, vol, interest, interest, isCall, POWER);
              final double exactThetaProp = theta(resSpot, strike, TIME, vol, interest, interest, isCall, POWER);

              final double appPriceCash = price(modSpot, strike, TIME, vol, interest, interest, isCall, POWER);
              final double appDeltaCash = delta(modSpot, strike, TIME, vol, interest, interest, isCall, POWER);
              final double appGammaCash = gamma(modSpot, strike, TIME, vol, interest, interest, isCall, POWER);
              final double appThetaCash = theta(modSpot, strike, TIME, vol, interest, interest, isCall, POWER);

              final OptionFunctionProvider1D function = new AsymmetricPowerOptionFunctionProvider(strike, nSteps, isCall, POWER);
              final DividendFunctionProvider cashDividend = new CashDividendFunctionProvider(dividendTimes, cashDividends);
              final DividendFunctionProvider propDividend = new ProportionalDividendFunctionProvider(dividendTimes, propDividends);
              final GreekResultCollection resProp = _model.getGreeks(lattice, function, SPOT, TIME, vol, interest, propDividend);
              final GreekResultCollection resCash = _model.getGreeks(lattice, function, SPOT, TIME, vol, interest, cashDividend);

              assertEquals(resProp.get(Greek.FAIR_PRICE), exactPriceProp, Math.max(1., Math.abs(exactPriceProp)) * 1.e-2);
              assertEquals(resProp.get(Greek.DELTA), exactDeltaProp, Math.max(1., Math.abs(exactDeltaProp)) * 1.e-1);
              assertEquals(resProp.get(Greek.GAMMA), exactGammaProp, Math.max(1., Math.abs(exactGammaProp)) * 1.e-1);
              assertEquals(resProp.get(Greek.THETA), exactThetaProp, Math.max(1., Math.abs(exactThetaProp)) * 1.e-1);

              assertEquals(resCash.get(Greek.FAIR_PRICE), appPriceCash, Math.max(1., Math.abs(appPriceCash)) * 1.e-1);
              assertEquals(resCash.get(Greek.DELTA), appDeltaCash, Math.max(1., Math.abs(appDeltaCash)) * 1.e-1);
              assertEquals(resCash.get(Greek.GAMMA), appGammaCash, Math.max(1., Math.abs(appGammaCash)) * 1.e-1);
              assertEquals(resCash.get(Greek.THETA), appThetaCash, Math.max(1., Math.abs(appThetaCash)));//theta is poorly approximated
            }
          }
        }
      }
    }
  }

  /**
   * non-constant volatility and interest rate
   */
  @Test
  public void timeVaryingVolTest() {
    final LatticeSpecification lattice1 = new TimeVaryingLatticeSpecification();
    final double[] time_set = new double[] {0.5, 1.2 };
    final int steps = 501;

    final double[] vol = new double[steps];
    final double[] rate = new double[steps];
    final double[] dividend = new double[steps];
    final double constA = 0.01;
    final double constB = 0.001;
    final double constC = 0.1;
    final double constD = 0.05;

    final boolean[] tfSet = new boolean[] {true, false };
    for (final boolean isCall : tfSet) {
      for (final double strike : STRIKES) {
        for (final double time : time_set) {
          for (int i = 0; i < steps; ++i) {
            rate[i] = constA + constB * i * time / steps;
            vol[i] = constC + constD * Math.sin(i * time / steps);
            dividend[i] = 0.005;
          }
          final double rateRef = constA + 0.5 * constB * time;
          final double volRef = Math.sqrt(constC * constC + 0.5 * constD * constD + 2. * constC * constD / time * (1. - Math.cos(time)) - constD * constD * 0.25 / time * Math.sin(2. * time));

          final OptionFunctionProvider1D function = new AsymmetricPowerOptionFunctionProvider(strike, steps, isCall, POWER);
          final double resPrice = _model.getPrice(function, SPOT, time, vol, rate, dividend);
          final GreekResultCollection resGreeks = _model.getGreeks(function, SPOT, time, vol, rate, dividend);

          final double resPriceConst = _model.getPrice(lattice1, function, SPOT, time, volRef, rateRef, dividend[0]);
          final GreekResultCollection resGreeksConst = _model.getGreeks(lattice1, function, SPOT, time, volRef, rateRef, dividend[0]);
          assertEquals(resPrice, resPriceConst, Math.max(Math.abs(resPriceConst), 1.) * 1.e-1);
          assertEquals(resGreeks.get(Greek.FAIR_PRICE), resGreeksConst.get(Greek.FAIR_PRICE), Math.max(Math.abs(resGreeksConst.get(Greek.FAIR_PRICE)), 1.) * 0.1);
          assertEquals(resGreeks.get(Greek.DELTA), resGreeksConst.get(Greek.DELTA), Math.max(Math.abs(resGreeksConst.get(Greek.DELTA)), 1.) * 0.1);
          assertEquals(resGreeks.get(Greek.GAMMA), resGreeksConst.get(Greek.GAMMA), Math.max(Math.abs(resGreeksConst.get(Greek.GAMMA)), 1.) * 0.1);
          assertEquals(resGreeks.get(Greek.THETA), resGreeksConst.get(Greek.THETA), Math.max(Math.abs(resGreeksConst.get(Greek.THETA)), 1.));
        }
      }
    }
  }

  /**
   * 
   */
  @Test
  public void getPowerTest() {
    final AsymmetricPowerOptionFunctionProvider function = new AsymmetricPowerOptionFunctionProvider(103., 1003, true, 12.);
    assertEquals(function.getPower(), 12.);
  }

  /**
   * 
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void negativePowerTest() {
    new AsymmetricPowerOptionFunctionProvider(103., 1003, true, -12.);
  }

  private double price(final double spot, final double strike, final double time, final double vol, final double interest, final double cost, final boolean isCall, final double power) {
    final double d1 = (Math.log(spot / Math.pow(strike, 1. / power)) + (cost + (power - 0.5) * vol * vol) * time) / vol / Math.sqrt(time);
    final double d2 = d1 - power * vol * Math.sqrt(time);
    final double sign = isCall ? 1. : -1.;
    return sign *
        (Math.pow(spot, power) * Math.exp(((power - 1.) * (interest + 0.5 * power * vol * vol) - power * (interest - cost)) * time) * NORMAL.getCDF(sign * d1) - strike * Math.exp((-interest) * time) *
            NORMAL.getCDF(sign * d2));
  }

  private double delta(final double spot, final double strike, final double time, final double vol, final double interest, final double cost, final boolean isCall, final double power) {
    final double d1 = (Math.log(spot / Math.pow(strike, 1. / power)) + (cost + (power - 0.5) * vol * vol) * time) / vol / Math.sqrt(time);
    final double sign = isCall ? 1. : -1.;
    return sign * power * Math.pow(spot, power - 1.) * Math.exp(((power - 1.) * (interest + 0.5 * power * vol * vol) - power * (interest - cost)) * time) * NORMAL.getCDF(sign * d1);
  }

  private double gamma(final double spot, final double strike, final double time, final double vol, final double interest, final double cost, final boolean isCall, final double power) {
    final double d1 = (Math.log(spot / Math.pow(strike, 1. / power)) + (cost + (power - 0.5) * vol * vol) * time) / vol / Math.sqrt(time);
    final double sign = isCall ? 1. : -1.;
    final double first = sign * power * (power - 1.) * Math.pow(spot, power - 2.) * Math.exp(((power - 1.) * (interest + 0.5 * power * vol * vol) - power * (interest - cost)) * time) *
        (NORMAL.getCDF(sign * d1));
    final double second = power * Math.pow(spot, power - 2.) * Math.exp(((power - 1.) * (interest + 0.5 * power * vol * vol) - power * (interest - cost)) * time) * NORMAL.getPDF(d1) / vol /
        Math.sqrt(time);
    return first + second;
  }

  private double theta(final double spot, final double strike, final double time, final double vol, final double interest, final double cost, final boolean isCall, final double power) {
    final double d1 = (Math.log(spot / Math.pow(strike, 1. / power)) + (cost + (power - 0.5) * vol * vol) * time) / vol / Math.sqrt(time);
    final double d2 = d1 - power * vol * Math.sqrt(time);
    final double sign = isCall ? 1. : -1.;
    final double cst = ((power - 1.) * (interest + 0.5 * power * vol * vol) - power * (interest - cost));

    final double firstTerm = sign *
        (cst * Math.pow(spot, power) * Math.exp(cst * time) * NORMAL.getCDF(sign * d1) + interest * strike * Math.exp(-interest * time) *
            NORMAL.getCDF(sign * d2));
    final double secondTerm = 0.5 * power * Math.pow(spot, power) * Math.exp(cst * time) * NORMAL.getPDF(d1) * vol / Math.sqrt(time);
    return -firstTerm - secondTerm;
  }

  //  /**
  //   * 
  //   */
  //  @Test
  //  public void functionTest() {
  //    final boolean[] tfSet = new boolean[] {true, false };
  //    final double eps = 1.e-6;
  //    for (final boolean isCall : tfSet) {
  //      for (final double strike : STRIKES) {
  //        for (final double interest : INTERESTS) {
  //          for (final double vol : VOLS) {
  //            for (final double dividend : DIVIDENDS) {
  //              final double delta = delta(SPOT, strike, TIME, vol, interest, interest - dividend, isCall, POWER);
  //              final double gamma = gamma(SPOT, strike, TIME, vol, interest, interest - dividend, isCall, POWER);
  //              final double theta = theta(SPOT, strike, TIME, vol, interest, interest - dividend, isCall, POWER);
  //              final double upSpot = price(SPOT + eps, strike, TIME, vol, interest, interest - dividend, isCall, POWER);
  //              final double downSpot = price(SPOT - eps, strike, TIME, vol, interest, interest - dividend, isCall, POWER);
  //              final double upSpotDelta = delta(SPOT + eps, strike, TIME, vol, interest, interest - dividend, isCall, POWER);
  //              final double downSpotDelta = delta(SPOT - eps, strike, TIME, vol, interest, interest - dividend, isCall, POWER);
  //              final double upTime = price(SPOT, strike, TIME + eps, vol, interest, interest - dividend, isCall, POWER);
  //              final double downTime = price(SPOT, strike, TIME - eps, vol, interest, interest - dividend, isCall, POWER);
  //              assertEquals(delta, 0.5 * (upSpot - downSpot) / eps, eps);
  //              assertEquals(gamma, 0.5 * (upSpotDelta - downSpotDelta) / eps, eps);
  //              assertEquals(theta, -0.5 * (upTime - downTime) / eps, eps);
  //            }
  //          }
  //        }
  //      }
  //    }
  //  }
}