package profect.eatcloud.Global.TimeData;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanUtil implements ApplicationContextAware {
	private static ApplicationContext context;
	@Override
	public void setApplicationContext(ApplicationContext ac) {
		context = ac;
	}
	public static <T> T getBean(Class<T> beanClass) {
		return context.getBean(beanClass);
	}
}