package by.tem.servlet;

import by.tem.dto.ErrorResponse;
import by.tem.dto.ExchangeRateDto;
import by.tem.exception.CurrencyNotFoundException;
import by.tem.exception.DatabaseConnectionException;
import by.tem.exception.ExchangeRateNotFoundException;
import by.tem.exception.InvalidDataException;
import by.tem.service.ExchangeRateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();

        String codes = req.getPathInfo().replace("/", "");

        try {
            ExchangeRateDto exchangeRate = exchangeRateService.findByCodes(codes);
            writer.write(objectMapper.writeValueAsString(exchangeRate));
        } catch (InvalidDataException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), writer);
        } catch (CurrencyNotFoundException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage(), writer);
        } catch (DatabaseConnectionException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), writer);
        } finally {
            writer.close();
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();

        String codes = req.getPathInfo().replace("/", "");
        String rate = req.getParameter("rate");

        try {
            ExchangeRateDto exchangeRate = exchangeRateService.update(codes, rate);
            writer.write(objectMapper.writeValueAsString(exchangeRate));
        } catch (InvalidDataException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), writer);
        } catch (ExchangeRateNotFoundException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage(), writer);
        } catch (DatabaseConnectionException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), writer);
        } finally {
            writer.close();
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message, PrintWriter writer) throws JsonProcessingException {
        resp.setStatus(statusCode);
        ErrorResponse errorResponse = new ErrorResponse(statusCode, message);
        writer.write(objectMapper.writeValueAsString(errorResponse));
    }
}
