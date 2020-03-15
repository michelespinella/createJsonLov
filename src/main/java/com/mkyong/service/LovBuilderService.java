package com.mkyong.service;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class LovBuilderService {

    @Autowired
    private ConfigProperties configProperties;

    public JSONObject getDataFromService() throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = configProperties.getUrl();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        String s = response.body().string();
        JSONObject jsonLovObject = new JSONObject(s);

        return buildJsonData(jsonLovObject);

    }

    private JSONObject buildJsonData(JSONObject jsonLovObject) throws IOException {
        //getting the "related" jsonObject
        String resourceName = "/config.json";
        InputStream is = LovBuilderService.class.getResourceAsStream(resourceName);
        if (is == null) {
            throw new NullPointerException("Cannot find resource file " + resourceName);
        }
        JSONTokener tokener = new JSONTokener(is);
        JSONObject object = new JSONObject(tokener);
        JSONObject related = jsonLovObject.getJSONObject(object.getString("proToRead"));

        //getting the "bought_together" as an jsonArray and do what you want with it.
        //you can act with jsonarray like an array
        writeJsonFile(related);

        //now if you run blow code
        return related;
    }

    private Boolean writeJsonFile(JSONObject jsonData) throws IOException {
        String fileName = configProperties.getFilepath();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
            jsonData.write(writer);
            writer.write("\n");
        } catch (Exception ex) {
            System.err.println("Couldn't write file\n"
                    + ex.getMessage());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
