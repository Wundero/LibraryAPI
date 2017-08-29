package com.mcsimonflash.sponge.libraryapi.message;

import com.google.common.collect.Maps;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.locale.Locales;

import java.util.Locale;
import java.util.Map;

/* TODO:
 * Discuss a system where players can add in new translations via command.
 */
/**
 * Contains a map of keys to {@link TranslatableMessage}s as well as utility
 * methods to quickly obtain a translation.
 *
 * Many translation services use a hierarchy where locale -> key -> message.
 * Instead, this service currently uses a key -> locale -> message system. A
 * discussion on the uses of each might be a good idea for a later time.
 *
 * This class is intended to be used such as
 *
 * {@code messageService.getMessage("message.key", player.getLocale());}
 */
public class MessageService {

    private final Locale defaultLocale;
    private final Map<String, TranslatableMessage> messages = Maps.newHashMap();

    public MessageService() {
        this(Locales.DEFAULT);
    }

    public MessageService(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * Adds a {@link TranslatableMessage} under the given key
     *
     * @param key the key for the message
     * @param message the message (with any translations)
     */
    public void addMessage(String key, TranslatableMessage message) {
        messages.put(key, message);
    }

    /**
     * Removes a message from the given key
     *
     * @param key the key for the message to be removed
     */
    public void removeMessage(String key) {
        messages.remove(key);
    }

    /**
     * Gets the message for the given key in the {@link #defaultLocale}
     *
     * @param key the key for the message
     * @return the message in the default locale
     */
    public Text getMessage(String key) {
        return getMessage(key, defaultLocale);
    }

    /**
     * Gets the message for the given key in the given locale.
     *
     * If the {@link TranslatableMessage} does not contain the provided locale,
     * it will attempt to retrieve the message for the {@link #defaultLocale}.
     *
     * @param key the key for the message
     * @param locale the locale for the translation
     * @return the Text message or error message
     */
    public Text getMessage(String key, Locale locale) {
        TranslatableMessage message = messages.get(key);
        if (message != null) {
            return message.getTranslation(locale, defaultLocale);
        }
        return Text.of("No message for key " + key);
    }
}
