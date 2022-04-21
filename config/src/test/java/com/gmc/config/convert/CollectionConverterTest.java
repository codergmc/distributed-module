package com.gmc.config.convert;

import com.gmc.config.TypeReferenceImpl;
import com.gmc.config.TypeWrapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CollectionConverterTest {
    @Test
    void testListToSet() {
        TypeConverters typeConverters = new TypeConverters(Collections.emptyList(), new ConvertSupportResultFactory());
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        Object convert = typeConverters.convert(list, TypeWrapper.of(new TypeReferenceImpl<List<String>>() {
        }), TypeWrapper.of(new TypeReferenceImpl<Set<String>>() {
        }));
        Set<String> set = new LinkedHashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
        assertTrue(convert instanceof LinkedHashSet);
        assertTrue(((LinkedHashSet<?>) convert).size() == 3);
        assertTrue(convert.equals(set));
        assertTrue(list.equals(setToArrayList(set)));
    }

    @Test
    void testSetToList() {
        TypeConverters typeConverters = new TypeConverters(Collections.emptyList(), new ConvertSupportResultFactory());
        Set<String> set = new HashSet<>();
        set.add("1");
        set.add("2");
        set.add("3");
        Object convert = typeConverters.convert(set, TypeWrapper.of(new TypeReferenceImpl<Set<String>>() {
        }), TypeWrapper.of(new TypeReferenceImpl<List<String>>() {
        }));

        assertTrue(convert instanceof LinkedList);
        assertTrue(((LinkedList<?>) convert).size() == 3);
        assertTrue(((LinkedList<?>) convert).contains("1"));
        assertTrue(((LinkedList<?>) convert).contains("2"));
        assertTrue(((LinkedList<?>) convert).contains("3"));
    }

    @Test
    void testArrayToList() {
        TypeConverters typeConverters = new TypeConverters(Collections.emptyList(), new ConvertSupportResultFactory());
        String[] strings = new String[3];
        strings[0] = "1";
        strings[1] = "2";
        strings[2] = "3";
        Object convert = typeConverters.convert(strings, TypeWrapper.of(strings.getClass()), TypeWrapper.of(new TypeReferenceImpl<List<String>>() {
        }));
        assertTrue(convert instanceof LinkedList);
        assertTrue(((LinkedList<?>) convert).size() == 3);
        assertTrue(((LinkedList<?>) convert).contains("1"));
        assertTrue(((LinkedList<?>) convert).contains("2"));
        assertTrue(((LinkedList<?>) convert).contains("3"));
    }
    @Test
    void testListToArray(){
        TypeConverters typeConverters = new TypeConverters(Collections.emptyList(), new ConvertSupportResultFactory());
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        Object convert = typeConverters.convert(list, TypeWrapper.of(new TypeReferenceImpl<List<String>>(){}), TypeWrapper.of(new TypeReferenceImpl<String[]>() {
        }));
        String[] strings = (String[]) convert;
        assertTrue(strings.length == 3);
        assertTrue(strings[0].equals("1"));
        assertTrue(strings[1].equals("2"));
        assertTrue(strings[2].equals("3"));
    }

    <T> List<T> setToArrayList(Set<T> set) {
        List<T> list = new ArrayList<>();
        for (T t : set) {
            list.add(t);
        }
        return list;

    }


}