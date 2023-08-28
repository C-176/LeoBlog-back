package com.chen.LeoBlog.exception;

import com.chen.LeoBlog.enums.ErrorEnum;
import lombok.Data;

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

    private Long receiveId;


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

    public FrequencyControlException(Long receiveId) {
        super("频率限制");
        this.receiveId = receiveId;
    }

}
