/**
 *
 */
package junitcast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
public class ListMergerTest
        extends
        AbstractTransientValueTestCase<ListMerger<String>, String, List<List<String>>> {


    /** */
    public enum Variable {
        /** */
        list_null, empty_list, list_1x3, list_w_null, list_1x3_w_null,

        /** */
        list_1x0, list_3x1, list_2x3, list_3x5, list_2x5x3
    }

    /** @param pParameter Data Transfer Object Parameter in Parameterized test. */
    public ListMergerTest(final Parameter<String> pParameter) {
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
            .genVarData("junitcast.ListMergerTest");
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
                final List<List<String>> listWithNull = new ArrayList<List<String>>();
                listWithNull.add(createList(1));
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
                    final List<List<String>> setWithNull = new ArrayList<List<String>>();
                    setWithNull.add(createList(1));
                    setWithNull.add(new ArrayList<String>(Arrays
                        .asList(new String[] {
                                "one",
                                "two",
                                null })));
                    setTransientValue(0, setWithNull);
                }
            });

        for (final String scenToken : getParameter().getScenario()) {


            final Pattern pattern = Pattern.compile("[a-z]+_\\d(x\\d)*$");
            final Matcher matcher = pattern.matcher(scenToken);
            if (matcher.find()) {
                final Variable currentVar = Variable.valueOf(scenToken);

                source.addTransientCase(
                    0,
                    createListOfSet(scenToken),
                    currentVar);
            }
        }

        source.notifyObservers();

    }

    private List<List<String>> createListOfSet(final String scenToken)
    {
        final String indecesRaw = scenToken
            .substring(scenToken.indexOf('_') + 1);
        final String[] indeces = indecesRaw.split("x");
        final List<List<String>> listOfList = new ArrayList<List<String>>();
        for (final String index : indeces) {
            listOfList.add(createList(Integer.parseInt(index)));
        }
        return listOfList;
    }

    /**
     * Create arbitrary set for testing.
     *
     * @param size size of set to create.
     */
    List<String> createList(final int size)
    {
        final List<String> indexList = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            indexList.add(String.valueOf(i));
        }

        return indexList;
    }

    /** {@inheritDoc} */
    @Override
    protected Object execute()
    {
        final List<List<String>> param = getTransientValue(0);
        try {
            final int mergeCount = getMockSubject().getMergeCount(param);
            setResult(String.valueOf(mergeCount));
        } catch (final IllegalArgumentException iae) {
            setResult("IAE");
        }
        return getResult();

    }


}
