package com.nakta.springlv1.domain.user.exception;

import com.nakta.springlv1.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode { //enum 클래스 이해 필요

    DUPLICATED_ID(HttpStatus.BAD_REQUEST, "중복된 아이디입니다."),
    ADMINTOKEN_NOT_MATCH(HttpStatus.BAD_REQUEST, "관리자 패스워드가 일치하지 않습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "잘못된 패스워드입니다."),
    ID_NOT_FOUND(HttpStatus.NOT_FOUND, "아이디를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}