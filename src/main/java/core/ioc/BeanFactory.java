package core.ioc;

public interface BeanFactory {

    /**
     * 获取bean
     * @param name bean的名字
     * @return bean 实例
     */
    Object getBean(String name) throws Exception;
}
