/*
 * Copyright © Wynntils 2018-2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.core.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.wynntils.core.webapi.request.RequestBuilder;
import com.wynntils.core.webapi.request.RequestHandler;
import com.wynntils.utils.StringUtils;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A TranslationService that uses the free googleapi translation API. This service is free but is severely
 * restricted. There is a rate limit of about 100 messages per hour and IP address. This is typically
 * sufficient for NPCs translation, but not for general chat messages, at least not in chatty areas like Detlas.
 */
public class GoogleApiTranslationService extends CachingTranslationService implements TranslationService {
    private static final AtomicInteger requestNumber = new AtomicInteger();

    @Override
    protected void translateNew(String message, String toLanguage, Consumer<String> handleTranslation) {
        if (toLanguage == null || toLanguage.isEmpty()) {
            handleTranslation.accept(null);
            return;
        }

        String encodedMessage = StringUtils.encodeUrl(message);
        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=" + toLanguage
                + "&dt=t&q=" + encodedMessage;

        RequestHandler handler = new RequestHandler();
        handler.addAndDispatch(
                new RequestBuilder(url, "translate-" + requestNumber.getAndIncrement())
                        .handleJsonArray(json -> {
                            StringBuilder builder = new StringBuilder();
                            JsonArray array = json.get(0).getAsJsonArray();
                            for (JsonElement elem : array) {
                                String part = elem.getAsJsonArray().get(0).getAsString();
                                builder.append(part);
                            }
                            String translatedMessage = builder.toString();
                            saveTranslation(toLanguage, message, translatedMessage);
                            handleTranslation.accept(translatedMessage);

                            return true;
                        })
                        .onError(() -> {
                            // If Google translate return no data ( 500 error ), display default lang
                            handleTranslation.accept(null);
                        })
                        .build(),
                true);
    }
}