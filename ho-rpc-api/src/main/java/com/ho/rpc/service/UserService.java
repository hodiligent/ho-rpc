package com.ho.rpc.service;

import com.ho.rpc.model.User;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/13 01:04
 */
public interface UserService {
    /**
     * 根据ID找用户信息
     *
     * @param id
     * @return
     */
    User findById(int id);

    /**
     * 验证重载方法
     *
     * @param id
     * @param name
     * @return
     */
    User findById(int id, String name);
}
