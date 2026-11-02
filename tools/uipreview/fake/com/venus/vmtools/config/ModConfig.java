package com.venus.vmtools.config;

/**
 * UI 预览器专用的 {@code ModConfig} <b>替身</b>（编译时替换真实类）。
 *
 * <p>真实类的静态字段要 {@code FabricLoader.getInstance().getConfigDir()}，
 * 预览器跑在普通 JVM 里、Fabric 加载器没启动 → 类初始化直接抛
 * {@code ExceptionInInitializerError}。替身把配置放在内存里。
 *
 * <p><b>字段与默认值逐条对齐真实类</b>（见 {@code config/ModConfig.java}）——
 * 默认值不一致会让预览出来的界面和游戏里不一样（比如"冻结功能默认开着"会影响
 * 设置界面的开关初始态），所以这里不是随便给的。
 */
public class ModConfig {

    private boolean confirmBeforeTeleport = false;
    private float uiScale = 1.0f;
    private int animationDuration = 200;
    private boolean showWaypointColors = true;
    private boolean showUseCount = true;
    private boolean autoConfirmTeleport = false;
    private String confirmCommand = "res tpconfirm";
    private int confirmDelay = 200;
    private boolean freezeEnabled = true;

    /** 预览器不落盘。 */
    public void save() {
    }

    public boolean isConfirmBeforeTeleport() {
        return confirmBeforeTeleport;
    }

    public void setConfirmBeforeTeleport(boolean value) {
        this.confirmBeforeTeleport = value;
    }

    public float getUiScale() {
        return uiScale;
    }

    public void setUiScale(float value) {
        this.uiScale = value;
    }

    public int getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(int value) {
        this.animationDuration = value;
    }

    public boolean isShowWaypointColors() {
        return showWaypointColors;
    }

    public void setShowWaypointColors(boolean value) {
        this.showWaypointColors = value;
    }

    public boolean isShowUseCount() {
        return showUseCount;
    }

    public void setShowUseCount(boolean value) {
        this.showUseCount = value;
    }

    public boolean isAutoConfirmTeleport() {
        return autoConfirmTeleport;
    }

    public void setAutoConfirmTeleport(boolean value) {
        this.autoConfirmTeleport = value;
    }

    public String getConfirmCommand() {
        return confirmCommand;
    }

    public void setConfirmCommand(String value) {
        this.confirmCommand = value;
    }

    public int getConfirmDelay() {
        return confirmDelay;
    }

    public void setConfirmDelay(int value) {
        this.confirmDelay = value;
    }

    public boolean isFreezeEnabled() {
        return freezeEnabled;
    }

    public void setFreezeEnabled(boolean value) {
        this.freezeEnabled = value;
    }
}
