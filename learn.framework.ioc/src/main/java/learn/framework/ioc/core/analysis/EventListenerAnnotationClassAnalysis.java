package learn.framework.ioc.core.analysis;

import learn.framework.ioc.aware.ElementFactoryAware;
import learn.framework.ioc.core.resovler.AnnotationInjectResolver;
import learn.framework.ioc.factory.DefaultElementFactory;
import learn.framework.ioc.util.ClassUtil;

/**
 *
 * {@link @EventListener } 注解解析器。
 * <p>
 *
 * @author: cw
 * @since: 2023/9/13 21:05
 * @version: v0.1
 * <p>
 * 修改记录：
 * 时间      修改人员    修改内容
 * ------------------------------
 */
public class EventListenerAnnotationClassAnalysis implements ClassAnalysis {

    @Override
    public boolean isAnalysis(Class<?> scanClass) {
        return ClassUtil.isExtendsInterface(ElementFactoryAware.class, scanClass);
    }

    @Override
    public void analysis(Class<?> scanClass, DefaultElementFactory defaultElementFactory) {

    }
}
