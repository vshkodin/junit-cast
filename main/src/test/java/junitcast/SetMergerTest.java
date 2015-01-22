/**
 *
 */
package junitcast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.runners.Parameterized.Parameters;

/**
 * Test class for SetMerger.
 *
 * <pre>
 * $Date$
 * $HeadURL$
 * </pre>
 *
 * @author Royce Remulla
 */
public class SetMergerTest
        extends
        AbstractTransientValueTestCase<SetMerger<String>, String, List<Set<String>>> {


    /** */
    public enum Variable {
        /** */
        list_null, empty_list, list_1x3, list_w_null, list_1x3_w_null,

        /** */
        list_1x0, list_3x1, list_2x3, list_3x5, list_2x5x3
    }

    /** @param pParameter Data Transfer Object Parameter in Parameterized test. */
    public SetMergerTest(final Parameter<String> pParameter) {
        super(pParameter);
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
            .genVarData("junitcast.SetMergerTest");
    }

    /** {@inheritDoc} */
    @Override
    protected void setupTargetObject(final List<Object> constructorParams)
    {
        new MockitoHelper().setupTargetObject(this, constructorParams);
    }

    /** {@inheritDoc} */
    @Override
    protected void prepare()
    {
        final ScenarioSource<String> source = new ScenarioSource<String>(this);

        source.addTransientCase(
            0,
            new ArrayList<Set<String>>(),
            Variable.empty_list);

        source.addTransientCase(
            0,
            new ArrayList<Set<String>>(),
            Variable.list_w_null);

        source.addObserver(Variable.list_w_null, new CaseObserver<String>() {
            @Override
            public void prepareCase(final String caseRaw)
            {
                final List<Set<String>> listWithNull = new ArrayList<Set<String>>();
                listWithNull.add(createSet(1));
                listWithNull.add(null);
                setTransientValue(0, listWithNull);
            }
        });

        source.addObserver(
            Variable.list_1x3_w_null,
            new CaseObserver<String>() {
                @Override
                public void prepareCase(final String caseRaw)
                {
                    final List<Set<String>> setWithNull = new ArrayList<Set<String>>();
                    setWithNull.add(createSet(1));
                    setWithNull.add(new HashSet<String>(Arrays
                        .asList(new String[] {
                                "one",
                                "two",
                                null })));
                    setTransientValue(0, setWithNull);
                }
            });

        for (final String scenToken : getParameter().getScenario()) {


            final Pattern pattern = Pattern.compile("\\w* \\d(x\\d)*$");
            final Matcher matcher = pattern.matcher(scenToken);
            if (matcher.find()) {
                final Variable currentVar = Variable.valueOf(scenToken
                    .replaceAll(" ", "_"));

                source.addTransientCase(
                    0,
                    createListOfSet(scenToken),
                    currentVar);
            }
        }

        source.notifyObservers();

    }

    private List<Set<String>> createListOfSet(final String scenToken)
    {
        final String indecesRaw = scenToken
            .substring(scenToken.indexOf(' ') + 1);
        final String[] indeces = indecesRaw.split("x");
        final List<Set<String>> listOfSet = new ArrayList<Set<String>>();
        for (final String index : indeces) {
            listOfSet.add(createSet(Integer.parseInt(index)));
        }
        return listOfSet;
    }

    /**
     * Create arbitrary set for testing.
     *
     * @param size size of set to create.
     */
    Set<String> createSet(final int size)
    {
        final Set<String> indexSet = new HashSet<String>();
        for (int i = 0; i < size; i++) {
            indexSet.add(String.valueOf(i));
        }

        return indexSet;
    }

    /** {@inheritDoc} */
    @Override
    protected Object execute()
    {
        final List<Set<String>> param = getTransientValue(0);
        try {
            final int mergeCount = getMockSubject().getMergeCount(param);
            setResult(String.valueOf(mergeCount));
        } catch (final IllegalArgumentException iae) {
            setResult("IAE");
        }
        return getResult();

    }


}
