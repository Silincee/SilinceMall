package cn.silince.silincemall.search.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-23 11:27
 **/
public class ThreadTest {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程: " + Thread.currentThread().getId());
            int i = 10 / 4;
            System.out.println("任务1结果: " + i);
            return i;
        }, executor);

        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程: " + Thread.currentThread().getId());
            System.out.println("任务2完成 ");
            return "Hello";
        }, executor);

//        future01.runAfterBothAsync(future02,()->{
//            System.out.println("任务3线程: " + Thread.currentThread().getId());
//        },executor);

        future01.thenAcceptBothAsync(future02,(f1,f2)->{
            System.out.println("任务3线程: " + Thread.currentThread().getId());
            System.out.println(f1);
            System.out.println(f2);
        },executor);

        System.out.println("main,,,end...");



    }}
