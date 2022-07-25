package com.olympus.utils.sliding;


import com.olympus.utils.sliding.data.TimeWindowSlidingDataSource;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 时间窗滑块
 * since 2019/12/8
 *
 * @author eddie
 */
public class TimeWindowSliding {

    /**
     * 队列的总长度
     */
    private final int timeSliceSize;

    /**
     * 每个时间片的时长，以毫秒为单位
     */
    private final int timeMillisPerSlice;

    /**
     * 当前所使用的时间片位置
     */
    private final AtomicInteger cursor = new AtomicInteger(0);

    /**
     * 在一个完整窗口期内允许通过的最大阈值
     */
    private final int threshold;

    private final int windowSize;

    /**
     * 最小每个时间片的时长，以毫秒为单位
     */
    private static final int MIN_TIME_MILLIS_PER_SLICE = 50;

    /**
     * 最小窗口数量
     */
    private static final int DEFAULT_WINDOW_SIZE = 5;

    /**
     * 数据存储
     */
    private final TimeWindowSlidingDataSource timeWindowSlidingDataSource;

    /**
     * 创建滑动时间窗
     * @param timeWindowSlidingDataSource       时间分配数据源
     * @param windowSize                        窗口数量 （require >= 5）
     * @param timeMillisPerSlice                每个时间片的时长，以毫秒为单位
     * @param threshold                         在一个完整窗口期内允许通过的最大阈值
     */
    public TimeWindowSliding(TimeWindowSlidingDataSource timeWindowSlidingDataSource, int windowSize, int timeMillisPerSlice, int threshold) {
        this.timeWindowSlidingDataSource = timeWindowSlidingDataSource;
        this.timeMillisPerSlice = timeMillisPerSlice;
        this.threshold = threshold;
        /* Less than a certain number of Window will lose accuracy */
        this.windowSize = Math.max(windowSize, DEFAULT_WINDOW_SIZE);
        /* Ensure that at least two Windows in each time window， do not overlap */
        this.timeSliceSize = this.windowSize * 2 + 1;
        /* This operation can be ignored, the data store structure defined in the lifecycle function,
         if there is an implementation of the interface will be called,
         not the implementation of the default implementation directly return
        */
        timeWindowSlidingDataSource.initTimeSlices();
        /* Initialization parameter verification */
        this.verifier();
    }

    private void verifier() {
        if (Objects.isNull(timeWindowSlidingDataSource) || timeMillisPerSlice < MIN_TIME_MILLIS_PER_SLICE || threshold <= 0) {
            throw new RuntimeException("Initialization exception，Incorrect parameter");
        }
    }


    public static void main(String[] args) throws InterruptedException {
        //0.2秒一个时间片，窗口共5个
        TimeWindowSliding window = new TimeWindowSliding(TimeWindowSlidingDataSource.defaultDataSource(), 5, 5000, 3);
        for (int i = 0; i < 1000; i++) {
            boolean allow = window.allowLimitTimes("a1");
            TimeUnit.MILLISECONDS.sleep(500);
            System.out.println(allow);
        }
    }

    /**
     * 判断是否允许进行访问，未超过阈值的话才会对某个时间片+1
     */
    public boolean allowLimitTimes(String key) {
        int index = locationIndex();
        int sum = 0;
        // cursor不等于index，将cursor设置为index
        int oldCursor = cursor.getAndSet(index);
        if (oldCursor != index) {
            // 清零，访问量不大时会有时间片跳跃的情况
            clearBetween(oldCursor, index);
        }
        for (int i = 1; i < timeSliceSize; i++) {
            sum += timeWindowSlidingDataSource.getAllocAdoptRecordTimes(i, key);
        }

        // 阈值判断
        if (sum < threshold) {
            // 未超过阈值才+1
            this.timeWindowSlidingDataSource.allocAdoptRecord(index, key);
            return true;
        }
        return false;
    }

    /**
     * 返回平均每秒访问次数
     */
    public int allowNotLimitTotal(String key) {
        int index = locationIndex();
        int sum = 0;
        int nextIndex = index + 1;
        this.timeWindowSlidingDataSource.clearSingle(nextIndex);
        int from = index, to = index;
        if (index < windowSize) {
            from += windowSize + 1;
            to += 2 * windowSize;
        } else {
            from = index - windowSize + 1;
        }
        while (from <= to) {
            int targetIndex = from;
            if (from >= timeSliceSize) {
                targetIndex = from - 2 * windowSize;
            }
            sum += timeWindowSlidingDataSource.getAllocAdoptRecordTimes(targetIndex, key);
            from++;
        }
        this.timeWindowSlidingDataSource.allocAdoptRecord(index, key);
        return (sum + 1) ;
    }

    /**
     * 返回每秒访问次数
     */
    public int allowNotLimit(String key) {
        int index = locationIndex();
        int sum = 0;
        // cursor不等于index，将cursor设置为index
        int oldCursor = cursor.getAndSet(index);
        if (oldCursor != index) {
            // 清零，访问量不大时会有时间片跳跃的情况
            clearBetween(oldCursor, index);
        }
        for (int i = 0; i <= timeSliceSize; i++) {
            sum += timeWindowSlidingDataSource.getAllocAdoptRecordTimes(i, key);
        }
        this.timeWindowSlidingDataSource.allocAdoptRecord(index, key);
        return sum + 1;
    }

    /**
     * <p>将fromIndex~toIndex之间的时间片计数都清零
     * <p>极端情况下，当循环队列已经走了超过1个timeSliceSize以上，这里的清零并不能如期望的进行
     */
    private synchronized void clearBetween(int fromIndex, int toIndex) {
        this.timeWindowSlidingDataSource.clearBetween(fromIndex, toIndex, timeSliceSize);
    }

    private int locationIndex() {
        long time = System.currentTimeMillis();
        return (int) ((time / timeMillisPerSlice) % timeSliceSize);
    }
}
