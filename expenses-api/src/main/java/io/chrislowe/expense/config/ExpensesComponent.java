package io.chrislowe.expense.config;

import dagger.Component;
import io.chrislowe.expense.handler.ExpensesGetHandler;
import io.chrislowe.expense.handler.ExpensesPutHandler;

import javax.inject.Singleton;

@Singleton
@Component(modules = {ExpensesModule.class})
public interface ExpensesComponent {
    void inject(ExpensesGetHandler requestHandler);
    void inject(ExpensesPutHandler requestHandler);
}
