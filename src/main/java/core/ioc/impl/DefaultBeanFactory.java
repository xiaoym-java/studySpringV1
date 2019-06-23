package core.ioc.impl;


import core.exception.BeanDefinitionRegistException;
import core.ioc.BeanDefinition;
import core.ioc.BeanDefinitionRegistry;
import core.ioc.BeanFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import javax.xml.ws.WebServiceException;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry, Closeable {

    private final Log logger = LogFactory.getLog(DefaultBeanFactory.class);

    //存在beanDefinition的缓存
    private Map<String,BeanDefinition> beanDefinitionMap= new ConcurrentHashMap<>(256);
    //存放单例bean实例的缓存
    private Map<String,Object> beanMap = new ConcurrentHashMap<>(256);

    //获取bean实例
    public Object getBean(String name) throws Exception {
        return this.doGetBean(name);
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
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionRegistException {
        Objects.requireNonNull(beanName,"注册bean需要给入beanName");
        Objects.requireNonNull(beanDefinition,"注册bean需要给入beanDefinition");

        //检验给如的bean是否合法
        if(!beanDefinition.validata()){
            throw new BeanDefinitionRegistException(String.format("名字为[%s]的bean的定义不合法：%s",beanName,beanDefinition));
        }

        //验证beanDefinition已经存在
        if(this.containBeanDefinition(beanName)){
            throw new BeanDefinitionRegistException(String.format("名字为[%s]的bean定义已经存在:%s",
                    beanName,this.getBeanDefinition(beanName)));
        }

        this.beanDefinitionMap.put(beanName,beanDefinition);
    }

    /**
     * 获取beanDefinition
     * @param beanName bean的名称 唯一标识
     * @return beanDefinition
     */
    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return this.beanDefinitionMap.get(beanName);
    }


    /**
     * 验证beanDefinition是否已经存在
     * @param beanName bean的名称 唯一标识
     * @return true：已存在 false：不存在
     */
    @Override
    public boolean containBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    /**
     * 执行指定的销毁方法
     * @throws WebServiceException
     */
    @Override
    public void close() throws WebServiceException {
        //执行单例实例的销毁方法
        for(Map.Entry<String,BeanDefinition> e:this.beanDefinitionMap.entrySet()){
            String beanName=e.getKey();
            BeanDefinition bd=e.getValue();

            if(bd.isSingleton() && StringUtils.isNotBlank(bd.getDestroyMethodName())){
                Object instance = this.beanMap.get(beanName);
                try {
                    Method m = instance.getClass().getMethod(bd.getDestroyMethodName());
                    m.invoke(instance,null);
                } catch (NoSuchMethodException e1) {
                    logger.error(String.format("执行bean[%s] %s 的 销毁方法异常！",beanName,bd), e1);
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    logger.error(String.format("执行bean[%s] %s 的 销毁方法异常！",beanName,bd), e1);
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    logger.error(String.format("执行bean[%s] %s 的 销毁方法异常！",beanName,bd), e1);
                    e1.printStackTrace();
                }
            }
        }

    }
}
