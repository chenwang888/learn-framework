package learn.framework.ioc.type.classreading;

import learn.framework.ioc.MethodMetadata;
import learn.framework.ioc.aware.ElementFactoryAware;
import learn.framework.ioc.factory.AbstractDefaultElementFactory;
import learn.framework.ioc.factory.ElementFactory;
import learn.framework.ioc.factory.DefaultElementFactory;
import learn.framework.ioc.lang.Nullable;
import learn.framework.ioc.type.io.Resource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 元数据缓存工厂
 * 用于缓存 MethodMetadata 对象，提高元数据读取的性能。
 * 原型自于spring中的同名类，不过在spring中维护的是 `ClassMetadata`，扩展性更好。
 *
 * @author: cw
 * @since:
 * @version: v0.1
 *
 * 修改记录：
 * 时间      修改人员    修改内容
 * ------------------------------
 */
public class CachingMetadataReaderFactory implements ElementFactoryAware {

    /**
     * 缓存方法元数据
     */
    private final Map<String, MethodMetadata> methodMetadataMap = new ConcurrentHashMap<>();


    @Nullable
    private Map<Resource, MetadataReader> metadataReaderCache;


    public Map<String, MethodMetadata> getMethodMetadataMap() {
        return methodMetadataMap;
    }

    /**
     *
     * @param putKey
     * @param val
     */
    public void put(String putKey, MethodMetadata val) {
        this.methodMetadataMap.put(putKey, val);
    }

    public void remove(String removeKey) {
        this.methodMetadataMap.remove(removeKey);
    }

    /**
     * 清空缓存中的元数据。
     */
    public void clearCache() {
        methodMetadataMap.clear();
    }

    @Override
    public void setBeanFactory(ElementFactory elementFactory) {
        AbstractDefaultElementFactory sudokuElementFactory = (AbstractDefaultElementFactory) elementFactory;
        sudokuElementFactory.cachingMetadataReaderFactory = this;
    }
}
