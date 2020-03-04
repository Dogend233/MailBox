package com.tripleying.qwq.MailBox;

import com.tripleying.qwq.MailBox.API.MailBoxAPI;
import com.tripleying.qwq.MailBox.Utils.FileUtil;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 外部信息
 */
public class OuterMessage {
    
    private static final HashMap<String,String> DISPLAY = new HashMap();
    public static String globalTopic;
    public static String globalContent;
    public static String globalSender;
    public static String globalFrom;
    public static String globalPreview;
    public static String globalHasFile;
    public static String globalNoFile;
    public static String globalNoPermission;
    public static String globalNumberError;
    public static String globalEmptyField;
    public static String globalMaxField;
    public static String globalSetField;
    public static String globalSetNull;
    public static String globalExceedMax;
    
    public static String tipsNew;
    public static String tipsJoin;
    public static String tipsKey;
    
    public static String listInBox;
    public static String listOutBox;
    public static String listNullBox;
    public static String listCountBox;
    public static String listNullConsole;
    public static String listCountConsole;
    
    public static String mailSend;
    public static String mailCollect;
    public static String mailDelete;
    public static String mailNotMail;
    public static String mailExpire;
    public static String mailNoStart;
    public static String mailListError;
    public static String mailPlayerError;
    public static String mailSendSuccess;
    public static String mailSendError;
    public static String mailSendSqlError;
    public static String mailFileNameError;
    public static String mailFileSaveError;
    public static String mailCollectSuccess;
    public static String mailCollectError;
    public static String mailDeleteSuccess;
    public static String mailDeleteError;
    public static String mailExpand;
    public static String mailExpandError;
    public static String mailReadError;
    
    public static String newStop;
    public static String newAddFile;
    public static String newRemoveFiles;
    public static String newCancel;
    public static String newStopMsg;
    public static String newInputPrompt;
    public static String newNullInputPrompt;
    public static String newCreate;
    public static String newSelect;
    public static String newPreview;
    public static String newSend;
    public static String newOptionNotExist;
    
    public static String sqlSuccess;
    public static String sqlError;
    
    public static List<String> updateNew;
    public static String updateNewest;
    public static String updateError;
    
    public static String exchangeSuccess;
    public static String exchangeError;
    public static String exchangeRepeat;
    public static String exchangeNotCdkey;
    public static String exchangeNotMail;
    public static String exchangeExceedDay;
    
    public static String fileUpload;
    public static String fileDownload;
    public static String fileFilename;
    public static String fileSuccess;
    public static String fileMulti;
    public static String fileError;
    public static String fileFailed;
    public static String fileNotFile;
    public static String fileFileInputPrompt;
    
    public static String playerRecipient;
    public static String playerNoRecipient;
    public static String playerSelfRecipient;
    public static String playerRecipientMax;
    public static String playerRecipientExceedMax;
    public static String playerMailOutMax;
    public static String playerRecipientInputPrompt;
    
    public static String permissionPermission;
    public static String permissionNoPermission;
    public static String permissionPermissionInputPrompt;
    
    public static String dateStart;
    public static String dateDeadline;
    public static String dateFormat;
    public static String datess;
    public static String datemm;
    public static String dateHH;
    public static String datedd;
    public static String dateMM;
    public static String dateyyyy;
    public static String dateStartInputPrompt;
    public static String dateDeadlineInputPrompt;
    
    public static String timesTimes;
    public static String timesZero;
    public static String timesSendZero;
    public static String timesSendExceed;
    public static String timesTimesInputPrompt;
    
    public static String keytimesKey;
    public static String keytimesKeyInputPrompt;
    public static String keytimesKeyPrefixBan;
    
    public static String cdkeyOnly;
    public static String cdkeyCreate;
    public static String cdkeyExport;
    public static String cdkeyReset;
    public static String cdkeyOnlyInputPrompt;
    
    public static String onlineNoPlayer;
    
    public static String templateTemplate;
    public static String templateSave;
    public static String templateSaveSuccess;
    public static String templateSaveError;
    public static String templateTemplateInputPrompt;
    
    public static String moneyBalance;
    public static String moneyExpand;
    public static String moneyVault;
    public static String moneyPlayerpoints;
    public static String moneyBalanceAdd;
    public static String moneyBalanceNotEnough;
    public static String moneyMoneyInputPrompt;
    
    public static String itemItem;
    public static String itemSlotBan;
    public static String itemItemClaim;
    public static String itemSlotNullInv;
    public static String itemSlotNullLocal;
    public static String itemItemNotEnough;
    public static String itemInvNotEnough;
    public static String itemItemInputPromptInv;
    public static String itemItemInputPromptLocal;
    
    public static String extracommandCommand;
    public static String extracommandDescription;
    public static String extracommandCommandError;
    public static String extracommandCommandInputPrompt;
    public static String extracommandDescriptionInputPrompt;
    
    public static String commandCollect;
    public static String commandDelete;
    public static String commandInvalid;
    public static String commandReload;
    public static String commandOnlyPlayer;
    public static String commandPlayerOffline;
    public static String commandEmptyItemList;
    public static String commandExportItemSuccess;
    public static String commandExportItemError;
    public static String commandImportItemSuccess;
    public static String commandReadItemError;
    public static String commandGiveItemSuccess;
    public static String commandGiveItemFull;
    public static String commandLoreNotExistent;
    public static String commandLoreNumberError;
    public static String commandLoreExcessMaximum;
    public static String commandLoreModifySuccess;
    public static String commandRenameItemSuccess;
    public static String commandFileNotExist;
    public static String commandMailTypeNotExist;
    public static String commandMailIdError;
    public static String commandMailNewCdkeyOnly;
    public static String commandMailNewKeytimesLength;
    public static String commandMailNewTimesCount;
    public static String commandMailNewTimesZero;
    public static String commandMailNewTimesMax;
    public static String commandMailNewDateLength;
    public static String commandMailNewDateTime;
    public static String commandMailSendSender;
    public static String commandMailClean;
    public static String commandMailUpdate;
    public static String commandMailNull;
    public static String commandMailCdkeyCreate;
    public static String commandMailCdkeyCreateError;
    public static String commandMailCdkeyExportSuccess;
    public static String commandMailCdkeyExportError;
    
    public static String placeholderHasMail;
    public static String placeholderNoMail;
    
    public static List<String> helpPlayer;
    public static List<String> helpAdmin;
    
    public static void setLanguage(String language){
        Bukkit.getConsoleSender().sendMessage(InnerMessage.lang_setup.replace("%lang%", language));
        YamlConfiguration lang = FileUtil.getConfig(language+".yml", "Message", "message");
        // 全局
        MailBoxAPI.getAllType().forEach(type -> updateTypeName(type, lang.getString("global."+type)));
        globalTopic = lang.getString("global.topic");
        globalContent = lang.getString("global.content");
        globalSender = lang.getString("global.sender");
        globalFrom = lang.getString("global.from");
        globalPreview = lang.getString("global.preview");
        globalHasFile = lang.getString("global.has-file");
        globalNoFile = lang.getString("global.no-file");
        globalNoPermission = lang.getString("global.no-permission");
        globalNumberError = lang.getString("global.number-error");
        globalEmptyField = lang.getString("global.empty-field");
        globalMaxField = lang.getString("global.max-field");
        globalSetField = lang.getString("global.set-field");
        globalSetNull = lang.getString("global.set-null");
        globalExceedMax = lang.getString("global.exceed-max");
        // 提示
        tipsNew = lang.getString("tips.new");
        tipsJoin = lang.getString("tips.join");
        tipsKey = lang.getString("tips.key","§b口令： §r%key% §a§n§l[点击快速输入口令]");
        // 列表
        listInBox = lang.getString("list.in-box");
        listOutBox = lang.getString("list.out-box");
        listNullBox = lang.getString("list.null-box");
        listCountBox = lang.getString("list.count-box");
        listNullConsole = lang.getString("list.null-console");
        listCountConsole = lang.getString("list.count-console");
        // 邮件
        mailSend = lang.getString("mail.send");
        mailCollect = lang.getString("mail.collect");
        mailDelete = lang.getString("mail.delete");
        mailNotMail = lang.getString("mail.not-mail");
        mailExpire = lang.getString("mail.expire");
        mailNoStart = lang.getString("mail.no-start");
        mailListError = lang.getString("mail.list-error");
        mailPlayerError = lang.getString("mail.player-error");
        mailSendSuccess = lang.getString("mail.send-success");
        mailSendError = lang.getString("mail.send-error");
        mailSendSqlError = lang.getString("mail.send-sql-error");
        mailFileNameError = lang.getString("mail.file-name-error");
        mailFileSaveError = lang.getString("mail.file-save-error");
        mailCollectSuccess = lang.getString("mail.collect-success");
        mailCollectError = lang.getString("mail.collect-error");
        mailDeleteSuccess = lang.getString("mail.delete-success");
        mailDeleteError = lang.getString("mail.delete-error");
        mailExpand = lang.getString("mail.expand");
        mailExpandError = lang.getString("mail.expand-error");
        mailReadError = lang.getString("mail.read-error");
        // 新邮件
        newStop = lang.getString("new.stop");
        newAddFile = lang.getString("new.add-file");
        newRemoveFiles = lang.getString("new.remove-files");
        newCancel = lang.getString("new.cancel").replace("%stop%", newStop);
        newStopMsg = lang.getString("new.stop-msg");
        newInputPrompt = lang.getString("new.input-prompt");
        newNullInputPrompt = lang.getString("new.null-input-prompt");
        newCreate = lang.getString("new.create");
        newSelect = lang.getString("new.select");
        newPreview = lang.getString("new.preview");
        newSend = lang.getString("new.send");
        newOptionNotExist = lang.getString("new.option-not-exist");
        // 数据库
        sqlSuccess = lang.getString("sql.success");
        sqlError = lang.getString("sql.error");
        // 更新检测
        updateNew = lang.getStringList("update.new");
        updateNewest = lang.getString("update.newest");
        updateError = lang.getString("update.error");
        // 兑换
        exchangeSuccess = lang.getString("exchange.success");
        exchangeError = lang.getString("exchange.error");
        exchangeRepeat = lang.getString("exchange.repeat");
        exchangeNotCdkey = lang.getString("exchange.not-cdkey");
        exchangeNotMail = lang.getString("exchange.not-mail");
        exchangeExceedDay = lang.getString("exchange.exceed-day");
        // 附件
        fileUpload = lang.getString("file.upload");
        fileDownload = lang.getString("file.download");
        fileFilename = lang.getString("file.filename");
        fileSuccess = lang.getString("file.success");
        fileMulti = lang.getString("file.multi");
        fileError = lang.getString("file.error");
        fileFailed = lang.getString("file.failed");
        fileNotFile = lang.getString("file.not-file");
        fileFileInputPrompt = lang.getString("file.file-input-prompt");
        // player邮件
        playerRecipient = lang.getString("player.recipient");
        playerNoRecipient = lang.getString("player.no-recipient");
        playerSelfRecipient = lang.getString("player.self-recipient");
        playerRecipientMax = lang.getString("player.recipient-max");
        playerRecipientExceedMax = lang.getString("player.recipient-exceed-max");
        playerMailOutMax = lang.getString("player.mail-out-max");
        playerRecipientInputPrompt = lang.getString("player.recipient-input-prompt");
        // permission邮件
        permissionPermission = lang.getString("permission.permission");
        permissionNoPermission = lang.getString("permission.no-permission");
        permissionPermissionInputPrompt = lang.getString("permission.permission-input-prompt");
        // date邮件
        dateStart = lang.getString("date.start");
        dateDeadline = lang.getString("date.deadline");
        dateFormat = lang.getString("date.format");
        datess = lang.getString("date.ss");
        datemm = lang.getString("date.mm");
        dateHH = lang.getString("date.HH");
        datedd = lang.getString("date.dd");
        dateMM = lang.getString("date.MM");
        dateyyyy = lang.getString("date.yyyy");
        dateStartInputPrompt = lang.getString("date.start-input-prompt");
        dateDeadlineInputPrompt = lang.getString("date.deadline-input-prompt");
        // times邮件
        timesTimes = lang.getString("times.times");
        timesZero = lang.getString("times.zero");
        timesSendZero = lang.getString("times.send-zero");
        timesSendExceed = lang.getString("times.send-exceed");
        timesTimesInputPrompt = lang.getString("times.times-input-prompt");
        // keytimes邮件
        keytimesKey = lang.getString("keytimes.key");
        keytimesKeyInputPrompt = lang.getString("keytimes.key-input-prompt");
        keytimesKeyPrefixBan = lang.getString("keytimes.key-prefix-ban","§b[邮箱]:这个前缀被禁止作为口令");
        // cdkey邮件
        cdkeyOnly = lang.getString("cdkey.only");
        cdkeyCreate = lang.getString("cdkey.create");
        cdkeyExport = lang.getString("cdkey.export");
        cdkeyReset = lang.getString("cdkey.reset","§a[邮箱]:已重置%player%兑换次数");
        cdkeyOnlyInputPrompt = lang.getString("cdkey.only-input-prompt");
        // online邮件
        onlineNoPlayer = lang.getString("online.no-player");
        // template邮件
        templateTemplate = lang.getString("template.template");
        templateSave = lang.getString("template.save");
        templateSaveSuccess = lang.getString("template.save-success");
        templateSaveError = lang.getString("template.save-error");
        templateTemplateInputPrompt = lang.getString("template.template-input-prompt");
        // 金钱
        moneyBalance = lang.getString("money.balance");
        moneyExpand = lang.getString("money.expand");
        moneyVault = lang.getString("money.vault");
        moneyPlayerpoints = lang.getString("money.playerpoints");
        moneyBalanceAdd = lang.getString("money.balance-add");
        moneyBalanceNotEnough = lang.getString("money.balance-not-enough");
        moneyMoneyInputPrompt = lang.getString("money.money-input-prompt");
        // 物品
        itemItem = lang.getString("item.item");
        itemSlotBan = lang.getString("item.slot-ban");
        itemItemClaim = lang.getString("item.item-claim");
        itemSlotNullInv = lang.getString("item.slot-null-inv");
        itemSlotNullLocal = lang.getString("item.slot-null-local");
        itemItemNotEnough = lang.getString("item.item-not-enough");
        itemInvNotEnough = lang.getString("item.inv-not-enough");
        itemItemInputPromptInv = lang.getString("item.item-input-prompt-inv");
        itemItemInputPromptLocal = lang.getString("item.item-input-prompt-local");
        // 附件指令
        extracommandCommand = lang.getString("extracommand.command");
        extracommandDescription = lang.getString("extracommand.description");
        extracommandCommandError = lang.getString("extracommand.command-error");
        extracommandCommandInputPrompt = lang.getString("extracommand.command-input-prompt");
        extracommandDescriptionInputPrompt = lang.getString("extracommand.description-input-prompt");
        // 插件指令
        commandCollect = lang.getString("command.collect");
        commandDelete = lang.getString("command.delete");
        commandInvalid = lang.getString("command.invalid");
        commandReload = lang.getString("command.reload");
        commandOnlyPlayer = lang.getString("command.only-player");
        commandPlayerOffline = lang.getString("command.player-offline");
        commandEmptyItemList = lang.getString("command.empty-item-list");
        commandExportItemSuccess = lang.getString("command.export-item-success");
        commandExportItemError = lang.getString("command.export-item-error");
        commandImportItemSuccess = lang.getString("command.import-item-success");
        commandReadItemError = lang.getString("command.read-item-error");
        commandGiveItemSuccess = lang.getString("command.give-item-success");
        commandGiveItemFull = lang.getString("command.give-item-full");
        commandLoreNotExistent = lang.getString("command.lore-not-existent");
        commandLoreNumberError = lang.getString("command.lore-number-error");
        commandLoreExcessMaximum = lang.getString("command.lore-excess-maximum");
        commandLoreModifySuccess = lang.getString("command.lore-modify-success");
        commandRenameItemSuccess = lang.getString("command.rename-item-success");
        commandFileNotExist = lang.getString("command.file-not-exist");
        commandMailTypeNotExist = lang.getString("command.mail-type-not-exist");
        commandMailIdError = lang.getString("command.mail-id-error");
        commandMailNewCdkeyOnly = lang.getString("command.mail-new-cdkey-only");
        commandMailNewKeytimesLength = lang.getString("command.mail-new-keytimes-length");
        commandMailNewTimesCount = lang.getString("command.mail-new-times-count");
        commandMailNewTimesZero = lang.getString("command.mail-new-times-zero");
        commandMailNewTimesMax = lang.getString("command.mail-new-times-max");
        commandMailNewDateLength = lang.getString("command.mail-new-date-length");
        commandMailNewDateTime = lang.getString("command.mail-new-date-time");
        commandMailSendSender = lang.getString("command.mail-send-sender");
        commandMailClean = lang.getString("command.mail-clean");
        commandMailUpdate = lang.getString("command.mail-update");
        commandMailNull = lang.getString("command.mail-null");
        commandMailCdkeyCreate = lang.getString("command.mail-cdkey-create");
        commandMailCdkeyCreateError = lang.getString("command.mail-cdkey-create-error");
        commandMailCdkeyExportSuccess = lang.getString("command.mail-cdkey-export-success");
        commandMailCdkeyExportError = lang.getString("command.mail-cdkey-export-error");
        // 变量
        placeholderHasMail = lang.getString("placeholder.has-mail");
        placeholderNoMail = lang.getString("placeholder.no-mail");
        // 帮助
        helpPlayer =  lang.getStringList("help.player");
        helpAdmin =  lang.getStringList("help.admin");
    }
    
    public static String getTypeName(String type) {
        return DISPLAY.get(type);
    }
    
    public static void updateTypeName(String type, String display) {
        if(DISPLAY.containsKey(type)){
            DISPLAY.replace(type, display);
        }else{
            DISPLAY.put(type, display);
        }
    }
    
}
