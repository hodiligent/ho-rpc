package com.ho.rpc.core.registry;

import com.ho.rpc.core.meta.InstanceMeta;
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
     * 实例数据
     */
    private List<InstanceMeta> instances;
}
