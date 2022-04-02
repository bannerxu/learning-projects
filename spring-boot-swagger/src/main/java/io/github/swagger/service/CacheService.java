package io.github.swagger.service;

import io.github.swagger.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Cacheable(cacheNames = "users")
public class CacheService {


    public List<User> users() {

        log.info(" 模拟新数据 。。。。。");
        ArrayList<User> all = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            all.add(User.builder()
                    .id((long) i)
                    .isVip(true)
                    .amount(BigDecimal.ZERO)
                    .createTime(new Date())
                    .updateTime(LocalDateTime.now())
                    .build());

        }
        return all;

    }
}
