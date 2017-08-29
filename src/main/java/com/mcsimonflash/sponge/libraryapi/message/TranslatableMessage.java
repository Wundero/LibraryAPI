package com.mcsimonflash.sponge.libraryapi.message;

import com.google.common.collect.Maps;
import org.spongepowered.api.text.Text;

import java.util.Locale;
import java.util.Map;

/**
 * A series of {@link Text} objects keyed by {@link Locale}s.
 */
public class TranslatableMessage {

    private final Map<Locale, Text> translations = Maps.newHashMap();

    public TranslatableMessage() {}

    /**
     * Adds a translation for this message.
     *
     * @param locale the locale of the translation
     * @param translation the text for the translation
     */
    public void addTranslation(Locale locale, Text translation) {
        translations.put(locale, translation);
    }

    /**
     * Removes a translation from this message
     *
     * @param locale the locale of the translation
     */
    public void removeTranslation(Locale locale) {
        translations.remove(locale);
    }

    /**
     * Gets the {@link Text} translation for the given locale.
     *
     * If the locale does not have a translation, returns the translation for
     * the provided fallback locale. If a translation is still not found,
     * returns a Text error message with the locale name.
     *
     * @param locale the locale of the translation
     * @return the Text translation or error message
     */
    public Text getTranslation(Locale locale, Locale fallback) {
        Text translation = translations.get(locale);
        if (translation != null) {
            return translation;
        }
        Text fallbackTranslation = translations.get(fallback);
        if (fallbackTranslation != null) {
            return fallbackTranslation;
        }
        return Text.of("No translation for locales " + locale.toString() + " and " + fallback.toString() + ".");
    }

}