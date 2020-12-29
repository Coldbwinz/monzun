package com.example.monzun.enums;

/**
 * Оценки результата работы стартапа за неделю
 */
public enum WeekReportEstimatesEnum {
    BAD(1, "Плохо"),
    BELOW_NORMAL(2, "Ниже среднего"),
    NORMAL(3, "Удовлетворительно"),
    GOOD(4, "Хорошо"),
    GREAT(5, "Отлично");

    private final Integer score;
    private final String description;

    WeekReportEstimatesEnum(Integer score, String text) {
        this.score = score;
        this.description = text;
    }

    /**
     * Поиск оценки по цифровому обозначению оценки
     *
     * @param score цифровое обозначнение оценки
     * @return WeekReportEstimatesEnum
     */
    public static WeekReportEstimatesEnum findByScore(Integer score) {
        for (WeekReportEstimatesEnum estimate : values()) {
            if (estimate.getScore().equals(score)) {
                return estimate;
            }
        }

        return null;
    }

    public Integer  getScore() {
        return score;
    }

    public String getDescription() {
        return description;
    }
}
