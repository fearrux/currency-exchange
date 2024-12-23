package by.tem.servlet;

import by.tem.dto.CurrencyDto;
import by.tem.dto.ErrorResponse;
import by.tem.exception.CurrencyNotFoundException;
import by.tem.exception.DatabaseConnectionException;
import by.tem.exception.InvalidDataException;
import by.tem.service.CurrencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        req.setCharacterEncoding("UTF-8");

        String code = req.getPathInfo().substring(1);
        PrintWriter writer = resp.getWriter();

        try {
            CurrencyDto currency = currencyService.findByCode(code);
            writer.write(objectMapper.writeValueAsString(currency));
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

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message, PrintWriter writer) throws JsonProcessingException {
        resp.setStatus(statusCode);
        ErrorResponse errorResponse = new ErrorResponse(statusCode, message);
        writer.write(objectMapper.writeValueAsString(errorResponse));
    }
}
