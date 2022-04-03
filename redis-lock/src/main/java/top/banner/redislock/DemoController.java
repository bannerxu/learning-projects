package top.banner.redislock;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.banner.lib.idempotent.Idempotent;

@RestController
public class DemoController {


    @GetMapping("/noKey")
    @Idempotent(expireTime = 3, info = "请勿重复查询")
    public String noKey() throws Exception {
        Thread.sleep(2000L);
        return "success";
    }

    @GetMapping("/get1")
    @Idempotent(key = "'a'", expireTime = 3, info = "请勿重复查询")
    public String get1(String key) throws Exception {
        Thread.sleep(2000L);
        return "success";
    }

    @GetMapping("/get2")
    @Idempotent(key = "#p0", expireTime = 3, info = "请勿重复查询")
    public String get2(String key) throws Exception {
        Thread.sleep(2000L);
        return "success";
    }


    @GetMapping("/get3")
    @Idempotent(key = "#key", expireTime = 3, info = "请勿重复查询")
    public String get3(String key) throws Exception {
        Thread.sleep(2000L);
        return "success";
    }

    @GetMapping("/get4")
    @Idempotent(key = "#key == 'a' ? true:false", expireTime = 3, info = "请勿重复查询")
    public String get4(String key) throws Exception {
        Thread.sleep(2000L);
        return "success";
    }

    @GetMapping("/get5")
    @Idempotent(key = "#key?: 'ass'", expireTime = 3, info = "请勿重复查询")
    public String get5(String key) throws Exception {
        Thread.sleep(2000L);
        return "success";
    }
}