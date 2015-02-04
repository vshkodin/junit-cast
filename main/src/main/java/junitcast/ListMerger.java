/**
 *   Copyright 2013 Royce Remulla
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Merges a Set of Set.
 * 
 * Example:<br/>
 * <br/>
 * List 1: a, b<br/>
 * List 2: 1, 2, 3<br/>
 * List 3: +, -<br/>
 * <br/>
 * Result: -a1, -a2, -a3, -b1, -b2, -b3, +a1, +a2, +a3, +b1, +b2, +b3
 * 
 * @param <E> Type of Set to merge.
 */
public class ListMerger<E> {

    /**
     * Derive the total count of every possible combinations.<br/>
     * <br/>
     * NOTICE: Thanks to original author is Sebastien Boutte.<br/>
     * <br/>
     * 
     * @param pListOfList List of List of Objects to count. Must not be null,
     *            empty, or contain null or empty element.
     */
    int getMergeCount(final List<List<E>> pListOfList)
    {
        if (pListOfList == null || pListOfList.contains(null)
                || pListOfList.isEmpty()) {
            throw new IllegalArgumentException(
                "List must not be null, empty, or contain null set. ");
        }
        int mergeSize = 1; //NOPMD: init default, conditionally redefine.
        for (final List<E> nextList : pListOfList) {
            if (nextList.contains(null) || nextList.isEmpty()) {
                throw new IllegalArgumentException(
                    "List in a List must not be empty or contain null element. ");
            } else {
                mergeSize = mergeSize * nextList.size();
            }
        }
        return mergeSize;
    }

    /**
     * NOTICE: Thanks to original author Sebastien Boutte.<br/>
     * 
     * @param uncombinedList list of list of Objects to combine.
     */
    @SuppressWarnings("unchecked")
    public List<List<E>> merge(final List<List<E>> uncombinedList)
    {
        final List<List<E>> mergedList = new ArrayList<List<E>>();

        final int[] indexArr = new int[uncombinedList.size()];
        final int mergeMaxIdx = getMergeCount(uncombinedList) - 1;

        Arrays.fill(indexArr, 0);

        // First combination is always valid
        final List<E> combination = new ArrayList<E>();
        for (int i = 0; i < indexArr.length; i++) {
            combination.add((E) ((List<List<E>>) uncombinedList.toArray()[i])
                .toArray()[indexArr[i]]);
        }

        mergedList.add(combination);
        combineSuceeding(uncombinedList, mergedList, indexArr, mergeMaxIdx);
        return mergedList;
    }

    /**
     * @param uncombinedList uncombined list to be merged.
     * @param mergedList merged list instance.
     * @param indexArr index array.
     * @param mergeMaxIndex max index of list to merge.
     * @param <T> element type.
     */
    @SuppressWarnings({
            "unchecked",
            "PMD.AvoidInstantiatingObjectsInLoops" })
    <T> void combineSuceeding(final List<List<T>> uncombinedList,
            final List<List<T>> mergedList, final int[] indexArr,
            final int mergeMaxIndex)
    {
        List<T> combination;
        for (int i = 0; i < mergeMaxIndex; i++) {
            combination = new ArrayList<T>();
            boolean found = false;
            // We Use reverse order
            for (int indexArrIdx = indexArr.length - 1; indexArrIdx >= 0
                    && !found; indexArrIdx--) {
                final int currentListSize = ((List<List<T>>) uncombinedList
                    .toArray()[indexArrIdx]).size();

                if (indexArr[indexArrIdx] < currentListSize - 1) {
                    indexArr[indexArrIdx] = indexArr[indexArrIdx] + 1;
                    found = true;
                }
            }
            for (int j = 0; j < indexArr.length; j++) {
                combination
                    .add((T) ((List<List<T>>) uncombinedList.toArray()[j])
                        .toArray()[indexArr[j]]);
            }
            mergedList.add(combination);
        }
    }

}