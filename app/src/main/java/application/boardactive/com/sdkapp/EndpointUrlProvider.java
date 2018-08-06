package application.boardactive.com.sdkapp;


/**
 * BoardActive 2018.08.05
 */
public class EndpointUrlProvider {

    private static final String API_URL = "https://api.boardactive.com/mobile/promotions";

    public static EndPointUrl getDefaultEndPointUrl() {
        return new EndPointUrl(API_URL);
    }
}
