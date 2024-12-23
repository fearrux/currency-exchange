package by.tem.servlet;

import by.tem.dto.ErrorResponse;
import by.tem.dto.ExchangeRateDto;
import by.tem.exception.CurrencyNotFoundException;
import by.tem.exception.DatabaseConnectionException;
import by.tem.exception.ExchangeRateExistsException;
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
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();

        try {
            List<ExchangeRateDto> exchangeRates = exchangeRateService.findAll();
            writer.write(objectMapper.writeValueAsString(exchangeRates));
        } catch (DatabaseConnectionException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), writer);
        } finally {
            writer.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        try {
            ExchangeRateDto exchangeRate = exchangeRateService.save(baseCurrencyCode, targetCurrencyCode, rate);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            writer.write(objectMapper.writeValueAsString(exchangeRate));
        } catch (InvalidDataException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), writer);
        } catch (ExchangeRateExistsException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_CONFLICT, e.getMessage(), writer);
        } catch (CurrencyNotFoundException e) {
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
