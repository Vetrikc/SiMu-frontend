package org.simu.client.api;

import java.util.Map;

/**
 * Maps the machine codes of the backend to short Russian messages. The backend is the source of
 * truth for the text; this is only a nicer presentation layer.
 */
public final class Messages {

    private static final Map<String, String> BY_CODE = Map.ofEntries(
            Map.entry("INVALID_CREDENTIALS", "Неверный логин или пароль"),
            Map.entry("USER_ALREADY_EXISTS", "Такое имя пользователя или e-mail уже занято"),
            Map.entry("USER_NOT_FOUND", "Пользователь не найден"),
            Map.entry("RATE_LIMIT_EXCEEDED", "Слишком много попыток. Подождите минуту и повторите"),
            Map.entry("INVALID_REFRESH_TOKEN", "Сессия истекла, войдите заново"),
            Map.entry("INVALID_TOKEN", "Ссылка недействительна или устарела"),
            Map.entry("TOKEN_EXPIRED", "Сессия истекла, войдите заново"),
            Map.entry("PLAYLIST_NOT_FOUND", "Плейлист не найден"),
            Map.entry("MUSIC_NOT_FOUND", "Трек не найден"),
            Map.entry("FILE_PROCESSING_ERROR", "Не удалось обработать файл"),
            Map.entry("INVALID_TICKET", "Ссылка входа устарела, попробуйте ещё раз"),
            Map.entry("OAUTH_NOT_CONFIGURED", "Вход через Google не настроен на сервере"),
            Map.entry("OAUTH_EMAIL_NOT_VERIFIED", "Google не подтвердил e-mail этого аккаунта"),
            Map.entry("BACKEND_UNAVAILABLE", "Сервис недоступен, попробуйте позже")
    );

    private Messages() {
    }

    public static String humanize(String code, String fallback) {
        if (code != null && BY_CODE.containsKey(code)) {
            return BY_CODE.get(code);
        }
        return fallback == null || fallback.isBlank() ? "Что-то пошло не так" : fallback;
    }
}
