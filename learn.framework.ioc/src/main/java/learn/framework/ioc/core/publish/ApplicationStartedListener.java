package learn.framework.ioc.core.publish;


/**
 * 观察者接口，用于接收应用启动事件并处理
 *
 * @author: cw
 * @since:
 * @version: v0.1
 *
 * 修改记录：
 * 时间      修改人员    修改内容
 * ------------------------------
 */
public interface ApplicationStartedListener<T> {
    void onApplicationStarted(T event);
}