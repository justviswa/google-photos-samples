import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/*
 * References:
 *   https://medium.com/@yogeswari.narayasamy/fetching-data-from-google-photos-api-with-postman-2959b0f35844
 *
 *   https://developers.google.com/identity/protocols/OAuth2UserAgent
 *
 *   https://medium.com/@osanda.deshan/getting-google-oauth-access-token-using-google-apis-18b2ba11a11a
 *
 *   https://developers.google.com/photos/library/reference/rest/v1/mediaItems/list
 * */

public class Sample {

    public static void main(String[] args) throws Exception {

        String nextPageToken = null;
        int totalPhotosCount = 0;
        FileWriter writer = new FileWriter("D:\\Temp\\Tokens.txt", true);

        while(true)
        {
            URL urlForGetRequest = new URL("https://photoslibrary.googleapis.com/v1/mediaItems?pageSize=100" + (nextPageToken != null ? "&pageToken=" + nextPageToken : ""));
            String readLine = null;
            HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
            conection.setRequestMethod("GET");
            conection.setRequestProperty("Authorization", "Bearer ya29.Il-6B6LJ3b-S2mOiFIzIaobZ2oF89EvwzP3ABC9JubyUeHmwByC4tivnjxQ6tyGGF2BCUEIcsJLf1PF6dD_27ECgIpPigcJzIXpPnXO2AKyMHG_MOGMbBbld7R4PKGJlLQ");
            conection.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); // set userId its a sample here
            int responseCode = conection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conection.getInputStream()));
                StringBuffer response = new StringBuffer();
                while ((readLine = in .readLine()) != null) {
                    response.append(readLine);
                } in .close();

                JSONObject responseJson = new JSONObject(response.toString());
                totalPhotosCount += responseJson.getJSONArray("mediaItems").length();

                if(responseJson.has("nextPageToken"))
                {
                    nextPageToken = responseJson.getString("nextPageToken");
                    writer.write(nextPageToken + "\n\n");
                }
                else
                {
                    System.out.println(response.toString());
                    break;
                }

                System.out.println("Photos count fetched = "+totalPhotosCount);
                if(responseJson.getJSONArray("mediaItems").length() == 0)
                {
                    break;
                }

                writer.flush();
            } else {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conection.getErrorStream()));
                StringBuffer response = new StringBuffer();
                while ((readLine = in .readLine()) != null) {
                    response.append(readLine);
                } in .close();

                JSONObject responseJson = new JSONObject(response.toString());
                if(responseJson.has("error") && responseJson.getJSONObject("error").getInt("code") == 503)
                {
                    System.out.println("Sleeping for 60 seconds..");
                    Thread.sleep(60000);
                }
                else
                {
                    throw new Exception("GET NOT WORKED - "+response.toString());
                }
            }
        }

        writer.close();

        System.out.println("Total Photos count = "+totalPhotosCount);
    }

}
