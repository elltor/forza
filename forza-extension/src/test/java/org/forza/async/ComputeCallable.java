package org.forza.async;

import java.util.concurrent.Callable;

/**
 * 子线程计算任务
 */
public class ComputeCallable implements Callable<Integer> {
    private Integer value;
    private String taskName;

    public ComputeCallable(Integer value, String taskName) {
        this.value = value;
        this.taskName = taskName;
        System.out.println("生成子线程计算任务: " + taskName);
    }

    @Override
    public Integer call() throws Exception {
        Thread.sleep(2000);
        String str = "ThreadName: " + Thread.currentThread().getName() +
                " value: " + value +
                " TaskName: " + taskName;
        System.out.println(str + ", Call Done");
        return value;
    }
}
