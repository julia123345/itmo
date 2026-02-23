package app.commands;

/**
 * Класс для уникальных типов команд
 */
public enum CommandType {
    ADD,
    INFO,
    UPDATE,
    REMOVE_BY_ID,
    CLEAR,
    SAVE,
    EXIT,
    ADD_IF_MAX,
    REMOVE_LOWER,
    FILTER_STARTS_WITH_NAME,
    SHOW,
    PRINT_ASCENDING,
    PRINT_DESCENDING,
    EXECUTE_SCRIPT,
    HISTORY,
    HELP,
    AUTH,
    REGISTER,
    // Команды из ТЗ, которых не было в enum — добавляю:
    REMOVE_HEAD,
    REMOVE_GREATER,
    AVERAGE_OF_HEIGHT,
    MIN_BY_NAME,
    PRINT_FIELD_DESCENDING_NATIONALITY
}