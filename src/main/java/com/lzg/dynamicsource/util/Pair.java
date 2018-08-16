package com.lzg.dynamicsource.util;

public class Pair<L,R> {

    private L l;
    private R r;

    public static <L,R> Pair<L,R> of (L left, R ight) {
        return new Pair<>(left, ight);
    }

    private Pair(L left, R right) {
        this.l = left;
        this.r = right;
    }

    public L getLeft() {
        return l;
    }

    public R getRight() {
        return r;
    }

}
