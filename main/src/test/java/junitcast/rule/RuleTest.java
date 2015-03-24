package junitcast.rule;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import junitcast.AbstractTransientValueTestCase;
import junitcast.Parameter;
import junitcast.ParameterGenerator;

import org.junit.Assert;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * We test only the constructor of our test subject.
 *
 * @author Royce Remulla
 */
public class RuleTest extends
        AbstractTransientValueTestCase<Rule, String, String> {


    /** */
    private static final Logger LOGGER = LoggerFactory
        .getLogger(AbstractTransientValueTestCase.class);

    /**
     * @param pParameter Data Transfer Object Parameter in Parameterized test.
     */
    public RuleTest(final Parameter<String> pParameter) {
        super(pParameter);
    }

    @Override
    protected void setupTargetObject(final List<Object> constructorParams)
    {
        try {
            @SuppressWarnings("unchecked")
            final Constructor<Rule> constructor = (Constructor<Rule>) getSubjectType()
                .getDeclaredConstructors()[0];
            constructor.setAccessible(true);

            prepareConstructor(getParameter().getScenario());
            final Object scenarioToken = getTransientValue(0);
            setRealSubject(constructor
                .newInstance(new Object[] { scenarioToken }));
            setMockSubject(Mockito.spy(getRealSubject()));

            setResult(getRealSubject().toString());
        } catch (final InvocationTargetException iae) {
            if (iae.getCause() instanceof IllegalArgumentException) {
                setResult("IAE");
            } else {
                Assert.fail();
            }

        } catch (final InstantiationException e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail();
        } catch (final IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail();
        } catch (final IllegalArgumentException e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail();
        }
    }

    /**
     * <pre>
     * Test data generator.
     * This method is called by the JUnit parameterized test runner and
     * returns a Collection of Arrays.  For each Array in the Collection,
     * each array element corresponds to a parameter in the constructor.
     * </pre>
     */
    @Parameters(name = "{0}")
    public static Collection<Object[]> generateData()
    {
        return new ParameterGenerator<String>()
            .genVarData("junitcast.rule.RuleTest");
    }

    /** @param scenario test scenario. */
    void prepareConstructor(final List<String> scenario)
    {
        //reserved word

        final String scenarioToken = scenario.toArray(new String[scenario
            .size()])[0];
        if ("null".equals(scenarioToken)) {
            setTransientValue(0, null);
        } else if ("end with separator".equals(scenarioToken)) {
            setTransientValue(0, "ENDWITHSEP~");
        } else if ("incomplete pair1".equals(scenarioToken)) {
            setTransientValue(0, "ACTION:logic~ACTION2");
        } else if ("incomplete pair2".equals(scenarioToken)) {
            setTransientValue(0, "ACTION:logic~ACTION2:");
        } else if ("duplicate action".equals(scenarioToken)) {
            setTransientValue(0, "ACTION:logic~ACTION:logic 2");
        } else if ("single".equals(scenarioToken)) {
            setTransientValue(0, "SINGLEACTION");
        } else if ("multi".equals(scenarioToken)) {
            setTransientValue(0, "EVEN:2|4|6~ODD:1|3|5|77");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void prepare()
    {
        //constructor testing.
    }

    /** {@inheritDoc} */
    @Override
    protected void execute()
    {
        //constructor testing.
    }

}