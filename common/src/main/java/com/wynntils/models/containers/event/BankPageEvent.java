/*
 * Copyright © Wynntils 2023.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.models.containers.event;

import net.minecraftforge.eventbus.api.Event;

public abstract class BankPageEvent extends Event {
    private int page;

    protected BankPageEvent(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public static class PageOpenEvent extends BankPageEvent {
        public PageOpenEvent(int page) {
            super(page);
        }
    }

    public static class PageLoadEvent extends BankPageEvent {
        public PageLoadEvent(int page) {
            super(page);
        }
    }
}
