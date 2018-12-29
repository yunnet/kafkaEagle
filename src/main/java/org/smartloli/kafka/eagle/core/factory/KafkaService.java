/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartloli.kafka.eagle.core.factory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smartloli.kafka.eagle.common.domain.KafkaSqlDomain;
import org.smartloli.kafka.eagle.common.domain.MetadataDomain;
import org.smartloli.kafka.eagle.common.domain.OffsetZkDomain;
import org.smartloli.kafka.eagle.common.domain.PageParamDomain;

/**
 * Kafka group,topic and partition interface.
 *
 * @author smartloli.
 *         <p>
 *         Created by Jan 18, 2017
 *         <p>
 *         Update by hexiang 20170216
 */
public interface KafkaService {

    /**
     * Find topic and group exist in zookeeper.
     */
    public boolean findTopicAndGroupExist(String clusterAlias, String topic, String group);

    /**
     * Obtaining metadata in zookeeper by topic.
     */
    public List<String> findTopicPartition(String clusterAlias, String topic);

    /**
     * Get kafka active consumer topic.
     */
    public Map<String, List<String>> getActiveTopic(String clusterAlias);

    /**
     * Get all broker list from zookeeper.
     */
    public String getAllBrokersInfo(String clusterAlias);

    /**
     * Get all topic info from zookeeper.
     */
    public String getAllPartitions(String clusterAlias);

    /**
     * Obtaining kafka consumer information from zookeeper.
     */
    public Map<String, List<String>> getConsumers(String clusterAlias);

    /**
     * Obtaining kafka consumer page information from zookeeper.
     */
    public Map<String, List<String>> getConsumers(String clusterAlias, PageParamDomain page);

    /**
     * Use Kafka low consumer API & get logsize size from zookeeper.
     */
    public long getLogSize(List<String> hosts, String topic, int partition);

    /**
     * According to group, topic and partition to get offset from zookeeper.
     */
    public OffsetZkDomain getOffset(String clusterAlias, String topic, String group, int partition);

    /**
     * Get kafka 0.10.x offset from topic.
     */
    public String getKafkaOffset(String clusterAlias);

    /**
     * Use kafka console comand to create topic.
     */
    public Map<String, Object> create(String clusterAlias, String topicName, String partitions, String replic);

    /**
     * Use kafka console command to delete topic.
     */
    public Map<String, Object> delete(String clusterAlias, String topicName);

    /**
     * Find leader through topic.
     */
    public List<MetadataDomain> findLeader(String clusterAlias, String topic);

    /**
     * Convert query kafka to topic in the sql message for standard sql.
     */
    public KafkaSqlDomain parseSql(String clusterAlias, String sql);

    /**
     * Get kafka 0.10.x active consumer group & topics.
     */
    public Set<String> getKafkaActiverTopics(String clusterAlias, String group);

    /**
     * Get kafka 0.10.x consumer group & topic information.
     */
    public String getKafkaConsumer(String clusterAlias);

    /**
     * Get kafka consumer information pages.
     */
    public String getKafkaActiverSize(String clusterAlias, String group);

    /**
     * Get kafka broker bootstrap server.
     */
    public String getKafkaBrokerServer(String clusterAlias);

    /**
     * Get kafka consumer groups.
     */
    public int getKafkaConsumerGroups(String clusterAlias);

    /**
     * Get kafka consumer topics.
     */
    public Set<String> getKafkaConsumerTopic(String clusterAlias, String group);

    /**
     * Get kafka consumer group & topic.
     */
    public String getKafkaConsumerGroupTopic(String clusterAlias, String group);

    /**
     * Get kafka sasl logsize .
     */
    public long getKafkaLogSize(String clusterAlias, String topic, int partitionid);

    /**
     * Get kafka sasl topic metadate.
     */
    public List<MetadataDomain> findKafkaLeader(String clusterAlias, String topic);

    /**
     * Send mock message to kafka.
     */
    public boolean mockMessage(String clusterAlias, String topic, String message);

}
