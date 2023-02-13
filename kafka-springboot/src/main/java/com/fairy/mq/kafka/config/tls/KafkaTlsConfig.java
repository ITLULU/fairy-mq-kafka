package com.fairy.mq.kafka.config.tls;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

public class KafkaTlsConfig {

    private KafkaConsumer<String, String> createKafkaConsumer(final Config tlsConfig, final boolean isMtlsEnabled, final String kafkaHost, final int kafkaPort, String podName) {
        String randomUUID = UUID.randomUUID().toString();
        String clientId = "proclog-state-consumer-" + randomUUID;
        String groupId = podName + "-" + randomUUID;

        Properties properties = new Properties();
        commonProperties(kafkaHost, kafkaPort, clientId, groupId, properties);

        if (isMtlsEnabled) {
            properties.put("ssl.endpoint.identification.algorithm", "");
            properties.put("security.protocol", "SSL");
            //  if (Java.IS_JAVA11_COMPATIBLE) {
            //            DEFAULT_SSL_PROTOCOL = "TLSv1.3";
            //            DEFAULT_SSL_ENABLED_PROTOCOLS = "TLSv1.2,TLSv1.3";
            //        } else {
            //            DEFAULT_SSL_PROTOCOL = "TLSv1.2";
            //            DEFAULT_SSL_ENABLED_PROTOCOLS = "TLSv1.2";
            //        }
            properties.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, "TLSv1.2,TLSv1.3");
            properties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "key-store");
            properties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getConfigString(tlsConfig, "password", "key-store-password"));
            properties.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, getConfigString(tlsConfig, "password", "key-password"));
            properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, tlsConfig.getString("trust-store"));

            properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getConfigString(tlsConfig, "password", "trust-store-password"));
        } else {
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        }

        return new KafkaConsumer<>(properties);
    }

    private void commonProperties(String kafkaHost, int kafkaPort, String clientId, String groupId, Properties properties) {
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
        properties.put(CommonClientConfigs.CLIENT_ID_CONFIG, clientId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(CommonClientConfigs.GROUP_ID_CONFIG, groupId);
        properties.put(CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG, 200);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
        properties.put(CommonClientConfigs.SESSION_TIMEOUT_MS_CONFIG, 30000);
    }

    static String getConfigString(Config config, String name, String defaultName) {
        String path;
        try {
            path = config.getString(name);
        } catch (ConfigException.Missing e) {
            path = config.getString(defaultName);
        }
        return path;
    }


    public KafkaProducer<Long, String> createKafkaProducer(Config tlsConfig, boolean mtls) {
        String clientId = "proclog-emitter-" + UUID.randomUUID();
        final Properties properties = commonProductProperties(clientId, "node01", 9092);

        boolean tlsEnabled = Boolean.parseBoolean(System.getenv().getOrDefault("MESSAGE_BUS_KF_TLS_ENABLED", "false"));
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, tlsEnabled ? "SSL" : "PLAINTEXT");

        if (tlsEnabled) {
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            properties.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, SslConfigs.DEFAULT_SSL_ENABLED_PROTOCOLS);
            properties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, tlsConfig.getString("key-store"));
            properties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getConfigString(tlsConfig, "password", "key-store-password"));
            properties.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, getConfigString(tlsConfig, "password", "key-password"));
            properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, tlsConfig.getString("trust-store"));
            properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getConfigString(tlsConfig, "password", "trust-store-password"));
        } else {
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        }
        return new KafkaProducer<>(properties);

    }

    private Properties commonProductProperties(String clientId, String kafkaHost, Integer kafkaPort) {
        final Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, "40000000");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 65536); // 65536 Bytes = 64KB
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 100);
        properties.put(ProducerConfig.RETRIES_CONFIG, 0); // Kafka retries are handled by the Emitter
        properties.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for acks from all insync replicas
        return properties;
    }
    public static Config getTlsConfig(Config conf,String fileKey){
        return ConfigFactory.parseResources(conf.getString(fileKey));
    }
    public static void main(String[] args) {
        final Config tlsConfig = ConfigFactory.parseResources("config.conf")
                .resolve()
                .getConfig("com.ericsson.activation.tls");


        System.out.println(String.format("config:%s",tlsConfig.getString("tlsConfigFile")));
        System.out.println(String.format("config:%s",tlsConfig));
        System.out.println(String.format("tlsfile:%s",getTlsConfig(tlsConfig, "tlsConfigFile")));

        System.out.println(String.format("jmxconfig:%s",tlsConfig.getConfig("jmx")));

        System.out.println(String.format("kafkaconfig:%s",tlsConfig.getConfig("kafka")));

    }
}
