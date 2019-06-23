package core.ioc;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String beanName,BeanDefinition beanDefinition)throws Exception;
    BeanDefinition getBeanDefinition(String beanName);
    boolean containBeanDefinition(String beanName);
}
