package com.thinkgem.jeesite.common.exception;

import org.apache.shiro.session.SessionException;

/**
 * Created by Aaron on 2016/12/8.
 */
public class AjaxSessionTimeOutExcepition extends SessionException{
    /**
     * Creates a new SessionException.
     */
    public AjaxSessionTimeOutExcepition() {
        super();
    }

    /**
     * Constructs a new SessionException.
     *
     * @param message the reason for the exception
     */
    public AjaxSessionTimeOutExcepition(String message) {
        super(message);
    }

    /**
     * Constructs a new SessionException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public AjaxSessionTimeOutExcepition(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new SessionException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public AjaxSessionTimeOutExcepition(String message, Throwable cause) {
        super(message, cause);
    }
}
