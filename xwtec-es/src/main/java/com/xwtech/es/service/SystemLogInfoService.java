package com.xwtech.es.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.support.AbstractClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xwtech.es.ConstantsCode;
import com.xwtech.es.DateFormatUtil;
import com.xwtech.es.ElasticConfig;
import com.xwtech.es.model.DateRange;
import com.xwtech.es.model.HostBean;
import com.xwtech.es.model.HostMonitorBean;
import com.xwtech.es.model.NginxBean;
import com.xwtech.es.model.OSBean;
import com.xwtech.es.model.RedisBean;

import io.netty.util.internal.StringUtil;

/**
 * Created by zl on 2017/2/15. 系统日志下的es数据获取
 *  1.主机系统日志 
 *  2.主机日志 
 *  3.Redis运行日志
 *  4.中间件运行日志-Nginx运行日志
 */
@Service
public class SystemLogInfoService {

	private static Logger logger = LoggerFactory.getLogger(SystemLogInfoService.class);

	private static final DecimalFormat df = new DecimalFormat("0.00");

	@Autowired
	TransportClient transportClient;

	@Autowired
	ElasticConfig elasticConfig;

	/**
	 * 1.主机系统日志
	 * 
	 * @param OSBean
	 *            bean
	 * @return List<OSBean>
	 */
	public List<OSBean> getOsInfos(OSBean bean) {
		List<OSBean> list = new ArrayList<OSBean>();

		try {
			// 设置查询条件
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			
			queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.time).from(bean.getBeginTime().getTime()).to(bean.getEndTime().getTime()));
			
			if (!StringUtil.isNullOrEmpty(bean.getHost_ip())) {
				queryBuilder.must(QueryBuilders.matchQuery(ConstantsCode.host_ip, bean.getHost_ip()));
			}
			
			if (!StringUtil.isNullOrEmpty(bean.getHostname())) {
				queryBuilder.must(QueryBuilders.matchQuery(ConstantsCode.hostname,bean.getHostname()));
			}

			logger.info(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getSystemInfoIndex()) // 设置搜索的索引
					.setTypes(elasticConfig.getOsType()) // 设置搜索类型
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(queryBuilder).execute().actionGet();

			for (SearchHit hit : response.getHits().getHits()) {
				Map<String, Object> obj = hit.getSource();
				OSBean osbean = new OSBean();

				if (obj.containsKey(ConstantsCode.log_type)) {
					osbean.setLog_type(String.valueOf(obj.get(ConstantsCode.log_type)));
				}
				if (obj.containsKey(ConstantsCode.host_ip)) {
					osbean.setHost_ip(String.valueOf(obj.get(ConstantsCode.host_ip)));
				}
				if (obj.containsKey(ConstantsCode.time)) {
					String time = DateFormatUtil.DateFormatGMT(String.valueOf(obj.get(ConstantsCode.time)),
							DateFormatUtil.GTMFormatMilSSS, DateFormatUtil.DateFormatMil);
					osbean.setTime(time);
				}
				if (obj.containsKey(ConstantsCode.hostname)) {
					osbean.setHostname(String.valueOf(obj.get(ConstantsCode.hostname)));
				}
				if (obj.containsKey(ConstantsCode.host_boot_time)) {
					String host_boot_time = DateFormatUtil.DateStampFormat(
							String.valueOf(obj.get(ConstantsCode.host_boot_time)), DateFormatUtil.DateFormatMil);
					osbean.setHost_boot_time(host_boot_time);
				}
				if (obj.containsKey(ConstantsCode.uptime)) {
					String uptime = DateFormatUtil.DateStampFormat(String.valueOf(obj.get(ConstantsCode.uptime)),
							new Date().getTime(), DateFormatUtil.DateFormatMil);
					osbean.setUptime(uptime);
				}
				if (obj.containsKey(ConstantsCode.sys_info)) {
					osbean.setSys_info(String.valueOf(obj.get(ConstantsCode.sys_info)));
				}
				if (obj.containsKey(ConstantsCode.sys_processes)) {
					osbean.setSys_processes(String.valueOf(obj.get(ConstantsCode.sys_processes)));
				}
				if (obj.containsKey(ConstantsCode.running_processes)) {
					osbean.setRunning_processes(String.valueOf(obj.get(ConstantsCode.running_processes)));
				}

				list.add(osbean);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 2.HOST主机日志
	 * 
	 * @param HostBean
	 *            bean
	 * @return List<HostBean>
	 */
	public List<HostBean> getHostInfos(HostBean bean) {
		List<HostBean> list = new ArrayList<HostBean>();

		try {
			
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			queryBuilder.must(QueryBuilders.rangeQuery(ConstantsCode.time).from(bean.getBeginTime().getTime()).to(bean.getEndTime().getTime()));
			
			if (!StringUtil.isNullOrEmpty(bean.getHost_ip())) {
				queryBuilder.must(QueryBuilders.matchQuery(ConstantsCode.host_ip, bean.getHost_ip()));
			}

			logger.info(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getSystemInfoIndex()) // 设置搜索的索引
					.setTypes(elasticConfig.getHostType()) // 设置搜索类型
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(queryBuilder)
					.addSort("time", SortOrder.DESC).execute().actionGet();

			for (SearchHit hit : response.getHits().getHits()) {
				Map<String, Object> obj = hit.getSource();
				HostBean hostbean = new HostBean();

				if (obj.containsKey(ConstantsCode.log_type)) {
					hostbean.setLog_type(String.valueOf(obj.get(ConstantsCode.log_type)));
				}
				if (obj.containsKey(ConstantsCode.host_ip)) {
					hostbean.setHost_ip(String.valueOf(obj.get(ConstantsCode.host_ip)));
				}
				if (obj.containsKey(ConstantsCode.time)) {
					String time = DateFormatUtil.DateFormatGMT(String.valueOf(obj.get(ConstantsCode.time)),
							DateFormatUtil.GTMFormatMilSSS, DateFormatUtil.DateFormatMil);
					hostbean.setTime(time);
				}
				if (obj.containsKey(ConstantsCode.processor_load_1)) {
					hostbean.setProcessor_load_1(String.valueOf(obj.get(ConstantsCode.processor_load_1)));
				}
				if (obj.containsKey(ConstantsCode.processor_load_5)) {
					hostbean.setProcessor_load_5(String.valueOf(obj.get(ConstantsCode.processor_load_5)));
				}
				if (obj.containsKey(ConstantsCode.processor_load_15)) {
					hostbean.setProcessor_load_15(String.valueOf(obj.get(ConstantsCode.processor_load_15)));
				}
				if (obj.containsKey(ConstantsCode.context_switches)) {
					hostbean.setContext_switches(String.valueOf(obj.get(ConstantsCode.context_switches)));
				}
				if (obj.containsKey(ConstantsCode.Interrupts)) {
					hostbean.setInterrupts(String.valueOf(obj.get(ConstantsCode.Interrupts)));
				}
				if (obj.containsKey(ConstantsCode.user_time)) {
					hostbean.setUser_time(String.valueOf(obj.get(ConstantsCode.user_time)));
				}
				if (obj.containsKey(ConstantsCode.Iowait_time)) {
					hostbean.setIowait_time(String.valueOf(obj.get(ConstantsCode.Iowait_time)));
				}
				if (obj.containsKey(ConstantsCode.nice_time)) {
					hostbean.setNice_time(String.valueOf(obj.get(ConstantsCode.nice_time)));
				}
				if (obj.containsKey(ConstantsCode.system_time)) {
					hostbean.setSystem_time(String.valueOf(obj.get(ConstantsCode.system_time)));
				}
				if (obj.containsKey(ConstantsCode.softirq_time)) {
					hostbean.setSoftirq_time(String.valueOf(obj.get(ConstantsCode.softirq_time)));
				}
				if (obj.containsKey(ConstantsCode.steal_time)) {
					hostbean.setSteal_time(String.valueOf(obj.get(ConstantsCode.steal_time)));
				}
				if (obj.containsKey(ConstantsCode.interrupt_time)) {
					hostbean.setInterrupt_time(String.valueOf(obj.get(ConstantsCode.interrupt_time)));
				}
				if (obj.containsKey(ConstantsCode.idle_time)) {
					hostbean.setIdle_time(String.valueOf(obj.get(ConstantsCode.idle_time)));
				}
				if (obj.containsKey(ConstantsCode.available_memory)) {
					hostbean.setAvailable_memory(Double.parseDouble(obj.get(ConstantsCode.available_memory).toString()));
				}
				if (obj.containsKey(ConstantsCode.free_swap_space)) {
					hostbean.setFree_swap_space(Double.parseDouble(obj.get(ConstantsCode.free_swap_space).toString()));
				}
				if (obj.containsKey(ConstantsCode.free_swap_space_per)) {
					hostbean.setFree_swap_space_per(Double.parseDouble(obj.get(ConstantsCode.free_swap_space_per).toString()));
				}
				if (obj.containsKey(ConstantsCode.total_memory)) {
					hostbean.setTotal_memory(Double.parseDouble(obj.get(ConstantsCode.total_memory).toString()));
				}
				if (obj.containsKey(ConstantsCode.total_swap_space)) {
					hostbean.setTotal_swap_space(Double.parseDouble(obj.get(ConstantsCode.total_swap_space).toString()));
				}
				if (obj.containsKey(ConstantsCode.incoming)) {
					hostbean.setIncoming(Double.parseDouble(obj.get(ConstantsCode.incoming).toString()));
				}
				if (obj.containsKey(ConstantsCode.outgoing)) {
					hostbean.setOutgoing(Double.parseDouble(obj.get(ConstantsCode.outgoing).toString()));
				}
				if (obj.containsKey(ConstantsCode.free_disk)) {
					hostbean.setFree_disk(Double.parseDouble(obj.get(ConstantsCode.free_disk).toString()));
				}
				if (obj.containsKey(ConstantsCode.free_disk_per)) {
					hostbean.setFree_disk_per(Double.parseDouble(obj.get(ConstantsCode.free_disk_per).toString()));
				}
				if (obj.containsKey(ConstantsCode.free_inodes)) {
					hostbean.setFree_inodes(Double.parseDouble(obj.get(ConstantsCode.free_inodes).toString()));
				}
				if (obj.containsKey(ConstantsCode.totall_disk)) {
					hostbean.setTotall_disk(Double.parseDouble(obj.get(ConstantsCode.totall_disk).toString()));
				}
				if (obj.containsKey(ConstantsCode.used_disk)) {
					hostbean.setUsed_disk(Double.parseDouble(obj.get(ConstantsCode.used_disk).toString()));
				}
				list.add(hostbean);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 3.Redis运行日志
	 * 
	 * @param OSBean
	 *            bean
	 * @return List<OSBean>
	 */
	public List<RedisBean> getRedisInfos(RedisBean bean) {
		List<RedisBean> list = new ArrayList<RedisBean>();

		try {
			// 设置查询条件
			BoolQueryBuilder queryBuilder =  QueryBuilders.boolQuery();
			
			queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(ConstantsCode.host_ip, bean.getHost_ip()));

			queryBuilder = queryBuilder.must(
					QueryBuilders.rangeQuery("time").from(bean.getBeginTime().getTime()).to(bean.getEndTime().getTime()));

			logger.info(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getSystemInfoIndex()) // 设置搜索的索引
					.setTypes(elasticConfig.getRedisType()) // 设置搜索类型
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(queryBuilder).execute().actionGet();

			for (SearchHit hit : response.getHits().getHits()) {
				Map<String, Object> obj = hit.getSource();
				RedisBean redisbean = new RedisBean();

				if (obj.containsKey(ConstantsCode.log_type)) {
					redisbean.setLog_type(String.valueOf(obj.get(ConstantsCode.log_type)));
				}
				if (obj.containsKey(ConstantsCode.host_ip)) {
					redisbean.setHost_ip(String.valueOf(obj.get(ConstantsCode.host_ip)));
				}
				if (obj.containsKey(ConstantsCode.time)) {
					String time = DateFormatUtil.DateFormatGMT(String.valueOf(obj.get(ConstantsCode.time)),
							DateFormatUtil.GTMFormatMilSSS, DateFormatUtil.DateFormatMil);
					redisbean.setTime(time);
				}
				if (obj.containsKey(ConstantsCode.used_memory)) {
					redisbean.setUsed_memory(String.valueOf(obj.get(ConstantsCode.used_memory)));
				}
				if (obj.containsKey(ConstantsCode.used_memory_peak)) {
					redisbean.setUsed_memory_peak(String.valueOf(obj.get(ConstantsCode.used_memory_peak)));
				}
				if (obj.containsKey(ConstantsCode.used_memory_lua)) {
					redisbean.setUsed_memory_lua(String.valueOf(obj.get(ConstantsCode.used_memory_lua)));
				}
				if (obj.containsKey(ConstantsCode.mem_fragmentation_ratio)) {
					redisbean
							.setMem_fragmentation_ratio(String.valueOf(obj.get(ConstantsCode.mem_fragmentation_ratio)));
				}
				if (obj.containsKey(ConstantsCode.connected_clients)) {
					redisbean.setConnected_clients(String.valueOf(obj.get(ConstantsCode.connected_clients)));
				}
				if (obj.containsKey(ConstantsCode.client_longest_output_list)) {
					redisbean.setClient_longest_output_list(
							String.valueOf(obj.get(ConstantsCode.client_longest_output_list)));
				}
				if (obj.containsKey(ConstantsCode.client_biggest_input_buf)) {
					redisbean.setClient_biggest_input_buf(
							String.valueOf(obj.get(ConstantsCode.client_biggest_input_buf)));
				}
				if (obj.containsKey(ConstantsCode.blocked_clients)) {
					redisbean.setBlocked_clients(String.valueOf(obj.get(ConstantsCode.blocked_clients)));
				}
				if (obj.containsKey(ConstantsCode.used_cpu_sys)) {
					redisbean.setUsed_cpu_sys(String.valueOf(obj.get(ConstantsCode.used_cpu_sys)));
				}
				if (obj.containsKey(ConstantsCode.used_cpu_user)) {
					redisbean.setUsed_cpu_user(String.valueOf(obj.get(ConstantsCode.used_cpu_user)));
				}
				if (obj.containsKey(ConstantsCode.used_cpu_sys_children)) {
					redisbean.setUsed_cpu_sys_children(String.valueOf(obj.get(ConstantsCode.used_cpu_sys_children)));
				}
				if (obj.containsKey(ConstantsCode.used_cpu_user_children)) {
					redisbean.setUsed_cpu_user_children(String.valueOf(obj.get(ConstantsCode.used_cpu_user_children)));
				}
				if (obj.containsKey(ConstantsCode.total_connections_received)) {
					redisbean.setTotal_connections_received(
							String.valueOf(obj.get(ConstantsCode.total_connections_received)));
				}
				if (obj.containsKey(ConstantsCode.total_commands_processed)) {
					redisbean.setTotal_commands_processed(
							String.valueOf(obj.get(ConstantsCode.total_commands_processed)));
				}
				if (obj.containsKey(ConstantsCode.instantaneous_ops_per_sec)) {
					redisbean.setInstantaneous_ops_per_sec(
							String.valueOf(obj.get(ConstantsCode.instantaneous_ops_per_sec)));
				}
				if (obj.containsKey(ConstantsCode.rejected_connections)) {
					redisbean.setRejected_connections(String.valueOf(obj.get(ConstantsCode.rejected_connections)));
				}
				if (obj.containsKey(ConstantsCode.expired_keys)) {
					redisbean.setExpired_keys(String.valueOf(obj.get(ConstantsCode.expired_keys)));
				}
				if (obj.containsKey(ConstantsCode.evicted_keys)) {
					redisbean.setEvicted_keys(String.valueOf(obj.get(ConstantsCode.evicted_keys)));
				}
				if (obj.containsKey(ConstantsCode.keyspace_hits)) {
					redisbean.setKeyspace_hits(String.valueOf(obj.get(ConstantsCode.keyspace_hits)));
				}
				if (obj.containsKey(ConstantsCode.keyspace_misses)) {
					redisbean.setKeyspace_misses(String.valueOf(obj.get(ConstantsCode.keyspace_misses)));
				}
				list.add(redisbean);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 4.中间件运行日志-Nginx运行日志
	 * 
	 * @param NginxBean
	 *            bean
	 * @return List<NginxBean>
	 */
	public List<NginxBean> getNginxInfos(NginxBean bean) {
		List<NginxBean> list = new ArrayList<NginxBean>();

		try {
			// 设置查询条件
			BoolQueryBuilder queryBuilder =  QueryBuilders.boolQuery();
			
			queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(ConstantsCode.host_ip, bean.getHost_ip()));

			queryBuilder = queryBuilder.must(
					QueryBuilders.rangeQuery("time").from(bean.getBeginTime().getTime()).to(bean.getEndTime().getTime()));

			logger.info(queryBuilder.toString());

			SearchResponse response = transportClient.prepareSearch(elasticConfig.getSystemInfoIndex()) // 设置搜索的索引
					.setTypes(elasticConfig.getNginxType()) // 设置搜索类型
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(queryBuilder).execute().actionGet();

			for (SearchHit hit : response.getHits().getHits()) {
				Map<String, Object> obj = hit.getSource();
				NginxBean nginxbean = new NginxBean();

				if (obj.containsKey(ConstantsCode.log_type)) {
					nginxbean.setLog_type(String.valueOf(obj.get(ConstantsCode.log_type)));
				}
				if (obj.containsKey(ConstantsCode.host_ip)) {
					nginxbean.setHost_ip(String.valueOf(obj.get(ConstantsCode.host_ip)));
				}
				if (obj.containsKey(ConstantsCode.time)) {
					String time = DateFormatUtil.DateFormatGMT(String.valueOf(obj.get(ConstantsCode.time)),
							DateFormatUtil.GTMFormatMilSSS, DateFormatUtil.DateFormatMil);
					nginxbean.setTime(time);
				}
				if (obj.containsKey(ConstantsCode.accepts)) {
					nginxbean.setAccepts(String.valueOf(obj.get(ConstantsCode.accepts)));
				}
				if (obj.containsKey(ConstantsCode.active)) {
					nginxbean.setActive(String.valueOf(obj.get(ConstantsCode.active)));
				}
				if (obj.containsKey(ConstantsCode.reading)) {
					nginxbean.setReading(String.valueOf(obj.get(ConstantsCode.reading)));
				}
				if (obj.containsKey(ConstantsCode.waiting)) {
					nginxbean.setWaiting(String.valueOf(obj.get(ConstantsCode.waiting)));
				}
				if (obj.containsKey(ConstantsCode.writing)) {
					nginxbean.setWriting(String.valueOf(obj.get(ConstantsCode.writing)));
				}
				if (obj.containsKey(ConstantsCode.handled)) {
					nginxbean.setHandled(String.valueOf(obj.get(ConstantsCode.handled)));
				}
				if (obj.containsKey(ConstantsCode.requests)) {
					nginxbean.setRequests(String.valueOf(obj.get(ConstantsCode.requests)));
				}

				list.add(nginxbean);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}


	/**
	 * 2.HOST主机日志
	 * 
	 * @param HostBean
	 *            bean
	 * @return List<HostBean>
	 */
	public List<HostMonitorBean> getHostMonitorInfo(DateRange dateRange, String hostIp) {

		List<HostMonitorBean> list = new ArrayList<HostMonitorBean>();
		DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("timeAgg");
		// 定义分组的日期字段
		dateAgg.field("time");
		dateAgg.timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00")));
		// dateAgg.dateHistogramInterval(DateHistogramInterval.MINUTE);
		dateAgg.interval(dateRange.getInterval());
		dateAgg.format(dateRange.getFormat());
		dateAgg.extendedBounds(new ExtendedBounds(dateRange.getStart().getTime(), dateRange.getEnd().getTime()));

		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.processor_load_5).field(ConstantsCode.processor_load_5));

		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.available_memory).field(ConstantsCode.available_memory));

		dateAgg.subAggregation(AggregationBuilders.stats(ConstantsCode.total_memory).field(ConstantsCode.total_memory));

		dateAgg.subAggregation(AggregationBuilders.stats(ConstantsCode.incoming).field(ConstantsCode.incoming));

		dateAgg.subAggregation(AggregationBuilders.stats(ConstantsCode.outgoing).field(ConstantsCode.outgoing));

		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.free_disk_per).field(ConstantsCode.free_disk_per));

		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

		queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(ConstantsCode.host_ip, hostIp));

		queryBuilder = queryBuilder.must(
				QueryBuilders.rangeQuery("time").from(dateRange.getStart().getTime()).to(dateRange.getEnd().getTime()));

		logger.info(queryBuilder.toString());

		SearchResponse response = transportClient.prepareSearch(elasticConfig.getSystemInfoIndex())
				.setTypes(elasticConfig.getHostType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.addAggregation(dateAgg).setQuery(queryBuilder).addSort(ConstantsCode.time, SortOrder.DESC).execute()
				.actionGet();
		if (response == null) {
			return null;
		}
		Aggregations aggregations = response.getAggregations();
		if (aggregations == null) {
			return null;
		}
		InternalDateHistogram inDhGram = aggregations.get("timeAgg");
		for (Histogram.Bucket bucket : inDhGram.getBuckets()) {
			HostMonitorBean bean = new HostMonitorBean();
			bean.setKey(bucket.getKeyAsString());
			Aggregations aggregationsStat = bucket.getAggregations();
			Stats processor_load_5 = aggregationsStat.get(ConstantsCode.processor_load_5);

			double processor_load_5_Avg = 0.00;
			double processor_load_5_Max = 0.00;
			double processor_load_5_Min = 0.00;
			if (processor_load_5.getCount() > 0) {
				processor_load_5_Avg = Double.isNaN(processor_load_5.getAvg()) ? 0 : processor_load_5.getAvg() * 100;
				processor_load_5_Max = Double.isNaN(processor_load_5.getMax()) ? 0 : processor_load_5.getMax() * 100;
				processor_load_5_Min = Double.isNaN(processor_load_5.getMin()) ? 0 : processor_load_5.getMin() * 100;
			}

			bean.setLoad5Avg(Double.valueOf(df.format(processor_load_5_Avg)));
			bean.setLoad5Max(Double.valueOf(df.format(processor_load_5_Max)));
			bean.setLoad5Min(Double.valueOf(df.format(processor_load_5_Min)));

			Stats available_memory = aggregationsStat.get(ConstantsCode.available_memory);
			Stats total_memory = aggregationsStat.get(ConstantsCode.total_memory);

			double total_memory_disk = 0.00;
			double available_memory_avg = 0.00;
			double available_memory_min = 0.00;
			double available_memory_max = 0.00;

			if (total_memory.getCount() > 0) {
				total_memory_disk = Double.isNaN(total_memory.getAvg()) ? 0 : total_memory.getAvg();
			}

			if (available_memory.getCount() > 0) {
				available_memory_avg = Double.isNaN(available_memory.getAvg()) ? 0 : available_memory.getAvg();
				available_memory_min = Double.isNaN(available_memory.getMin()) ? 0 : available_memory.getMin();
				available_memory_max = Double.isNaN(available_memory.getMax()) ? 0 : available_memory.getMax();
			}

			double used_memoryAvg = total_memory_disk - available_memory_avg > 0
					? total_memory_disk - available_memory_avg : 0;
			double used_memoryMax = total_memory_disk - available_memory_min > 0
					? total_memory_disk - available_memory_min : 0;
			double used_memoryMin = total_memory_disk - available_memory_max > 0
					? total_memory_disk - available_memory_max : 0;

			if (total_memory_disk > 0) {
				bean.setuMemoryAvg(Double.valueOf(df.format(used_memoryAvg * 100 / total_memory_disk)));
				bean.setuMemoryMax(Double.valueOf(df.format(used_memoryMax * 100 / total_memory_disk)));
				bean.setuMemoryMin(Double.valueOf(df.format(used_memoryMin * 100 / total_memory_disk)));
			}

			Stats incoming = aggregationsStat.get(ConstantsCode.incoming);

			double incoming_Avg = 0.00;
			double incoming_Max = 0.00;
			double incoming_Min = 0.00;

			if (incoming.getCount() > 0) {
				incoming_Avg = Double.isNaN(incoming.getAvg()) ? 0 : incoming.getAvg();
				incoming_Max = Double.isNaN(incoming.getMax()) ? 0 : incoming.getMax();
				incoming_Min = Double.isNaN(incoming.getMin()) ? 0 : incoming.getMin();
			}

			bean.setInAvg(Double.valueOf(df.format(incoming_Avg / 1024)));
			bean.setInMax(Double.valueOf(df.format(incoming_Max / 1024)));
			bean.setInMin(Double.valueOf(df.format(incoming_Min / 1024)));

			Stats outgoing = aggregationsStat.get(ConstantsCode.outgoing);

			double outgoing_Avg = 0.00;
			double outgoing_Max = 0.00;
			double outgoing_Min = 0.00;

			if (outgoing.getCount() > 0) {
				outgoing_Avg = Double.isNaN(outgoing.getAvg()) ? 0 : outgoing.getAvg();
				outgoing_Max = Double.isNaN(outgoing.getMax()) ? 0 : outgoing.getMax();
				outgoing_Min = Double.isNaN(outgoing.getMin()) ? 0 : outgoing.getMin();
			}

			bean.setOutAvg(Double.valueOf(df.format(outgoing_Avg / 1024)));
			bean.setOutMax(Double.valueOf(df.format(outgoing_Max / 1024)));
			bean.setOutMin(Double.valueOf(df.format(outgoing_Min / 1024)));

			Stats free_disk_per = aggregationsStat.get(ConstantsCode.free_disk_per);

			double free_disk_per_Avg = 0.00;
			double free_disk_per_Max = 0.00;
			double free_disk_per_Min = 0.00;

			if (free_disk_per.getCount() > 0) {
				free_disk_per_Avg = Double.isNaN(free_disk_per.getAvg()) ? 0 : free_disk_per.getAvg();
				free_disk_per_Max = Double.isNaN(free_disk_per.getMax()) ? 0 : free_disk_per.getMax();
				free_disk_per_Min = Double.isNaN(free_disk_per.getMin()) ? 0 : free_disk_per.getMin();

			}
			bean.setFreeDiskAvg(Double.valueOf(df.format(free_disk_per_Avg)));
			bean.setFreeDiskMax(Double.valueOf(df.format(free_disk_per_Max)));
			bean.setFreeDiskMin(Double.valueOf(df.format(free_disk_per_Min)));

			list.add(bean);
		}

		return list;

	}
	
	
	public List<OSBean> getOsMonitorInfo(DateRange dateRange, String hostIp) {

		List<OSBean> list = new ArrayList<OSBean>();
		DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("timeAgg");
		// 定义分组的日期字段
		dateAgg.field("time");
		dateAgg.timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00")));
		// dateAgg.dateHistogramInterval(DateHistogramInterval.MINUTE);
		dateAgg.interval(dateRange.getInterval());
		dateAgg.format(dateRange.getFormat());
		dateAgg.extendedBounds(new ExtendedBounds(dateRange.getStart().getTime(), dateRange.getEnd().getTime()));

		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.sys_processes).field(ConstantsCode.sys_processes));

		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.running_processes).field(ConstantsCode.running_processes));


		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

		queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(ConstantsCode.host_ip, hostIp));

		queryBuilder = queryBuilder.must(
				QueryBuilders.rangeQuery("time").from(dateRange.getStart().getTime()).to(dateRange.getEnd().getTime()));

		logger.info(queryBuilder.toString());

		SearchResponse response = transportClient.prepareSearch(elasticConfig.getSystemInfoIndex())
				.setTypes(elasticConfig.getOsType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.addAggregation(dateAgg).setQuery(queryBuilder).addSort(ConstantsCode.time, SortOrder.DESC).execute()
				.actionGet();
		if (response == null) {
			return null;
		}
		Aggregations aggregations = response.getAggregations();
		if (aggregations == null) {
			return null;
		}
		InternalDateHistogram inDhGram = aggregations.get("timeAgg");
		for (Histogram.Bucket bucket : inDhGram.getBuckets()) {
			OSBean bean = new OSBean();
			bean.setKey(bucket.getKeyAsString());
			Aggregations aggregationsStat = bucket.getAggregations();
			Stats sys_processes = aggregationsStat.get(ConstantsCode.sys_processes);

			int sys_processes_Max = 0;
			if (sys_processes.getCount() > 0) {
				sys_processes_Max = (int) (Double.isNaN(sys_processes.getMax()) ? 0 : sys_processes.getMax());
			}

			bean.setSys_processes(String.valueOf(sys_processes_Max));

			Stats running_processes = aggregationsStat.get(ConstantsCode.running_processes);
			
			int running_processes_Max = 0;
			
			if (running_processes.getCount() > 0) {
				running_processes_Max = (int) (Double.isNaN(running_processes.getMax()) ? 0 : running_processes.getMax());
			}
			bean.setRunning_processes(String.valueOf(running_processes_Max));

			list.add(bean);
		}

		return list;
	}
	
	
	
	public List<NginxBean> getNginxMonitorInfo(DateRange dateRange, String hostIp) {

		List<NginxBean> list = new ArrayList<NginxBean>();
		DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("timeAgg");
		// 定义分组的日期字段
		dateAgg.field("time");
		dateAgg.timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00")));
		// dateAgg.dateHistogramInterval(DateHistogramInterval.MINUTE);
		dateAgg.interval(dateRange.getInterval());
		dateAgg.format(dateRange.getFormat());
		dateAgg.extendedBounds(new ExtendedBounds(dateRange.getStart().getTime(), dateRange.getEnd().getTime()));

		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.accepts).field(ConstantsCode.accepts));

		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.active).field(ConstantsCode.active));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.reading).field(ConstantsCode.reading));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.waiting).field(ConstantsCode.waiting));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.writing).field(ConstantsCode.writing));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.handled).field(ConstantsCode.handled));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.requests).field(ConstantsCode.requests));


		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

		queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(ConstantsCode.host_ip, hostIp));

		queryBuilder = queryBuilder.must(
				QueryBuilders.rangeQuery("time").from(dateRange.getStart().getTime()).to(dateRange.getEnd().getTime()));

		logger.info(queryBuilder.toString());

		SearchResponse response = transportClient.prepareSearch(elasticConfig.getSystemInfoIndex())
				.setTypes(elasticConfig.getNginxType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.addAggregation(dateAgg).setQuery(queryBuilder).addSort(ConstantsCode.time, SortOrder.DESC).execute()
				.actionGet();
		if (response == null) {
			return null;
		}
		Aggregations aggregations = response.getAggregations();
		if (aggregations == null) {
			return null;
		}
		InternalDateHistogram inDhGram = aggregations.get("timeAgg");
		for (Histogram.Bucket bucket : inDhGram.getBuckets()) {
			NginxBean bean = new NginxBean();
			bean.setKey(bucket.getKeyAsString());
			Aggregations aggregationsStat = bucket.getAggregations();
			
			Stats accepts = aggregationsStat.get(ConstantsCode.accepts);
			int accepts_Max = 0;
			if (accepts.getCount() > 0) {
				accepts_Max = (int) (Double.isNaN(accepts.getMax()) ? 0 : accepts.getMax());
			}
			bean.setAccepts(String.valueOf(accepts_Max));

			Stats active = aggregationsStat.get(ConstantsCode.active);
			int active_Max = 0;
			if (active.getCount() > 0) {
				active_Max = (int) (Double.isNaN(active.getMax()) ? 0 : active.getMax());
			}
			bean.setActive(String.valueOf(active_Max));
			
			
			Stats reading = aggregationsStat.get(ConstantsCode.reading);
			int reading_Max = 0;
			if (reading.getCount() > 0) {
				reading_Max = (int) (Double.isNaN(reading.getMax()) ? 0 : reading.getMax());
			}
			bean.setReading(String.valueOf(reading_Max));
			
			Stats waiting = aggregationsStat.get(ConstantsCode.waiting);
			int waiting_Max = 0;
			if (waiting.getCount() > 0) {
				waiting_Max = (int) (Double.isNaN(waiting.getMax()) ? 0 : waiting.getMax());
			}
			bean.setWaiting(String.valueOf(waiting_Max));
			
			
			Stats writing = aggregationsStat.get(ConstantsCode.writing);
			int writing_Max = 0;
			if (writing.getCount() > 0) {
				writing_Max = (int) (Double.isNaN(writing.getMax()) ? 0 : writing.getMax());
			}
			bean.setWriting(String.valueOf(writing_Max));
			
			
			Stats handled = aggregationsStat.get(ConstantsCode.handled);
			int handled_Max = 0;
			if (handled.getCount() > 0) {
				handled_Max = (int) (Double.isNaN(handled.getMax()) ? 0 : handled.getMax());
			}
			bean.setHandled(String.valueOf(handled_Max));
			
			
			Stats requests = aggregationsStat.get(ConstantsCode.requests);
			int requests_Max = 0;
			if (requests.getCount() > 0) {
				requests_Max = (int) (Double.isNaN(requests.getMax()) ? 0 : requests.getMax());
			}
			bean.setRequests(String.valueOf(requests_Max));

			list.add(bean);
		}

		return list;

	}
	
	
	
	public List<RedisBean> getRedisMonitorInfo(DateRange dateRange, String hostIp) {

		List<RedisBean> list = new ArrayList<RedisBean>();
		DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("timeAgg");
		// 定义分组的日期字段
		dateAgg.field("time");
		dateAgg.timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00")));
		// dateAgg.dateHistogramInterval(DateHistogramInterval.MINUTE);
		dateAgg.interval(dateRange.getInterval());
		dateAgg.format(dateRange.getFormat());
		dateAgg.extendedBounds(new ExtendedBounds(dateRange.getStart().getTime(), dateRange.getEnd().getTime()));

		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.mem_fragmentation_ratio).field(ConstantsCode.mem_fragmentation_ratio));

		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.used_cpu_sys).field(ConstantsCode.used_cpu_sys));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.used_cpu_user).field(ConstantsCode.used_cpu_user));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.used_cpu_sys_children).field(ConstantsCode.used_cpu_sys_children));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.used_cpu_user_children).field(ConstantsCode.used_cpu_user_children));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.total_connections_received).field(ConstantsCode.total_connections_received));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.connected_clients).field(ConstantsCode.connected_clients));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.rejected_connections).field(ConstantsCode.rejected_connections));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.expired_keys).field(ConstantsCode.expired_keys));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.evicted_keys).field(ConstantsCode.evicted_keys));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.keyspace_hits).field(ConstantsCode.keyspace_hits));
		
		dateAgg.subAggregation(
				AggregationBuilders.stats(ConstantsCode.keyspace_misses).field(ConstantsCode.keyspace_misses));

		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

		queryBuilder = queryBuilder.must(QueryBuilders.matchQuery(ConstantsCode.host_ip, hostIp));

		queryBuilder = queryBuilder.must(
				QueryBuilders.rangeQuery("time").from(dateRange.getStart().getTime()).to(dateRange.getEnd().getTime()));

		logger.info(queryBuilder.toString());

		SearchResponse response = transportClient.prepareSearch(elasticConfig.getSystemInfoIndex())
				.setTypes(elasticConfig.getRedisType()).setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.addAggregation(dateAgg).setQuery(queryBuilder).addSort(ConstantsCode.time, SortOrder.DESC).execute()
				.actionGet();
		if (response == null) {
			return null;
		}
		Aggregations aggregations = response.getAggregations();
		if (aggregations == null) {
			return null;
		}
		InternalDateHistogram inDhGram = aggregations.get("timeAgg");
		for (Histogram.Bucket bucket : inDhGram.getBuckets()) {
			RedisBean bean = new RedisBean();
			bean.setKey(bucket.getKeyAsString());
			Aggregations aggregationsStat = bucket.getAggregations();
			
			Stats mem_fragmentation_ratio = aggregationsStat.get(ConstantsCode.mem_fragmentation_ratio);
			double mem_fragmentation_ratio_Max = 0.00;
			if (mem_fragmentation_ratio.getCount() > 0) {
				mem_fragmentation_ratio_Max = (Double.isNaN(mem_fragmentation_ratio.getMax()) ? 0 : mem_fragmentation_ratio.getMax());
			}
			bean.setMem_fragmentation_ratio(df.format(mem_fragmentation_ratio_Max));
			
			
			Stats used_cpu_sys = aggregationsStat.get(ConstantsCode.used_cpu_sys);
			double used_cpu_sys_Max = 0.00;
			if (used_cpu_sys.getCount() > 0) {
				used_cpu_sys_Max = (Double.isNaN(used_cpu_sys.getMax()) ? 0 : used_cpu_sys.getMax());
			}
			bean.setUsed_cpu_sys(df.format(used_cpu_sys_Max));
			
			
			Stats used_cpu_user = aggregationsStat.get(ConstantsCode.used_cpu_user);
			double used_cpu_user_Max = 0.00;
			if (used_cpu_user.getCount() > 0) {
				used_cpu_user_Max = (Double.isNaN(used_cpu_user.getMax()) ? 0 : used_cpu_user.getMax());
			}
			bean.setUsed_cpu_user(df.format(used_cpu_user_Max));
			
			
			Stats used_cpu_sys_children = aggregationsStat.get(ConstantsCode.used_cpu_sys_children);
			double used_cpu_sys_children_Max = 0.00;
			if (used_cpu_sys_children.getCount() > 0) {
				used_cpu_sys_children_Max = (Double.isNaN(used_cpu_sys_children.getMax()) ? 0 : used_cpu_sys_children.getMax());
			}
			bean.setUsed_cpu_sys_children(df.format(used_cpu_sys_children_Max));
			
			
			Stats used_cpu_user_children = aggregationsStat.get(ConstantsCode.used_cpu_user_children);
			double used_cpu_user_children_Max = 0.00;
			if (used_cpu_user_children.getCount() > 0) {
				used_cpu_user_children_Max = (Double.isNaN(used_cpu_user_children.getMax()) ? 0 : used_cpu_user_children.getMax());
			}
			bean.setUsed_cpu_user_children(df.format(used_cpu_user_children_Max));
			
			Stats total_connections_received = aggregationsStat.get(ConstantsCode.total_connections_received);
			int total_connections_received_Max = 0;
			if (total_connections_received.getCount() > 0) {
				total_connections_received_Max = (int) (Double.isNaN(total_connections_received.getMax()) ? 0 : total_connections_received.getMax());
			}
			bean.setTotal_connections_received(String.valueOf(total_connections_received_Max));
			
			Stats connected_clients = aggregationsStat.get(ConstantsCode.connected_clients);
			int connected_clients_Max = 0;
			if (connected_clients.getCount() > 0) {
				connected_clients_Max = (int) (Double.isNaN(connected_clients.getMax()) ? 0 : connected_clients.getMax());
			}
			bean.setConnected_clients(String.valueOf(connected_clients_Max));
			
			
			Stats rejected_connections = aggregationsStat.get(ConstantsCode.rejected_connections);
			int rejected_connections_Max = 0;
			if (rejected_connections.getCount() > 0) {
				rejected_connections_Max = (int) (Double.isNaN(rejected_connections.getMax()) ? 0 : rejected_connections.getMax());
			}
			bean.setRejected_connections(String.valueOf(rejected_connections_Max));
			
			
			Stats expired_keys = aggregationsStat.get(ConstantsCode.expired_keys);
			int expired_keys_Max = 0;
			if (expired_keys.getCount() > 0) {
				expired_keys_Max = (int) (Double.isNaN(expired_keys.getMax()) ? 0 : expired_keys.getMax());
			}
			bean.setExpired_keys(String.valueOf(expired_keys_Max));
			
			Stats evicted_keys = aggregationsStat.get(ConstantsCode.evicted_keys);
			int evicted_keys_Max = 0;
			if (evicted_keys.getCount() > 0) {
				evicted_keys_Max = (int) (Double.isNaN(evicted_keys.getMax()) ? 0 : evicted_keys.getMax());
			}
			bean.setEvicted_keys(String.valueOf(evicted_keys_Max));
			
			
			Stats keyspace_hits = aggregationsStat.get(ConstantsCode.keyspace_hits);
			int keyspace_hits_Max = 0;
			if (keyspace_hits.getCount() > 0) {
				keyspace_hits_Max = (int) (Double.isNaN(keyspace_hits.getMax()) ? 0 : keyspace_hits.getMax());
			}
			bean.setKeyspace_hits(String.valueOf(keyspace_hits_Max));
			
			
			Stats keyspace_misses = aggregationsStat.get(ConstantsCode.keyspace_misses);
			int keyspace_misses_Max = 0;
			if (keyspace_misses.getCount() > 0) {
				keyspace_misses_Max = (int) (Double.isNaN(keyspace_misses.getMax()) ? 0 : keyspace_misses.getMax());
			}
			bean.setKeyspace_misses(String.valueOf(keyspace_misses_Max));
			
			list.add(bean);

		}

		return list;

	}
}
