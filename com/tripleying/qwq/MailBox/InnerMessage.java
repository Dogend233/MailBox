package com.tripleying.qwq.MailBox;

import com.tripleying.qwq.MailBox.Utils.FileUtil;
import java.io.UnsupportedEncodingException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 内部信息
 */
public class InnerMessage {
    
    public static String enable;
    public static String author;
    public static String website;
    public static String version;
    public static String server_jar;
    public static String server_version;
    public static String under1_11;
    public static String under1_10;
    public static String under1_9;
    public static String under1_8;
    public static String config_charset;
    public static String reflect_version;
    public static String folder_create;
    public static String file_create;
    public static String file_error;
    public static String file_read;
    public static String soft_depend_enable;
    public static String soft_depend_close;
    public static String soft_depend_disable;
    public static String vexview_under2_6_8_server_over1_12;
    public static String vexview_under2_6_8_over2_6;
    public static String vexview_under2_6_8;
    public static String vexview_under2_6;
    public static String vexview_under2_5_6;
    public static String vexview_under2_5_6_over2_5;
    public static String vexview_under2_5;
    public static String vexview_under2_4;
    public static String lang_check;
    public static String lang_not_exist;
    public static String lang_error;
    public static String lang_setup;
    public static String vexview;
    public static String hud_box;
    public static String command_box;
    public static String single_box;
    public static String double_box;
    public static String sql_connect;
    public static String enabled;
    public static String disable;
    public static String vexview_hud_close;
    public static String placeholder_unhook;
    public static String sql_shutdown;
    public static String sql_shutdown_error;
    public static String diasbled;
    
    public static boolean set(MailBox mb){
        try {
            YamlConfiguration config;
            if(FileUtil.existFile("Message/message.yml")) config = FileUtil.getYaml(FileUtil.getFile("Message/message.yml"));
            else config = FileUtil.getYaml(FileUtil.getInputStreamReader("message/message.yml", "UTF-8"));
            enable = config.getString("enable","§b-----[MailBox]:插件启动-----");
            author = config.getString("author","§6-----作者: %author%");
            website = config.getString("website","§6-----官网: %website%");
            version = config.getString("version","§6-----版本: %version%");
            server_jar = config.getString("server-jar","§6-----服务端核心: %server%");
            server_version = config.getString("server-version","§6-----服务器版本: %version%");
            under1_11 = config.getString("under1-11","§c-----服务器版本低于1.11, 调整Title提醒方法和获取物品名字方法");
            under1_10 = config.getString("under1-10","§c-----服务器版本低于1.10, 调整获取背包物品方法");
            under1_9 = config.getString("under1-9","§c-----服务器版本低于1.9, 调整获取手上物品方法, 生成配置文件时将进行转码");
            under1_8 = config.getString("under1-8","§c-----服务器版本低于1.8, 禁用标题提醒方式");
            folder_create = config.getString("folder-create","§a-----创建文件夹: %folder%");
            file_create = config.getString("file-create","§a-----创建文件: %file%");
            file_error = config.getString("file-error","§c-----文件: %file% 创建失败");
            file_read = config.getString("file-read","§6-----读取文件: %file%");
            soft_depend_enable = config.getString("soft-depend-enable","§a-----前置插件[%plugin%]已安装，版本: %version%");
            soft_depend_close = config.getString("soft-depend-close","§c-----前置插件[%plugin%]未安装，已关闭相关功能");
            soft_depend_disable = config.getString("soft-depend-disable","§c-----未开启[%plugin%]，已关闭相关功能");
            vexview_under2_6_8_server_over1_12 = config.getString("vexview-under2-6-8-server-over1-12","§c-----前置插件[VexView]版本小于2.6.8并且服务器版本高于1.12，已关闭相关功能");
            vexview_under2_6_8_over2_6 = config.getString("vexview-under2-6-8-over2-6","§c-----前置插件[VexView]版本小于2.6.8且大于2.6，已关闭相关功能");
            vexview_under2_6_8 = config.getString("vexview-under2-6-8","§c-----前置插件[VexView]版本小于2.6.8, 文本域替换为文本框");
            vexview_under2_6 = config.getString("vexview-under2-6","§c-----前置插件[VexView]版本小于2.6, HUD不可点击");
            vexview_under2_5_6 = config.getString("vexview-under2-5-6","§c-----前置插件[VexView]版本小于2.5.6, 调整发送邮件GUI");
            vexview_under2_5_6_over2_5 = config.getString("vexview-under2-5-6-over2-5","§c-----前置插件[VexView]版本小于2.5.6且大于2.5, 已关闭发送邮件GUI");
            vexview_under2_5 = config.getString("vexview-under2-5","§c-----前置插件[VexView]版本小于2.5, 已关闭发送邮件GUI和导出物品列表GUI");
            vexview_under2_4 = config.getString("vexview-under2-4","§c-----前置插件[VexView]版本小于2.4，已关闭相关功能");
            lang_check = config.getString("lang-check","§6-----检查语言文件");
            lang_not_exist = config.getString("lang-not-exist","§c-----目标语言: %lang%文件不存在");
            lang_setup = config.getString("lang-setup","§a-----设置语言: %lang%");
            vexview = config.getString("vexview","§6-----配置VexView");
            hud_box = config.getString("hud-box","§a-----启用邮箱HUD");
            command_box = config.getString("command-box","§a-----启用指令打开邮箱GUI");
            single_box = config.getString("single-box","§a-----已启用%key%键打开邮箱GUI");
            double_box = config.getString("double-box","§a-----已启用%key1%+%key2%键打开邮箱GUI");
            sql_connect = config.getString("sql-connect","§6-----连接数据库");
            enabled = config.getString("enabled","§b-----[MailBox]:插件启动成功-----");
            disable = config.getString("disable","§b-----[MailBox]:插件正在卸载-----");
            vexview_hud_close = config.getString("vexview-hud-close","§6-----正在移除玩家HUD");
            placeholder_unhook = config.getString("placeholder-unhook","§6-----正在注销PlaceholderAPI变量");
            sql_shutdown = config.getString("sql-shutdown","§6-----断开数据库连接");
            sql_shutdown_error = config.getString("sql-shutdown-error","§6-----断开数据库连接失败");
            diasbled = config.getString("diasbled","§b-----[MailBox]:插件卸载完毕-----");
            return true;
        }catch (UnsupportedEncodingException e) {
            return false;
        }
    }
    
}