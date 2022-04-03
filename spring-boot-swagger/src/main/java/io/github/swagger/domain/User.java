package io.github.swagger.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.swagger.ext.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private Long id;
    private Boolean isVip;
    private Date createTime;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;
    private BigDecimal amount;
    private String a;
}
