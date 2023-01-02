package com.uroria.kebab.utils.scheduler;

public record Pair<F, S>(F first, S second){
    public F first() {
        return this.first;
    }

    public S second() {
        return this.second;
    }
}
