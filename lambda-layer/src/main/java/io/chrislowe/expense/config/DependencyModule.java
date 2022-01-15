package io.chrislowe.expense.config;

import com.google.gson.Gson;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.utils.StringUtils;

import javax.inject.Singleton;
import java.net.URI;

@Module
public class DependencyModule {

    @Provides
    @Singleton
    String provideUsersTableName() {
        return System.getenv("USERS_TABLE_NAME");
    }

    @Provides
    @Singleton
    DynamoDbClient provideDynamoDb() {
        final String endpoint = System.getenv("ENDPOINT_OVERRIDE");

        DynamoDbClientBuilder builder = DynamoDbClient.builder();
        builder.httpClient(ApacheHttpClient.builder().build());
        if (StringUtils.isNotBlank(endpoint)) {
            builder.endpointOverride(URI.create(endpoint));
        }

        return builder.build();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }
}
