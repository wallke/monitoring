package com.xwtech.es.model;

/**
 * 主机监控信息
 * 
 * @author lin.mr
 *
 */
public class HostMonitorBean {

	public Object key;

	/**
	 * 
	 */
	public String hostIp;

	public double load5Avg = 0.00;

	public double load5Min = 0.00;

	public double load5Max= 0.00;

	public double uMemoryAvg= 0.00;

	public double uMemoryMin= 0.00;

	public double uMemoryMax= 0.00;

	public double inAvg= 0.00;

	public double inMin= 0.00;

	public double inMax= 0.00;

	public double outAvg= 0.00;

	public double outMax= 0.00;

	public double outMin= 0.00;
	
	public double freeDiskAvg= 0.00;
	
	public double freeDiskMax= 0.00;
	
	public double freeDiskMin= 0.00;

	public double getuMemoryAvg() {
		return uMemoryAvg;
	}

	public void setuMemoryAvg(double uMemoryAvg) {
		this.uMemoryAvg = uMemoryAvg;
	}

	public double getuMemoryMin() {
		return uMemoryMin;
	}

	public void setuMemoryMin(double uMemoryMin) {
		this.uMemoryMin = uMemoryMin;
	}

	public double getuMemoryMax() {
		return uMemoryMax;
	}

	public void setuMemoryMax(double uMemoryMax) {
		this.uMemoryMax = uMemoryMax;
	}

	public double getFreeDiskAvg() {
		return freeDiskAvg;
	}

	public void setFreeDiskAvg(double freeDiskAvg) {
		this.freeDiskAvg = freeDiskAvg;
	}

	public double getFreeDiskMax() {
		return freeDiskMax;
	}

	public void setFreeDiskMax(double freeDiskMax) {
		this.freeDiskMax = freeDiskMax;
	}

	public double getFreeDiskMin() {
		return freeDiskMin;
	}

	public void setFreeDiskMin(double freeDiskMin) {
		this.freeDiskMin = freeDiskMin;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public double getLoad5Avg() {
		return load5Avg;
	}

	public void setLoad5Avg(double load5Avg) {
		this.load5Avg = load5Avg;
	}

	public double getLoad5Min() {
		return load5Min;
	}

	public void setLoad5Min(double load5Min) {
		this.load5Min = load5Min;
	}

	public double getLoad5Max() {
		return load5Max;
	}

	public void setLoad5Max(double load5Max) {
		this.load5Max = load5Max;
	}

	public double getInAvg() {
		return inAvg;
	}

	public void setInAvg(double inAvg) {
		this.inAvg = inAvg;
	}

	public double getInMin() {
		return inMin;
	}

	public void setInMin(double inMin) {
		this.inMin = inMin;
	}

	public double getInMax() {
		return inMax;
	}

	public void setInMax(double inMax) {
		this.inMax = inMax;
	}

	public double getOutAvg() {
		return outAvg;
	}

	public void setOutAvg(double outAvg) {
		this.outAvg = outAvg;
	}

	public double getOutMax() {
		return outMax;
	}

	public void setOutMax(double outMax) {
		this.outMax = outMax;
	}

	public double getOutMin() {
		return outMin;
	}

	public void setOutMin(double outMin) {
		this.outMin = outMin;
	}
	
	public static void main(String[] args) {
		double s = 7.11111111111111111111111111;
		System.out.println(s*1.00);
	}
}
