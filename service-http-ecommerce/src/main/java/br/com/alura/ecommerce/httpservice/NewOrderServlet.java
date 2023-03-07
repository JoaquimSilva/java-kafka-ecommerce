package br.com.alura.ecommerce.httpservice;

import br.com.alura.ecommerce.util.BigDecimalRandomUtil;
import br.com.alura.ecommerce.CorrelationId;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

public class NewOrderServlet extends HttpServlet {

    private final CorrelationId id = new CorrelationId(NewOrderServlet.class.getSimpleName());
    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final static String TOPIC = "ECOMMERCE_NEW_ORDER";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {

            var email = req.getParameter("email");
            var orderId = UUID.randomUUID().toString();
            var amount = BigDecimalRandomUtil.random(5000);

            var order = new Order(orderId, email, amount);
            orderDispatcher.send(TOPIC, email, id, order);

            System.out.println("New order has created with successfully.");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("New order sent");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
