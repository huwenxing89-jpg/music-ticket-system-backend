package com.music.common.constants;

/**
 * 系统常量定义
 *
 * @author 黄晓倩
 */
public class SystemConstants {

    /**
     * Redis键前缀
     */
    public static final String REDIS_KEY_PREFIX = "music:";

    /**
     * 用户Token前缀
     */
    public static final String TOKEN_PREFIX = "token:";

    /**
     * 座位锁定前缀
     */
    public static final String SEAT_LOCK_PREFIX = "seat:lock:";

    /**
     * 用户信息缓存前缀
     */
    public static final String USER_INFO_PREFIX = "user:info:";

    /**
     * 验证码前缀
     */
    public static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * 座位状态：可选
     */
    public static final Integer SEAT_STATUS_AVAILABLE = 0;

    /**
     * 座位状态：已锁定
     */
    public static final Integer SEAT_STATUS_LOCKED = 1;

    /**
     * 座位状态：已售出
     */
    public static final Integer SEAT_STATUS_SOLD = 2;

    /**
     * 订单状态：待支付
     */
    public static final Integer ORDER_STATUS_PENDING = 0;

    /**
     * 订单状态：已支付
     */
    public static final Integer ORDER_STATUS_PAID = 1;

    /**
     * 订单状态：已取消
     */
    public static final Integer ORDER_STATUS_CANCELLED = 2;

    /**
     * 订单状态：已完成
     */
    public static final Integer ORDER_STATUS_COMPLETED = 3;

    /**
     * 订单状态：退款中
     */
    public static final Integer ORDER_STATUS_REFUNDING = 4;

    /**
     * 订单状态：已退款
     */
    public static final Integer ORDER_STATUS_REFUNDED = 5;

    /**
     * 用户角色：普通用户
     */
    public static final String USER_ROLE = "user";

    /**
     * 用户角色：管理员
     */
    public static final String ADMIN_ROLE = "admin";

    /**
     * 用户状态：正常
     */
    public static final Integer USER_STATUS_NORMAL = 0;

    /**
     * 用户状态：禁用
     */
    public static final Integer USER_STATUS_DISABLE = 1;

    /**
     * 剧目状态：即将上映
     */
    public static final Integer SHOW_STATUS_COMING = 0;

    /**
     * 剧目状态：正在热映
     */
    public static final Integer SHOW_STATUS_SHOWING = 1;

    /**
     * 剧目状态：已下映
     */
    public static final Integer SHOW_STATUS_ENDED = 2;

    /**
     * 场次状态：未开始
     */
    public static final Integer SESSION_STATUS_NOT_STARTED = 0;

    /**
     * 场次状态：进行中
     */
    public static final Integer SESSION_STATUS_ONGOING = 1;

    /**
     * 场次状态：已结束
     */
    public static final Integer SESSION_STATUS_ENDED = 2;

    /**
     * 场次状态：已取消
     */
    public static final Integer SESSION_STATUS_CANCELLED = 3;

    /**
     * 支付方式：支付宝
     */
    public static final String PAY_METHOD_ALIPAY = "alipay";

    /**
     * 支付方式：微信支付
     */
    public static final String PAY_METHOD_WECHAT = "wechat";

    /**
     * 支付方式：余额支付
     */
    public static final String PAY_METHOD_BALANCE = "balance";

    /**
     * 支付状态：待支付
     */
    public static final Integer PAY_STATUS_PENDING = 0;

    /**
     * 支付状态：支付成功
     */
    public static final Integer PAY_STATUS_SUCCESS = 1;

    /**
     * 支付状态：支付失败
     */
    public static final Integer PAY_STATUS_FAILED = 2;

    /**
     * 支付状态：已退款
     */
    public static final Integer PAY_STATUS_REFUNDED = 3;

    /**
     * 评论状态：待审核
     */
    public static final Integer COMMENT_STATUS_PENDING = 0;

    /**
     * 评论状态：已通过
     */
    public static final Integer COMMENT_STATUS_APPROVED = 1;

    /**
     * 评论状态：已拒绝
     */
    public static final Integer COMMENT_STATUS_REJECTED = 2;

    /**
     * Token过期时间：7天（单位：秒）
     */
    public static final Long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60L;

    /**
     * 座位锁定时间：15分钟（单位：秒）
     */
    public static final Long SEAT_LOCK_TIME = 15 * 60L;

    /**
     * 订单超时时间：30分钟（单位：秒）
     */
    public static final Long ORDER_TIMEOUT = 30 * 60L;

    /**
     * 验证码过期时间：5分钟（单位：秒）
     */
    public static final Long CAPTCHA_EXPIRE_TIME = 5 * 60L;

    /**
     * 默认页码
     */
    public static final Integer DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页数量
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页数量
     */
    public static final Integer MAX_PAGE_SIZE = 100;

    /**
     * 日期时间格式
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 时间格式
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * 默认头像URL
     */
    public static final String DEFAULT_AVATAR = "/static/images/default-avatar.png";

    /**
     * 文件上传路径
     */
    public static final String UPLOAD_PATH = "/uploads/";

    /**
     * 最大文件大小：10MB
     */
    public static final Long MAX_FILE_SIZE = 10 * 1024 * 1024L;

    /**
     * 允许的图片格式
     */
    public static final String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };

    private SystemConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
