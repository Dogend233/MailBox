package com.tripleying.dogend.mailbox.api.module.message;

/**
 * 消息回复
 * @since 3.3.2
 * @author Dogend
 */
public class MessageReply {
    
    private final Message message;
    private final Object result;
    private final Class<?> resultType;
    private final boolean success;
    
    public MessageReply(Message message, Object result, Class<?> resultType){
        this.message = message;
        this.result = result;
        this.resultType = resultType;
        this.success = true;
    }
    
    public MessageReply(Message message, String errorMessage){
        this.message = message;
        this.result = errorMessage;
        this.resultType = String.class;
        this.success = false;
    }
    
    public boolean isSuccess(){
        return this.success;
    }
    
    public Message getMessage(){
        return this.message;
    }
    
    public Object getResult(){
        return this.result;
    }
    
    public Class<?> resultType(){
        return this.resultType;
    }
    
}
