package com.laioffer.job.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "TestServlet", urlPatterns = {"/test"})
public class TestServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("Hello post");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("hello ZYY");
        /* response: 想要传回给前端的结果。正式的名字是HttpServletResponse ↑
        getWriter()：为了在response这个object上有缓冲性地写入，我们需要拿到writer这个object
        println()：可以写入以后，具体该怎么写         */
    }
}

