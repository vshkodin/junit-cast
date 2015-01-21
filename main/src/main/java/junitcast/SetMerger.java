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
     * @param pSet set of set of Objects to count. Must not be null, empty, or contain null or empty element.
     */
    int getMergeCount(final List<Set<E>> pSet)
    {
        final IllegalArgumentException iae = new IllegalArgumentException(
                "Parameter and any of its element must not be null nor empty. ");
        if (pSet == null || pSet.contains(null) || pSet.isEmpty()) {
            throw iae;
        }
        int count = 1;
        for (final Set<E> nextSet : pSet) {
            if (nextSet.contains(null) || nextSet.isEmpty()) {
                throw iae;
            } else {
                count = count * nextSet.size();
            }
        }
        return count;
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

        final int[] index = new int[uncombinedSet.size()];
        final int mergeMaxIdx = getMergeCount(uncombinedSet) - 1;

        Arrays.fill(index, 0);

        // First combination is always valid
        final List<E> combination = new ArrayList<E>();
        for (int i = 0; i < index.length; i++) {
            combination.add((E) ((Set<List<E>>) uncombinedSet.toArray()[i]).toArray()[index[i]]);
        }

        mergedList.add(combination);
        combineSuceeding(uncombinedSet, mergedList, index, mergeMaxIdx);
        return mergedList;
    }

    /**
     * @param uncombinedSet uncombined set to be merged.
     * @param mergedList merged list instance.
     * @param index index array.
     * @param mergeMaxIdx max index of list to merge.
     * @param <T> element type.
     */
    @SuppressWarnings("unchecked")
    <T> void combineSuceeding(final List<Set<T>> uncombinedSet, final Set<List<T>> mergedList, final int[] index,
            final int mergeMaxIdx)
    {
        List<T> combination;
        for (int i = 0; i < mergeMaxIdx; i++) {
            combination = new ArrayList<T>();
            boolean found = false;
            // We Use reverse order
            for (int j = index.length - 1; j >= 0 && !found; j--) {
                final int currentListSize = ((Set<Set<T>>) uncombinedSet.toArray()[j]).size();
                if (index[j] < currentListSize - 1) {
                    index[j] = index[j] + 1;
                    found = true;
                } else {
                    index[j] = 0;
                }
            }
            for (int j = 0; j < index.length; j++) {
                combination.add((T) ((Set<Set<T>>) uncombinedSet.toArray()[j]).toArray()[index[j]]);
            }
            mergedList.add(combination);
        }
    }

}