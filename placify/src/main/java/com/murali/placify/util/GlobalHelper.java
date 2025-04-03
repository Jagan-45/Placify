package com.murali.placify.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

@Component
public class GlobalHelper {

    public <T> List<T> getTwoRandomElements(List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        List<T> result = new ArrayList<>();

        if (list.isEmpty()) {
            return result;
        }

        if (list.size() == 1) {
            result.add(list.get(0));
            return result;
        }

        int index1 = ThreadLocalRandom.current().nextInt(list.size());
        int index2;

        do {
            index2 = ThreadLocalRandom.current().nextInt(list.size());
        } while (index1 == index2);

        result.add(list.get(index1));
        result.add(list.get(index2));

        return result;
    }
}