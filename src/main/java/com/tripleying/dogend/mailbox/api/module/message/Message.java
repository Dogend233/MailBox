package com.tripleying.dogend.mailbox.api.module.message;

/**
 * 消息
 * @since 3.3.2
 * @author Dogend
 */
public interface Message {
    
    /**
     * 发送消息
     * @return MessageReply
     */
    public MessageReply sendMessage();
    
}
