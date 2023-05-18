package com.tripleying.dogend.mailbox.api.module;

/**
 * 无效的模块异常
 * @since 3.1.0
 * @author Administrator
 */
public class InvalidModuleException extends Exception {

    public InvalidModuleException(Throwable cause) {
        super(cause);
    }

    public InvalidModuleException() {
    }

    public InvalidModuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidModuleException(String message) {
        super(message);
    }
}
