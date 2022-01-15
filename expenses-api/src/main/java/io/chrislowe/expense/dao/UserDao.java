package io.chrislowe.expense.dao;

import io.chrislowe.expense.dto.Expense;
import io.chrislowe.expense.dto.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.utils.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class UserDao {
    private final static String usersTablePk = "user-id";

    private final String usersTableName;
    private final DynamoDbClient dynamoDbClient;

    public UserDao(String usersTableName, DynamoDbClient dynamoDbClient) {
        this.usersTableName = usersTableName;
        this.dynamoDbClient = dynamoDbClient;
    }

    public User getUser(String userId) {
        return Optional.ofNullable(dynamoDbClient
                                .getItem(GetItemRequest.builder()
                                .tableName(usersTableName)
                                .key(Collections.singletonMap(usersTablePk, AttributeValue.builder().s(userId).build()))
                                .build()))
                .map(GetItemResponse::item)
                .map(this::convert)
                .orElse(null);
    }

    public void putUser(User user) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(usersTablePk, AttributeValue.builder().s(user.userId).build());
        if (!CollectionUtils.isNullOrEmpty(user.expenses)) {
            List<AttributeValue> expenses = user.expenses.stream()
                    .map(expense -> {
                        Map<String, AttributeValue> map = new HashMap<>();
                        map.put("name", AttributeValue.builder().s(expense.name).build());
                        map.put("cost", AttributeValue.builder().n(expense.cost.toString()).build());
                        return AttributeValue.builder().m(map).build();
                    })
                    .collect(Collectors.toList());
            item.put("expenses", AttributeValue.builder().l(expenses).build());
        }

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(usersTableName)
                .item(item)
                .build());
    }

    public void deleteUser(String userId) {
        dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(usersTableName)
                .key(Collections.singletonMap(usersTablePk, AttributeValue.builder().s(userId).build()))
                .build());
    }

    private User convert(Map<String, AttributeValue> item) {
        if (item == null || item.isEmpty()) {
            return null;
        }

        User user = new User();
        user.userId = item.get(usersTablePk).s();
        user.expenses = new ArrayList<>();

        List<AttributeValue> expenseDbos = item.containsKey("expenses") ? item.get("expenses").l() : null;
        if (!CollectionUtils.isNullOrEmpty(expenseDbos)) {
            for (AttributeValue expenseDbo : expenseDbos) {
                Map<String, AttributeValue> fieldMap = expenseDbo.m();

                Expense expense = new Expense();
                expense.name = getAttributeString(fieldMap.get("name"));
                expense.cost = getAttributeLong(fieldMap.get("cost"));
                user.expenses.add(expense);
            }
        }

        return user;
    }

    private String getAttributeString(AttributeValue attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.s();
    }

    private Long getAttributeLong(AttributeValue attribute) {
        try {
            return Long.parseLong(attribute.n());
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

}
