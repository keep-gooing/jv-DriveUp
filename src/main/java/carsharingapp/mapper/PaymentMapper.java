package carsharingapp.mapper;

import carsharingapp.config.MapperConfig;
import carsharingapp.dto.payment.PaymentResponseDto;
import carsharingapp.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toDto(Payment payment);
}
