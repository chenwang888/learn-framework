package learn.framework.ioc;

import learn.framework.ioc.core.AbstractApplicationContext;
import learn.framework.ioc.core.ApplicationContext;
import learn.framework.ioc.core.DefaultApplicationContext;
import learn.framework.ioc.core.publish.ApplicationPreparedEvent;
import learn.framework.ioc.core.publish.ApplicationStartedEvent;
import learn.framework.ioc.core.publish.ApplicationStartingEvent;
import learn.framework.ioc.execption.IOCException;
import learn.framework.ioc.factory.DefaultElementFactory;
import learn.framework.ioc.factory.DefaultElementFactoryImpl;

public class DefaultApplication {

	private DefaultElementFactory elementFactory;
	private ApplicationContext applicationContext;
	private DefaultApplication() {}

	public static ApplicationContext run(Class<?> primarySource) {

		DefaultApplication application = new DefaultApplication();
		DefaultElementFactoryImpl defaultElementFactory = new DefaultElementFactoryImpl();
		AbstractApplicationContext applicationContext = new DefaultApplicationContext(primarySource, defaultElementFactory);


		applicationContext.registerListener(new ApplicationStartingEvent());
		applicationContext.registerListener(new ApplicationPreparedEvent());


		application.setElementFactory(defaultElementFactory);
		application.setApplicationContext(applicationContext);

		ApplicationStartedEvent event = new ApplicationStartedEvent();
		event.setMainApplicationClass(primarySource);
		event.setApplicationContext(applicationContext);
		event.setDefaultApplication(application);

		applicationContext.publishEvent(event);

		return applicationContext;
	}

	public void starter() {

		try {
			applicationContext.initialize();
		} catch (Throwable throwable) {
			throw new IOCException(String.format("%s 做容器初始化的过程中发生异常，具体问题位于:", DefaultApplication.class), throwable);
		}

		try {
			applicationContext.refresh();
		} catch (Throwable throwable) {
			throw new IOCException(String.format("%s 在执行 refresh() 时发生异常，问题位于：", DefaultApplication.class), throwable);
		}

	}


	public DefaultElementFactory getElementFactory() {
		return elementFactory;
	}

	public void setElementFactory(DefaultElementFactory elementFactory) {
		this.elementFactory = elementFactory;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
