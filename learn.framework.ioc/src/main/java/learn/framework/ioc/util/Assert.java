package learn.framework.ioc.util;

import learn.framework.ioc.execption.IOCAssertException;
import learn.framework.ioc.lang.Nullable;

/**
 * 参数校验工具类
 *
 * @author: cw
 * @since: 2023/7/25 19:17
 * @version: v0.1
 * <p>
 * 修改记录：
 * 时间      修改人员    修改内容
 * ------------------------------
 */
public class Assert {

    public static void isNull(Object obj, String msg) {

        if (obj == null) {
            throw new IOCAssertException(msg);
        }
    }


    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }


    public static void notNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
