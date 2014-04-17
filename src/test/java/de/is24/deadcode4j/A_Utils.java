package de.is24.deadcode4j;

import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public final class A_Utils {

    @Test
    public void returnsDefaultValue() {
        final int defaultValue = 42;
        Map<String, Integer> map = newHashMap();
        map.put("key", 23);

        Integer value = Utils.getValueOrDefault(map, "anotherKey", defaultValue);

        assertThat(value, is(defaultValue));
    }

    @Test
    public void returnsMappedValue() {
        final int mappedValue = 42;
        Map<String, Integer> map = newHashMap();
        map.put("key", mappedValue);

        Integer value = Utils.getValueOrDefault(map, "key", 23);

        assertThat(value, is(mappedValue));
    }

    @Test
    public void addsSetToMapIfNoMappedSetExists() {
        final String key = "foo";
        Map<String, Set<Integer>> map = newHashMap();

        Set<Integer> set = Utils.getOrAddMappedSet(map, key);

        assertThat(map, hasEntry(is(equalTo(key)), is(sameInstance(set))));
    }

    @Test
    public void preservesExistingSetIfMappedSetExists() {
        final String key = "foo";
        Map<String, Set<Integer>> map = newHashMap();
        Set<Integer> existingSet = newHashSet();
        map.put(key, existingSet);

        Set<Integer> set = Utils.getOrAddMappedSet(map, key);

        assertThat(set, is(sameInstance(existingSet)));
    }

}
