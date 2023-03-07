package br.com.alura.ecommerce.consumer;

import br.com.alura.ecommerce.Message;
import br.com.alura.ecommerce.dispatcher.GsonSerializer;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {
    private final KafkaConsumer<String, Message<T>> consumer;
    private final ConsumerFunction<T> parse;
    private final String IP_SERVER = "127.0.0.1:9092";
    private String TOPIC_DEAD_LETTER = "ECOMMERCE_DEAD_LETTER";

    public KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Class<T> type, Map<String,String> properties) {
        this(parse, groupId, type, properties);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Class<T> type, Map<String,String> properties) {
        this(parse, groupId, type, properties);
        consumer.subscribe(topic);
    }

    private KafkaService(ConsumerFunction parse, String groupId, Class<T> type, Map<String, String> properties) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(getProperties(type, groupId, properties));
    }

    public void run() throws ExecutionException, InterruptedException {
        try (var deadLetter = new KafkaDispatcher<>()) {

            while (true) {
                var records = consumer.poll(Duration.ofMillis(100));
                if (!records.isEmpty()) {
                    System.out.println("I found " + records.count() + " records");
                    for (var record : records) {
                        try {
                            parse.consume(record);
                        } catch (Exception e) {
                            e.getStackTrace();
                            var message = record.value();

                            deadLetter.send(TOPIC_DEAD_LETTER,
                                    message.getCorrelationId().toString(),
                                    message.getCorrelationId().continueWith("DeadLetter"),
                                    new GsonSerializer().serialize("", message));

                        }
                    }
                }
            }
        }
    }

    private Properties getProperties(Class<T> type, String groupId, Map<String, String> overrideProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, IP_SERVER);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        properties.putAll(overrideProperties);
        return properties;
    }

    @Override
    public void close() {
        consumer.close();
    }
}
