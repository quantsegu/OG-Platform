/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.equity;

import com.opengamma.core.exchange.ExchangeSource;
import com.opengamma.core.holiday.HolidaySource;
import com.opengamma.core.position.impl.TradeImpl;
import com.opengamma.financial.analytics.interestratefuture.AbstractFutureSecurityVisitor;
import com.opengamma.financial.convention.ConventionBundleSource;
import com.opengamma.financial.equity.future.definition.EquityFutureDefinition;
import com.opengamma.financial.instrument.FixedIncomeFutureInstrumentDefinition;
import com.opengamma.financial.security.future.EquityFutureSecurity;

/**
 * Visits a Trade of an EquityFutureSecurity (OG-Financial)
 * Converts it to a EquityFutureDefinition (OG-Analytics)  
 * TODO - Not sure this should extend from what looks to be an InterestRateFutureConverter
 */
//TODO this is extending the wrong thing
public class EquityFutureConverter extends AbstractFutureSecurityVisitor<FixedIncomeFutureInstrumentDefinition<?>> {

  public EquityFutureConverter(final HolidaySource holidaySource, final ConventionBundleSource conventionSource, final ExchangeSource exchangeSource) {
  }

  /**
   * Converts an EquityFutureSecurity Trade to an EquityFutureDefinition
   * @param trade The trade
   * @return EquityFutureDefinition
   */
  public EquityFutureDefinition visitEquityFutureTrade(final TradeImpl trade) {

    final EquityFutureSecurity security = (EquityFutureSecurity) trade.getSecurity();

    // TODO Case 2011-5-27 Revisit use of trade._premium as a futures price (often simply be an index value). Ensure no payments are being automatically computed here.
    // What this futuresPrice represents is the last margin price, then when one computes pv, they get back the value expected if one unwinds the trade 
    final double futuresPrice = trade.getPremium();

    /* FIXME Case 2011-05-27 Revisit holiday conventions for input dates 
    final ConventionBundle conventions = super.getConventionSource().getConventionBundle(Identifier.of(InMemoryConventionBundleMaster.SIMPLE_NAME_SCHEME, currency + "_EQFUTURE"));
    final Calendar calendar = CalendarUtil.getCalendar(super.getHolidaySource(), currency); //TODO use exchange holiday
    final BusinessDayConvention businessDayConvention = conventions.getBusinessDayConvention();
    */

    return new EquityFutureDefinition(security.getExpiry().getExpiry(), security.getSettlementDate(), futuresPrice, security.getCurrency(), security.getUnitAmount());
  }
}
