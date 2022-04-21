package com.gmc.config.file;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class ClassPathLocationParserTest {
    @Test
    void test() throws NoSuchFieldException {
        Field integerList = A.class.getDeclaredField("integerList");

        A a = new A();
        a.integerList = new ArrayList<>(){
            {
                add(1);
                add(2);
            }
        };
        a.integers = new LinkedBlockingQueue<>(){
            {
                add(3);
                add(4);
            }
        };
        String s = JSONObject.toJSONString(a);
        System.out.println(s);
        A a1 = JSONObject.parseObject(s, A.class);
        System.out.println(a.equals(a1));
    }
    static class A{
        LinkedBlockingQueue<Integer> integers;
        List<Integer> integerList;

        public BlockingQueue<Integer> getIntegers() {
            return integers;
        }

        public A setIntegers(LinkedBlockingQueue<Integer> integers) {
            this.integers = integers;
            return this;
        }

        public List<Integer> getIntegerList() {
            return integerList;
        }

        public A setIntegerList(List<Integer> integerList) {
            this.integerList = integerList;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            A a = (A) o;

            if (integers != null ? !integers.equals(a.integers) : a.integers != null) return false;
            return integerList != null ? integerList.equals(a.integerList) : a.integerList == null;
        }

        @Override
        public int hashCode() {
            int result = integers != null ? integers.hashCode() : 0;
            result = 31 * result + (integerList != null ? integerList.hashCode() : 0);
            return result;
        }
    }

}