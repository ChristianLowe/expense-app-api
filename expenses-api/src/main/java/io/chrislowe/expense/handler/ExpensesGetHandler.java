package io.chrislowe.expense.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.chrislowe.expense.config.DaggerExpensesComponent;
import io.chrislowe.expense.dao.UserDao;
import io.chrislowe.expense.dto.User;
import software.amazon.awssdk.utils.StringUtils;

import javax.inject.Inject;
import java.util.Map;

public class ExpensesGetHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    Map<String, String> headers;

    @Inject
    UserDao userDao;

    @Inject
    Gson gson;

    public ExpensesGetHandler() {
        DaggerExpensesComponent.create().inject(this);
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        String userId = input.getPathParameters().get("user_id");
        if (StringUtils.isBlank(userId)) {
            return response.withStatusCode(400).withBody("No user_id provided");
        }

        User user = userDao.getUser(userId);
        if (user == null) {
            return response.withStatusCode(404).withBody("User " + userId + " not found");
        }

        return response.withStatusCode(200).withBody(gson.toJson(user));
    }
}
