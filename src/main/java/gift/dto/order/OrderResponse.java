package gift.dto.order;

import gift.dto.option.OptionInformation;

import java.time.LocalDateTime;

public record OrderResponse(Long id, OptionInformation optionInformation, Integer quantity, LocalDateTime orderDateTime, String message) {
    public static OrderResponse of(Long id, OptionInformation optionInformation, Integer quantity, LocalDateTime orderDateTime, String message) {
        return new OrderResponse(id, optionInformation, quantity, orderDateTime, message);
    }
}
