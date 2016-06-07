package com.yin.myproject.practice.json.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

public class Money implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4807833613734457731L;

    public static final Currency DEFAULT_CURRENCY = Currency.getInstance(Locale.CHINA);

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private BigDecimal denomination;

    public BigDecimal getDenomination() {
        return denomination;
    }

    private String currencyCode = DEFAULT_CURRENCY.getCurrencyCode();

    // protected Money() {
    // Locale l = Locale.CHINESE;
    // }

    public Money(BigDecimal denomination) {
        this.denomination = denomination;
        init();
    }

    private void init() {
        this.denomination = denomination.setScale(2, RoundingMode.HALF_EVEN);
    }

    public Money(Object obj) {
        if (obj instanceof Double) {
            denomination = new BigDecimal((Double) obj);
        } else if (obj instanceof String) {
            denomination = new BigDecimal((String) obj);
        } else if (obj instanceof Long) {
            Double _obj = (double) (((Long) obj) / 10000);
            denomination = new BigDecimal((Double) _obj);
        } else {
            this.denomination = new BigDecimal("0");
        }
        init();
    }

    public Money(double denomination) {
        this(new BigDecimal(denomination));
    }

    public Money multiplyBy(double multiplier) {
        return multiplyBy(new BigDecimal(multiplier));
    }

    public Money multiplyBy(BigDecimal multiplier) {
        return new Money(denomination.multiply(multiplier));
    }

    public Money multiplyBy(Money multiplier) {
        return new Money(denomination.multiply(multiplier.denomination));
    }

    public Money division(Money money) {
        if (money == null) {
            return this;
        }
        return new Money(denomination.divide((money.denomination), 2));
    }

    public Money add(Money money) {
        if (money == null) {
            return this;
        }
        if (!compatibleCurrency(money)) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        return new Money(denomination.add(money.denomination));
    }

    public Money subtract(Money money) {
        if (!compatibleCurrency(money))
            throw new IllegalArgumentException("Currency mismatch");

        return new Money(denomination.subtract(money.denomination));
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public boolean greaterThan(Money other) {
        return denomination.compareTo(other.denomination) > 0;
    }

    public boolean greaterOrEquals(Money other) {
        return denomination.compareTo(other.denomination) >= 0;
    }

    public boolean lessThan(Money other) {
        return denomination.compareTo(other.denomination) < 0;
    }

    public boolean lessOrEquals(Money other) {
        return denomination.compareTo(other.denomination) <= 0;
    }

    public boolean valEquals(Money other) {
        return denomination.compareTo(other.denomination) == 0;
    }

    public Currency getCurrency() {
        return Currency.getInstance(currencyCode);
    }

    @Override
    public String toString() {
        return String.format("%0$.2f", denomination);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
        result = prime * result + ((denomination == null) ? 0 : denomination.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Money other = (Money) obj;
        return compatibleCurrency(other) && true;
        // Objects.areEqual(denomination, other.denomination);
    }

    private boolean compatibleCurrency(Money money) {
        return isZero(denomination) || isZero(money.denomination) || currencyCode.equals(money.getCurrencyCode());
    }

    private boolean isZero(BigDecimal testedValue) {
        return BigDecimal.ZERO.compareTo(testedValue) == 0;
    }

    public double doubleValue() {
        return this.denomination.doubleValue();
    }

    public Long longValue() {
        return this.denomination.multiply(new BigDecimal((Long) new Long(10000))).longValue();
    }

    public Money negate() {
        return new Money(this.denomination.negate());
    }

    public Money abs() {
        return new Money(this.denomination.abs());
    }

    public static Money Zero() {
        return new Money(BigDecimal.ZERO);
    }

}
