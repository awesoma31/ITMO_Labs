package com.cryptoterm.backend.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

@Configuration
public class MqttConfig {
    private static final Logger log = LoggerFactory.getLogger(MqttConfig.class);

    @Value("${mqtt.broker}")
    private String brokerUrl;
    @Value("${mqtt.clientId}")
    private String clientId;
    @Value("${mqtt.username:}")
    private String username;
    @Value("${mqtt.password:}")
    private String password;
    
    // SSL Configuration
    @Value("${mqtt.ssl.enabled:false}")
    private boolean sslEnabled;
    @Value("${mqtt.ssl.trust-store:}")
    private String trustStore;
    @Value("${mqtt.ssl.trust-store-password:}")
    private String trustStorePassword;
    @Value("${mqtt.ssl.key-store:}")
    private String keyStore;
    @Value("${mqtt.ssl.key-store-password:}")
    private String keyStorePassword;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        
        options.setServerURIs(new String[]{brokerUrl});
        
        // Username/Password authentication
        if (username != null && !username.isEmpty()) {
            options.setUserName(username);
        }
        if (password != null && !password.isEmpty()) {
            options.setPassword(password.toCharArray());
        }
        
        // SSL/TLS Configuration
        if (sslEnabled) {
            try {
                log.info("Configuring MQTT SSL/TLS connection");
                SSLContext sslContext = createSSLContext();
                options.setSocketFactory(sslContext.getSocketFactory());
                log.info("MQTT SSL/TLS configured successfully");
            } catch (Exception e) {
                log.error("Failed to configure MQTT SSL/TLS", e);
                throw new RuntimeException("Failed to configure MQTT SSL/TLS", e);
            }
        }
        
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);
        
        factory.setConnectionOptions(options);
        return factory;
    }
    
    /**
     * Create SSL context for MQTT connection
     */
    private SSLContext createSSLContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        
        // Load trust store (CA certificate)
        TrustManagerFactory tmf = null;
        if (trustStore != null && !trustStore.isEmpty()) {
            KeyStore ts = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream(trustStore)) {
                ts.load(fis, trustStorePassword != null ? trustStorePassword.toCharArray() : null);
            }
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);
            log.info("Loaded MQTT trust store from: {}", trustStore);
        }
        
        // Load key store (client certificate) - optional
        KeyManagerFactory kmf = null;
        if (keyStore != null && !keyStore.isEmpty()) {
            KeyStore ks = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream(keyStore)) {
                ks.load(fis, keyStorePassword != null ? keyStorePassword.toCharArray() : null);
            }
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, keyStorePassword != null ? keyStorePassword.toCharArray() : null);
            log.info("Loaded MQTT key store from: {}", keyStore);
        }
        
        sslContext.init(
            kmf != null ? kmf.getKeyManagers() : null,
            tmf != null ? tmf.getTrustManagers() : null,
            null
        );
        
        return sslContext;
    }

    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inboundAdapter(MqttPahoClientFactory factory) {
        // Subscribe to metrics, logs, other_metrics, and all device response topics
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
            clientId + "-in", 
            factory, 
            Topics.METRICS, 
            Topics.LOGS, 
            Topics.OTHER_METRICS,
            "devices/+/responses"  // Subscribe to all device response topics
        );
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(MqttPahoClientFactory factory) {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId + "-out", factory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(1);
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
}


