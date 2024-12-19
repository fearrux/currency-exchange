package by.tem.servlet;

import by.tem.dto.CurrencyDto;
import by.tem.exception.CurrencyExistsException;
import by.tem.exception.InvalidDataException;
import by.tem.service.CurrencyService;
import by.tem.validation.CurrencyValidator;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;


@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyService instance = CurrencyService.getInstance();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        try {
            List<CurrencyDto> currencies = instance.findAll();
            String json = objectMapper.writeValueAsString(currencies);
            writer.write(json);
        } catch (RuntimeException exception) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write(exception.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        PrintWriter writer = resp.getWriter();
        try {
            CurrencyValidator.isValidName(name);
            CurrencyValidator.isValidCode(code);
            CurrencyValidator.isValidSign(sign);
            CurrencyDto currencyDto = new CurrencyDto();
            currencyDto.setName(name);
            currencyDto.setCode(code);
            currencyDto.setSign(sign);
            CurrencyDto result = instance.save(currencyDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            String json = objectMapper.writeValueAsString(result);
            writer.write(json);
        } catch (InvalidDataException exception) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write(exception.getMessage());
        } catch (CurrencyExistsException exception) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            writer.write(exception.getMessage());
        } catch (RuntimeException exception) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write(exception.getMessage());
        } finally {
            writer.close();
        }
    }
}
