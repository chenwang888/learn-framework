package learn.framework.ioc.core;

import learn.framework.ioc.core.publish.ApplicationStartedEvent;
import learn.framework.ioc.core.publish.ApplicationStartedListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * <p>
 *
 * @author: cw
 * @since: 2023/9/13 20:44
 * @version: v0.1
 * <p>
 * 修改记录：
 * 时间      修改人员    修改内容
 * ------------------------------
 */
public abstract class AbstractApplicationContext implements ApplicationContext{

    /** 事件 */
    protected final Set<ApplicationStartedListener<ApplicationStartedEvent>> listeners = new LinkedHashSet<>();

    public final void registerListener(ApplicationStartedListener<ApplicationStartedEvent> listener) {
        listeners.add(listener);
    }

    public void publishEvent(ApplicationStartedEvent event) {
        for (ApplicationStartedListener<ApplicationStartedEvent> listener : listeners) {
            listener.onApplicationStarted(event);
        }
    }
}
