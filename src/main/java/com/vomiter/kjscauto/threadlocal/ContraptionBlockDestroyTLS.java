package com.vomiter.kjscauto.threadlocal;

import com.vomiter.kjscauto.machine.ContraptionAfterBlockDestroyEventJS;

public final class ContraptionBlockDestroyTLS {
    private ContraptionBlockDestroyTLS() {}

    public static final ThreadLocal<Entry> TL = new ThreadLocal<>();

    public static final class Entry {
        public final ContraptionAfterBlockDestroyEventJS event;

        public Entry(ContraptionAfterBlockDestroyEventJS event) {
            this.event = event;
        }
    }

    public static void push(ContraptionAfterBlockDestroyEventJS event) {
        TL.set(new Entry(event));
    }

    public static void pop() {
        TL.remove();
    }

    public static Entry get() {
        return TL.get();
    }
}