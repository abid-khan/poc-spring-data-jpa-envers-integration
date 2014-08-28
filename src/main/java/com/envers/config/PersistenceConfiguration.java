package com.envers.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.envers.strategy.ValidityAuditStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author abidk
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.envers.repository", repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableAspectJAutoProxy
@Import({ AppConfig.class })
public class PersistenceConfiguration {

	@Autowired
	private Environment environment;

	@Bean
	public AbstractEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(this.dataSource());
		AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setGenerateDdl(true);
		adapter.setDatabase(Database.MYSQL);
		entityManagerFactoryBean.setJpaVendorAdapter(adapter);
		entityManagerFactoryBean.setJpaPropertyMap(this.jpaPropertyMap());
		entityManagerFactoryBean.setPackagesToScan("com.envers.entity");
		return entityManagerFactoryBean;
	}

	@Bean
	public Map<String, ?> jpaPropertyMap() {
		Map<String, Object> jpaPropertyMap = new HashMap<String, Object>();
		jpaPropertyMap.put("hibernate.show_sql", Boolean
				.parseBoolean(environment.getProperty("hibernate.show_sql")));
		jpaPropertyMap.put("hibernate.format_sql", Boolean
				.parseBoolean(environment.getProperty("hibernate.format_sql")));
		jpaPropertyMap.put("hibernate.ejb.naming_strategy",
				"org.hibernate.cfg.ImprovedNamingStrategy");
		jpaPropertyMap.put("hibernate.connection.charSet", "UTF-8");
		jpaPropertyMap.put("hibernate.generate_ddl",
				environment.getProperty("hibernate.generate_ddl"));
		jpaPropertyMap.put("hibernate.dialect",
				environment.getProperty("hibernate.dialect"));
		jpaPropertyMap.put("org.hibernate.envers.audit_strategy",
				ValidityAuditStrategy.class.getName());
		return jpaPropertyMap;
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(environment
				.getProperty("database.driver"));
		dataSource.setUrl(environment.getProperty("database.url"));
		dataSource.setUsername(environment.getProperty("database.user"));
		dataSource.setPassword(environment.getProperty("database.password"));
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		// jpaTransactionManager.setEntityManagerFactory(this
		// .entityManagerFactory().getObject());
		return jpaTransactionManager;

	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}


	@Bean
	public AuditorAwareImpl auditorAwareImpl() {
		return new AuditorAwareImpl();

	}
}