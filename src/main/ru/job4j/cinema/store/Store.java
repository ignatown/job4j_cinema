package ru.job4j.cinema.store;

import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;

import java.sql.SQLException;
import java.util.Collection;

public interface Store {

    Collection<Ticket> findAllTicket();

    Account saveAccount(Account account);

    boolean saveTicket(Ticket ticket);

    Account findByIdAccount(String phone, String email) throws SQLException;

    Ticket findByTicket(int row, int cell) throws SQLException;
}