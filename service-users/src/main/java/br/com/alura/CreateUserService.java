package br.com.alura;

import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private static final String TOPIC = "ECOMMERCE_NEW_ORDER";
    private static final String CLASS_NAME = CreateUserService.class.getSimpleName();
    private final Connection connection;
    private final static String DB = "jdbc:sqlite:users_database.db";
    private final static String CREATE_QUERY = "create table if not exists Users(" +
            "uuid varchar(200) primary key," +
            "email varchar(200))";

    CreateUserService() throws SQLException {
        this.connection = DriverManager.getConnection(DB);
        connection.createStatement().execute(CREATE_QUERY);
    }
    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        var userService = new CreateUserService();
        try (var service = new KafkaService<>(CLASS_NAME,
                TOPIC,
                userService::parse,
                Order.class,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.key());
        System.out.println(record.value());

        Message<Order> message = record.value();
        Order order = message.getPayLoad();
        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        }
    }

    private void insertNewUser(String email) throws SQLException {

        var insert = connection.prepareStatement("insert into Users (uuid, email) values (?,?)");

        String userId = UUID.randomUUID().toString();

        insert.setString(1, userId);
        insert.setString(2, email);
        insert.execute();

        System.out.println("User " + userId + " and " + email + " has add");
    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("select uuid from Users where email = ? limit 1");
        exists.setString(1, email);
        ResultSet results = exists.executeQuery();
        return !results.next();
    }
}