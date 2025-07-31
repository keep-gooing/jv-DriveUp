package mate.academy.carsharingapp.mapper;

import mate.academy.carsharingapp.config.MapperConfig;
import mate.academy.carsharingapp.dao.payment.PaymentResponseDto;
import mate.academy.carsharingapp.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toDto(Payment payment);
}
