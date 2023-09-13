package learn.framework.ioc.core.publish;

import learn.framework.ioc.Constant;
import learn.framework.ioc.DefaultApplication;
import learn.framework.ioc.ElementDefinition;
import learn.framework.ioc.core.PropertiesLoader;
import learn.framework.ioc.factory.DefaultElementFactory;
import learn.framework.ioc.util.ResourcePathUtil;

import java.beans.Introspector;

/**
 * 应用程序启动前的一个事件，它在应用程序启动过程中非常早期被触发。
 * <ol>
 *     <li>初始化 DefaultApplication：在 ApplicationStartingEvent 事件被触发时，DefaultApplication 实例已经被创建，但它还没有被配置或启动。</li>
 *     <li>加载应用程序的配置文件：在应用程序启动前，容器 会加载应用程序的配置文件，类似于 spring 加载 application.properties 或 application.yml 等文件。
 *          在加载配置文件之前，ApplicationStartingEvent 事件会被触发。
 *      </li>
 * </ol>
 *
 * <p>
 *
 * @author: cw
 * @since: 2023/9/13 20:56
 * @version: v0.1
 * <p>
 * 修改记录：
 * 时间      修改人员    修改内容
 * ------------------------------
 */
public class ApplicationStartingEvent implements ApplicationStartedListener<ApplicationStartedEvent> {


    @Override
    public void onApplicationStarted(ApplicationStartedEvent event) {

        DefaultApplication defaultApplication = event.getDefaultApplication();
        DefaultElementFactory elementFactory = defaultApplication.getElementFactory();


        // 加载配置文件
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        String resourcePath = ResourcePathUtil.getResourcePath();
        propertiesLoader.loadProperties(resourcePath);

        String decapitalize = Introspector.decapitalize(propertiesLoader.getClass().getSimpleName());

        // 将配置加入到工厂
        elementFactory.addSingletonElement(decapitalize, propertiesLoader);


        // 准备环境配置



    }

}
