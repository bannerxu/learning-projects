package top.banner.redislock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.banner.redislock.service.TestService;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisLockApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(RedisLockApplicationTests.class);

    @Resource
    private TestService testService;


    @Test
    public void add() {

        final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1000);

        for (int i = 0; i < 1000; i++) {
            pool.submit(() -> {
                try {
                    testService.add();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    @Test
    public void LockAdd() {

        final long l = System.currentTimeMillis();
        final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1000);

        for (int i = 0; i < 1000; i++) {
            pool.submit(() -> {
                try {
                    testService.lockAdd();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        testService.awaitAfterShutdown(pool);

        log.info("耗时：{}", System.currentTimeMillis() - l);
    }


}
