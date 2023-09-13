package learn.framework.ioc.factory;

import learn.framework.ioc.Constant;
import learn.framework.ioc.ElementDefinition;
import learn.framework.ioc.aware.InitializingElement;
import learn.framework.ioc.core.resovler.AnnotationInjectFactory;
import learn.framework.ioc.core.resovler.AnnotationInjectResolver;
import learn.framework.ioc.core.supplier.InstanceSupplier;
import learn.framework.ioc.execption.FoundElementException;
import learn.framework.ioc.factory.processor.ElementFactoryPostProcessor;
import learn.framework.ioc.factory.processor.ElementFactoryPostProcessorFactory;
import learn.framework.ioc.factory.processor.ElementPostProcessor;
import learn.framework.ioc.factory.processor.ElementPostProcessorFactory;
import learn.framework.ioc.type.classreading.CachingMetadataReaderFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * SudokuElementFactory这个框架中的一个核心实现类，
 * 继承自 {@link DefaultElementFactory} 类，它是一个可列表化的 ElementFactory 实现，用于维护和创建 element 实例。
 * 它提供了基本的 element 管理功能，但是不支持 AOP
 *
 * @author: cw
 * @since:
 * @version: v0.1
 *
 * 修改记录：
 * 时间      修改人员    修改内容
 * ------------------------------
 */
public abstract class AbstractDefaultElementFactory implements DefaultElementFactory {

	// 元素描述信息
	private final Map<String, ElementDefinition> elementDefinitionMap = new ConcurrentHashMap<>();

	// 单例对象
	private final Map<String, Object> singletonObjectsMap = new ConcurrentHashMap<>();

	// 元数据缓存工厂
	public CachingMetadataReaderFactory cachingMetadataReaderFactory;

	// 依赖注入扩展工厂
	public AnnotationInjectFactory annotationInjectFactory;
	// 元素处理工厂扩展工厂
	public ElementFactoryPostProcessorFactory elementFactoryPostProcessorFactory;
	// 元素后处理器工厂
	public ElementPostProcessorFactory elementPostProcessorFactory;


	@Override
	public Object getElement(String elementName) {

		ElementDefinition elementDefinition = elementDefinitionMap.get(elementName);
		if (elementDefinition == null) {
			return null;
		}

		// 获取单例
		if (Constant.SCOPE_SINGLETON.equals(elementDefinition.getScope())) {
			Object element = this.singletonObjectsMap.get(elementName);
			if (element == null) {
				element = this.createElement(elementName, elementDefinition);
				singletonObjectsMap.put(elementName, element);
			}
			return element;
		}

		// 获取多例
		if (Constant.SCOPE_PROTOTYPE.equals(elementDefinition.getScope())) {
			return this.createElement(elementName, elementDefinition);
		}

		return singletonObjectsMap.get(elementName);
	}

	@Override
	public <T> T getElement(Class<T> clazz) {

		// 遍历所有通过扫描 加载的元素信息
		for (ElementDefinition value : elementDefinitionMap.values()) {
			if (value.getType().equals(clazz)) {
				return (T) getElement(value.getElementName());
			}
		}

		// 二级检查
		// 这是一个检查问题机制
		for (Object value : singletonObjectsMap.values()) {

			if (value.getClass().equals(clazz)) {
				throw new FoundElementException("这是一个警告。这个类使用了不当的注入方式，请检查这个类：" + clazz);
			}
		}

		throw new FoundElementException("not find " + clazz.toString());
	}


	@Override
	public Object createElement(String elementName, ElementDefinition elementDefinition) {
		Class<?> clazz = elementDefinition.getType();
		try {

			// 添加方法创建
			InstanceSupplier instanceSupplier = elementDefinition.getInstanceSupplier();
			Object generatorElement = instanceSupplier.generator(this);

			// simple impl
			Optional.ofNullable(annotationInjectFactory).ifPresent(
					of -> {
						for (Field declaredField : clazz.getDeclaredFields()) {
							of.foreach(factory -> {
								if (factory.isResolver(declaredField)) {
									try {
										factory.inject(declaredField, generatorElement, this);
									} catch (IllegalAccessException e) {
										throw new RuntimeException(e);
									}
								}
							});
						}
					}
			);

			// 初始化-前
			Optional.ofNullable(elementPostProcessorFactory).ifPresent(of->of.foreach(factory -> factory.postProcessBeforeInitialization(generatorElement, elementName)));

			// 初始化
			if (generatorElement instanceof InitializingElement) {
				((InitializingElement) generatorElement).afterPropertiesSet();
			}

			// 初始化-后
			Optional.ofNullable(elementPostProcessorFactory).ifPresent(of->of.foreach(factory -> factory.postProcessAfterInitialization(generatorElement, elementName)));

			return generatorElement;

		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addSingletonElement(String elementName, Object element) {

		// hint 230913: 对于程序逻辑设计而言，添加一个ElementDefinition会提高注入和查询效率
		ElementDefinition elementDefinition = new ElementDefinition();
		elementDefinition.setType(element.getClass());
		elementDefinition.setScope(Constant.SCOPE_SINGLETON);
		elementDefinition.setElementName(elementName);
		this.addElementDefinition(elementName, elementDefinition);

		this.singletonObjectsMap.put(elementName, element);
	}

	@Override
	public void addElementDefinition(ElementDefinition elementDefinition) {
		this.addElementDefinition(elementDefinition.getElementName(),elementDefinition);
	}

	@Override
	public void addElementDefinition(String elementName,ElementDefinition elementDefinition) {
		this.elementDefinitionMap.put(elementDefinition.getElementName(),elementDefinition);
	}

	public Map<String, ElementDefinition> getElementDefinitionMap() {
		return elementDefinitionMap;
	}

	public Map<String, Object> getSingletonObjectsMap() {
		return singletonObjectsMap;
	}

	public CachingMetadataReaderFactory getCachingMetadataReaderFactory() {
		return cachingMetadataReaderFactory;
	}


	public void addExtenderFactory(ExtenderFactory<?> processorFactory) {

		if (processorFactory instanceof AnnotationInjectFactory) {
			annotationInjectFactory = (AnnotationInjectFactory) processorFactory;
		} else if (processorFactory instanceof ElementFactoryPostProcessorFactory) {
			elementFactoryPostProcessorFactory = (ElementFactoryPostProcessorFactory) processorFactory;
		} else if (processorFactory instanceof ElementPostProcessorFactory) {
			elementPostProcessorFactory = (ElementPostProcessorFactory) processorFactory;
		}
	}

	@Override
	public void registerExtender(Object processor) {

		if (annotationInjectFactory.isExtender(processor)) {
			annotationInjectFactory.registerExtender((AnnotationInjectResolver) processor);
		} else if (elementFactoryPostProcessorFactory.isExtender(processor)) {
			elementFactoryPostProcessorFactory.registerExtender((ElementFactoryPostProcessor) processor);
		} else if (elementPostProcessorFactory.isExtender(processor)) {
			elementPostProcessorFactory.registerExtender((ElementPostProcessor) processor);
		}

	}

	@Override
	public void registerExtender(Class<?> processorClass) {

		if (annotationInjectFactory.isExtender(processorClass)) {
			annotationInjectFactory.registerExtender((Class<? extends AnnotationInjectResolver>) processorClass);
		} else if (elementFactoryPostProcessorFactory.isExtender(processorClass)) {
			elementFactoryPostProcessorFactory.registerExtender((Class<? extends ElementFactoryPostProcessor>) processorClass);
		} else if (elementPostProcessorFactory.isExtender(processorClass)) {
			elementPostProcessorFactory.registerExtender((Class<? extends ElementPostProcessor>) processorClass);
		}
	}

	@Override
	public void removeExtender(Class<?> clazz) {

		if (annotationInjectFactory.isExtender(clazz)) {
			annotationInjectFactory.removeExtender((Class<? extends AnnotationInjectResolver>) clazz);
		} else if (elementFactoryPostProcessorFactory.isExtender(clazz)) {
			elementFactoryPostProcessorFactory.removeExtender((Class<? extends ElementFactoryPostProcessor>) clazz);
		} else if (elementPostProcessorFactory.isExtender(clazz)) {
			elementPostProcessorFactory.removeExtender((Class<? extends ElementPostProcessor>) clazz);
		}
	}

	@Override
	public <T> Collection<T> getExtenderFactory(Class<T> clazz) {

		if (annotationInjectFactory.isExtender(clazz)) {
			return (Collection<T>) annotationInjectFactory.getAllExtenderFactory();
		} else if (elementFactoryPostProcessorFactory.isExtender(clazz)) {
			return (Collection<T>) elementFactoryPostProcessorFactory.getAllExtenderFactory();
		} else if (elementPostProcessorFactory.isExtender(clazz)) {
			return (Collection<T>) elementPostProcessorFactory.getAllExtenderFactory();
		}
		throw new RuntimeException("found empty");
	}

	@Override
	public void destroyExtenderFactory() {
		// 销毁依赖注入扩展工厂
		Optional.ofNullable(annotationInjectFactory).ifPresent(ExtenderFactory::destroy);
		// 销毁元素处理工厂扩展工厂
		Optional.ofNullable(elementFactoryPostProcessorFactory).ifPresent(ExtenderFactory::destroy);
		// 销毁元素后处理器工厂
		Optional.ofNullable(elementPostProcessorFactory).ifPresent(ExtenderFactory::destroy);

		// 销毁元数据缓存工厂
		Optional.ofNullable(cachingMetadataReaderFactory).ifPresent(CachingMetadataReaderFactory::clearCache);
	}
}
