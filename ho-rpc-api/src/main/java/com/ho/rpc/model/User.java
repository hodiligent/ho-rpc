package com.ho.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/13 01:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String name;
}
