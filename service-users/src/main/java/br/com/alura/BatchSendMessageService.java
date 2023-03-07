package br.com.alura;

import br.com.alura.ecommerce.CorrelationId;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {

    private static final String TOPIC = "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS";
    private final Connection connection;
    private static final String CLASS_NAME = BatchSendMessageService.class.getSimpleName();
    private final static CorrelationId id = new CorrelationId(CLASS_NAME);
    private final static String DB = "jdbc:sqlite:users_database.db";
    private final static String CREATE_QUERY = "create table if not exists Users(" +
            "uuid varchar(200) primary key," +
            "email varchar(200))";

    BatchSendMessageService() throws SQLException {
        this.connection = DriverManager.getConnection(DB);
        connection.createStatement().execute(CREATE_QUERY);
    }
    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        var batchService = new BatchSendMessageService();
        try (var service = new KafkaService<>(CLASS_NAME, TOPIC, batchService::parse, String.class, Map.of())) {
            service.run();
        }
    }

    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();
    private void parse(ConsumerRecord<String, Message<String>> record) throws SQLException {

        Message<String> message = record.value();

        System.out.println("------------------------------------------");
        System.out.println("Processing new batch");
        System.out.println("Topic: " + message.getPayLoad());

        for (User user : getAllUsers()) {

            CorrelationId correlationId = message.getCorrelationId().continueWith(id.getId());
            userDispatcher.sendAsync(message.getPayLoad(), user.getUuid(), correlationId, user);

            System.out.println("Message has sent to " + user);
        }
    }

    private List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String SELECT_QUERY = "select uuid from Users";
        ResultSet resultSet = connection.prepareStatement(SELECT_QUERY).executeQuery();

        while (resultSet.next()) {
            users.add(new User(resultSet.toString()));
        }

        return users;
    }
}
