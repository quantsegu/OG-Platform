/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.marketdata.manipulator.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.threeten.bp.Period;
import org.threeten.bp.ZonedDateTime;

import com.google.common.collect.ImmutableList;
import com.opengamma.util.ArgumentChecker;

/**
 * Shifts a volatility surface whose Y axis is time.
 */
@BeanDefinition(hierarchy = "immutable")
public final class DoubleDateSurfaceShift extends VolatilitySurfaceShiftManipulator {

  @PropertyDefinition(validate = "notNull")
  private final double[] _xValues;

  @PropertyDefinition(validate = "notNull")
  private final List<Period> _yValues;

  /* package */ DoubleDateSurfaceShift(ScenarioShiftType shiftType,
                                       double[] shiftValues,
                                       double[] xValues,
                                       List<Period> yValues) {
    super(shiftType, shiftValues);
    _xValues = ArgumentChecker.notEmpty(xValues, "xValues");
    _yValues = ArgumentChecker.notEmpty(yValues, "yValues");
  }

  @Override
  protected double[] getXValues(ZonedDateTime valuationTime) {
    return _xValues;
  }

  @Override
  protected double[] getYValues(ZonedDateTime valuationTime) {
    return yearFractions(_yValues, valuationTime);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code DoubleDateSurfaceShift}.
   * @return the meta-bean, not null
   */
  public static DoubleDateSurfaceShift.Meta meta() {
    return DoubleDateSurfaceShift.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(DoubleDateSurfaceShift.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static DoubleDateSurfaceShift.Builder builder() {
    return new DoubleDateSurfaceShift.Builder();
  }

  /**
   * Restricted constructor.
   * @param builder  the builder to copy from, not null
   */
  private DoubleDateSurfaceShift(DoubleDateSurfaceShift.Builder builder) {
    super(builder);
    JodaBeanUtils.notNull(builder._xValues, "xValues");
    JodaBeanUtils.notNull(builder._yValues, "yValues");
    this._xValues = builder._xValues.clone();
    this._yValues = ImmutableList.copyOf(builder._yValues);
  }

  @Override
  public DoubleDateSurfaceShift.Meta metaBean() {
    return DoubleDateSurfaceShift.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the xValues.
   * @return the value of the property, not null
   */
  public double[] getXValues() {
    return (_xValues != null ? _xValues.clone() : null);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the yValues.
   * @return the value of the property, not null
   */
  public List<Period> getYValues() {
    return _yValues;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  @Override
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      DoubleDateSurfaceShift other = (DoubleDateSurfaceShift) obj;
      return JodaBeanUtils.equal(getXValues(), other.getXValues()) &&
          JodaBeanUtils.equal(getYValues(), other.getYValues()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getXValues());
    hash += hash * 31 + JodaBeanUtils.hashCode(getYValues());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("DoubleDateSurfaceShift{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  @Override
  protected void toString(StringBuilder buf) {
    super.toString(buf);
    buf.append("xValues").append('=').append(JodaBeanUtils.toString(getXValues())).append(',').append(' ');
    buf.append("yValues").append('=').append(JodaBeanUtils.toString(getYValues())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code DoubleDateSurfaceShift}.
   */
  public static final class Meta extends VolatilitySurfaceShiftManipulator.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code xValues} property.
     */
    private final MetaProperty<double[]> _xValues = DirectMetaProperty.ofImmutable(
        this, "xValues", DoubleDateSurfaceShift.class, double[].class);
    /**
     * The meta-property for the {@code yValues} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<Period>> _yValues = DirectMetaProperty.ofImmutable(
        this, "yValues", DoubleDateSurfaceShift.class, (Class) List.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "xValues",
        "yValues");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1681280954:  // xValues
          return _xValues;
        case -1726182661:  // yValues
          return _yValues;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public DoubleDateSurfaceShift.Builder builder() {
      return new DoubleDateSurfaceShift.Builder();
    }

    @Override
    public Class<? extends DoubleDateSurfaceShift> beanType() {
      return DoubleDateSurfaceShift.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code xValues} property.
     * @return the meta-property, not null
     */
    public MetaProperty<double[]> xValues() {
      return _xValues;
    }

    /**
     * The meta-property for the {@code yValues} property.
     * @return the meta-property, not null
     */
    public MetaProperty<List<Period>> yValues() {
      return _yValues;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1681280954:  // xValues
          return ((DoubleDateSurfaceShift) bean).getXValues();
        case -1726182661:  // yValues
          return ((DoubleDateSurfaceShift) bean).getYValues();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code DoubleDateSurfaceShift}.
   */
  public static final class Builder extends VolatilitySurfaceShiftManipulator.Builder {

    private double[] _xValues;
    private List<Period> _yValues = new ArrayList<Period>();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(DoubleDateSurfaceShift beanToCopy) {
      this._xValues = beanToCopy.getXValues().clone();
      this._yValues = new ArrayList<Period>(beanToCopy.getYValues());
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1681280954:  // xValues
          return _xValues;
        case -1726182661:  // yValues
          return _yValues;
        default:
          return super.get(propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 1681280954:  // xValues
          this._xValues = (double[]) newValue;
          break;
        case -1726182661:  // yValues
          this._yValues = (List<Period>) newValue;
          break;
        default:
          super.set(propertyName, newValue);
          break;
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public DoubleDateSurfaceShift build() {
      return new DoubleDateSurfaceShift(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code xValues} property in the builder.
     * @param xValues  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder xValues(double[] xValues) {
      JodaBeanUtils.notNull(xValues, "xValues");
      this._xValues = xValues;
      return this;
    }

    /**
     * Sets the {@code yValues} property in the builder.
     * @param yValues  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder yValues(List<Period> yValues) {
      JodaBeanUtils.notNull(yValues, "yValues");
      this._yValues = yValues;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("DoubleDateSurfaceShift.Builder{");
      buf.append("xValues").append('=').append(JodaBeanUtils.toString(_xValues)).append(',').append(' ');
      buf.append("yValues").append('=').append(JodaBeanUtils.toString(_yValues));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
