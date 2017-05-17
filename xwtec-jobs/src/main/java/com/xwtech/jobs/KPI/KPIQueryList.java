package com.xwtech.jobs.KPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 *  获取es内的KPIlist
 * @author by lin.mr 2017/02/20
 *
 */
public class KPIQueryList {
    
	 public static List<Map<String,Object>> list  = null;
	 
	 public static BlockingQueue<Map<String,Object>> queue =null;
	 
	 public static KPIQueryList kpiQueryList;
	 
	 public static KPIQueryList getKPIQueryList(){
		 if(kpiQueryList == null){
			 kpiQueryList = new KPIQueryList();
		 }
		 return kpiQueryList;
	 }
	 
	 //TODO
	 public  List<Map<String,Object>> getQueryList(){
		 if(list == null){
			 list = new ArrayList<Map<String,Object>>();
			 for(int i=1;i<=4;i++){
				 Map<String,Object> map = new HashMap<String,Object>();
				 map.put("key"+String.valueOf(i), "value"+String.valueOf(i));
				 list.add(map);
			 }
		 }
		 
		 return list;
	 }

	 
}
