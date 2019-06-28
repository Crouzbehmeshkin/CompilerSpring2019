import java.util.Objects;

public class SymbolTableKey {
    private String type;
    private String token;

    public SymbolTableKey(String type, String token)
    {
        this.type = type;
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolTableKey that = (SymbolTableKey) o;
        return type.equals(that.type) &&
                token.equals(that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, token);
    }
}
