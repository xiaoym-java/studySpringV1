package core.ioc.impl;

import core.exception.BeanDefinitionRegistException;
import core.ioc.BeanDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现线程安全
 */
public class PreBuildBeanFactory extends DefaultBeanFactory{

    private final Log log= LogFactory.getLog(PreBuildBeanFactory.class);
    private List<String> beanNames = new ArrayList<>();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionRegistException {
        super.registerBeanDefinition(beanName, beanDefinition);
        synchronized (beanNames){
            beanNames.add(beanName);
        }
    }

    public void preInstantiateSingletons() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        synchronized (beanNames){
            for(String name:beanNames){
                BeanDefinition bd = this.getBeanDefinition(name);
                if(bd.isSingleton()){
                    this.doGetBean(name);
                    if(log.isDebugEnabled()){
                        log.debug(String.format("preInstantiate: name=%s ,bd=%s",name,bd));
                    }
                }
            }
        }
    }
}
