package core.exception;

public class BeanDefinitionRegistException extends Exception{

//    private static final long serialVersionUID=;

    public BeanDefinitionRegistException(String message){
        super(message);
    }

    public BeanDefinitionRegistException(String message,Throwable e){
        super(message,e);
    }

}
