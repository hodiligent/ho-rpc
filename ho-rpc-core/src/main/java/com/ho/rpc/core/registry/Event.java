package com.ho.rpc.core.registry;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @Description TODO
 * @Author LinJinhao
 * @Date 2024/3/23 16:58
 */
@Data
@AllArgsConstructor
public class Event {
    /**
     *
     */
    private List<String> data;
}
