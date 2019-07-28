package core.ioc.impl;

import core.ioc.BeanDefinition;
import org.apache.commons.lang3.StringUtils;

public class GenericBeanDefinition implements BeanDefinition {


    private Class<?> beanClass;

    //是否为单例
    private String scope = BeanDefinition.SCOPE_SINGLETION;

    //bean工厂的名称
    private String factoryBeanName;
    //bean工厂方法名
    private String factoryMethodName;

    //初始化方法
    private String initMethodName;
    //销毁方法
    private String destroyMethodName;




    /**
     * 自动生成设置的方法 start
     */
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public void setScope(String scope) {
        if(StringUtils.isNoneBlank(scope)){
            this.scope = scope;
        }
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }
    /**
     * 自动生成设置的方法 end
     */


    @Override
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Override
    public String getScope() {

        return this.scope;
    }

    @Override
    public boolean isSingleton() {
        return BeanDefinition.SCOPE_SINGLETION.equals(this.scope);
    }

    @Override
    public boolean isPrototype() {
        return BeanDefinition.SCOPE_PROTOTYPE.equals(this.scope);
    }

    @Override
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    @Override
    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }

    @Override
    public String getInitMehtodName() {
        return this.initMethodName;
    }

    @Override
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }



    @Override
    public String toString() {
        return String.format("GenericBeanDefinition [beanClass=%s, scope=%s, factoryBeanName=%s, " +
                "factoryMethodName=%s, initMethodName=%s, destroyMethodName=%s]",
                beanClass,scope,factoryBeanName,factoryMethodName,initMethodName,destroyMethodName);
    }

    /**
     * 疑问: 为什么要重写equals 方法
     *
     * 重写equals方法需要注意以下几点：
     * 1自反性：对于任何非空引用x，x.equals(x)应该返回true
     * 2对称：对于任何引用x，y，如果x.equals(y) 返回true，那么 y.equals(x)也应该返回true。
     * 3传递性：对于任何引用x，y和z，如果x=y 为true，那么y=z也一定为true，x=z也一定为true。
     * 4一致性：如果x和y引用的对象没有发生变化，那么返回调用x.equals(y),应该返回同样的结果。
     * 5非空性：对于任意非空引用x，x.equals(null)应该返回false。
     *
     * 重写equals方法，就必须重写hashCode
     * 原因是HashMap的需要
     */

    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }

        if(null==obj){
            return false;
        }

        if(getClass() !=obj.getClass()){
            return false;
        }

        GenericBeanDefinition other=(GenericBeanDefinition) obj;

        //验证每个属性是否相当，只有当每个属性均相等时，才是一个对象
        if(beanClass ==null){
            if(other.beanClass!=null){
                return false;
            }
        }else if(!beanClass.equals(other.beanClass)){
            return false;
        }

        if(destroyMethodName== null){
            if(other.destroyMethodName!=null){
                return false;
            }
        }else if(!destroyMethodName.equals(other.destroyMethodName) ){
            return false;
        }

        if(factoryBeanName== null){
            if(other.factoryBeanName!=null){
                return false;
            }
        }else if(!factoryBeanName.equals(other.factoryBeanName) ){
            return false;
        }

        if(factoryMethodName== null){
            if(other.factoryMethodName!=null){
                return false;
            }
        }else if(!factoryMethodName.equals(other.factoryMethodName) ){
            return false;
        }

        if(initMethodName== null){
            if(other.initMethodName!=null){
                return false;
            }
        }else if(!initMethodName.equals(other.initMethodName) ){
            return false;
        }

        if(scope== null){
            if(other.scope!=null){
                return false;
            }
        }else if(!scope.equals(other.scope) ){
            return false;
        }


        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((beanClass == null) ? 0 : beanClass.hashCode());
        result = prime * result + ((destroyMethodName == null) ? 0 : destroyMethodName.hashCode());
        result = prime * result + ((factoryBeanName == null) ? 0 : factoryBeanName.hashCode());
        result = prime * result + ((factoryMethodName == null) ? 0 : factoryMethodName.hashCode());
        result = prime * result + ((initMethodName == null) ? 0 : initMethodName.hashCode());
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        return result;
    }
}
