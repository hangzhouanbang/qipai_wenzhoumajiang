package com.anbang.qipai.wenzhoumajiang;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.anbang.qipai.wenzhoumajiang.conf.FilePathConfig;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.repository.SingletonEntityFactoryImpl;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.disruptor.CoreSnapshotFactory;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.disruptor.ProcessCoreCommandEventHandler;
import com.anbang.qipai.wenzhoumajiang.cqrs.c.service.disruptor.SnapshotJsonUtil;
import com.anbang.qipai.wenzhoumajiang.init.InitProcessor;
import com.dml.users.UserSessionsManager;
import com.highto.framework.ddd.SingletonEntityRepository;

@SpringBootApplication
@EnableScheduling
public class QipaiWenzhoumajiangApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(QipaiWenzhoumajiangApplication.class);
	}

	@Autowired
	private SnapshotJsonUtil snapshotJsonUtil;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private CoreSnapshotFactory coreSnapshotFactory;

	@Autowired
	private FilePathConfig filePathConfig;

	@Bean
	public HttpClient httpClient() {
		return new HttpClient();
	}

	@Bean
	public HttpClient sslHttpClient() {
		return new HttpClient(new SslContextFactory());
	}

	@Bean
	public UserSessionsManager userSessionsManager() {
		return new UserSessionsManager();
	}

	@Bean
	public SingletonEntityRepository singletonEntityRepository() {
		SingletonEntityRepository singletonEntityRepository = new SingletonEntityRepository();
		singletonEntityRepository.setEntityFactory(new SingletonEntityFactoryImpl());
		return singletonEntityRepository;
	}

	@Bean
	public ProcessCoreCommandEventHandler processCoreCommandEventHandler() {
		return new ProcessCoreCommandEventHandler(coreSnapshotFactory, snapshotJsonUtil, filePathConfig);
	}

	@Bean
	public InitProcessor initProcessor() {
		InitProcessor initProcessor = new InitProcessor(httpClient(), sslHttpClient(), snapshotJsonUtil,
				processCoreCommandEventHandler(), singletonEntityRepository(), applicationContext);
		initProcessor.init();
		return initProcessor;
	}

	public static void main(String[] args) {
		SpringApplication.run(QipaiWenzhoumajiangApplication.class, args);
	}
}
