package com.music.common.result;

/**
 * 响应码枚举
 *
 * @author 黄晓倩
 */
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),

    // 用户相关 1000-1999
    USER_NOT_LOGIN(1001, "用户未登录"),
    USER_LOGIN_SUCCESS(1002, "登录成功"),
    USER_REGISTER_SUCCESS(1003, "注册成功"),
    USER_ACCOUNT_EXIST(1004, "账号已存在"),
    USER_ACCOUNT_NOT_EXIST(1005, "账号不存在"),
    USER_PASSWORD_ERROR(1006, "密码错误"),
    USER_ACCOUNT_DISABLE(1007, "账号已被禁用"),
    USER_TOKEN_EXPIRED(1008, "Token已过期"),
    USER_TOKEN_INVALID(1009, "Token无效"),

    // 剧目相关 2000-2999
    SHOW_NOT_EXIST(2001, "剧目不存在"),
    SHOW_ALREADY_FAVORITE(2002, "已收藏该剧目"),
    SHOW_FAVORITE_SUCCESS(2003, "收藏成功"),
    SHOW_FAVORITE_CANCEL(2004, "取消收藏成功"),

    // 剧院相关 3000-3999
    THEATER_NOT_EXIST(3001, "剧院不存在"),
    SESSION_NOT_EXIST(3002, "场次不存在"),
    SESSION_ALREADY_STARTED(3003, "场次已开始"),
    SESSION_ALREADY_END(3004, "场次已结束"),

    // 座位相关 4000-4999
    SEAT_NOT_EXIST(4001, "座位不存在"),
    SEAT_NOT_AVAILABLE(4002, "座位不可选"),
    SEAT_ALREADY_LOCKED(4003, "座位已被锁定"),
    SEAT_ALREADY_SOLD(4004, "座位已售出"),
    SEAT_LOCK_TIMEOUT(4005, "座位锁定超时"),
    SEAT_LOCK_SUCCESS(4006, "座位锁定成功"),
    SEAT_UNLOCK_SUCCESS(4007, "座位解锁成功"),

    // 购物车相关 5000-5999
    CART_ITEM_NOT_EXIST(5001, "购物车项不存在"),
    CART_ITEM_ADD_SUCCESS(5002, "添加到购物车成功"),
    CART_ITEM_REMOVE_SUCCESS(5003, "移除购物车项成功"),
    CART_CLEAR_SUCCESS(5004, "清空购物车成功"),

    // 订单相关 6000-6999
    ORDER_NOT_EXIST(6001, "订单不存在"),
    ORDER_CREATE_SUCCESS(6002, "订单创建成功"),
    ORDER_PAY_SUCCESS(6003, "支付成功"),
    ORDER_CANCEL_SUCCESS(6004, "订单取消成功"),
    ORDER_REFUND_SUCCESS(6005, "退款成功"),
    ORDER_TIMEOUT(6006, "订单已超时"),
    ORDER_ALREADY_PAID(6007, "订单已支付"),
    ORDER_CANNOT_CANCEL(6008, "订单不可取消"),
    ORDER_CANNOT_REFUND(6009, "订单不可退款"),
    ORDER_STATUS_ERROR(6010, "订单状态错误"),

    // 支付相关 7000-7999
    PAY_AMOUNT_ERROR(7001, "支付金额错误"),
    PAY_TIMEOUT(7002, "支付超时"),
    PAY_FAILED(7003, "支付失败"),
    PAY_CALLBACK_VERIFY_FAILED(7004, "支付回调验证失败"),

    // 评论相关 8000-8999
    COMMENT_NOT_EXIST(8001, "评论不存在"),
    COMMENT_CREATE_SUCCESS(8002, "评论成功"),
    COMMENT_DELETE_SUCCESS(8003, "删除评论成功"),

    // 文件上传相关 9000-9999
    FILE_UPLOAD_SUCCESS(9001, "文件上传成功"),
    FILE_UPLOAD_FAILED(9002, "文件上传失败"),
    FILE_TYPE_ERROR(9003, "文件类型错误"),
    FILE_SIZE_EXCEED(9004, "文件大小超出限制"),
    FILE_NOT_EXIST(9005, "文件不存在"),

    // 系统相关 10000-10999
    SYSTEM_ERROR(10001, "系统错误"),
    DATABASE_ERROR(10002, "数据库错误"),
    REDIS_ERROR(10003, "Redis错误"),
    NETWORK_ERROR(10004, "网络错误"),
    PARAM_ERROR(10005, "参数错误"),
    PERMISSION_DENIED(10006, "权限不足"),
    REQUEST_TOO_FREQUENT(10007, "请求过于频繁"),
    CAPTCHA_ERROR(10008, "验证码错误"),
    CAPTCHA_EXPIRED(10009, "验证码已过期");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
