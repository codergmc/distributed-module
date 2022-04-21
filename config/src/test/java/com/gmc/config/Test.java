package com.gmc.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Test {


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        int n = Integer.parseInt(s);
        if(n<0&&n>100){
            return ;
        }
        List<Person> list = new ArrayList<>(n);
        String[] heightsStr = in.nextLine().split(" ");
        String[] weightsStr = in.nextLine().split(" ");
        for (int i = 0; i < n; i++) {
            Person person = new Person();
            person.height = Integer.parseInt(heightsStr[i]);
            person.weight = Integer.parseInt(weightsStr[i]);
            person.oriRank = i + 1;
            list.add(person);
        }
        List<Person> newList = new ArrayList<>();
        newList.addAll(list);
        Collections.sort(newList);
        for (int i = 0; i < n; i++) {
            newList.get(i).rank = i + 1;
        }

        for (int i = 0; i < n; i++) {
            Person p = list.get(i);
            if (i == n - 1) {
                System.out.println(p.rank);
            } else {
                System.out.print(p.rank);
                System.out.print(" ");
            }


        }
    }


    static class Person implements Comparable<Person> {
        int height;
        int weight;
        int oriRank;
        int rank;

        @Override
        public int compareTo(Person o) {
            if (height == o.height) {
                if(o.weight == weight){
                    return oriRank-o.oriRank;
                } else return weight-o.weight;
            } else return height - o.height;
        }
    }

}
