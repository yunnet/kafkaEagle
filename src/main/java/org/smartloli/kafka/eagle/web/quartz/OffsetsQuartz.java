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
package org.smartloli.kafka.eagle.web.quartz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartloli.kafka.eagle.api.email.MailFactory;
import org.smartloli.kafka.eagle.api.email.MailProvider;
import org.smartloli.kafka.eagle.common.domain.AlarmDomain;
import org.smartloli.kafka.eagle.common.domain.OffsetZkDomain;
import org.smartloli.kafka.eagle.common.domain.OffsetsLiteDomain;
import org.smartloli.kafka.eagle.common.util.CalendarUtils;
import org.smartloli.kafka.eagle.common.util.SystemConfigUtils;
import org.smartloli.kafka.eagle.core.factory.KafkaFactory;
import org.smartloli.kafka.eagle.core.factory.KafkaService;
import org.smartloli.kafka.eagle.core.factory.ZkFactory;
import org.smartloli.kafka.eagle.core.factory.ZkService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Per 5 mins to stats offsets to offsets table.
 *
 * @author smartloli.
 *         <p>
 *         Created by Aug 18, 2016
 */
@Component
public class OffsetsQuartz {

    private Logger LOG = LoggerFactory.getLogger(OffsetsQuartz.class);

    /**
     * Kafka service interface.
     */
    private KafkaService kafkaService = new KafkaFactory().create();

    /**
     * Zookeeper service interface.
     */
    private ZkService zkService = new ZkFactory().create();

    /**
     * Get alarmer configure.
     */
    private List<AlarmDomain> alarmConfigure(String clusterAlias) {
        LOG.info("invoke alarmConfigure::clusterAlias={}", clusterAlias);
        String alarmer = zkService.getAlarm(clusterAlias);
        List<AlarmDomain> targets = new ArrayList<>();
        JSONArray alarmers = JSON.parseArray(alarmer);
        for (Object object : alarmers) {
            AlarmDomain alarm = new AlarmDomain();
            JSONObject alarmSerialize = (JSONObject) object;
            alarm.setGroup(alarmSerialize.getString("group"));
            alarm.setTopics(alarmSerialize.getString("topic"));
            alarm.setLag(alarmSerialize.getLong("lag"));
            alarm.setOwners(alarmSerialize.getString("owner"));
            targets.add(alarm);
        }
        return targets;
    }

    private void alert(String clusterAlias, List<OffsetsLiteDomain> offsetLites) {
        LOG.info("invoke alert::clusterAlias={}, offsetLites={}", clusterAlias, offsetLites);
        boolean enableAlarm = SystemConfigUtils.getBooleanProperty("kafka.eagle.mail.enable");
        if (enableAlarm) {
            List<AlarmDomain> alarmers = alarmConfigure(clusterAlias);
            for (AlarmDomain alarm : alarmers) {
                for (OffsetsLiteDomain offset : offsetLites) {
                    if (offset.getGroup().equals(alarm.getGroup()) && offset.getTopic().equals(alarm.getTopics())
                            && offset.getLag() > alarm.getLag()) {
                        try {
                            MailProvider provider = new MailFactory();
                            String subject = "Alarm Lag Alert";
                            String address = alarm.getOwners();
                            String content = "Lag exceeds a specified threshold,Topic is [" + alarm.getTopics()
                                    + "],current lag is [" + offset.getLag() + "],expired lag is ["
                                    + alarm.getLag() + "].";
                            provider.create().send(subject, address, content, "");
                        } catch (Exception ex) {
                            LOG.error("Topic[" + alarm.getTopics() + "] Send alarm mail has error,msg is " + ex
                                    .getMessage());
                        }
                    }
                }
            }
        }
    }

    /**
     * Get kafka brokers.
     */
    private List<String> getBrokers(String clusterAlias) {
        LOG.info("invoke getBrokers::clusterAlias={}", clusterAlias);
        String brokers = kafkaService.getAllBrokersInfo(clusterAlias);
        ;
        JSONArray kafkaBrokers = JSON.parseArray(brokers);
        List<String> targets = new ArrayList<String>();
        for (Object object : kafkaBrokers) {
            JSONObject kafkaBroker = (JSONObject) object;
            String host = kafkaBroker.getString("host");
            int port = kafkaBroker.getInteger("port");
            targets.add(host + ":" + port);
        }
        return targets;
    }

    private OffsetZkDomain getKafkaOffset(String clusterAlias, String bootstrapServers, String topic, String group,
                                          int partition) {
        LOG.info("invoke getKafkaOffset::clusterAlias={}, bootstrapServers={}, topic={}, group={}, partition={}",
                clusterAlias, bootstrapServers, topic, group, partition);
        JSONArray kafkaOffsets = JSON.parseArray(kafkaService.getKafkaOffset(clusterAlias));
        OffsetZkDomain targets = new OffsetZkDomain();
        if (kafkaOffsets != null) {
            for (Object object : kafkaOffsets) {
                JSONObject kafkaOffset = (JSONObject) object;
                String _topic = kafkaOffset.getString("topic");
                String _group = kafkaOffset.getString("group");
                int _partition = kafkaOffset.getInteger("partition");
                long timestamp = kafkaOffset.getLong("timestamp");
                long offset = kafkaOffset.getLong("offset");
                if (topic.equals(_topic) && group.equals(_group) && partition == _partition) {
                    targets.setOffset(offset);
                    targets.setCreate(CalendarUtils.convertUnixTime2Date(timestamp));
                    targets.setModify(CalendarUtils.convertUnixTime2Date(timestamp));
                }
            }
        } else {
            LOG.warn("kafkaService.getKafkaOffset(clusterAlias) got kafka offsets is null!");
        }

        return targets;
    }

    /**
     * Get the corresponding string per minute.
     */
    private String getStatsPerDate() {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(new Date());
    }

    // 5 分钟级别的执行
    @Scheduled(
            cron = "0 1,6,11,16,21,26,31,36,41,46,51,56 * * * ?")
    public void jobQuartz() {
        LOG.info("invoke jobQuartz::");
        String[] clusterAliass = SystemConfigUtils.getPropertyArray("kafka.eagle.zk.cluster.alias", ",");
        for (String clusterAlias : clusterAliass) {
            execute(clusterAlias);
        }
    }

    /**
     * Perform offset statistical tasks on time.
     */
    private void execute(String clusterAlias) {
        LOG.info("invoke execute::clusterAlias={}", clusterAlias);
        try {
            List<String> hosts = getBrokers(clusterAlias);
            List<OffsetsLiteDomain> offsetLites = new ArrayList<OffsetsLiteDomain>();
            String formatter = SystemConfigUtils.getProperty("kafka.eagle.offset.storage");
            Map<String, List<String>> consumers = null;
            if ("kafka".equals(formatter)) {
                Map<String, List<String>> consumerGroupMap = new HashMap<String, List<String>>();
                try {
                    JSONArray consumerGroups = JSON.parseArray(kafkaService.getKafkaConsumer(clusterAlias));
                    for (Object object : consumerGroups) {
                        JSONObject consumerGroup = (JSONObject) object;
                        String group = consumerGroup.getString("group");
                        List<String> topics = new ArrayList<>();
                        for (String topic : kafkaService.getKafkaConsumerTopic(clusterAlias, group)) {
                            topics.add(topic);
                        }
                        consumerGroupMap.put(group, topics);
                    }
                    consumers = consumerGroupMap;
                } catch (Exception e) {
                    LOG.error("Get consumer info from [kafkaService.getKafkaConsumer] has error,msg is " + e
                            .getMessage());
                }
            } else {
                consumers = kafkaService.getConsumers(clusterAlias);
            }
            String statsPerDate = getStatsPerDate();
            for (Entry<String, List<String>> entry : consumers.entrySet()) {
                String group = entry.getKey();
                for (String topic : entry.getValue()) {
                    if (topic.equals("__consumer_offsets")) {
                        continue;
                    }
                    OffsetsLiteDomain offsetSQLite = new OffsetsLiteDomain();
                    for (String partitionStr : kafkaService.findTopicPartition(clusterAlias, topic)) {
                        int partition = Integer.parseInt(partitionStr);
                        long logSize = 0L;
                        if (SystemConfigUtils.getBooleanProperty("kafka.eagle.sasl.enable")) {
                            logSize = kafkaService.getKafkaLogSize(clusterAlias, topic, partition);
                        } else {
                            logSize = kafkaService.getLogSize(hosts, topic, partition);
                        }
                        OffsetZkDomain offsetZk = null;
                        if ("kafka".equals(formatter)) {
                            String bootstrapServers = "";
                            for (String host : hosts) {
                                bootstrapServers += host + ",";
                            }
                            bootstrapServers = bootstrapServers.substring(0, bootstrapServers.length() - 1);
                            offsetZk = getKafkaOffset(clusterAlias, bootstrapServers, topic, group, partition);
                        } else {
                            offsetZk = kafkaService.getOffset(clusterAlias, topic, group, partition);
                        }
                        offsetSQLite.setGroup(group);
                        offsetSQLite.setCreated(statsPerDate);
                        offsetSQLite.setTopic(topic);
                        if (logSize == 0) {
                            offsetSQLite.setLag(0L + offsetSQLite.getLag());
                        } else {
                            long lag = offsetSQLite.getLag() + (offsetZk.getOffset() == -1 ? 0
                                                                        : logSize - offsetZk.getOffset());
                            offsetSQLite.setLag(lag);
                        }
                        offsetSQLite.setLogSize(logSize + offsetSQLite.getLogSize());
                        offsetSQLite.setOffsets(offsetZk.getOffset() + offsetSQLite.getOffsets());
                    }
                    offsetLites.add(offsetSQLite);
                }
            }
            // Plan A: Storage into zookeeper.
            zkService.insert(clusterAlias, offsetLites);

            // Plan B: Storage single file.
            //keService.write(clusterAlias, offsetLites.toString());
            alert(clusterAlias, offsetLites);
        } catch (Exception ex) {
            LOG.error("Quartz statistics offset has error,msg is ,", ex);
        }
    }
}
