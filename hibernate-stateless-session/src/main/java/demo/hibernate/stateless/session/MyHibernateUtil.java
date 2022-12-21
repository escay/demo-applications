package demo.hibernate.stateless.session;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;

public class MyHibernateUtil {
	/**
	 * This example does not use a JPA context. Otherwise we could ask the EntityManager for the session factory.
	 * @return a new manually created SessionFactory instance.
	 */
	public static SessionFactory createSessionFactory() {
		Properties settings = new Properties();

		// Use an in memory database
		settings.put("hibernate.connection.driver_class", "org.h2.Driver");
		settings.put("hibernate.connection.url", "jdbc:h2:mem:test");
		settings.put("hibernate.connection.username", "sa");
		settings.put("hibernate.connection.password", "");
		settings.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		settings.put(AvailableSettings.SHOW_SQL, "true");
		settings.put("hibernate.format_sql", "true");
		settings.put("hibernate.current_session_context_class", "thread"); // Options are jta, thread and managed

		// Bootstrap Hibernate 6:
		// https://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#bootstrap
		StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
		standardServiceRegistryBuilder.applySettings(settings);
		StandardServiceRegistry standardRegistry = standardServiceRegistryBuilder.build();
		MetadataSources metadataSources = new MetadataSources(standardRegistry);
		Metadata metadata = metadataSources.buildMetadata();

		// Build the session factory
		SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
		return sessionFactory;
	}
}
