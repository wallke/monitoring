package com.xwtech.jobs.KPI.queue;

/**
 * 重写Queue方法
 * @author zl 2017/0220
 *
 */
public abstract interface Queue
{
  public abstract int size();

  public abstract boolean isEmpty();

  public abstract boolean push(Object paramObject);

  public abstract Object pop();

  public abstract Object pop(long paramLong);

  public abstract Object peek(int paramInt);

  public abstract Object remove(int paramInt);

  public abstract boolean remove(Object paramObject);

  public abstract String getName();
}