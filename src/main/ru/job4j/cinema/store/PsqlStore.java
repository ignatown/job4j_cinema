package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new InputStreamReader(
                        PsqlStore.class.getClassLoader()
                                .getResourceAsStream("db.properties")
                )
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Ticket> findAllTicket() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "select acc.id as accountId, tic.id as ticId, tic.session_id as sessionId,"
                             + " tic.row as row, tic.cell as cell from ticket as tic inner join "
                             + "account as acc on tic.account_id = acc.id;")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    tickets.add(new Ticket(
                            it.getInt("ticId"),
                            it.getInt("sessionId"),
                            it.getInt("row"),
                            it.getInt("cell"),
                            it.getInt("accountId")));
                }
            }
        } catch (Exception e) {
            LOG.error("An error occurred while trying to find all tickets in the database", e);
        }
        return tickets;
    }

    @Override
    public Account saveAccount(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO account(username, email, phone) VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, account.getName());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    account.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("An error occurred while trying to save account in the database", e);
        }
        return account;
    }

    @Override
    public boolean saveTicket(Ticket ticket) {
        boolean result = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO ticket(id, session_id, row, cell, account_id) VALUES (?, ?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, ticket.getId());
            ps.setInt(2, ticket.getSessionId());
            ps.setInt(3, ticket.getRow());
            ps.setInt(4, ticket.getCell());
            ps.setInt(5, ticket.getAccountId());
            ps.execute();
            result = true;
        } catch (SQLException e) {
            LOG.error("SQL exception has occurred", e);
        } catch (Exception e) {
            LOG.error("An error occurred while trying to save ticket in the database", e);
        }
        return result;
    }

    @Override
    public Account findByIdAccount(String phone, String email) throws SQLException {
        Account account = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM account WHERE phone = (?) or email = (?)")
        ) {
            ps.setString(1, phone);
            ps.setString(2, email);
            try (ResultSet ids = ps.executeQuery()) {
                if (ids.next()) {
                    account = new Account(
                            ids.getInt("id"),
                            ids.getString("username"),
                            ids.getString("email"),
                            ids.getString("phone")
                    );
                }
            } catch (Exception e) {
                LOG.error("An error occurred while trying to find account by id in the database", e);
            }
            return account;
        }
    }

    @Override
    public Ticket findByTicket(int row, int cell) {
        Ticket ticket = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM ticket WHERE row = (?) and cell = (?)")
        ) {
            ps.setInt(1, row);
            ps.setInt(2, cell);
            try (ResultSet ids = ps.executeQuery()) {
                if (ids.next()) {
                    ticket = new Ticket(
                            ids.getInt("id"),
                            ids.getInt("session_id"),
                            ids.getInt("row"),
                            ids.getInt("cell"),
                            ids.getInt("account_id")
                    );
                }
            } catch (Exception e) {
                LOG.error("An error occurred while trying to find ticket by cell and row in the database", e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ticket;
    }
}