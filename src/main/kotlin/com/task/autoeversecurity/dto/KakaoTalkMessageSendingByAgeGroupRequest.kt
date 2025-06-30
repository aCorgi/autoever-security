package com.task.autoeversecurity.dto

data class KakaoTalkMessageSendingByAgeGroupRequest(
    val ageGroup: AgeGroup,
)

enum class AgeGroup(val minAge: Int, val maxAge: Int) {
    UNDER_10(0, 9),
    AGE_10S(10, 19),
    AGE_20S(20, 29),
    AGE_30S(30, 39),
    AGE_40S(40, 49),
    AGE_50S(50, 59),
    AGE_60S(60, 69),
    AGE_70S(70, 79),
    AGE_80S(80, 89),
    AGE_90S(90, 99),
    // 100대 이상은 없다고 가정
}
