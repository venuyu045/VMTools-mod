package com.venus.vmtools;

import com.venus.vmtools.config.ModConfig;
import com.venus.vmtools.feature.waypoint.WaypointManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UI 预览器专用的 {@code VMToolsClient} <b>替身</b>（编译时替换真实类）。
 *
 * <p>真实类 implements {@code ClientModInitializer}，初始化链会牵进 Fabric API 的
 * 生命周期事件、连接事件、按键注册……整条客户端启动流程。UI 预览只需要它的三样东西：
 * {@link #LOGGER}、{@link #getWaypointManager()}、{@link #getConfig()}。
 */
public final class VMToolsClient {

    public static final Logger LOGGER = LoggerFactory.getLogger("vmtools-uipreview");

    private static final VMToolsClient INSTANCE = new VMToolsClient();

    private final WaypointManager waypointManager = new WaypointManager();
    private final ModConfig config = new ModConfig();

    private VMToolsClient() {
    }

    public static VMToolsClient getInstance() {
        return INSTANCE;
    }

    public WaypointManager getWaypointManager() {
        return waypointManager;
    }

    public ModConfig getConfig() {
        return config;
    }
}
