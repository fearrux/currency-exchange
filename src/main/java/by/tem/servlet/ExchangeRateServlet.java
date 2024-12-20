package by.tem.servlet;

import by.tem.dto.ExchangeRateDto;
import by.tem.exception.ExchangeRateNotFoundException;
import by.tem.exception.InvalidDataException;
import by.tem.service.ExchangeRateService;
import by.tem.validation.CurrencyExchangeValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
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
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        String codes = req.getPathInfo().substring(1);
        PrintWriter writer = resp.getWriter();
        try {
            CurrencyExchangeValidator.isValidExchangeRate(codes);
            String baseCode = codes.substring(0, 3);
            String targetCode = codes.substring(3, 6);
            ExchangeRateDto exchangeRate = exchangeRateService.findByCode(baseCode, targetCode);
            String json = objectMapper.writeValueAsString(exchangeRate);
            writer.write(json);
        } catch (InvalidDataException exception) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write(exception.getMessage());
        } catch (ExchangeRateNotFoundException exception) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.write(exception.getMessage());
        } catch (RuntimeException exception) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            writer.close();
        }
    }
}
