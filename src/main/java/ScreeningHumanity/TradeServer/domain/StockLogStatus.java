package ScreeningHumanity.TradeServer.domain;

/**
 * BUY = 매수한 상태
 * SELL = 매도한 상태
 * RESERVATION_SALE = 예약 매수 완료
 * RESERVATION_SALE = 예약 매도 완료
 */
public enum StockLogStatus {
    BUY,
    SALE,
    RESERVATION_BUY,
    RESERVATION_SALE
}
