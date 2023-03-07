package br.com.alura.ecommerce.dispatcher;

import br.com.alura.ecommerce.CorrelationId;
import br.com.alura.ecommerce.Message;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaDispatcher<T> implements Closeable {

    private static final String KAFKA_PORT = "127.0.0.1:9092";
    private final KafkaProducer<String, Message<T>> producer;

    public KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }

    private static Properties properties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_PORT);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG,"all");
        return properties;
    }

//    Mode async
//    public void send(String topic, String key, CorrelationId id, T payload) throws ExecutionException, InterruptedException {
//
//        var value = new Message<>(id, payload);
//        var record = new ProducerRecord<>(topic, key, value);
//
//        Callback callback = (data, ex) -> {
//            if (ex != null) {
//                ex.printStackTrace();
//                return;
//            }
//            System.out.println("Success, sent " + data.topic() + ":::partition " + data.partition() + "/ offset " + data.offset() + "/ timestamp " + data.timestamp());
//        };
//        producer.send(record, callback).get();
//    }

    public void send(String topic, String key, CorrelationId id, T payload) throws ExecutionException, InterruptedException {

        Future<RecordMetadata> future = sendAsync(topic, key, id, payload);
        future.get();
    }

    public Future<RecordMetadata> sendAsync(String topic, String key, CorrelationId correlationId, T payload) {
        var value = new Message<>(correlationId.continueWith("_" + topic), payload);
        var record = new ProducerRecord<>(topic, key, value);

        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println("Success, sent " + data.topic() +
                                ":::partition " + data.partition() +
                                "/ offset " + data.offset() +
                                "/ timestamp " + data.timestamp());
        };
        return producer.send(record, callback);
    }


    @Override
    public void close() {
        producer.close();
    }
}
