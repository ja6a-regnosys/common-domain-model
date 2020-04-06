package org.isda.cdm.calculation;

import cdm.base.datetime.*;
import cdm.base.datetime.metafields.ReferenceWithMetaBusinessCenters;
import cdm.base.math.NonNegativeQuantity;
import cdm.base.staticdata.asset.rates.FloatingRateIndexEnum;
import cdm.base.staticdata.asset.rates.metafields.FieldWithMetaFloatingRateIndexEnum;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.rosetta.model.lib.records.DateImpl;
import org.isda.cdm.*;
import org.isda.cdm.functions.AbstractFunctionTest;
import org.isda.cdm.functions.FloatingAmount;
import org.isda.cdm.metafields.FieldWithMetaDayCountFractionEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.BigDecimalCloseTo.closeTo;

class FloatingAmountTest extends AbstractFunctionTest{
	
	@Inject Provider<FloatingAmount> floatingAmount;
    
	
	private static final FloatingInterestRate SPREAD = FloatingInterestRate.builder().setSpread(BigDecimal.valueOf(0)).build();
	
	private static final NonNegativeQuantity QUANTITY = NonNegativeQuantity.builder().setAmount(BigDecimal.valueOf(50_000_000)).build();
	
	private static final InterestRatePayout INTEREST_RATE_PAYOUT = InterestRatePayout.builder()
            .setRateSpecification(RateSpecification.builder()
                    .setFloatingRate(FloatingRateSpecification.builder()
                    		.setAssetIdentifier(AssetIdentifier.builder()
                    				.setRateOption(FloatingRateOption.builder()
                                          .setFloatingRateIndex(FieldWithMetaFloatingRateIndexEnum.builder()
                                        		  .setValue(FloatingRateIndexEnum.GBP_LIBOR_BBA)
                                        		  .build())
                                          .build())
                    				.build())
                    		.build())
                    .build())
            .setDayCountFraction(FieldWithMetaDayCountFractionEnum.builder().setValue(DayCountFractionEnum._30E_360).build())
            .setCalculationPeriodDates(CalculationPeriodDates.builder()
                    .setEffectiveDate((AdjustableOrRelativeDate.builder()
                            .setAdjustableDate(AdjustableDate.builder()
                                    .setUnadjustedDate(DateImpl.of(2018, 1, 3))
                                    .setDateAdjustments(BusinessDayAdjustments.builder()
                                            .setBusinessDayConvention(BusinessDayConventionEnum.NONE)
                                            .build())
                                    .build())
                            .build()))
                    .setTerminationDate(AdjustableOrRelativeDate.builder()
                            .setAdjustableDate(AdjustableDate.builder()
                                    .setUnadjustedDate(DateImpl.of(2020, 1, 3))
                                    .setDateAdjustments(BusinessDayAdjustments.builder()
                                            .setBusinessDayConvention(BusinessDayConventionEnum.MODFOLLOWING)
                                            .setBusinessCenters(BusinessCenters.builder()
                                                    .setBusinessCentersReference(ReferenceWithMetaBusinessCenters.builder()
                                                            .setExternalReference("primaryBusinessCenters")
                                                            .build())
                                                    .build())
                                            .build())
                                    .build())
                            .build())
                    .setCalculationPeriodFrequency((CalculationPeriodFrequency) CalculationPeriodFrequency.builder()
                            .setRollConvention(RollConventionEnum._3)
                            .setPeriodMultiplier(3)
                            .setPeriod(PeriodExtendedEnum.M)
                            .build())
                    .setCalculationPeriodDatesAdjustments(BusinessDayAdjustments.builder()
                            .setBusinessDayConvention(BusinessDayConventionEnum.MODFOLLOWING)
                            .setBusinessCenters(BusinessCenters.builder()
                                    .setBusinessCentersReference(ReferenceWithMetaBusinessCenters.builder()
                                            .setExternalReference("primaryBusinessCenters")
                                            .build())
                                    .build())
                            .build())
                    .build())
            .build();

    @Test
    void shouldApplyMultiplication() {
    	FloatingAmount floatingAmount = this.floatingAmount.get();
        BigDecimal result = floatingAmount.evaluate(INTEREST_RATE_PAYOUT, SPREAD, QUANTITY, DateImpl.of(2018, 1, 3));
        assertThat(result, closeTo(BigDecimal.valueOf(1093750), BigDecimal.valueOf(0.0000001)));
    }


}