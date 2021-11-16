package ru.job4j.cinema.servlets;

import javax.servlet.http.HttpServlet;
import org.json.JSONArray;
import ru.job4j.cinema.store.PsqlStore;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HallServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        JSONArray json  = new JSONArray(PsqlStore.instOf().findAllTicket());
        writer.println(json);
        writer.flush();
    }
}