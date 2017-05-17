package com.xwtech.jobs.KPI.queue;
/**
 * 实现Queue
 * @author zl 2017/0220
 *
 */
public class LinkedQueue implements Queue {
	private int size = 0;

	private Entry head = new Entry();
	private Entry tail = this.head;
	private Entry free = new Entry();
	private int freeSize = 0;
	private String name;

	public LinkedQueue(String name) {
		this.name = name;
	}

	public synchronized boolean isEmpty() {
		return size() <= 0;
	}

	public int size() {
		return this.size;
	}

	public synchronized boolean push(Object obj) {
		if (obj != null) {
			push0(obj);
			notifyAll();
			return true;
		}
		return false;
	}

	private Entry getFree() {
		if (this.free.next == null) {
			return new Entry();
		}
		Entry entry = this.free.next;
		this.free.next = entry.next;
		entry.next = null;
		this.freeSize -= 1;
		return entry;
	}

	private void recycle(Entry entry) {
		entry.recycle();
		if (this.freeSize < 16) {
			entry.next = this.free.next;
			this.free.next = entry;
			this.freeSize += 1;
		}
	}

	protected synchronized void push0(Object obj) {
		Entry entry = getFree();
		entry.value = obj;
		this.tail.next = entry;
		this.tail = entry;
		this.size += 1;
	}

	protected Object pop0() {
		Entry entry = this.head.next;
		Object obj = entry.value;
		this.head.next = entry.next;
		if (this.tail == entry) {
			this.tail = this.head;
		}
		this.size -= 1;
		recycle(entry);
		return obj;
	}

	protected Object pop1() {
		while (isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				return null;
			}
		}
		return pop0();
	}

	public synchronized Object pop() {
		return pop1();
	}

	public synchronized Object peek(int i) {
		if ((i < 0) || (i >= this.size)) {
			throw new IllegalArgumentException("Index out of bound:" + i);
		}

		int j = 0;
		Entry entry = this.head.next;
		for (; j < i; j++) {
			entry = entry.next;
		}

		return entry.value;
	}

	public Object remove(int i) {
		if ((i < 0) || (i >= this.size)) {
			throw new IllegalArgumentException("Index out of bound:" + i);
		}

		synchronized (this) {
			if (i == 0) {
				return pop0();
			}
			int j = 0;
			int e = i - 1;
			Entry pre = this.head.next;
			for (; j < e; j++) {
				pre = pre.next;
			}

			Entry entry = pre.next;
			Object obj = entry.value;
			pre.next = entry.next;
			if (this.tail == entry) {
				this.tail = pre;
			}
			this.size -= 1;
			recycle(entry);
			return obj;
		}
	}

	public boolean remove(Object obj) {
		if (isEmpty()) {
			return false;
		}
		synchronized (this) {
			Entry pre = this.head;
			Entry entry = pre.next;
			while (entry != null) {
				if (obj.equals(entry.value)) {
					pre.next = entry.next;
					if (this.tail == entry) {
						this.tail = pre;
					}
					recycle(entry);
					this.size -= 1;
					return true;
				}
				pre = entry;
				entry = entry.next;
			}
		}
		return false;
	}

	public synchronized Object pop(long timeout) {
		if (timeout <= 0L) {
			return pop1();
		}

		long start = System.currentTimeMillis();
		int wait = 500;
		if (timeout < wait) {
			wait = 50;
		}

		while (isEmpty()) {
			try {
				wait(wait);
			} catch (InterruptedException e) {
				return null;
			}
			if (!isEmpty())
				break;
			if (System.currentTimeMillis() - start > timeout) {
				return null;
			}
		}
		return pop0();
	}

	public String toString() {
		return "Queue Size:" + this.size;
	}

	public String getName() {
		return this.name;
	}

	static class Entry {
		Entry next = null;
		Object value = null;

		Entry() {
		}

		Entry(Object value, Entry next) {
			this.value = value;
			this.next = next;
		}

		void recycle() {
			this.next = null;
			this.value = null;
		}

		public String toString() {
			return this.value.toString();
		}
	}
}