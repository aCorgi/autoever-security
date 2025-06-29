package com.task.autoeversecurity.util

object Constants {
    object Exception {
        const val DEFAULT_CLIENT_EXCEPTION_MESSAGE = "잘못된 요청입니다."
        const val DEFAULT_SERVER_EXCEPTION_MESSAGE = "서버 연결이 원활하지 않습니다."
        const val IMPOSSIBLE_PHONE_NUMBER_EXCEPTION_MESSAGE = "휴대전화 번호 형식이 올바르지 않습니다. 하이픈이 있다면, 하이픈을 제거해주세요."
        const val RRN_CONTAINS_HYPHEN_EXCEPTION_MESSAGE = "주민등록번호에 하이픈(-)이 포함되어서는 안 됩니다."
        const val RRN_LENGTH_INVALID_EXCEPTION_MESSAGE = "주민등록번호는 정확히 11자리여야 합니다."
        const val INVALID_RRN_FORMAT_EXCEPTION_MESSAGE = "잘못된 주민등록번호 형식입니다."
        const val DUPLICATE_LOGIN_ID_EXCEPTION_MESSAGE = "이미 사용 중인 로그인 ID입니다."
        const val DUPLICATE_RRN_EXCEPTION_MESSAGE = "이미 사용 중인 주민등록번호입니다."
    }

    const val AES_ALGORITHM = "AES"

    object Redis {
        const val BASIC_AUTH_USERS_REDIS_KEY = "BASIC_AUTH_USERS"
        const val BASIC_AUTH_USERS_CHANNEL = "BASIC_AUTH_USERS"
    }
}
