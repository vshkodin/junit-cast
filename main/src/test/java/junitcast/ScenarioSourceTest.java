/**
 *   Copyright 2014 Royce Remulla
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package junitcast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/** */
public class ScenarioSourceTest {


    /** */
    @SuppressWarnings({ "rawtypes" })
    private final transient ScenarioSource<String> sut =
            new ScenarioSource<String>(new AbstractTestCase<Object, String>(
                null) {

                @Override
                protected void setupTargetObject(final List constructorParams)
                {}

                @Override
                protected void prepare()
                {}

                @Override
                protected void execute()
                {}
            });

    /** */
    @SuppressWarnings({ "rawtypes" })
    private final transient ScenarioSource<String> sutTrans =
            new ScenarioSource<String>(
                new AbstractTransientValueTestCase<Object, String, Object>(null) {

                    @Override
                    protected void setupTargetObject(final List constructorParams)
                    {}

                    @Override
                    protected void prepare()
                    {}

                    @Override
                    protected void execute()
                    {}
                });


    /** Null Case. */
    @SuppressWarnings("unchecked")
	@Test(expected = IllegalArgumentException.class)
    public void checkValidTestCase_nullTest()
    {
        this.sut.checkValidTestCase(null);
    }

    /** Empty Case. */
    @Test(expected = IllegalArgumentException.class)
    public void checkValidTestCase_emptyTest()
    {
        this.sut.checkValidTestCase(new TestEnum[0]);
    }

    /** Not a sub class of Transient class. */
    @Test(expected = UnsupportedOperationException.class)
    public void checkValidTestCase_nonTransientTest()
    {
        this.sut.checkValidTestCase(new TestEnum[] { TestEnum.Item1 });
    }

    /**  */
    @Test
    public void createNewCase_caseParserTest()
    {
        final CaseObserver<String> caseObs =
                this.sutTrans.createNewCase(null, null, new CaseParser() {

                    @Override
                    public <E extends Enum<E>> Object parse(final E kaso)
                    {
                        return null;
                    }
                });

        caseObs.prepareCase(0, "test");

    }

    /**  */
    @Test
    public void toStringTest()
    {
        Assert.assertEquals(this.sut.getClass().getSimpleName()
                + "[] Observer size: 0", this.sut.toString());

    }


    /** */
    enum TestEnum {
        /** */
        Item1
    }

}
