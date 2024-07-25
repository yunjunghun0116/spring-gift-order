package gift.dto.option;

public record OptionInformation(Long id, String productName, String name) {
    public static OptionInformation of(Long id, String productName, String name) {
        return new OptionInformation(id, productName, name);
    }
}
