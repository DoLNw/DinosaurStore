package com.jcwang.store.seckill.scheduler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 *      1 @EnableScheduling 开启定时任务
 *      2 @Scheduled 开启一个定时任务
 *      3 自动配置类 TaskSchedulingAutoConfiguration
 *
 * 异步任务
 *      1 @EnableAsync 开启异步任务功能
 *      2 @Async 给希望异步执行的方法上标注
 *      3 自动配置类 TaskExecutionAutoConfiguration
 */

@Slf4j
@Component
@EnableScheduling
@EnableAsync
public class HelloSchedule {

    /**
     * 1 Spring中6位组成，不允许第七位的年
     * 2 在最后一位周的位置，1-7代表周一到周日
     * 3 定时任务不应该阻塞，比如远程调用会阻塞，默认是阻塞的。
     *   1 可以让业务运行以异步的方式，completableFuture
     *   2 定时任务的线程池的个数默认是1，可以给池子的默认线程个数改多一点呢（这个版本不好使）
     *   3 让定时任务异步进行 @EnableAsync,@Async给希望异步执行的方法上面标注
     *
     *   解决：使用异步任务+定时任务来完成定时任务不阻塞的功能
     */
//    @Async
//    @Scheduled(cron = "* * * * * ?")
//    public void hello() throws InterruptedException {
//        System.out.println("hello..");
//        Thread.sleep(3000);
//    }
}
