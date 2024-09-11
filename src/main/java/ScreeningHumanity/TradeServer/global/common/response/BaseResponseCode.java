package ScreeningHumanity.TradeServer.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 식별 코드 범위
 * 성공 : 200(통일)
 * 유저 에러 : 1000~1999
 * 매수/매도 에러 : 2000~2999
 * 랭킹 에러 : 3000~3999
 * 알림 : 4000~4999
 * 추가 기능 에러 :
 *      북마크 : 5000~5099
 *      구독 : 5100~5199
 *      {추가기능 발생 시} : 5200~5299
 * 차트 에러 : 6000~6999
 * 공통 에러 : 9000~9999
 */
@Getter
@RequiredArgsConstructor
public enum BaseResponseCode {
    // Success
    SUCCESS(HttpStatus.OK, true, 200, "요청 응답 성공"),

    // 매수/매도 에러
    SALE_STOCK_NOT_EXIST_ERROR(HttpStatus.BAD_REQUEST, false, 2000, "매도할 보유주식이 없습니다."),
    SALE_STOCK_NEGATIVE_TARGET_ERROR(HttpStatus.BAD_REQUEST, false, 2001, "매도 수량보다 보유 수량이 적습니다."),
    DELETE_RESERVATION_SALE_STOCK_ERROR(HttpStatus.BAD_REQUEST, false, 2002, "삭제할 예약 매수가 없습니다."),
    DELETE_RESERVATION_BUY_STOCK_ERROR(HttpStatus.BAD_REQUEST, false, 2003, "삭제할 예약 매도가 없습니다."),
    SALE_RESERVATION_STOCK_NOTFOUND_ERROR(HttpStatus.BAD_REQUEST, false, 2004, "예약 매도를 진행하는데 가지고 있는 주식 정보가 없습니다."),
    SALE_RESERVATION_STOCK_AMOUNT_ERROR(HttpStatus.BAD_REQUEST, false, 2005, "예약 매도 수량이 보유 수량보다 많습니다."),
    BUY_RESERVATION_STOCK_FAIL_ERROR(HttpStatus.BAD_REQUEST, false, 2006, "예약 매수를 진행 중 문제가 발생하였습니다."),
    SALE_STOCK_FAIL_ERROR(HttpStatus.BAD_REQUEST, false, 2007, "매도를 진행 중 문제가 발생하였습니다."),
    SALE_RESERVATION_STOCK_FAIL_ERROR(HttpStatus.BAD_REQUEST, false, 2008, "예약 매도를 진행 중 문제가 발생하였습니다."),
    BUY_RESERVATION_STOCK_CANCEL_FAIL_ERROR(HttpStatus.BAD_REQUEST, false, 2009, "예약 매수를 취소하다가 문제가 발생하였습니다."),
    BUY_STOCK_FAIL_ERROR(HttpStatus.BAD_REQUEST, false, 2010, "매수를 진행 중 문제가 발생하였습니다."),
    BUY_STOCK_NOT_ENOUGH_WON(HttpStatus.BAD_REQUEST, false, 2011, "돈이 부족 합니다."),

    //공통 에러. 9000 ~ 9999
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, 9000, "서버 에러"),
    VALIDATION_FAIL_ERROR(HttpStatus.BAD_REQUEST, false, 9100, "(exception error 메세지에 따름)"),
    PATH_VARIABLE_ERROR(HttpStatus.BAD_REQUEST, false, 9200, "잘못된 Path Variable 입력"),
    REQUEST_PARAM_ERROR(HttpStatus.BAD_REQUEST, false, 9300, "잘못된 Request Parameter 입력"),
    NO_HANDLER_FOUND_ERROR(HttpStatus.BAD_REQUEST, false, 9400, "존재 하지 않는 END-POINT"),
    METHOD_NOT_ALLOW_ERROR(HttpStatus.METHOD_NOT_ALLOWED, false, 9500, "(exception error 메세지에 따름)"),
    TOKEN_IS_EXPIRED_ERROR(HttpStatus.UNAUTHORIZED, false, 9999, "(gateway 에서 error 처리)");

    private final HttpStatus httpStatus;
    private final boolean isSuccess;
    private final int code;
    private final String message;
}