/*
 * Copyright © Wynntils 2023.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.models.containers;

import com.wynntils.core.WynntilsMod;
import com.wynntils.core.components.Model;
import com.wynntils.core.components.Models;
import com.wynntils.mc.event.ScreenClosedEvent;
import com.wynntils.mc.event.ScreenOpenedEvent;
import com.wynntils.mc.event.SetSlotEvent;
import com.wynntils.models.containers.event.BankPageEvent;
import java.util.List;
import java.util.regex.Matcher;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.Container;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class BankModel extends Model {
    // slot indices of the navigation buttons
    public static final int PAGE_FORWARD = 8;
    public static final int PAGE_BACK = 17;
    // quick-jump button slot indices and destination pages
    public static final int[] PAGE_JUMP_SLOTS = {7, 16, 25, 34, 43, 52};
    public static final int[] PAGE_JUMP_PAGES = {1, 5, 9, 13, 17, 21};
    public static final int PAGE_JUMP_BUTTONS = 6;

    private AbstractContainerScreen<?> bankScreen = null;
    private boolean inBank = false;
    private boolean pageLoaded = false;
    private int currentPage = 0;

    private int maxPage = 0;

    public BankModel(ContainerModel containerModel) {
        super(List.of(containerModel));
    }

    @SubscribeEvent
    public void onScreenClosed(ScreenClosedEvent event) {
        if (!inBank) return;

        // reset state
        bankScreen = null;
        inBank = false;
        pageLoaded = false;
        currentPage = 0;
    }

    @SubscribeEvent
    public void onScreenOpened(ScreenOpenedEvent.Pre event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)) return;

        Matcher bankMatcher = Models.Container.bankPageMatcher(event.getScreen());
        if (bankMatcher.matches()) {
            bankScreen = screen;
            inBank = true;
            pageLoaded = false;
            currentPage = Integer.parseInt(bankMatcher.group(1));

            WynntilsMod.postEvent(new BankPageEvent.PageOpenEvent(currentPage));
            updateMaxPage();
        }
    }

    @SubscribeEvent
    public void onSetSlot(SetSlotEvent.Post event) {
        if (!inBank || pageLoaded) return;
        Container bankContainer = event.getContainer();

        // if one of these items is present, the page has loaded in
        if (bankContainer.getItem(PAGE_FORWARD).isEmpty()
                && bankContainer.getItem(PAGE_BACK).isEmpty()) return;

        pageLoaded = true;
        WynntilsMod.postEvent(new BankPageEvent.PageLoadEvent(currentPage));
    }

    public boolean isBankOpen() {
        return inBank;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean isPageLoaded() {
        return pageLoaded;
    }

    public int getMaxPage() {
        return maxPage;
    }

    private void updateMaxPage() {
        if (maxPage < currentPage) {
            maxPage = currentPage;
        }
    }
}
