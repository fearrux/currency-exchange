package by.tem.dto;

import java.util.Objects;

public class CurrencyDto {
    private Integer id;
    private String name;
    private String code;
    private String sign;

    public CurrencyDto(Integer id, String name, String code, String sign) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.sign = sign;
    }

    public CurrencyDto() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getSign() {
        return sign;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CurrencyDto that = (CurrencyDto) object;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(code, that.code) && Objects.equals(sign, that.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code, sign);
    }

    @Override
    public String toString() {
        return "CurrencyDto{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", code='" + code + '\'' +
               ", sign='" + sign + '\'' +
               '}';
    }
}
