package io.chrislowe.expense.config;

import dagger.Module;
import dagger.Provides;
import io.chrislowe.expense.dao.UserDao;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Module(includes = DependencyModule.class)
public class ExpensesModule {

    @Provides
    @Singleton
    UserDao provideUserDao(String usersTableName, DynamoDbClient dynamoDbClient) {
        return new UserDao(usersTableName, dynamoDbClient);
    }

    @Provides
    Map<String, String> provideHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, PATCH, DELETE");
        headers.put("Access-Control-Allow-Headers", "X-Requested-With,content-type");
        return headers;
    }
}
