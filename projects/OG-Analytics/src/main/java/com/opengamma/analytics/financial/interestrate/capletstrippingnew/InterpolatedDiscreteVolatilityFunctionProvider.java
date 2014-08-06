/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.capletstrippingnew;

import org.apache.commons.lang.NotImplementedException;

import com.opengamma.analytics.math.interpolation.Interpolator1D;
import com.opengamma.analytics.math.matrix.DoubleMatrix1D;
import com.opengamma.analytics.math.matrix.DoubleMatrix2D;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.tuple.DoublesPair;

/**
 * 
 */
public class InterpolatedDiscreteVolatilityFunctionProvider extends DiscreteVolatilityFunctionProvider {

  private final InterpolatedVectorFunctionProvider _funcPro;

  public InterpolatedDiscreteVolatilityFunctionProvider(final double[] knotPoints, final Interpolator1D interpolator) {
    _funcPro = new InterpolatedVectorFunctionProvider(interpolator, knotPoints);

  }

  @Override
  public int getNumModelParameters() {
    throw new NotImplementedException();
  }

  @Override
  public DiscreteVolatilityFunction from(final DoublesPair[] expiryStrikePoints) {
    ArgumentChecker.noNulls(expiryStrikePoints, "expiryStrikePoints");
    final int n = expiryStrikePoints.length;
    final Double[] expiries = new Double[n];
    for (int i = 0; i < n; i++) {
      expiries[i] = expiryStrikePoints[i].first;
    }
    final VectorFunction vf = _funcPro.from(expiries);

    return new DiscreteVolatilityFunction() {

      @Override
      public int getSizeOfRange() {
        return vf.getSizeOfRange();
      }

      @Override
      public int getSizeOfDomain() {
        return vf.getSizeOfDomain();
      }

      @Override
      public DoubleMatrix2D evaluateJacobian(final DoubleMatrix1D x) {
        return vf.evaluateJacobian(x);
      }

      @Override
      public DoubleMatrix1D evaluate(final DoubleMatrix1D x) {
        return vf.evaluate(x);
      }
    };
  }

}
