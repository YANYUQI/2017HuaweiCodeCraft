package com;

public class Timer {
	private long startTime;
	private long thresholdTime;
	private long curDuration;
	
	public void begin() {
		this.startTime = System.currentTimeMillis();
		this.curDuration = 0;
	}
	
	public void print() {
		long curDuration = System.currentTimeMillis() - this.startTime;
		System.out.println((curDuration) + "ms");
	}
	
	public long duration() {
		return System.currentTimeMillis() - this.startTime;
	}
	
	public void clean() {
		this.startTime = 0;
		this.thresholdTime = 0;
	}
	
	public void setThreshold(int threshold) {
		this.thresholdTime = threshold;
	}
	
	public boolean overtime() {
		curDuration = System.currentTimeMillis() - this.startTime;
		if(curDuration < this.thresholdTime) {
			System.out.print("---------------");
			System.out.print("未超时："+curDuration+" ms");
			System.out.println("----------------");
			return false;
		}else {
			System.out.print("-----------------");
			System.out.print("超时："+ curDuration+" ms");
			System.out.println("---------------------");
			return true;
		}
	}
	
}
