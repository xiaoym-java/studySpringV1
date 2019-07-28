package core.di;

public class BeanRefernce {

    private String beanName;

    public BeanRefernce(String beanName){
        this.beanName=beanName;
    }

    //获取bean的名字（bean的唯一标识）
    public String getBeanName() {
        return beanName;
    }
}
