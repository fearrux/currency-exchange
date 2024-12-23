package by.tem.servlet;

import by.tem.dto.CurrencyExchangeDto;
import by.tem.dto.ErrorResponse;
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

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amount = req.getParameter("amount");

        try {
            CurrencyExchangeDto exchange = exchangeRateService.exchange(from, to, amount);
            writer.write(objectMapper.writeValueAsString(exchange));
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
