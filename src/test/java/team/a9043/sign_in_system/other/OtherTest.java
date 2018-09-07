package team.a9043.sign_in_system.other;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;
import team.a9043.sign_in_system.mapper.SisJoinCourseMapper;
import team.a9043.sign_in_system.mapper.SisScheduleMapper;
import team.a9043.sign_in_system.mapper.SisUserInfoMapper;
import team.a9043.sign_in_system.pojo.SisJoinCourse;
import team.a9043.sign_in_system.pojo.SisJoinCourseExample;
import team.a9043.sign_in_system.pojo.SisSchedule;
import team.a9043.sign_in_system.pojo.SisScheduleExample;

import javax.annotation.Resource;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author a9043
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class OtherTest {
    @Resource
    private SisJoinCourseMapper sisJoinCourseMapper;
    @Resource
    private SisScheduleMapper sisScheduleMapper;
    @Resource
    private TaskExecutor taskExecutor;
    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Resource
    private SisUserInfoMapper sisUserInfoMapper;

    @Value("${location.privateKey}")
    private String privateKey;
    @Value("${location.publicKey}")
    private String publicKey;

    @Test
    public void test() {
        List<String> scIdList = new ArrayList<>();
        scIdList.add("A");

        CompletableFuture<List<SisJoinCourse>> listCompletableFuture =
            CompletableFuture.supplyAsync(() -> {
                SisJoinCourseExample sisJoinCourseExample =
                    new SisJoinCourseExample();
                sisJoinCourseExample.createCriteria().andScIdIn(scIdList);
                return sisJoinCourseMapper.selectByExample(sisJoinCourseExample);
            }, taskExecutor).toCompletableFuture();
        CompletableFuture<List<SisSchedule>> listCompletableFuture1 =
            CompletableFuture.supplyAsync(() -> {
                SisScheduleExample sisScheduleExample =
                    new SisScheduleExample();
                sisScheduleExample.createCriteria().andScIdIn(scIdList);
                return sisScheduleMapper.selectByExample(sisScheduleExample);
            }, taskExecutor).toCompletableFuture();

        CompletableFuture.allOf(listCompletableFuture,
            listCompletableFuture1).join();

        log.info("end");
    }

    @Test
    public void test2() {
        AtomicBoolean isEnd = new AtomicBoolean();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);
        threadPoolTaskScheduler.schedule(() -> isEnd.set(true),
            calendar.toInstant());

        while (!isEnd.get()) {

        }
        log.info("end: " + isEnd);
    }

    @Test
    public void test3() {
        String ssSuspension = "1,a,2,c.,-1";
        List<Integer> integers = Arrays.stream(ssSuspension.split(","))
            .map(String::trim)
            .map(s -> {
                try {
                    return Integer.valueOf(s);
                } catch (NumberFormatException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .filter(integer -> integer > 0)
            .collect(Collectors.toList());
        log.info(new JSONArray(integers).toString(2));
    }

    @Test
    public void teat4() throws NoSuchAlgorithmException,
        NoSuchPaddingException, InvalidKeyException, BadPaddingException,
        IllegalBlockSizeException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128, new SecureRandom());
        SecretKey secretKey = keyGenerator.generateKey();
        String keyStr =
            Base64.getEncoder().encodeToString(secretKey.getEncoded());
        log.info("new Key: " + keyStr);

        //do
        SecretKeySpec secretKeySpec =
            new SecretKeySpec(Base64.getDecoder().decode(keyStr), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        String enStr = Base64.getEncoder().encodeToString(cipher.doFinal(
            "123456".getBytes()));

        Cipher cipher1 = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        log.info(new String(cipher.doFinal(Base64.getDecoder().decode(enStr))));
    }
}

