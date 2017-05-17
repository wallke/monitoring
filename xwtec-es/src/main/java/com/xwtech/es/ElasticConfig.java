package com.xwtech.es;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by zl on 17/2/16.
 */
@ConfigurationProperties(prefix="elasticsearch")
public class ElasticConfig {
	
	    @Value("${elasticsearch.application.index}")
	    String applicationIndex;
	    
	    @Value("${elasticsearch.application.transactionType}")
	    String transactionType;
	    
	    @Value("${elasticsearch.application.chainType}")
	    String chainType;
	    
	    @Value("${elasticsearch.application.methodType}")
	    String methodType;

        @Value("${elasticsearch.application.appcrash}")
        String appcrash;
	    
	    @Value("${elasticsearch.systemInfo.index}")
	    String systemInfoIndex;
	    
	    @Value("${elasticsearch.systemInfo.osType}")
	    String osType;
	    
	    @Value("${elasticsearch.systemInfo.hostType}")
	    String hostType;
	    
	    @Value("${elasticsearch.systemInfo.redisType}")
	    String redisType;
	    
	    @Value("${elasticsearch.systemInfo.nginxType}")
	    String nginxType;
	    
	    @Value("${elasticsearch.isQueryAll}")
	    Boolean isQueryAll;

		@Value("${elasticsearch.basicConfig.xwIndex}")
		String xwIndex;

		@Value("${elasticsearch.basicConfig.esType}")
		String esType;

		@Value("${elasticsearch.basicConfig.esId}")
		String esId;
		//监听模板
		@Value("${elasticsearch.basicConfig.monitorTemplateType}")
		String monitorTemplateType;

		//监听目标
		@Value("${elasticsearch.basicConfig.monitorTargetType}")
		String monitorTargetType;

		//消息模板
		@Value("${elasticsearch.basicConfig.templateType}")
		String templateType;

		//预警级别
		@Value("${elasticsearch.basicConfig.dictType}")
		String dictType;

		//预警级别ID
		@Value("${elasticsearch.basicConfig.alert_level}")
		String alert_level;

		//统计指标ID
		@Value("${elasticsearch.basicConfig.metrics}")
		String metrics;

		//预警网格
		@Value("${elasticsearch.basicConfig.thresholdType}")
		String thresholdType;

		//干系人账户
		@Value("${elasticsearch.basicConfig.accountType}")
		String accountType;

		//变频规则
		@Value("${elasticsearch.basicConfig.frequencyType}")
		String frequencyType;

		//通知策略
		@Value("${elasticsearch.basicConfig.noticeType}")
		String noticeType;

		//告警方案
		@Value("${elasticsearch.basicConfig.schemeType}")
		String schemeType;

		//统一应用
		@Value("${elasticsearch.basicConfig.updateType}")
		String updateType;

	public String getUpdateType() {
		return updateType;
	}

	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}

	public Boolean getQueryAll() {
		return isQueryAll;
	}

	public void setQueryAll(Boolean queryAll) {
		isQueryAll = queryAll;
	}

	public String getMonitorTemplateType() {
		return monitorTemplateType;
	}

	public void setMonitorTemplateType(String monitorTemplateType) {
		this.monitorTemplateType = monitorTemplateType;
	}

	public String getMonitorTargetType() {
		return monitorTargetType;
	}

	public void setMonitorTargetType(String monitorTargetType) {
		this.monitorTargetType = monitorTargetType;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getDictType() {
		return dictType;
	}

	public void setDictType(String dictType) {
		this.dictType = dictType;
	}

	public String getAlert_level() {
		return alert_level;
	}

	public void setAlert_level(String alert_level) {
		this.alert_level = alert_level;
	}

	public String getMetrics() {
		return metrics;
	}

	public void setMetrics(String metrics) {
		this.metrics = metrics;
	}

	public String getThresholdType() {
		return thresholdType;
	}

	public void setThresholdType(String thresholdType) {
		this.thresholdType = thresholdType;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getFrequencyType() {
		return frequencyType;
	}

	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

	public String getSchemeType() {
		return schemeType;
	}

	public void setSchemeType(String schemeType) {
		this.schemeType = schemeType;
	}

	public String getXwIndex() {
		return xwIndex;
	}

	public void setXwIndex(String xwIndex) {
		this.xwIndex = xwIndex;
	}

	public String getEsType() {
		return esType;
	}

	public void setEsType(String esType) {
		this.esType = esType;
	}

	public String getEsId() {
		return esId;
	}

	public void setEsId(String esId) {
		this.esId = esId;
	}

	public String getApplicationIndex() {
			return applicationIndex;
		}

		public void setApplicationIndex(String applicationIndex) {
			this.applicationIndex = applicationIndex;
		}

		public String getTransactionType() {
			return transactionType;
		}

		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}

		public String getChainType() {
			return chainType;
		}

		public void setChainType(String chainType) {
			this.chainType = chainType;
		}

		public String getMethodType() {
			return methodType;
		}

		public void setMethodType(String methodType) {
			this.methodType = methodType;
		}

		public String getSystemInfoIndex() {
			return systemInfoIndex;
		}

		public void setSystemInfoIndex(String systemInfoIndex) {
			this.systemInfoIndex = systemInfoIndex;
		}

		public String getOsType() {
			return osType;
		}

		public void setOsType(String osType) {
			this.osType = osType;
		}

		public String getHostType() {
			return hostType;
		}

		public void setHostType(String hostType) {
			this.hostType = hostType;
		}

		public String getRedisType() {
			return redisType;
		}

		public void setRedisType(String redisType) {
			this.redisType = redisType;
		}

		public String getNginxType() {
			return nginxType;
		}

		public void setNginxType(String nginxType) {
			this.nginxType = nginxType;
		}

		public Boolean getIsQueryAll() {
			return isQueryAll;
		}

		public void setIsQueryAll(Boolean isQueryAll) {
			this.isQueryAll = isQueryAll;
		}

    public String getAppcrash() {
        return appcrash;
    }

    public void setAppcrash(String appcrash) {
        this.appcrash = appcrash;
    }
}
