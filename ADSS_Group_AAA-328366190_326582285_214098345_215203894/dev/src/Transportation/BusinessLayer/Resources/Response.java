package Transportation.BusinessLayer.Resources;

public class Response
{
    private Object returnValue;
    private String errorMessage;

    public Response(Object returnValue, String errorMessage)
    {
        this.returnValue = returnValue;
        this.errorMessage = errorMessage;
    }

    public Object getReturnValue()
    {
        return returnValue;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

}

