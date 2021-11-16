package ru.job4j.cinema.servlets;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PayServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        String username = req.getParameter("username");
        String phone = req.getParameter("phone");
        Account account = PsqlStore.instOf().saveAccount(new Account(0,
                username, email, phone));
        String places = req.getParameter("places");
        JSONObject objJSON = new JSONObject(places);
        HashMap<String, Object> res = new HashMap<>();
        Iterator itr = objJSON.keys();
        while (itr.hasNext()) {
            String key = itr.next().toString();
            res.put(key, objJSON.get(key));
        }
        boolean free = true;
        List<Ticket> buy = new ArrayList<>();
        for (Object rs : res.keySet()) {
            String[] rowCell = (rs.toString()).split("");
            int row = Integer.parseInt(rowCell[0]);
            int cell = Integer.parseInt(rowCell[1]);
            Ticket ticket = new Ticket(
                    Integer.parseInt(rs.toString()),
                    1,
                    row,
                    cell,
                    account.getId());
            try {
                if (PsqlStore.instOf().findByTicket(row, cell) != null) {
                    free = false;
                } else {
                    buy.add(ticket);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (free) {
            for (Ticket ticket : buy) {
                PsqlStore.instOf().saveTicket(ticket);
            }
        } else {
            this.getServletContext().getRequestDispatcher("/error.jsp").forward(req, resp);
        }
        buy.clear();
        resp.sendRedirect(req.getContextPath() + "/result.jsp");
    }
}