package com.vomiter.kjscauto.machine;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;

public final class ArmDepositTargetTLS {
    private ArmDepositTargetTLS() {}

    public static final ThreadLocal<Entry> TL = new ThreadLocal<>();

    public static final class Entry {
        public final ArmInteractionPoint target;

        public Entry(ArmInteractionPoint target) {
            this.target = target;
        }
    }

    public static void push(ArmInteractionPoint event) {
        TL.set(new Entry(event));
    }

    public static void pop() {
        TL.remove();
    }

    public static Entry get() {
        return TL.get();
    }
}