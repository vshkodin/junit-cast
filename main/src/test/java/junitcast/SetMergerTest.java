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

    /** Case Descriptions. */
    public enum Case {
        /** */
        GetMergeCount, IsValid, MergeListOfListOfT,
        /** */
        MergeListOfListOfTListOfListOfT
    }

    /** */
    public enum Variable1 {
        /** */
        list_null, empty_list, list_1x3, list_w_null, list_1x3_w_null,
        /** */
        list_3x1, list_2x3, list_3x5, list_2x5x3
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
        final Case currentCase = Case.valueOf(getParameter().getCaseDesc());
        for (final String scenToken : getParameter().getScenario()) {
            switch (currentCase) {
                case GetMergeCount:
                    prepareCase1(scenToken);
                    break;
                case MergeListOfListOfT:
                    prepareCase2(scenToken);
                    break;
                case MergeListOfListOfTListOfListOfT:
                    prepareCase3(scenToken);
                    break;
                case IsValid:
                    prepareCase4(scenToken);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @param scenToken scenario element.
     */
    private void prepareCase1(final String scenToken)
    {
        final Variable1 currentVar = Variable1.valueOf(scenToken.replaceAll(
            " ",
            "_"));
        switch (currentVar) {
            case list_null:
                break;
            case empty_list:
                setTransientValue(0, new ArrayList<Set<String>>());
                break;

            case list_w_null:
                final List<Set<String>> listWithNull = new ArrayList<Set<String>>();
                listWithNull.add(createSet(1));
                listWithNull.add(null);
                break;

            case list_1x3_w_null:
                final List<Set<String>> setWithNull = new ArrayList<Set<String>>();
                setWithNull.add(createSet(1));
                setWithNull.add(new HashSet<String>(Arrays.asList(new String[] {
                        "one",
                        "two",
                        null })));
                setTransientValue(0, setWithNull);
                break;

            default:
                final Pattern pattern = Pattern.compile("\\w* \\d(x\\d)*");
                final Matcher matcher = pattern.matcher(scenToken);
                if (matcher.find()) {
                    final String indecesRaw = scenToken.substring(scenToken
                        .indexOf(' ') + 1);
                    final String[] indeces = indecesRaw.split("x");
                    final List<Set<String>> setOfSet = new ArrayList<Set<String>>();
                    for (final String index : indeces) {
                        setOfSet.add(createSet(Integer.parseInt(index)));
                    }
                    setTransientValue(0, setOfSet);
                }
                break;
        }
    }

    /**
     * Create arbitrary set for testing.
     *
     * @param size size of set to create.
     */
    Set<String> createSet(final int size)
    {
        final String[] arrSet = new String[size];
        for (int i = 0; i < size; i++) {
            arrSet[i] = String.valueOf(i);
        }

        return new HashSet<String>(Arrays.asList(arrSet));
    }

    /** {@inheritDoc} */
    @Override
    protected String execute()
    {
        String retval = null;
        final Case currentCase = Case.valueOf(getParameter().getCaseDesc());

        switch (currentCase) {

            case GetMergeCount:
                final List<Set<String>> param = getTransientValue(0);
                try {
                    final int mergeCount = getMockSubject()
                        .getMergeCount(param);
                    retval = String.valueOf(mergeCount);
                } catch (final IllegalArgumentException iae) {
                    retval = "IAE";
                }
                break;

            case MergeListOfListOfT:
                break;

            case MergeListOfListOfTListOfListOfT:
                break;

            case IsValid:
                break;
            default:
                break;

        }

        return retval;

    }

    /**
     * @param scenToken scenario element.
     */
    private void prepareCase2(final String scenToken)
    {
        // TODO Auto-generated method stub

    }

    /**
     * @param scenToken scenario element.
     */
    private void prepareCase3(final String scenToken)
    {
        // TODO Auto-generated method stub

    }

    /**
     * @param scenToken scenario element.
     */
    private void prepareCase4(final String scenToken)
    {
        // TODO Auto-generated method stub

    }

}
