package learn.framework.ioc.core.publish;

import learn.framework.ioc.DefaultApplication;
import learn.framework.ioc.core.ApplicationContext;

/**
 * 事件类来表示应用启动事件
 *
 * @author: cw
 * @since:
 * @version: v0.1
 *
 * 修改记录：
 * 时间      修改人员    修改内容
 * ------------------------------
 */
public class ApplicationStartedEvent {

    // 可以在事件类中定义一些需要传递的数据或者属性
    // ...

    private ApplicationContext applicationContext;

    private DefaultApplication defaultApplication;

    private Class<?> mainApplicationClass;


    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public DefaultApplication getDefaultApplication() {
        return defaultApplication;
    }

    public void setDefaultApplication(DefaultApplication defaultApplication) {
        this.defaultApplication = defaultApplication;
    }

    public Class<?> getMainApplicationClass() {
        return mainApplicationClass;
    }

    public void setMainApplicationClass(Class<?> mainApplicationClass) {
        this.mainApplicationClass = mainApplicationClass;
    }
}