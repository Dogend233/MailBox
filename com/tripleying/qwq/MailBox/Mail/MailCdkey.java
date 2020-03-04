package com.tripleying.qwq.MailBox.Mail;

import com.tripleying.qwq.MailBox.Utils.CdkeyUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * cdkey邮件
 */
public interface MailCdkey {
    
    /**
     * 设置邮件是否只能有一个Cdkey
     * @param only Cdkey唯一性
     */
    public void setOnly(boolean only);
    
    /**
     * 返回邮件是否只能有一个Cdkey
     * @return boolean
     */
    public boolean isOnly();
    
    /**
     * 生成指定数量的Cdkey
     * @param i 数量
     * @return 成功生成的数量
     */
    default int generateCdkey(int i) {
        if(isOnly()){
            try {
                if(CdkeyUtil.sendCdkey(CdkeyUtil.generateCdkey(),getId())) return 1;
            } catch (Exception ex) {}
            return 0;
        }else{
            int count = 0;
            for(int j=0;j<i;j++){
                try {
                    if(CdkeyUtil.sendCdkey(CdkeyUtil.generateCdkey(),getId())) count++;
                } catch (Exception ex) {
                    Logger.getLogger(CdkeyMail.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return count;
        }
    }
    
    /**
     * 删除本地已导出的Cdkey
     */
    default void DeleteLocalCdkey(){
        CdkeyUtil.deleteLocalCdkey(getId());
    }
    
    public int getId();

}
