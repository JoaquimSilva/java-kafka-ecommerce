package br.com.alura.ecommerce.httpservice;

import br.com.alura.ecommerce.CorrelationId;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GenerateAllReportServlet extends HttpServlet {
    private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();
    private final static CorrelationId id = new CorrelationId(GenerateAllReportServlet.class.getSimpleName());
    private final static String TOPIC = "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS";
    private final static String KEY = "USER_GENERATE_READING_REPORT";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void destroy() {
        super.destroy();
        batchDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            batchDispatcher.send(TOPIC, KEY, id, KEY);

            System.out.println("Sent generate report to all users");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Report request generated");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
