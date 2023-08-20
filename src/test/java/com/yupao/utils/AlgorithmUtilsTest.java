package com.yupao.utils;

import org.junit.jupiter.api.Test;

class AlgorithmUtilsTest {

    @Test
    void minDistance() {
        String a = "鱼皮是狗";
        String b = "鱼皮不是狗";
        String c = "鱼皮不是鱼是狗";

        int i1 = AlgorithmUtils.minDistance(a, c);
        int i2 = AlgorithmUtils.minDistance(a, b);
        System.out.println(i1);
        System.out.println(i2);
    }

    @Test
    void testMinDistance() {
    }
}