package by.tem.servlet;

import by.tem.dto.CurrencyDto;
import by.tem.dto.ExchangeRateDto;
import by.tem.exception.CurrencyNotFoundException;
import by.tem.exception.ExchangeRateExistsException;
import by.tem.exception.InvalidDataException;
import by.tem.mapper.CurrencyMapper;
import by.tem.service.CurrencyService;
import by.tem.service.ExchangeRateService;
import by.tem.validation.CurrencyValidator;
import by.tem.validation.ExchangeRateValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
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
    private static final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
            List<ExchangeRateDto> exchangeRates = exchangeRateService.findAll();
            String json = objectMapper.writeValueAsString(exchangeRates);
            writer.write(json);
        } catch (RuntimeException exception) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write(exception.getMessage());
        } finally {
            writer.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        PrintWriter writer = resp.getWriter();
        try {
            CurrencyValidator.isValidCode(baseCurrencyCode);
            CurrencyValidator.isValidCode(targetCurrencyCode);
            ExchangeRateValidator.isValidRate(rate);
            ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
            CurrencyService currencyService = CurrencyService.getInstance();
//            try to find currency in database with its code
            CurrencyDto baseCurrencyDto = currencyService.findByCode(baseCurrencyCode);
            CurrencyDto targetCurrencyDto = currencyService.findByCode(targetCurrencyCode);
//            set found currency to exchange rate
            exchangeRateDto.setBaseCurrency(CurrencyMapper.toCurrency(baseCurrencyDto));
            exchangeRateDto.setTargetCurrency(CurrencyMapper.toCurrency(targetCurrencyDto));
            exchangeRateDto.setRate(Double.parseDouble(rate));
            ExchangeRateDto savedExchangeRate = exchangeRateService.save(exchangeRateDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            String json = objectMapper.writeValueAsString(savedExchangeRate);
            writer.write(json);
        } catch (InvalidDataException exception) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write(exception.getMessage());
        } catch (ExchangeRateExistsException exception) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            writer.write(exception.getMessage());
        } catch (CurrencyNotFoundException exception) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.write(exception.getMessage());
        } catch (RuntimeException exception) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write(exception.getMessage());
        } finally {
            writer.close();
        }
    }
}
