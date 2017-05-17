package com.xwtech.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.net.InetAddress;
import java.util.List;

/**
 * Created by zhangq on 2017/2/14.
 */
public class ESTransportClient implements FactoryBean<TransportClient>, InitializingBean, DisposableBean {

    static Logger logger = LoggerFactory.getLogger(ESTransportClient.class);

    private String clusterName;

    private String discoveryPort;

    private List<String> servers;

    private TransportClient client;

    protected TransportClient create() {

        try {
            if (client == null) {
                //设置client.transport.sniff为true来使客户端去嗅探整个集群的状态,把集群中其它机器的ip地址加到客户端中
                Settings settings = Settings.builder()
                        .put("cluster.name", clusterName)
                        .put("client.transport.sniff", true).build();
                client = new PreBuiltTransportClient(settings);
                for (String server : servers) {
                    logger.debug("esIp {}:{}",server,discoveryPort);
                    client.addTransportAddress(
                            new InetSocketTransportAddress(InetAddress.getByName(server), Integer.valueOf(discoveryPort)));

                }
            }
        } catch (Exception e) {
            logger.warn("es 服务器连接异常！！！", e);
        }
        return client;
    }


    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    @Override
    public void destroy() throws Exception {
        try {
            logger.info("Closing elasticSearch  client");
            if (client != null) {
                client.close();
            }
        } catch (final Exception e) {
            logger.error("Error closing ElasticSearch client: ", e);
        }
    }

    @Override
    public TransportClient getObject() throws Exception {
        return client;
    }

    @Override
    public Class<TransportClient> getObjectType() {
        return TransportClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        create();
    }

    public String getDiscoveryPort() {
        return discoveryPort;
    }

    public void setDiscoveryPort(String discoveryPort) {
        this.discoveryPort = discoveryPort;
    }
}
