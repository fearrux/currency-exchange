package by.tem.servlet;

import by.tem.dto.CurrencyDto;
import by.tem.exception.CurrencyNotFoundException;
import by.tem.exception.InvalidDataException;
import by.tem.service.CurrencyService;
import by.tem.validation.CurrencyValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        String code = req.getPathInfo().substring(1);
        CurrencyValidator.isValidCode(code);
        PrintWriter writer = resp.getWriter();
        try {
            CurrencyDto currencyDto = currencyService.findByCode(code);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            String json = objectMapper.writeValueAsString(currencyDto);
            writer.write(json);
        } catch (InvalidDataException exception) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write(exception.getMessage());
        } catch (CurrencyNotFoundException exception) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.write(exception.getMessage());
        } catch (RuntimeException exception) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            writer.close();
        }
    }
}
