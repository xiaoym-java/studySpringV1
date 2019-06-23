package core.ioc;

import org.apache.commons.lang3.StringUtils;

/**
 * BeanDefinition是在定义bean何种策略生产bean
 * 具体有以下策略
 * 1通过指定方法生成bean，例如指定的单例方法
 * 2通过指定的bean工厂（需要bean工厂的名字和方法名）
 *
 */

public interface BeanDefinition {
    String scope_singletion="singleton";
    String scope_prototype="prototype";

    /**
     * 获取类的信息
     * @return
     */
    Class<?> getBeanClass();

    /**
     * 获取bean生成策略
     * @return
     */
    String getScope();

    /**
     * 是否单例
     */
    boolean isSingleton();

    boolean isPrototype();

    /**
     * 工厂bean名
     * @return
     */
    String getFactoryBeanName();

    /**
     * 工厂方法名
     * @return
     */
    String getFactoryMethodName();

    /**
     * 初始化方法
     * @return
     */
    String getInitMehtodName();

    /**
     * 销毁方法
     * @return
     */
    String getDestroyMethodName();

    /**
     * 检验bean定义的合法性
     *
     *
     *
     */
//    default boolean validata(){
//        //没定义class，工厂bean或者工厂方法没指定，则不合法
//        if(null==this.getBeanClass()){
//            if(StringUtils.isBlank(getFactoryBeanName())||StringUtils.isBlank(getFactoryMethodName())){
//                return false;
//            }
//        }
//
//        //定义类，有定义了工厂bean，则不合法
//        if(null!=this.getBeanClass() && StringUtils.isNotBlank(getFactoryBeanName())){
//            return false;
//        }
//
//        return true;
//    }

}
