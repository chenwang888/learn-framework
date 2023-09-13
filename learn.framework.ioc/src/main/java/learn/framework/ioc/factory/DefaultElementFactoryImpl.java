package learn.framework.ioc.factory;

import learn.framework.ioc.util.ForeachFunction;

import java.util.Collection;

public class DefaultElementFactoryImpl extends AbstractDefaultElementFactory {

    @Override
    public boolean isExtender(Object obj) {
        return false;
    }

    @Override
    public void foreach(ForeachFunction<Object> function) {

    }

    @Override
    public Collection<Object> getAllExtenderFactory() {
        return null;
    }
}
