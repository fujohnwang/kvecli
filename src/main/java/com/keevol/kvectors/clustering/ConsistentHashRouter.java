package com.keevol.kvectors.clustering;

/**
 * <pre>
 * ██╗  ██╗ ███████╗ ███████╗ ██╗   ██╗  ██████╗  ██╗
 * ██║ ██╔╝ ██╔════╝ ██╔════╝ ██║   ██║ ██╔═══██╗ ██║
 * █████╔╝  █████╗   █████╗   ██║   ██║ ██║   ██║ ██║
 * ██╔═██╗  ██╔══╝   ██╔══╝   ╚██╗ ██╔╝ ██║   ██║ ██║
 * ██║  ██╗ ███████╗ ███████╗  ╚████╔╝  ╚██████╔╝ ███████╗
 * ╚═╝  ╚═╝ ╚══════╝ ╚══════╝   ╚═══╝    ╚═════╝  ╚══════╝
 * </pre>
 * <p>
 * KEEp eVOLution!
 * <p>
 *
 * @author fq@keevol.cn
 * @since 2017.5.12
 * <p>
 * Copyright 2017 © 杭州福强科技有限公司版权所有 (<a href="https://www.keevol.cn">keevol.cn</a>)
 */

import com.google.common.hash.HashFunction;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConsistentHashRouter<T> {

    private final ConcurrentSkipListMap<Long, T> ring = new ConcurrentSkipListMap<>();
    private final int virtualNodeReplicas;
    private final HashFunction hashFunction;

    public ConsistentHashRouter(HashFunction hashFunction) {
        this(null, hashFunction, 32);
    }

    public ConsistentHashRouter(HashFunction hashFunction, int virtualNodeReplicas) {
        this(null, hashFunction, virtualNodeReplicas);
    }

    /**
     * 构造函数
     *
     * @param initialNodes        初始的物理节点列表
     * @param hashFunction        hash function to use
     * @param virtualNodeReplicas 每个物理节点对应的虚拟节点数量
     */
    public ConsistentHashRouter(Collection<T> initialNodes, HashFunction hashFunction, int virtualNodeReplicas) {
        this.virtualNodeReplicas = virtualNodeReplicas;
        this.hashFunction = hashFunction;

        if (initialNodes != null) {
            for (T node : initialNodes) {
                addNode(node);
            }
        }
    }

    /**
     * 添加一个物理节点及其虚拟节点到哈希环。
     *
     * @param node 要添加的物理节点
     */
    public void addNode(T node) {
        for (int i = 0; i < virtualNodeReplicas; i++) {
            String virtualNodeKey = node.toString() + "-" + i;
            long hash = hashFunction.hashString(virtualNodeKey, StandardCharsets.UTF_8).padToLong();
            ring.put(hash, node);
        }
    }

    /**
     * 从哈希环中移除一个物理节点及其所有虚拟节点。
     *
     * @param node 要移除的物理节点
     */
    public void removeNode(T node) {
        for (int i = 0; i < virtualNodeReplicas; i++) {
            String virtualNodeKey = node.toString() + "-" + i;
            long hash = hashFunction.hashString(virtualNodeKey, StandardCharsets.UTF_8).padToLong();
            ring.remove(hash);
        }
    }

    /**
     * 根据给定的 key，查找其应该被路由到的物理节点。
     *
     * @param key 数据的键
     * @return 查找到的物理节点；如果环为空，则返回 null。
     */
    public T getNode(String key) {
        if (ring.isEmpty()) {
            return null;
        }

        long hash = hashFunction.hashString(key, StandardCharsets.UTF_8).padToLong();

        // 使用 ceilingEntry 寻找顺时针方向的第一个节点
        // 这是 ConcurrentSkipListMap 的关键优势所在
        SortedMap.Entry<Long, T> entry = ring.ceilingEntry(hash);

        if (entry == null) {
            // 如果 hash 大于环上所有节点的哈希值，则根据环形结构，它属于环的第一个节点
            return ring.firstEntry().getValue();
        } else {
            return entry.getValue();
        }
    }

    public int getRingSize() {
        return ring.size();
    }

}