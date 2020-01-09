package com.嘤嘤嘤.qwq.MailBox.Utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.potion.PotionEffectType;

public class Data2cn {
    
    ItemStack is;
    ItemMeta im;
    String m;
    short d;
    
    Data2cn(ItemStack is){
        this.is = is;
        this.im = is.getItemMeta();
        this.m = is.getType().toString();
        this.d = is.getDurability();
    }
    
    public static String itemName(ItemStack is){
        Data2cn cn = new Data2cn(is);
        switch(cn.m.length()-cn.m.replace("_", "").length()){
            case 3:
                return cn.itemThree_();
            case 2:
                return cn.itemTwo_();
            case 1:
                return cn.itemOne_();
            case 0:
                return cn.itemZero_();
            default:
                return cn.m;
        }
    }
    
    public static String enchantName(Enchantment e){
        switch(e.getName()){
            case "DIG_SPEED":
                return "效率";
            case "SILK_TOUCH":
                return "精准采集";
            case "DURABILITY":
                return "耐久";
            case "LOOT_BONUS_BLOCKS":
                return "时运";
            case "LUCK":
                return "海神眷顾";
            case "LURE":
                return "钓饵";
            case "MENDING":
                return "经验修补";
            case "VANISHING_CURSE":
                return "§c消失诅咒";
            case "PROTECTION_ENVIRONMENTAL":
                return "保护";
            case "PROTECTION_FIRE":
                return "火焰保护";
            case "PROTECTION_FALL":
                return "摔落保护";
            case "PROTECTION_EXPLOSIONS":
                return "爆炸保护";
            case "PROTECTION_PROJECTILE":
                return "弹射物保护";
            case "OXYGEN":
                return "水下呼吸";
            case "WATER_WORKER":
                return "水下速掘";
            case "THORNS":
                return "荆棘";
            case "DEPTH_STRIDER":
                return "深海探索者";
            case "FROST_WALKER":
                return "冰霜行者";
            case "BINDING_CURSE":
                return "§c绑定诅咒";
            case "DAMAGE_ALL":
                return "锋利";
            case "DAMAGE_UNDEAD":
                return "亡灵杀手";
            case "DAMAGE_ARTHROPODS":
                return "节肢杀手";
            case "KNOCKBACK":
                return "击退";
            case "FIRE_ASPECT":
                return "火焰附加";
            case "LOOT_BONUS_MOBS":
                return "抢夺";
            case "SWEEPING_EDGE":
                return "横扫之刃";
            case "ARROW_DAMAGE":
                return "力量";
            case "ARROW_KNOCKBACK":
                return "冲击";
            case "ARROW_FIRE":
                return "火矢";
            case "ARROW_INFINITE":
                return "无限";
            default:
                return e.getName();
        }
    }
    
    public static String potionName(PotionMeta pm){
        PotionEffectType pet = pm.getBasePotionData().getType().getEffectType();
        if(pet==null){
            switch(pm.getBasePotionData().getType().toString()){
                case "MUNDANE":
                    return "平凡的药水";
                case "THICK":
                    return "浓稠的药水";
                case "AWKWARD":
                    return "粗制的药水";
                case "WATER":
                default:
                    return "水瓶";
            }
        }else{
            return potion(pet)+"药水";
        }
    }
    
    private static String potion(PotionEffectType pet){
        switch(pet.getName()){
            case "NIGHT_VISION":
                return "夜视";
            case "INVISIBILITY":
                return "隐身";
            case "JUMP":
                return "跳跃";
            case "FIRE_RESISTANCE":
                return "抗火";
            case "SPEED":
                return "迅捷";
            case "SLOW":
                return "迟缓";
            case "WATER_BREATHING":
                return "水肺";
            case "HEAL":
                return "治疗";
            case "HARM":
                return "伤害";
            case "POISON":
                return "剧毒";
            case "REGENERATION":
                return "再生";
            case "INCREASE_DAMAGE":
                return "力量";
            case "WEAKNESS":
                return "虚弱";
            case "LUCK":
                return "幸运";
            default:
                return pet.getName();
        }
    }
    
    public static String spawnEggName(SpawnEggMeta sem){
        return "生成 "+entity(sem.getSpawnedType());
    }
    
    private static String entity(EntityType et){
        switch(et.toString().toLowerCase()){
            case "bat":
                return "蝙蝠";
            case "blaze":
                return "烈焰人";
            case "cave_spider":
                return "洞穴蜘蛛";
            case "chicken":
                return "鸡";
            case "cow":
                return "牛";
            case "creeper":
                return "爬行者";
            case "donkey":
                return "驴";
            case "elder_guardian":
                return "远古守卫者";
            case "enderman":
                return "末影人";
            case "endermite":
                return "末影螨";
            case "evoker":
                return "唤魔者";
            case "ghast":
                return "恶魂";
            case "guardian":
                return "守卫者";
            case "horse":
                return "马";
            case "husk":
                return "尸壳";
            case "llama":
                return "羊驼";
            case "magma_cube":
                return "岩浆怪";
            case "mushroom_cow":
                return "哞菇";
            case "mule":
                return "骡";
            case "ocelot":
                return "豹猫";
            case "parrot":
                return "鹦鹉";
            case "pig":
                return "猪";
            case "polar_bear":
                return "北极熊";
            case "rabbit":
                return "兔子";
            case "sheep":
                return "羊";
            case "shulker":
                return "潜影贝";
            case "silverfish":
                return "蠹虫";
            case "skeleton":
                return "骷髅";
            case "skeleton_horse":
                return "骷髅马";
            case "slime":
                return "史莱姆";
            case "spider":
                return "蜘蛛";
            case "squid":
                return "鱿鱼";
            case "stray":
                return "流髑";
            case "vex":
                return "恼鬼";
            case "villager":
                return "村民";
            case "vindicator":
                return "卫道士";
            case "witch":
                return "女巫";
            case "wither_skeleton":
                return "凋零骷髅";
            case "wolf":
                return "狼";
            case "zombie":
                return "僵尸";
            case "zombie_horse":
                return "僵尸马";
            case "pig_zombie":
                return "僵尸猪人";
            case "zombie_villager":
                return "僵尸村民";
            case "player":
                return "玩家";
            default:
                return et.toString();
        }
    }
    
    public static String color(short d){
        switch(d){
            case 15:
                return "黑色";
            case 14:
                return "红色";
            case 13:
                return "绿色";
            case 12:
                return "棕色";
            case 11:
                return "蓝色";
            case 10:
                return "紫色";
            case 9:
                return "青色";
            case 8:
                return "淡灰色";
            case 7:
                return "灰色";
            case 6:
                return "粉红色";
            case 5:
                return "黄绿色";
            case 4:
                return "黄色";
            case 3:
                return "淡蓝色";
            case 2:
                return "品红色";
            case 1:
                return "橙色";
            default:
                return "白色";
        }
    }
    
    public static String color(String c){
        switch(c){
            case "BLACK":
                return "黑色";
            case "RED":
                return "红色";
            case "GREEN":
                return "绿色";
            case "BROWN":
                return "棕色";
            case "BLUE":
                return "蓝色";
            case "PURPLE":
                return "紫色";
            case "CYAN":
                return "青色";
            case "SILVER":
                return "淡灰色";
            case "GRAY":
                return "灰色";
            case "PINK":
                return "粉红色";
            case "LIME":
                return "黄绿色";
            case "YELLOW":
                return "黄色";
            case "LIGHT_BLUE":
            case "LIGHT":
                return "淡蓝色";
            case "MAGENTA":
                return "品红色";
            case "ORANGE":
                return "橙色";
            default:
                return "白色";
        }
    }
    
    public String wood(){
        switch(d){
            case 5:
                if("SAPLING".equals(m) || "LEAVES".equals(m)) return "深色橡树";
                return "深色橡木";
            case 4:
                return "金合欢";
            case 3:
                return "丛林";
            case 2:
                if(m.contains("_"))return "桦";
                return "白桦";
            case 1:
                return "云杉";
            default:
                if(m.contains("_")){
                    switch(m.substring(m.indexOf('_')+1)){
                        case "SPADE":
                        case "AXE":
                        case "PICKAXE":
                        case "HOE":
                        case "HELMET":
                        case "CHESTPLATE":
                        case "LEGGINGS":
                        case "BOOTS":
                        case "SWORD":
                            return "木";
                    }
                }else{
                    switch(m){
                        case "LOG":
                            return "橡";
                        case "SAPLING":
                        case "LEAVES":
                            return "橡树";
                    }
                }
                return "橡木";
        }
    }
    
    public String itemThree_(){
        switch(m){
            case "DARK_OAK_FENCE_GATE":
                return "深色橡木栅栏门";
            case "DARK_OAK_DOOR_ITEM":
                return "深色橡木门";
            case "LIGHT_BLUE_SHULKER_BOX":
                return "淡蓝色潜影盒";
            case "LIGHT_BLUE_GLAZED_TERRACOTTA":
                return "淡蓝色带釉陶瓦";
            default:
                return m;
        }
    }
    
    public String itemTwo_(){
        switch(m.substring(m.indexOf('_')+1)){
            case "SHULKER_BOX":
                return color(m.substring(0,m.indexOf('_')))+"潜影盒";
            case "GLAZED_TERRACOTTA":
                return color(m.substring(0,m.indexOf('_')))+"带釉陶瓦";
        }
        switch(m){
            case "JACK_O_LANTERN":
                return "南瓜灯";
            case "NETHER_BRICK_STAIRS":
                return "地狱砖楼梯";
            case "SPRUCE_WOOD_STAIRS":
                return "云杉木楼梯";
            case "BIRCH_WOOD_STAIRS":
                return "桦木楼梯";
            case "JUNGLE_WOOD_STAIRS":
                return "丛林木楼梯";
            case "DARK_OAK_STAIRS":
                return "深色橡木楼梯";
            case "RED_SANDSTONE_STAIRS":
                return "红砂岩楼梯";
            case "NETHER_WART_BLOCK":
                return "地狱疣块";
            case "RED_NETHER_BRICK":
                return "红色地狱砖";
            case "ENDER_PORTAL_FRAME":
                return "末地传送门";
            case "STAINED_GLASS_PANE":
                return color(d)+"染色玻璃板";
            case "DARK_OAK_FENCE":
                return "深色橡木栅栏";
            case "FLOWER_POT_ITEM":
                return "花盆";
            case "PISTON_STICKY_BASE":
                return "粘性活塞";
            case "REDSTONE_TORCH_ON":
                return "红石火把";
            case "REDSTONE_LAMP_OFF":
                return "红石灯";
            case "SPRUCE_FENCE_GATE":
                return "云杉木栅栏门";
            case "BIRCH_FENCE_GATE":
                return "白桦木栅栏门";
            case "JUNGLE_FENCE_GATE":
                return "丛林木栅栏门";
            case "ACACIA_FENCE_GATE":
                return "金合欢木栅栏门";
            case "SPRUCE_DOOR_ITEM":
                return "云杉木门";
            case "BIRCH_DOOR_ITEM":
                return "白桦木门";
            case "JUNGLE_DOOR_ITEM":
                return "丛林木门";
            case "ACACIA_DOOR_ITEM":
                return "金合欢木门";
            case "BOAT_DARK_OAK":
                return "深色橡木船";
            case "EYE_OF_ENDER":
                return "末影之眼";
            case "BOOK_AND_QUILL":
                return "书与笔";
            case "NETHER_BRICK_ITEM":
                return "地狱砖";
            case "CHORUS_FRUIT_POPPED":
                return "爆裂紫颂果";
            case "FLINT_AND_STEEL":
                return "打火石";
            case "FERMENTED_SPIDER_EYE":
                return "发酵蛛眼";
            case "BREWING_STAND_ITEM":
                return "酿造台";
            default:
                return m;
        }
    }
    
    public String itemOne_(){
        switch(m){
            case "NETHER_BRICK":
                return "地狱砖块";
            case "ENDER_STONE":
                return "末地石";
            case "STAINED_CLAY":
                return color(d)+"陶瓦";
            case "LOG_2":
                if(d==1) return "深色橡木";
                return "金合欢木";
            case "SEA_LANTERN":
                return "海晶石灯";
            case "HARD_CLAY":
                return "陶瓦";
            case "PACKED_ICE":
                return "浮冰";
            case "RED_SANDSTONE":
                switch (d){
                    case 2:
                        return "平滑红砂岩";
                    case 1:
                        return "錾制红砂岩";
                    default:
                        return "红砂岩";
                }
            case "STONE_SLAB2":
                return "红砂岩台阶";
            case "PURPUR_PILLAR":
                return "竖纹紫铂块";
            case "END_BRICKS":
                return "末地石砖";
            case "CONCRETE_POWDER":
                return color(d)+"混凝土粉末";
            case "LONG_GRASS":
                switch (d){
                    case 2:
                        return "蕨";
                    case 1:
                        return "草";
                    default:
                        return m;
                }
            case "DEAD_BUSH":
                return "枯死的灌木";
            case "YELLOW_FLOWER":
                return "蒲公英";
            case "RED_ROSE":
                switch(d){
                    case 8:
                        return "滨菊";
                    case 7:
                        return "粉红色郁金香";
                    case 6:
                        return "白色郁金香";
                    case 5:
                        return "橙色郁金香";
                    case 4:
                        return "红色郁金香";
                    case 3:
                        return "茜西花";
                    case 2:
                        return "绒球葱";
                    case 1:
                        return "兰花";
                    default:
                        return "虞美人";
                }
            case "BROWN_MUSHROOM":
            case "RED_MUSHROOM":
                return "蘑菇";
            case "MONSTER_EGGS":
                switch(d){
                    case 5:
                        return "錾制石砖怪物蛋";
                    case 4:
                        return "裂石砖怪物蛋";
                    case 3:
                        return "苔石砖怪物蛋";
                    case 2:
                        return "石砖怪物蛋";
                    case 1:
                        return "圆石怪物蛋";
                    default:
                        return "石头怪物蛋";
                }
            case "IRON_FENCE":
                return "铁栏杆";
            case "THIN_GLASS":
                return "玻璃板";
            case "WATER_LILY":
                return "睡莲";
            case "ENCHANTMENT_TABLE":
                return "附魔台";
            case "ENDER_CHEST":
                return "末影箱";
            case "COBBLE_WALL":
                if(d==1) return "苔石墙";
                return "圆石墙";
            case "LEAVES_2":
                if(d==1) return "深色橡树树叶";
                return "金合欢树叶";
            case "DOUBLE_PLANT":
                switch(d){
                    case 5:
                        return "牡丹";
                    case 4:
                        return "玫瑰丛";
                    case 3:
                        return "大型蕨";
                    case 2:
                        return "高草丛";
                    case 1:
                        return "丁香";
                    default:
                        return "向日葵";
                }
            case "END_ROD":
                return "末地烛";
            case "ITEM_FRAME":
                return "物品展示框";
            case "SKULL_ITEM":
                switch(d){
                    case 5:
                        return "龙首";
                    case 4:
                        return "爬行者的头";
                    case 3:
                        return "头";
                    case 2:
                        return "僵尸的头";
                    case 1:
                        return "凋零骷髅头颅";
                    default:
                        return "骷髅头颅";
                }
            case "ARMOR_STAND":
                return "盔甲架";
            case "END_CRYSTAL":
                return "末影水晶";
            case "NOTE_BLOCK":
                return "音符盒";
            case "PISTON_BASE":
                return "活塞";
            case "STONE_PLATE":
                return "石质压力板";
            case "WOOD_PLATE":
                return "木质压力板";
            case "STONE_BUTTON":
            case "WOOD_BUTTON":
                return "按钮";
            case "TRAP_DOOR":
                return "活板门";
            case "FENCE_GATE":
                return "橡木栅栏门";
            case "TRIPWIRE_HOOK":
                return "绊线钩";
            case "TRAPPED_CHEST":
                return "陷阱箱";
            case "GOLD_PLATE":
                return "测重压力板(轻质)";
            case "IRON_PLATE":
                return "测重压力板(重质)";
            case "DAYLIGHT_DETECTOR":
                return "阳光传感器";
            case "CARROT_STICK":
                return "胡萝卜钓竿";
            case "BOAT_SPRUCE":
                return "云杉木船";
            case "BOAT_BIRCH":
                return "桦木船";
            case "BOAT_JUNGLE":
                return "丛林木船";
            case "BOAT_ACACIA":
                return "金合欢木船";
            case "MILK_BUCKET":
                return "牛奶";
            case "CLAY_BRICK":
                return "红砖";
            case "CLAY_BALL":
                return "粘土";
            case "SUGAR_CANE":
                return "甘蔗";
            case "INK_SACK":
                switch(d){
                    case 15:
                        return "骨粉";
                    case 14:
                    case 13:
                    case 12:
                    case 11:
                    case 10:
                    case 9:
                    case 8:
                    case 7:
                    case 6:
                    case 5:
                        return color((short)(15-d))+"染料";
                    case 4:
                        return "青金石";
                    case 3:
                        return "可可豆";
                    case 2:
                        return "仙人掌绿";
                    case 1:
                        return "玫瑰红";
                    default:
                        return "墨囊";
                }
            case "ENDER_PEARL":
                return "末影珍珠";
            case "BLAZE_ROD":
                return "烈焰棒";
            case "NETHER_STALK":
                return "地狱疣";
            case "MONSTER_EGG":
                return spawnEggName((SpawnEggMeta)im);
            case "EXP_BOTTLE":
                return "附魔之瓶";
            case "EMPTY_MAP":
                return "空地图";
            case "NETHER_STAR":
                return "下界之星";
            case "FIREWORK_CHARGE":
                return "烟火之星";
            case "RABBIT_HIDE":
                return "兔子皮";
            case "SHULKER_SHELL":
                return "潜影壳";
            case "WRITTEN_BOOK":
                return "成书";
            case "FISHING_ROD":
                return "钓鱼竿";
            case "NAME_TAG":
                return "命名牌";
            case "ENCHANTED_BOOK":
                return "§e附魔书";
            case "SPECTRAL_ARROW":
                return "光灵箭";
            case "TIPPED_ARROW":
                return "药水之箭";
            case "GHAST_TEAR":
                return "恶魂之泪";
            case "GLASS_BOTTLE":
                return "玻璃瓶";
            case "DRAGONS_BREATH":
                return "龙息";
            case "SPECKLED_MELON":    
                return "闪烁的西瓜";
            case "RABBIT_FOOT":
                return "兔子脚";
            case "MAGMA_CREAM":
                return "岩浆膏";
            case "BLAZE_POWDER":
                return "烈焰粉";
            case "CAULDRON_ITEM":
                return "炼药锅";
            case "GOLDEN_CARROT":
                return "金胡萝卜";
            case "MUSHROOM_SOUP":
                return "蘑菇煲";
            case "GRILLED_PORK":
                return "熟猪排";
            case "GOLDEN_APPLE":
                if(d==1) return "§d金苹果";
                return "§b金苹果";
            case "ROTTEN_FLESH":
                return "腐肉";
            case "SPIDER_EYE":
                return "蜘蛛眼";
            case "CARROT_ITEM":
                return "胡萝卜";
            case "POTATO_ITEM":
                return "马铃薯";
            case "RABBIT_STEW":
                return "兔肉煲";
            default:
                String str = "";
                switch (m.substring(0,m.indexOf('_'))){
                    case "GOLD":
                        str += "金";break;
                    case "IRON":
                        str += "铁";break;
                    case "COAL":
                        str += "煤";break;
                    case "LAPIS":
                        str += "青金石";break;
                    case "WOOD":
                        str += wood();break;
                    case "MOSSY":
                        str += "苔";break;
                    case "DIAMOND":
                        str += "钻石";break;
                    case "COBBLESTONE":
                        str += "圆石";break;
                    case "REDSTONE":
                        str += "红石";break;
                    case "SNOW":
                        str += "雪";break;
                    case "SOUL":
                        str += "灵魂";break;
                    case "STAINED":
                        str += color(d);break;
                    case "SMOOTH":
                        switch (d){
                            case 3:
                                str += "錾制石砖";break;
                            case 2:
                                str += "裂石砖";break;
                            case 1:
                                str += "苔石砖";break;
                            default:
                                str += "石砖";
                        }    break;
                    case "MELON":
                        str += "西瓜";break;
                    case "BRICK":
                        str += "砖";break;
                    case "SANDSTONE":
                        str += "砂岩";break;
                    case "EMERALD":
                        str += "绿宝石";break;
                    case "QUARTZ":
                        str += "石英";break;
                    case "ACACIA":
                        str += "金合欢木";break;
                    case "HAY":
                        str += "干草";break;
                    case "PURPUR":
                        str += "紫铂";break;
                    case "BONE":
                        str += "骨";break;
                    case "NETHER":
                        str += "地狱砖";break;
                    case "SLIME":
                        str += "粘液";break;
                    case "SPRUCE":
                        str += "云杉木";break;
                    case "BIRCH":
                        str += "白桦木";break;
                    case "JUNGLE":
                        str += "从林木";break;
                    case "CHORUS":
                        str += "紫颂";break;
                    case "POWERED":
                        str += "充能";break;
                    case "DETECTOR":
                        str += "探测";break;
                    case "ACTIVATOR":
                        str += "激活";break;
                    case "STORAGE":
                        str += "运输";break;
                    case "EXPLOSIVE":
                        str += "TNT";break;
                    case "HOPPER":
                        str += "漏斗";break;
                    case "WATER":
                        str += "水";break;
                    case "LAVA":
                        str += "岩浆";break;
                    case "GLOWSTONE":
                        str += "萤石";break;
                    case "PUMPKIN":
                        str += "南瓜";break;
                    case "PRISMARINE":
                        str += "海晶";break;
                    case "BEETROOT":
                        str += "甜菜";break;
                    case "RECORD":
                        return "§b音乐唱片";
                    case "STONE":
                        str += "石";break;
                    case "LEATHER":
                        switch(m.substring(m.indexOf('_')+1)){
                            case "HELMET":
                                return "皮革帽子";
                            case "CHESTPLATE":
                                return "皮革外套";
                            case "LEGGINGS":
                                return "皮革裤子";
                            default:
                                str += "皮革";
                        }    break;
                    case "CHAINMAIL":
                        str += "锁链";break;
                    case "SPLASH":
                        str += "喷溅型";break;
                    case "LINGERING":
                        str += "滞留型";break;
                    case "RAW":
                        str += "生";break;
                    case "COOKED":
                        str += "熟";break;
                    case "BAKED":
                        str += "烤";break;
                    case "POISONOUS":
                        str += "毒";break;
                    default:
                        str += m.substring(0,m.indexOf('_'));
                }
                switch(m.substring(m.indexOf('_')+1)){
                    case "ORE":
                        if("石英".equals(str)) str = "下界"+str;
                        str += "矿石";
                        break;
                    case "BLOCK":
                        switch (str){
                            case "雪":
                            case "西瓜":
                                break;
                            case "石英":
                                switch(d){
                                    case 2:
                                        str = "錾制"+str+"块";break;
                                    case 1:
                                        str = "竖纹"+str+"块";break;
                                    default:
                                        str += "块";
                                }    break;
                            case "煤":
                                str += "炭块";break;
                            default:
                                str += "块";
                        }    break;
                    case "STEP":
                    case "SLAB":
                        str += "台阶";break;
                    case "COBBLESTONE":
                        str += "石";break;
                    case "STAIRS":
                        str += "楼梯";break;
                    case "SAND":
                        str += "沙";break;
                    case "GLASS":
                        str += "染色玻璃";break;
                    case "BRICK":
                        break;
                    case "FENCE":
                        str += "栅栏";break;
                    case "PLANT":
                        str += "植物";break;
                    case "FLOWER":
                        str += "花";break;
                    case "TRAPDOOR":
                        str += "活板门";break;
                    case "DOOR":
                        str += "门";break;
                    case "COMPARATOR":
                        str += "比较器";break;
                    case "RAIL":
                        str += "铁轨";break;
                    case "MINECART":
                        if("充能".equals(str)) str = "动力";
                        str += "矿车";break;
                    case "INGOT":
                        str += "锭";break;
                    case "BUCKET":
                        str += "桶";break;
                    case "BALL":
                        str += "球";break;
                    case "DUST":
                        str += "粉";break;
                    case "SEEDS":
                        str += "种子";break;
                    case "NUGGET":
                        str += "粒";break;
                    case "SHARD":
                        str += "碎片";break;
                    case "CRYSTALS":
                        str += "砂粒";break;
                    case "BARDING":
                        str += "马铠";break;
                    case "FRUIT":
                        str += "果";break;
                    case "RECORD":
                        return "音乐唱片";
                    case "SPADE":
                        str += "锹";break;
                    case "AXE":
                        str += "斧";break;
                    case "PICKAXE":
                        str += "镐";break;
                    case "HOE":
                        str += "锄";break;
                    case "HELMET":
                        str += "头盔";break;
                    case "CHESTPLATE":
                        str += "胸甲";break;
                    case "LEGGINGS":
                        str += "护腿";break;
                    case "BOOTS":
                        str += "靴子";break;
                    case "SWORD":
                        str += "剑";break;
                    case "POTION":
                        str += potionName((PotionMeta)im);break;
                    case "FISH":
                        switch(d){
                            case 3:
                                return "河豚";
                            case 2:
                                return "小丑鱼";
                            case 1:
                                str += "鲑";
                            default:
                                str += "鱼";
                        }    break;
                    case "BEEF":
                        if("熟".equals(str)) return "牛排";
                        str += "牛肉";break;
                    case "CHICKEN":
                        str += "鸡肉";break;
                    case "POTATO":
                        str += "马铃薯";break;
                    case "PIE":
                        str += "派";break;
                    case "RABBIT":
                        str += "兔肉";break;
                    case "MUTTON":
                        str += "羊肉";break;
                    case "SOUP":
                        str += "汤";break;
                    default:
                        str += m.substring(m.indexOf('_'));
                }
                return str;
        }
    }
    
    public String itemZero_(){
        switch (m){
            case "STONE":
                switch (d){
                    case 6:
                        return "磨制安山岩";
                    case 5:
                        return "安山岩";
                    case 4:
                        return "磨制闪长岩";
                    case 3:
                        return "闪长岩";
                    case 2:
                        return "磨制花岗岩";
                    case 1:
                        return "花岗岩";
                    default:
                        return "石头";
                }
            case "GRASS":
                return "草方块";
            case "DIRT":
                switch (d){
                    case 2:
                        return "灰化土";
                    case 1:
                        return "砂土";
                    default:
                        return "泥土";
                }
            case "COBBLESTONE":
                return "圆石";
            case "WOOD":
                return wood()+"木板";
            case "BEDROCK":
                return "基岩";
            case "SAND":
                if(d==1) return "红沙";
                return "沙子";
            case "GRAVEL":
                return "沙砾";
            case "LOG":
                return wood()+"木";
            case "SPONGE":
                if(d==1) return "湿海绵";
                return "海绵";
            case "GLASS":
                return "玻璃";
            case "SANDSTONE":
                switch (d){
                    case 2:
                        return "平滑砂岩";
                    case 1:
                        return "錾制砂岩";
                    default:
                        return "砂岩";
                }
            case "WOOL":
                return color(d)+"羊毛";
            case "STEP":
                switch (d){
                    case 7:
                        return "石英台阶";
                    case 6:
                        return "地狱砖台阶";
                    case 5:
                        return "石砖台阶";
                    case 4:
                        return "砖台阶";
                    case 3:
                        return "圆石台阶";
                    case 1:
                        return "砂岩台阶";
                    default:
                        return "石台阶";
                }
            case "BRICK":
                return "砖块";
            case "BOOKSHELF":
                return "书架";
            case "OBSIDIAN":
                return "黑曜石";
            case "ICE":
                return "冰";
            case "CLAY":
                return "粘土块";
            case "PUMPKIN":
                return "南瓜";
            case "NETHERRACK":
                return "地狱岩";
            case "GLOWSTONE":
                return "萤石";
            case "MYCEL":
                return "菌丝";
            case "PRISMARINE":
                switch (d){
                    case 2:
                        return "暗海晶石";
                    case 1:
                        return "海晶石砖";
                    default:
                        return "海晶石";
                }
            case "MAGMA":
                return "岩浆块";
            case "CONCRETE":
                return color(d)+"混凝土";
            case "SAPLING":
                return wood()+"树苗";
            case "LEAVES":
                return wood()+"树叶";
            case "WEB":
                return "蜘蛛网";
            case "TORCH":
                return "火把";
            case "CHEST":
                return "箱子";
            case "WORKBENCH":
                return "工作台";
            case "FURNACE":
                return "熔炉";
            case "LADDER":
                return "梯子";
            case "SNOW":
                return "雪";
            case "CACTUS":
                return "仙人掌";
            case "JUKEBOX":
                return "唱片机";
            case "FENCE":
                return "橡木栅栏";
            case "VINE":
                return "藤蔓";
            case "ANVIL":
                switch (d){
                    case 2:
                        return "严重损坏的铁砧";
                    case 1:
                        return "轻微损坏的铁砧";
                    default:
                        return "铁砧";
                }
            case "CARPET":
                return color(d)+"地毯";
            case "PAINTING":
                return "画";
            case "SIGN":
                return "告示牌";
            case "BED":
                return color(d)+"床";
            case "BANNER":
                return color((short)(15-d))+"旗帜";
            case "DISPENSER":
                return "发射器";
            case "TNT":
                return "TNT";
            case "LEVER":
                return "拉杆";
            case "HOPPER":
                return "漏斗";
            case "DROPPER":
                return "投掷器";
            case "OBSERVER":
                return "侦测器";
            case "REDSTONE":
                return "红石";
            case "DIODE":
                return "红石中继器";
            case "RAILS":
                return "铁轨";
            case "MINECART":
                return "矿车";
            case "SADDLE":
                return "鞍";
            case "BOAT":
                return "橡木船";
            case "ELYTRA":
                return "鞘翅";
            case "BEACON":
                return "信标";
            case "COAL":
                if(d==1) return "木炭";
                return "煤炭";
            case "DIAMOND":
                return "钻石";
            case "STICK":
                return "木棍";
            case "BOWL":
                return "碗";
            case "STRING":
                return "线";
            case "FEATHER":
                return "羽毛";
            case "SULPHUR":
                return "火药";
            case "SEEDS":
                return "小麦种子";
            case "WHEAT":
                return "小麦";
            case "FLINT":
                return "燧石";
            case "BUCKET":
                return "桶";
            case "LEATHER":
                return "皮革";
            case "PAPER":
                return "纸";
            case "BOOK":
                return "书";
            case "EGG":
                return "鸡蛋";
            case "BONE":
                return "骨头";
            case "SUGAR":
                return "糖";
            case "FIREBALL":
                return "火焰弹";
            case "EMERALD":
                return "绿宝石";
            case "MAP":
                return "地图";
            case "QUARTZ":
                return "下界石英";
            case "WATCH":
                return "钟";
            case "SHEARS":
                return "剪刀";
            case "LEASH":
                return "拴绳";
            case "COMPASS":
                return "指南针";
            case "BOW":
                return "弓";
            case "ARROW":
                return "箭";
            case "TOTEM":
                return "不死图腾";
            case "SHIELD":
                return "盾牌";
            case "POTION":
                return potionName((PotionMeta)im);
            case "APPLE":
                return "苹果";
            case "BREAD":
                return "面包";
            case "PORK":
                return "生猪排";
            case "CAKE":
                return "蛋糕";
            case "COOKIE":
                return "曲奇";
            case "MELON":
                return "西瓜";
            case "RABBIT":
                return "生兔肉";
            case "MUTTON":
                return "生羊肉";
            case "BEETROOT":
                return "甜菜根";
            default:
                return m;
        }
    }
}
