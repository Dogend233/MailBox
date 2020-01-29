package com.tripleying.qwq.MailBox.Utils;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.GlobalConfig;
import com.tripleying.qwq.MailBox.MailBox;
import com.tripleying.qwq.MailBox.Message;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class Placeholder extends PlaceholderExpansion {
    
    @Override
    public String onPlaceholderRequest(Player p, String identifier){

        if(p!=null){
            // 玩家可领取邮件数量
            for(String type:MailBoxAPI.getTrueTypeWhithoutSpecial()){
                if(identifier.equals("player_mail_"+type)){
                    return Integer.toString(MailBox.getRelevantMailList(p, type).get("asRecipient").size());
                }
            }
            // 玩家可领取邮件总数量
            if(identifier.equals("player_mail_all")){
                return Integer.toString(MailBox.getMailAllCount(p));
            }
            // 玩家可发送物品的最大数量
            if(identifier.equals("player_send_item")){
                return Integer.toString(MailBoxAPI.playerSendItemAllow(p));
            }
            // 玩家已发送player邮件的数量
            if(identifier.equals("player_send_number")){
                return Integer.toString(MailBoxAPI.playerAsSender(p));
            }
            // 玩家可发送player邮件的最大数量
            if(identifier.equals("player_send_max")){
                return Integer.toString(MailBoxAPI.playerAsSenderAllow(p));
            }
            // 玩家收件箱是否有邮件
            if(identifier.equals("player_hasmail")){
                for(String type:MailBoxAPI.getTrueTypeWhithoutSpecial()){
                    MailBox.updateRelevantMailList(p, type);
                    if(!MailBox.getRelevantMailList(p, type).get("asRecipient").isEmpty()) return Message.placeholderHasMail;
                }
                return Message.placeholderNoMail;
            }
            // 玩家今日输入兑换码的次数
            if(identifier.equals("player_cdkey_day")){
                return Integer.toString(MailBoxAPI.cdkeyDay(p));
            }
        }
        
        // 服务器邮件数量
        for(String type:MailBoxAPI.getTrueType()){
            if(identifier.equals("server_mail_"+type)){
                return Integer.toString(MailBox.getMailHashMap(type).size());
            }
        }
        // 服务器邮件总数量
        if(identifier.equals("server_mail_all")){
            return Integer.toString(MailBox.getMailAllCount(null));
        }
        // 服务器允许发送物品的最大数量
        if(identifier.equals("server_send_item")){
            return Integer.toString(GlobalConfig.maxItem);
        }
        // 服务器发送一封邮件消耗的金币
        if(identifier.equals("server_send_expand_vault")){
            return Double.toString(GlobalConfig.vaultExpand);
        }
        // 服务器发送一封邮件消耗的点券
        if(identifier.equals("server_send_expand_point")){
            return Double.toString(GlobalConfig.playerPointsExpand);
        }
        // 服务器每附带一个物品额外消耗的金币
        if(identifier.equals("server_send_expand_item_vault")){
            return Double.toString(GlobalConfig.vaultItem);
        }
        // 服务器每附带一个物品额外消耗的点券
        if(identifier.equals("server_send_expand_item_point")){
            return Double.toString(GlobalConfig.playerPointsItem);
        }
        // 服务器允许发送的最大金币
        if(identifier.equals("server_send_vault")){
            return Double.toString(GlobalConfig.vaultMax);
        }
        // 服务器允许发送的最大点券
        if(identifier.equals("server_send_point")){
            return Integer.toString(GlobalConfig.playerPointsMax);
        }
        
        return null;
    }

    @Override
    public String getIdentifier() {
        return "mailbox";
    }

    @Override
    public String getAuthor() {
        return MailBox.getInstance().getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return MailBox.getInstance().getDescription().getVersion();
    }
    
    @Override
    public boolean persist(){
        return true;
    }
    
    @Override
    public boolean canRegister(){
        return true;
    }

}
