import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Stanislav Rymar
 */
public class BruteforceThread implements Callable {

    private final static Logger logger = Logger.getLogger(BruteforceThread.class);

    public String call() {

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30 * 1000).build();
        HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost("http://www.rollshop.co.il/test.php");

        PasswordGenerator generator = PasswordGenerator.getInstance();
        List<NameValuePair> params = new LinkedList<NameValuePair>();


        boolean isAnswerFound = false;
        String password;

        while (!isAnswerFound) {

            if (!generator.isRecheckPassListEmpty()) {
                password = generator.getPassToRecheck();
            } else {
                password = generator.getNewPassword();
            }


            //Clear array from old value
            if (params.size() > 0)
                params.remove(0);


            //If both Thread pass isRecheckPassListEmpty(), getPassToRecheck() return null from one of them and this Thread will start
            // new Iteration on while loop
            if (password == null)
                continue;


            params.add(new BasicNameValuePair("code", password));


            try {
                //send POST and get responce
                httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity entity = httpResponse.getEntity();


                //Convert response to String. This code work the same like if we are use BufferedReader with FOR loop
                String result = EntityUtils.toString(entity, "UTF-8");
//                logger.debug(result);

                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    if (result != null && !result.contains("WRONG =(")) {
                        isAnswerFound = true;
                        result = Jsoup.parse(result).text();
                        result = result.substring(result.indexOf("https"), result.length());

                        //format and return result string
                        return String.format("\n\n\n Correct link is %s \n Correct password is %s\n\n\n", result, password);
                    }
                } else {
                    generator.addPassToRecheck(password);
                    logger.warn(String.format("Password %s add to recheck list! %s start new attempt!\n", password, Thread.currentThread().getName()));
                    logger.debug(result);
                    continue;
                }

                logger.info(String.format("Password %s incorrect! %s start new attempt!\n", password, Thread.currentThread().getName()));

            } catch (IOException e) {
                logger.error("IOException: Something going wrong", e);
                generator.addPassToRecheck(password);
            }
        }
        return null;
    }
}
