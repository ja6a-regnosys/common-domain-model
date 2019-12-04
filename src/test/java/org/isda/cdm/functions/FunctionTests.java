package org.isda.cdm.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.isda.cdm.NumberList;
import org.isda.cdm.test.Bar;
import org.isda.cdm.test.Bar.BarBuilder;
import org.isda.cdm.test.Baz;
import org.isda.cdm.test.Foo;
import org.isda.cdm.test.TypeToGroup;
import org.isda.cdm.test.TypeToGroup.TypeToGroupBuilder;
import org.isda.cdm.test.functions.FeatureCallEqualToFeatureCall;
import org.isda.cdm.test.functions.FeatureCallEqualToLiteral;
import org.isda.cdm.test.functions.FeatureCallListEqualToFeatureCall;
import org.isda.cdm.test.functions.FeatureCallListNotEqualToFeatureCall;
import org.isda.cdm.test.functions.FeatureCallsEqualToLiteralOr;
import org.isda.cdm.test.functions.MultipleOrFeatureCallsEqualToMultipleOrFeatureCalls;
import org.isda.cdm.test.functions.TestGroupBy;
import org.junit.jupiter.api.Test;

import com.google.inject.Inject;

public class FunctionTests extends AbstractFunctionTest {

	@Inject
	TestGroupBy func;

	@Test
	void collectGroupedItems() {
		TypeToGroupBuilder input = TypeToGroup.builder().addManyAttr(createTypeToGroup("group1", 1))
				.addManyAttr(createTypeToGroup("group1", 2));
		NumberList evaluate = func.evaluate(input.build());
		assertEquals(2, evaluate.getNumbers().size(), "Do flatten");
	}

	@Inject
	FeatureCallEqualToLiteral featureCallEqualToLiteral;
	@Inject
	FeatureCallEqualToFeatureCall featureCallEqualToFeatureCall;
	@Inject
	FeatureCallsEqualToLiteralOr featureCallsEqualToLiteralOr;

	@Test
	public void testFunctionOperationFeature() {
		Bar barInstance = Bar.builder().setBefore(BigDecimal.valueOf(5)).setAfter(BigDecimal.valueOf(5)).build();
		Baz bazInstance = Baz.builder().setOther(BigDecimal.valueOf(5)).build();
		Foo fooInstance = Foo.builder().setBaz(bazInstance).addBar(barInstance).build();
		assertEquals(true, featureCallEqualToLiteral.evaluate(fooInstance));
		assertEquals(true, featureCallEqualToFeatureCall.evaluate(fooInstance));
		assertEquals(true, featureCallsEqualToLiteralOr.evaluate(fooInstance));

	}

	@Inject
	FeatureCallListEqualToFeatureCall featureCallListEqualToFeatureCall;
	@Inject
	FeatureCallListNotEqualToFeatureCall featureCallListNotEqualToFeatureCall;
	@Inject
	MultipleOrFeatureCallsEqualToMultipleOrFeatureCalls multipleOrFeatureCallsEqualToMultipleOrFeatureCalls;

	@Test
	public void testFunctionOperationFeatureMulti() {
		BarBuilder barBuilder = Bar.builder().setBefore(BigDecimal.valueOf(5)).setAfter(BigDecimal.valueOf(5));
		Bar barInstance = barBuilder.build();
		Bar barInstance2 = barBuilder.build();
		Baz bazInstance = Baz.builder().setOther(BigDecimal.valueOf(5)).build();
		Foo fooInstance = Foo.builder().setBaz(bazInstance).addBar(barInstance).addBar(barInstance2).build();
		assertEquals(true, featureCallEqualToLiteral.evaluate(fooInstance));
		assertEquals(true, featureCallEqualToFeatureCall.evaluate(fooInstance));
		assertEquals(true, featureCallsEqualToLiteralOr.evaluate(fooInstance));
		
		assertEquals(true, featureCallListEqualToFeatureCall.evaluate(fooInstance));
		assertEquals(false, featureCallListNotEqualToFeatureCall.evaluate(fooInstance));
		assertEquals(true, multipleOrFeatureCallsEqualToMultipleOrFeatureCalls.evaluate(fooInstance));
	}

	private TypeToGroup createTypeToGroup(String strAttr, int number) {
		return TypeToGroup.builder().setStrAttr(strAttr).setNumAttr(BigDecimal.valueOf(number)).build();
	}
}