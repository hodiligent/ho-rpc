package com.ho.rpc.core.registry.zk;

import com.ho.rpc.core.api.RegistryCenter;
import com.ho.rpc.core.meta.InstanceMeta;
import com.ho.rpc.core.meta.ServiceMeta;
import com.ho.rpc.core.registry.ChangedListener;
import com.ho.rpc.core.registry.Event;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description ZK注册中心
 * @Author LinJinhao
 * @Date 2024/3/22 18:02
 */
@Slf4j
public class ZkRegistryCenter implements RegistryCenter {
    private CuratorFramework CLIENT = null;

    private List<TreeCache> caches = new ArrayList<>();

    @Value("${ho-rpc.zkServer}")
    private String servers;

    @Value("${ho-rpc.zkNamespace}")
    private String namespace;


    @Override
    public void start() {
        // 最大重试三次，每次间隔度量为1s
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CLIENT = CuratorFrameworkFactory.builder()
                .connectString(servers)
                .namespace(namespace)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        CLIENT.start();
    }

    @Override
    public void stop() {
        CLIENT.close();
    }

    @Override
    public void registry(ServiceMeta serviceMeta, InstanceMeta instance) {
        String servicePath = "/" + serviceMeta.toPath();
        try {
            // 创建服务的持久化节点
            if (Objects.isNull(CLIENT.checkExists().forPath(servicePath))) {
                CLIENT.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时性节点
            String instancePath = servicePath + "/" + instance.toPath();
            CLIENT.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unRegistry(ServiceMeta serviceMeta, InstanceMeta instance) {
        String servicePath = "/" + serviceMeta.toPath();
        try {
            if (Objects.isNull(CLIENT.checkExists().forPath(servicePath))) {
                return;
            }
            // 创建实例的临时性节点
            String instancePath = servicePath + "/" + instance.toPath();
            CLIENT.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta serviceMeta) {
        String servicePath = "/" + serviceMeta.toPath();
        try {
            return CLIENT.getChildren().forPath(servicePath)
                    .stream().map(item -> {
                        String[] itemArr = item.split(":");
                        return InstanceMeta.http(itemArr[0], Integer.parseInt(itemArr[1]));
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public void subscribe(ServiceMeta service, ChangedListener changedListener) {
        final TreeCache cache = TreeCache.newBuilder(CLIENT, "/" + service.toPath())
                .setCacheData(true).setMaxDepth(2).build();
        cache.getListenable().addListener(
                (curator, event) -> {
                    // 有任何节点变动这里会执行
                    log.info("zk subscribe event: " + event);
                    List<InstanceMeta> nodes = fetchAll(service);
                    changedListener.fire(new Event(nodes));
                }
        );
        cache.start();
        caches.add(cache);
    }
}
