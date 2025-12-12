package eg.alrawi.alrawi_award.error;

import org.springframework.http.HttpStatus;

public abstract  class BaseException extends  RuntimeException {

    public BaseException(String message){
        super(message);
    }
    abstract  public HttpStatus getStatusCode();

}