package com.ho.rpc.core.registry;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/23 16:50
 */
public interface ChangedListener {
    /**
     * 激活
     *
     * @param event
     */
    void fire(Event event);
}
