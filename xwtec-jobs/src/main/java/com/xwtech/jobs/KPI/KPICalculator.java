package com.xwtech.jobs.KPI;

import java.util.Map;

/**
 * 获取ES内KPI值
 * @author zl 2017/0220
 *
 */
public class KPICalculator   {
	
	protected  static  Map<String, Object> map;
	
	public KPICalculator(Map<String, Object> map){
		KPICalculator.map = map;
	}
	
	//TODO
	public  static Map<String, Object>  getKpi(Map<String, Object> map){
		try {
			System.out.println(Thread.currentThread().getName() + "==第一中方法获取es value" + "==== i==" + map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	    
	}
	
}
