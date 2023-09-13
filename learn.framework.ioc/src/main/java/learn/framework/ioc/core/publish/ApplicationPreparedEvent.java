package learn.framework.ioc.core.publish;

import learn.framework.ioc.DefaultApplication;
import learn.framework.ioc.core.ApplicationContext;
import learn.framework.ioc.core.DefaultApplicationContext;
import learn.framework.ioc.core.analysis.AnnotationInjectResolverClassAnalysis;
import learn.framework.ioc.core.analysis.ComponentAnnotationClassAnalysis;
import learn.framework.ioc.core.analysis.ConfigurationAnnotationClassAnalysis;
import learn.framework.ioc.core.analysis.ElementFactoryAwareClassAnalysis;
import learn.framework.ioc.core.resovler.AnnotationInjectFactory;
import learn.framework.ioc.factory.DefaultElementFactory;
import learn.framework.ioc.factory.DefaultElementFactoryImpl;
import learn.framework.ioc.factory.processor.ConfigurationAnnotationPostProcessor;
import learn.framework.ioc.factory.processor.ElementAnnotationPostProcessor;
import learn.framework.ioc.factory.processor.ElementFactoryPostProcessorFactory;
import learn.framework.ioc.factory.processor.ElementPostProcessorFactory;

/**
 * 应用程序准备就绪但尚未启动的一个事件。它在应用程序启动过程中的较早阶段被触发。
 * ApplicationPreparedEvent 事件通常用于执行一些在应用程序启动前必须完成的操作。
 * <ol>
 *     <li>
 *         配置应用程序环境：在加载上下文之后，ApplicationPreparedEvent 事件会执行一些操作来配置应用程序的环境。
 *         这包括设置应用程序的默认配置、配置类和配置属性等。
 *     </li>
 *     <li>
 *         注册 一系列PostProcessors：在 ApplicationPreparedEvent 事件期间，
 *         可以注册 PostProcessor，以对应用程序上下文中的 Bean 进行进一步的处理。
 *     </li>
 *     <li>
 *         执行应用程序的初始化逻辑：在 ApplicationPreparedEvent 事件触发之后，
 *         可以执行一些应用程序初始化的逻辑，例如数据库连接、缓存初始化等。
 *     </li>
 * </ol>
 *
 * <p>
 * @author: cw
 * @since: 2023/9/13 21:21
 * @version: v0.1
 * 修改记录：
 * 时间      修改人员    修改内容
 * ------------------------------
 * <p>
 */
public class ApplicationPreparedEvent implements ApplicationStartedListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationStarted(ApplicationStartedEvent event) {

        buildApplicationContext(event);
        buildElementFactory(event);
    }


    private static void buildApplicationContext(ApplicationStartedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        // 注册 处理扫描出的class文件 的解析器
        applicationContext.registerExtender(AnnotationInjectResolverClassAnalysis.class);
        // 注册 ElementFactoryAware 元素工厂回调机制解析器
        applicationContext.registerExtender(ElementFactoryAwareClassAnalysis.class);
        // 注册 @Component 注解解析器
        applicationContext.registerExtender(ComponentAnnotationClassAnalysis.class);
        // 注册 @Configuration 注解解析器
        applicationContext.registerExtender(ConfigurationAnnotationClassAnalysis.class);
    }

    private static void buildElementFactory(ApplicationStartedEvent event) {
        DefaultApplication defaultApplication = event.getDefaultApplication();
        DefaultElementFactory elementFactory = defaultApplication.getElementFactory();
        // 添加依赖注入工厂
        AnnotationInjectFactory.embedElementFactory(elementFactory);
        // 添加工厂后处理器工厂
        ElementFactoryPostProcessorFactory.embedElementFactory(elementFactory);
        // 添加后置处理器
        ElementPostProcessorFactory.embedElementFactory(elementFactory);

        // 添加 工厂 后处理器
        elementFactory.registerExtender(ConfigurationAnnotationPostProcessor.class);
        elementFactory.registerExtender(ElementAnnotationPostProcessor.class);


        // 做完一系列配置，开始启动应用容器
        defaultApplication.starter();
    }
}
