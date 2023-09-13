package learn.framework.ioc.core.analysis;

import learn.framework.ioc.Constant;
import learn.framework.ioc.ElementDefinition;
import learn.framework.ioc.annotaion.Component;
import learn.framework.ioc.annotaion.Scope;
import learn.framework.ioc.factory.DefaultElementFactory;
import learn.framework.ioc.factory.processor.ElementPostProcessor;
import learn.framework.ioc.util.ClassUtil;

import java.beans.Introspector;

/**
 * Component 注解解析器
 *
 * <p>
 * @author: cw
 * @since: 2023/7/25 21:21
 * @version: v0.1
 * <p>
 * 修改记录：
 * 时间      修改人员    修改内容
 * ------------------------------
 */
public class ComponentAnnotationClassAnalysis implements ClassAnalysis {
    @Override
    public boolean isAnalysis(Class<?> scanClass) {
        return scanClass.isAnnotationPresent(Component.class);
    }

    @Override
    public void analysis(Class<?> scanClass, DefaultElementFactory defaultElementFactory) {

        // 判断这个类是否为特殊类： bean 初始化后处理器
        if (ClassUtil.isExtendsInterface(ElementPostProcessor.class, scanClass)) {
            defaultElementFactory.registerExtender(scanClass);
        }

        Component component = scanClass.getAnnotation(Component.class);
        String elementName = component.value();
        if ("".equals(elementName)) {
            // 获取注册 元素名
            elementName = Introspector.decapitalize(scanClass.getSimpleName());
        }

        // 设置作用域
        String scope = Constant.SCOPE_SINGLETON;
        Scope scopeAnnotation = scanClass.getAnnotation(Scope.class);
        if (!(scopeAnnotation == null || "".equals(scopeAnnotation.value()))) {
            scope = scopeAnnotation.value();
        }

        ElementDefinition elementDefinition = new ElementDefinition();
        elementDefinition.setType(scanClass);
        elementDefinition.setScope(scope);
        elementDefinition.setElementName(elementName);
        defaultElementFactory.registerElementDefinition(elementName, elementDefinition);
    }
}
