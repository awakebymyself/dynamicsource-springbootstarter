package com.ymatou.dynamicsource.util;

public class Pair<L, R> {

    private L l;
    private R r;

    private Pair(L left, R right) {
        this.l = left;
        this.r = right;
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    public L getLeft() {
        return l;
    }

    public R getRight() {
        return r;
    }

}
