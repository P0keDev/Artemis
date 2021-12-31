/*
 * Copyright © Wynntils 2021.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.model;

import com.wynntils.WynntilsMod;
import com.wynntils.model.impl.WorldStateImpl;
import java.util.Arrays;

public abstract class Models {
    private static final WorldState worldState = new WorldStateImpl();
    public static final Model[] MODELS = new Model[] {worldState};

    public static WorldState getWorldState() {
        return worldState;
    }

    public static void init() {
        Arrays.stream(MODELS)
                .forEach(
                        model -> {
                            WynntilsMod.EVENT_BUS.register(model);
                            WynntilsMod.EVENT_BUS.register(model.getClass());
                        });
    }
}