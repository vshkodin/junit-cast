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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
public class SetMerger<E> {

    /**
     * Derive the total count of every possible combinations.<br/>
     * <br/>
     * NOTICE: Thanks to original author is Sebastien Boutte.<br/>
     * <br/>
     * 
     * @param pListOfSet List of Set of Objects to count. Must not be null,
     *            empty, or contain null or empty element.
     */
    int getMergeCount(final List<Set<E>> pListOfSet)
    {
        if (pListOfSet == null || pListOfSet.contains(null)
                || pListOfSet.isEmpty()) {
            throw new IllegalArgumentException(
                "List must not be null, empty, or contain null set. ");
        }
        int mergeSize = 1; //NOPMD: init default, conditionally redefine.
        for (final Set<E> nextSet : pListOfSet) {
            if (nextSet.contains(null) || nextSet.isEmpty()) {
                throw new IllegalArgumentException(
                    "List Set must not be empty or contain null element. ");
            } else {
                mergeSize = mergeSize * nextSet.size();
            }
        }
        return mergeSize;
    }

    /**
     * NOTICE: Thanks to original author Sebastien Boutte.<br/>
     * 
     * @param uncombinedSet list of list of Objects to combine.
     */
    @SuppressWarnings("unchecked")
    public Set<List<E>> merge(final List<Set<E>> uncombinedSet)
    {
        final Set<List<E>> mergedList = new LinkedHashSet<List<E>>();

        final int[] indexArr = new int[uncombinedSet.size()];
        final int mergeMaxIdx = getMergeCount(uncombinedSet) - 1;

        Arrays.fill(indexArr, 0);

        // First combination is always valid
        final List<E> combination = new ArrayList<E>();
        for (int i = 0; i < indexArr.length; i++) {
            combination.add((E) ((Set<List<E>>) uncombinedSet.toArray()[i])
                .toArray()[indexArr[i]]);
        }

        mergedList.add(combination);
        combineSuceeding(uncombinedSet, mergedList, indexArr, mergeMaxIdx);
        return mergedList;
    }

    /**
     * @param uncombinedSet uncombined set to be merged.
     * @param mergedList merged list instance.
     * @param indexArr index array.
     * @param mergeMaxIndex max index of list to merge.
     * @param <T> element type.
     */
    @SuppressWarnings({
            "unchecked",
            "PMD.AvoidInstantiatingObjectsInLoops" })
    <T> void combineSuceeding(final List<Set<T>> uncombinedSet,
            final Set<List<T>> mergedList, final int[] indexArr,
            final int mergeMaxIndex)
    {
        List<T> combination;
        for (int i = 0; i < mergeMaxIndex; i++) {
            combination = new ArrayList<T>();
            boolean found = false;
            // We Use reverse order
            for (int indexArrIdx = indexArr.length - 1; indexArrIdx >= 0
                    && !found; indexArrIdx--) {
                final int currentListSize = ((Set<Set<T>>) uncombinedSet
                    .toArray()[indexArrIdx]).size();

                if (indexArr[indexArrIdx] < currentListSize - 1) {
                    indexArr[indexArrIdx] = indexArr[indexArrIdx] + 1;
                    found = true;
                }
            }
            for (int j = 0; j < indexArr.length; j++) {
                combination.add((T) ((Set<Set<T>>) uncombinedSet.toArray()[j])
                    .toArray()[indexArr[j]]);
            }
            mergedList.add(combination);
        }
    }

}