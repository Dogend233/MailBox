package com.tripleying.dogend.mailbox.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 消息工具
 * @author Dogend
 */
public class MessageUtil {
    
    /**
     * 控制台对象
     */
    private static final ConsoleCommandSender console;
    /**
     * 插件前缀
     */
    private static String prefix;
    // 重载
    public static String reload_load;
    public static String reload_unload;
    public static String reload_finish;
    // 文件
    public static String file_read;
    public static String file_create;
    public static String file_create_error;
    // 配置
    public static String config_update;
    public static String config_save_error;
    // 数据
    public static String data_enable;
    public static String data_enable_error;
    public static String data_reg;
    public static String data_reg_error;
    public static String data_unreg;
    // 邮件
    public static String mail_reg;
    public static String mail_reg_error;
    public static String mail_unreg;
    // 金钱
    public static String money_reg;
    public static String money_reg_invalid;
    public static String money_reg_duplicate;
    public static String money_unreg;
    // 指令
    public static String command_reg;
    public static String command_reg_error;
    public static String command_unreg;
    public static String command_help;
    // 监听器
    public static String listener_reg;
    public static String listener_unreg;
    // 模块
    public static String modlue_load_empty;
    public static String modlue_load_success;
    public static String modlue_load_error_not_jar;
    public static String modlue_load_error_not_info;
    public static String modlue_load_error_info_err;
    public static String modlue_load_error_has_duplicate;
    public static String modlue_load_error_depend_plugin;
    public static String modlue_load_error_depend_module;
    public static String modlue_load_error_recycle_depend;
    public static String modlue_load_error_main_err;
    public static String modlue_unload;
    
    static {
        console = Bukkit.getConsoleSender();
        prefix = "§b[MailBox]: ";
        reload_load = "正在加载插件......";
        reload_unload = "正在卸载插件......";
        reload_finish = "插件重载完成.";
        file_read = "读取文件: §b%file%";
        file_create = "创建文件: §b%file%";
        file_create_error = "创建文件失败: §b%file%";
        config_update = "已将 %config%.yml 更新至 v%version%";
        config_save_error = "保存 %config% 的新配置文件失败";
    }
    
    public static void init(YamlConfiguration yml){
        prefix = color(yml.getString("prefix", "&b[MailBox]: "));
        reload_load = color(yml.getString("reload.load", "正在加载插件......"));
        reload_unload = color(yml.getString("reload.unload", "正在卸载插件......"));
        reload_finish = color(yml.getString("reload.finish", "插件重载完成."));
        file_read = color(yml.getString("file.read", "读取文件: &b%file%"));
        file_create = color(yml.getString("file.create", "创建文件: &b%file%"));
        file_create_error = color(yml.getString("file.create-error", "创建文件失败: &b%file%"));
        config_update = color(yml.getString("config.update", "已将 %config%.yml 更新至 v%version%"));
        config_save_error = color(yml.getString("config.save-error", "保存 %config% 的新配置文件失败"));
        data_enable = color(yml.getString("data.enable", "已启用数据源: &6%data%"));
        data_enable_error = color(yml.getString("data.enable-error", "数据源: %data% 启用失败, 卸载插件"));
        data_reg = color(yml.getString("data.reg", "注册数据源: &6%data%"));
        data_reg_error = color(yml.getString("data.reg-error", "注册数据源失败, 已有同名数据源被注册: &6%data%"));
        data_unreg = color(yml.getString("data.unreg", "已卸载其他数据源"));
        mail_reg = color(yml.getString("mail.reg", "注册系统邮件: &6%mail%"));
        mail_reg_error = color(yml.getString("mail.reg-error", "注册系统邮件失败, 已有同名系统邮件被注册: &6%mail%"));
        mail_unreg = color(yml.getString("mail.unreg", "注销系统邮件: &6%mail%"));
        money_reg = color(yml.getString("money.reg", "注册金钱: &6%money%"));
        money_reg_invalid = color(yml.getString("money.reg-invalid", "注册金钱失败, 无效的金钱类: &6%money%"));
        money_reg_duplicate = color(yml.getString("money.reg-duplicate", "注册金钱失败, 已有同名金钱被注册: &6%money%"));
        money_unreg = color(yml.getString("money.unreg", "注销金钱: &6%money%"));
        command_reg = color(yml.getString("command.reg", "注册指令: &6%command%"));
        command_reg_error = color(yml.getString("command.reg-error", "注册指令失败, 已有同名指令被注册: &6%command%"));
        command_unreg = color(yml.getString("command.unreg", "注销指令: &6%command%"));
        command_help = color(yml.getString("command.help", "&b==========&6[MailBox邮箱]&b=========="));
        listener_reg = color(yml.getString("listener.reg", "注册监听器: &6%listener%"));
        listener_unreg = color(yml.getString("listener.unreg", "注销监听器: &6%listener%"));
        modlue_load_empty = color(yml.getString("modlue.load.empty", "无本地模块可加载"));
        modlue_load_success = color(yml.getString("modlue.load.success", "加载模块: &6%module% - v%version%"));
        modlue_load_error_not_jar = color(yml.getString("modlue.load.error.not-jar", "模块加载失败: %module% 无效的Jar文件 "));
        modlue_load_error_not_info = color(yml.getString("modlue.load.error.not-info", "模块加载失败: %module% 读取模块信息失败 "));
        modlue_load_error_info_err = color(yml.getString("modlue.load.error.info-err", "模块加载失败: %module% 模块信息不可用 "));
        modlue_load_error_has_duplicate = color(yml.getString("modlue.load.error.has-duplicate", "模块加载失败: %module% 已有重名模块被加载: %another%"));
        modlue_load_error_depend_plugin = color(yml.getString("modlue.load.error.depend-plugin", "模块加载失败: %module% 缺少前置插件: %depends%"));
        modlue_load_error_depend_module = color(yml.getString("modlue.load.error.depend-module", "模块加载失败: %module% 缺少前置模块: %depends%"));
        modlue_load_error_recycle_depend = color(yml.getString("modlue.load.error.recycle-depend", "模块加载失败: %module% 检测到循环依赖"));
        modlue_load_error_main_err = color(yml.getString("modlue.load.error.main.read-err", "模块加载失败: %module% 主类错误"));
        modlue_unload = color(yml.getString("modlue.unload", "卸载模块: &6%module%"));
    }
    
    /**
     * 将&amp;转换为颜色字符§
     * @param original 原字符串
     * @return 转换后的字符串
     */
    public static String color(String original){
        return original.replaceAll("&", "§");
    }
    
    /**
     * 发送日志(绿色)
     * @param cs 指令发送者
     * @param msg 信息
     */
    public static void log(CommandSender cs, String msg){
        cs.sendMessage(prefix+"§a"+msg);
    }
    
    /**
     * 后台打印日志(绿色)
     * @param msg 信息
     */
    public static void log(String msg){
        log(console, msg);
    }
    
    /**
     * 发送错误(红色)
     * @param cs 指令发送者
     * @param msg 信息
     */
    public static void error(CommandSender cs, String msg){
        cs.sendMessage(prefix+"§c"+msg);
    }
    
    /**
     * 后台打印错误(红色)
     * @param msg 信息
     */
    public static void error(String msg){
        error(console, msg);
    }
    
}
