package com.chen.LeoBlog.exception;

import com.chen.LeoBlog.enums.ErrorEnum;
import lombok.Data;

import javax.websocket.Session;

/**
 * 自定义限流异常
 */
@Data
public class FrequencyControlException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected Integer errorCode;

    /**
     * 错误信息
     */
    protected String errorMsg;

    private Session session;

    public FrequencyControlException() {
        super("频率限制");
    }

    public FrequencyControlException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public FrequencyControlException(ErrorEnum error) {
        super(error.getErrorMsg());
        this.errorCode = error.getErrorCode();
        this.errorMsg = error.getErrorMsg();
    }

    public FrequencyControlException(Session session) {
        super("频率限制");
        this.session = session;
    }

}
