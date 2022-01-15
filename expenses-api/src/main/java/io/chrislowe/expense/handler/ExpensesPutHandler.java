package io.chrislowe.expense.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.chrislowe.expense.config.DaggerExpensesComponent;
import io.chrislowe.expense.dao.UserDao;
import io.chrislowe.expense.dto.Expense;
import io.chrislowe.expense.dto.User;
import software.amazon.awssdk.utils.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpensesPutHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    Map<String, String> headers;

    @Inject
    UserDao userDao;

    @Inject
    Gson gson;

    public ExpensesPutHandler() {
        DaggerExpensesComponent.create().inject(this);
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);

        String userId = input.getPathParameters().get("user_id");
        if (StringUtils.isBlank(userId)) {
            return response.withStatusCode(400).withBody("No user_id provided");
        }

        List<Expense> expenses;
        try {
            expenses = gson.fromJson(input.getBody(), new TypeToken<List<Expense>>(){}.getType());
        } catch (JsonParseException e) {
            return response.withStatusCode(400).withBody("Unable to parse request body: " + e.getMessage());
        }

        User user = new User();
        user.userId = userId;
        user.expenses = expenses;
        userDao.putUser(user);

        return response.withStatusCode(204);
    }
}
