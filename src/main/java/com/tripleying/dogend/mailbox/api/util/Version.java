package com.tripleying.dogend.mailbox.api.util;

/**
 * 版本工具
 * @author Dogend
 */
public class Version {
    
    /**
     * 模块版本字符串
     */
    private final String version;
    /**
     * 模块版本数组
     */
    private final int[] versionarr;
    /**
     * 是否可用
     */
    private final boolean avaliable;
    
    public Version(String version){
        this.version = version;
        if(this.version!=null){
            String[] vsr = this.version.split("\\.");
            if(vsr.length==3){
                int[] vr = new int[3];
                for(int i=0;i<3;i++){
                    try{
                        vr[i] = Integer.parseInt(vsr[i]);
                    }catch(NumberFormatException ex){
                        vr = null;
                        break;
                    }
                }
                if(vr!=null){
                    this.versionarr = vr;
                    this.avaliable = true;
                    return;
                }
            }
        }
        this.versionarr = null;
        this.avaliable = false;
    }
    
    /**
     * 检查版本是否最新
     * @param newest 最新版本
     * @return boolean
     */
    public boolean checkNewest(Version newest){
        if(newest.avaliable){
            for(int i=0;i<3;i++){
                if(this.versionarr[i]>newest.versionarr[i]) return true;
                if(this.versionarr[i]<newest.versionarr[i]) return false;
            }
        }
        return true;
    }
    
    /**
     * 检查版本是否最新
     * 返回true是最新版本
     * @param version 最新版本号(a.b.c)
     * @return boolean
     */
    public boolean checkNewest(String version){
        return checkNewest(new Version(version));
    }
    
    public boolean isAvaliable(){
        return this.avaliable;
    }
    
    @Override
    public String toString(){
        return this.version;
    }
    
}
