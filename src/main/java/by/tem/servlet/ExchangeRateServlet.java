package by.tem.servlet;

import by.tem.dto.ExchangeRateDto;
import by.tem.exception.DatabaseConnectionException;
import by.tem.exception.ExchangeRateNotFoundException;
import by.tem.exception.InvalidDataException;
import by.tem.service.ExchangeRateService;
import by.tem.validation.CurrencyValidator;
import by.tem.validation.ExchangeRateValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

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

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        String codes = req.getPathInfo();
        String rate = req.getParameter("rate");
        codes = codes.replace("/", "");
        ExchangeRateValidator.isValidExchangeRate(codes);
        try {
            String baseCode = codes.substring(0, 3);
            String targetCode = codes.substring(3, 6);
            CurrencyValidator.isValidCode(baseCode);
            CurrencyValidator.isValidCode(targetCode);
            ExchangeRateValidator.isValidRate(rate);
            BigDecimal rateValue = new BigDecimal(rate);
            ExchangeRateDto updateExchangeRate = exchangeRateService.update(baseCode, targetCode, rateValue);
            String json = objectMapper.writeValueAsString(updateExchangeRate);
            resp.getWriter().write(json);
        } catch (InvalidDataException exception) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write(exception.getMessage());
        } catch (ExchangeRateNotFoundException exception) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.write(exception.getMessage());
        } catch (DatabaseConnectionException exception) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write(exception.getMessage());
        } finally {
            writer.close();
        }
    }
}
