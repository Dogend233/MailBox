package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.TemplateUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;

/**
 * template邮件
 */
public interface MailTemplate {
    
    /**
     * 设置模板文件名
     * @param template 模板文件名
     */
    public void setTemplate(String template);

    /**
     * 获取模板文件名
     * @return 模板文件名
     */
    public String getTemplate();
    
    default boolean Send(CommandSender send, ConversationContext cc) {
        return TemplateUtil.saveTemplateMail(this);
    }
    
}
