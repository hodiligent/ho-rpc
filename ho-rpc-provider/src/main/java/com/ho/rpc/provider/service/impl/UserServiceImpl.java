package com.ho.rpc.provider.service.impl;

import com.ho.rpc.core.annotation.HoProvider;
import com.ho.rpc.model.User;
import com.ho.rpc.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/18 09:28
 */
@Service
@HoProvider
public class UserServiceImpl implements UserService {
    /**
     * 根据ID查询用户
     *
     * @param id
     * @return
     */
    @Override
    public User findById(int id) {
        return new User(id, "HoHo" + System.currentTimeMillis());
    }
}
