package core.ioc.impl;

import core.ioc.BeanDefinition;
import core.ioc.BeanDefinitionRegistry;
import core.ioc.BeanFactory;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry {

    //存放单例bean的缓存
    private Map<String,Object> beanMap = new ConcurrentHashMap<>(256);

    public Object getBean(String name) throws Exception {
        return null;
    }

    protected Object doGetBean(String beanName) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //验证bean不能为空
        Objects.requireNonNull(beanName,"beanName不能为空");

        //查看是否已经创建，如果已经创建则直接从缓存中取得返回
        Object instance = beanMap.get(beanName);
        if(null!=instance){
            return instance;
        }

        //如果没有创建，则需要创建，
        BeanDefinition bd = this.getBeanDefinition(beanName);
        Objects.requireNonNull(bd,"beanDefinition 不能为空");

        Class<?> type=bd.getBeanClass();
        if(type!=null){
            if(StringUtils.isBlank(bd.getFactoryMethodName())){
                //构造方法来构造对象
                instance =this.createInstanceByConstructor(bd);
            }else{
                //通过静态工厂方法创建对象
                instance=this.createInstanceByStaticFactoryMethod(bd);

            }
        }else{
            //通过工厂bean方式来构造对象
            instance=this.createInstanceByFactoryBean(bd);
        }


        //执行初始化方法，比如说给属性赋值等
        this.doInit(bd,instance);

        //如果是单例，则将bean实例放入缓存中
        if(bd.isSingleton()){
            beanMap.put(beanName,instance);
        }

        return instance;
    }


    /**
     * 通过构造哦方法来构造对象
     * @param bd dean定义
     * @return bean实例
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private Object createInstanceByConstructor(BeanDefinition bd) throws IllegalAccessException, InstantiationException {
        return bd.getBeanClass().newInstance();
    }

    /**
     * 通过静态工厂方法创建bean
     * @param bd bean定义
     * @return bean 实例
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Object createInstanceByStaticFactoryMethod(BeanDefinition bd) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> type=bd.getBeanClass();
        Method m=type.getMethod(bd.getFactoryMethodName(),null);
        return m.invoke(type,null);
    }

    /**
     * 通过工厂bean 方式来构造对象
     * @param bd
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private Object createInstanceByFactoryBean(BeanDefinition bd) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Object factoryBean =this.doGetBean(bd.getFactoryBeanName());
        Method m=factoryBean.getClass().getMethod(bd.getFactoryMethodName(),null);

        return m.invoke(factoryBean,null);

    }


    /**
     * 初始化
     * @param bd
     * @param instance
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void doInit(BeanDefinition bd,Object instance) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(StringUtils.isNotBlank(bd.getInitMehtodName())){
            Method m=instance.getClass().getMethod(bd.getInitMehtodName(),null);
            m.invoke(instance,null);
        }
    }


    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws Exception {

    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return null;
    }

    @Override
    public boolean containBeanDefinition(String beanName) {
        return false;
    }
}
