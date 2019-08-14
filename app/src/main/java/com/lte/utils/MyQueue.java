package com.lte.utils;

import java.util.LinkedList;

/**
 * Created by chenxiaojun on 2017/10/19.
 */

public class MyQueue<T> {

    private LinkedList<T> list = new LinkedList<T>();

    public void clear()//销毁队列
    {
        list.clear();
    }

    public boolean isQueueEmpty()//判断队列是否为空
    {
        return list.isEmpty();
    }

    public void enQueue(T o)//进队
    {
        list.addLast(o);
    }

    public T deQueue()//出队
    {
        if (!list.isEmpty()) {
            return list.removeFirst();
        }
        return null;
    }

    public int QueueLength()//获取队列长度
    {
        return list.size();
    }

    public Object QueuePeek()//查看队首元素
    {
        return list.getFirst();
    }
}
