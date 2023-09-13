package learn.framework.ioc.factory.processor;

import learn.framework.ioc.Constant;
import learn.framework.ioc.ElementDefinition;
import learn.framework.ioc.MethodMetadata;
import learn.framework.ioc.annotaion.Element;
import learn.framework.ioc.annotaion.Scope;
import learn.framework.ioc.core.supplier.MethodInstanceSupplier;
import learn.framework.ioc.execption.ElementException;
import learn.framework.ioc.factory.DefaultElementFactory;
import learn.framework.ioc.factory.DefaultElementFactoryImpl;
import learn.framework.ioc.type.classreading.CachingMetadataReaderFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;


/**
 * ElementAnnotationPostProcessor 类是一个后置处理器。
 * 作用于处理基于注解的 element 定义。在 容器启动时，
 * ElementAnnotationPostProcessor 会扫描所有的 element 定义，
 * 查找带有@Element注解的 element 定义，并将其注册到容器中。
 * <p>
 *     这个后处理器需要搭配 {@link ConfigurationAnnotationPostProcessor} 一起使用。
 * </p>
 * <p>原型来自于之前学习 @Bean 注解实现的BeanAnnotationPostProcessor </p>
 * @author: cw
 * @since:
 * @version: v0.1
 *
 * 修改记录：
 * 时间      修改人员    修改内容
 * ------------------------------
 */
public class ElementAnnotationPostProcessor implements ElementFactoryPostProcessor {


    @Override
    public void postProcessElementFactory(DefaultElementFactory defaultElementFactory) {
        DefaultElementFactoryImpl elementFactory = (DefaultElementFactoryImpl) defaultElementFactory;
        CachingMetadataReaderFactory readerFactory = elementFactory.getCachingMetadataReaderFactory();
        Map<String, MethodMetadata> methodMetadataMap = readerFactory.getMethodMetadataMap();
        Collection<MethodMetadata> methods = methodMetadataMap.values();
        for (MethodMetadata metadata : methods) {
            Method method = metadata.getMethod();

            // 。。 Object[] paramElements = this.dsd(metadata, elementFactory);
            Parameter[] parameters = metadata.getParameters();
            Object[] paramElements = new Object[parameters.length];

            // 2. 获取实例
            try {
                Object element = elementFactory.getElement(metadata.getDeclaringClass());

                // 执行函数 获取 element 结果
                // Object result = metadata.getMethod().invoke(element, paramElements);

                // 3. 完成注入
                Element elementAnnotation = method.getAnnotation(Element.class);
                Scope scopeAnnotation = method.getAnnotation(Scope.class);
                ElementDefinition elementDefinition = new ElementDefinition();

                // 检查并初始化作用域
                if (scopeAnnotation == null || Constant.SCOPE_SINGLETON.equals(scopeAnnotation.value())) {
                    // elementFactory.createSingletonElement(elementAnnotation.value(), result);
                    elementDefinition.setScope(Constant.SCOPE_SINGLETON);
                } else {
                    elementDefinition.setScope(Constant.SCOPE_PROTOTYPE);
                }

                // 组装方法实现器
                MethodInstanceSupplier methodInstanceSupplier = new MethodInstanceSupplier(element, method, paramElements, elementAnnotation);
                elementDefinition.setInstanceSupplier(methodInstanceSupplier);
                elementDefinition.setType(method.getReturnType());
                elementDefinition.setElementName(elementAnnotation.value());
                elementFactory.registerElementDefinition(elementAnnotation.value(), elementDefinition);

            } catch (Exception e) {
                e.printStackTrace();
                throw new ElementException(String.format("%s : initialize element err", getClass()), e);
            }
        }
    }
}
