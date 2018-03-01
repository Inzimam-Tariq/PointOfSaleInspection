package web2design.epos;

/**
 * Created by root on 11/16/15.
 */
public interface HttpResponseCallback {

    void onCompleteHttpResponse(String response, String requestUrl);
}
