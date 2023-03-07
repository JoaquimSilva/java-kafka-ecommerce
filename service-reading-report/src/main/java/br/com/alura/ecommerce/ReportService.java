package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.nio.file.Path;

public class ReportService implements ConsumerService<User> {

    private final static Path SOURCE = new File("src/main/resources/report.txt").toPath();
    public static void main(String[] args) {
        new ServiceRunner(ReportService::new).start(5);
    }
    public void parse(ConsumerRecord<String, Message<User>> record) throws Exception {
        System.out.println("------------------------------------------");
        Message<User> user = record.value();
        System.out.println("Processing report for " + user);

        var target = new File(user.getPayLoad().getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for user " + user.getPayLoad().getUuid());

        System.out.println("File created at " + target.getAbsolutePath());
    }
    @Override
    public String getTopic() {
        return "USER_GENERATE_READING_REPORT";
    }

    @Override
    public String getConsumerGroup() {
        return ReportService.class.getSimpleName();
    }
}
