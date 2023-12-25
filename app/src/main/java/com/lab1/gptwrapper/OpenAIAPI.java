package com.lab1.gptwrapper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;


public class OpenAIAPI {
// для давинчи private final String url = "https://api.openai.com/v1/completions";
    private final String url = "https://api.openai.com/v1/chat/completions";
    private String APIkey;
    private HttpURLConnection connection;
    OpenAIAPI(String APIkey) {
    this.APIkey = APIkey;
    }


    // обработка прихода новых данных при вызове текст-давинчи
    private OnResponseReceivedTextDavinciListener onResponseReceivedTextDavinciListener;
    public interface OnResponseReceivedTextDavinciListener {
        void onResponseReceivedTextDavinci(String response);
    }
    public void setOnResponseReceivedTextDavinciListener(OnResponseReceivedTextDavinciListener listener) {
        this.onResponseReceivedTextDavinciListener = listener;
    }

    public Boolean davinciResponse(String requestText) throws Exception {
        Boolean connectionSuccess = openConnection();

        if (connectionSuccess) {

            JSONObject data = new JSONObject();
            data.put("model", "text-davinci-003");
            data.put("prompt", requestText);
            data.put("max_tokens", 4000);
            data.put("temperature", 1.0);

            connection.setDoOutput(true);
            connection.getOutputStream().write(data.toString().getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (onResponseReceivedTextDavinciListener != null) {
                    onResponseReceivedTextDavinciListener.onResponseReceivedTextDavinci(line);
                }
            }

          reader.close();

//            String output = new BufferedReader(new InputStreamReader(connection.getInputStream())).lines()
//                .reduce((a, b) -> a + b).get();
//            String out = new JSONObject(output).getJSONArray("choices").getJSONObject(0).getString("text");
//
//            if (onResponseReceivedTextDavinciListener != null) {
//                onResponseReceivedTextDavinciListener.onResponseReceivedTextDavinci(out);
//            }
        }
        return true;
    }

    // обработка прихода новых данных при вызове ChatGPT
    private OnResponseReceivedChatGPTListener onResponseReceivedChatGPTListener;
    public interface OnResponseReceivedChatGPTListener {
        void onResponseReceivedChatGPT(String response);
    }
    public void setOnResponseReceivedChatGPTListener(OnResponseReceivedChatGPTListener listener) {
        this.onResponseReceivedChatGPTListener = listener;
    }

    public Boolean chatGPTResponse(String userContent, String systemContent, double temperature) throws Exception {
        if (connection.getResponseCode() > 400) {
            boolean connectStatus = openConnection();
            if (!connectStatus) {
                throw new IOException("Failed to open connection");
            }
        }
        if (onResponseReceivedChatGPTListener == null) {
            throw new IOException("ResponseReceivedChatGPTListener not seted");
        }

        String model = "gpt-3.5-turbo";

        // Create a JSONArray for the messages
        JSONArray messages = new JSONArray();

        // Create a system message
        JSONObject systemMessage = new JSONObject()
                .put("role", "system")
                .put("content", systemContent);
        messages.put(systemMessage);

        // Create a user message
        JSONObject userMessage = new JSONObject()
                .put("role", "user")
                .put("content", userContent);
        messages.put(userMessage);

        // Create request object
        JSONObject body = new JSONObject()
                .put("model", model)
                .put("temperature", temperature)
                .put("messages", messages)
                .put("stream", true);


        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(body.toString());
        writer.flush();
        writer.close();

        // Response from ChatGPT
        InputStream inpStr = connection.getInputStream();
        InputStreamReader inpStrRead = new InputStreamReader(inpStr);
        BufferedReader br = new BufferedReader(inpStrRead);
        String line;


        // чтение буфера
        while (!Objects.equals(br.toString(), "data: [DONE]")) {
            String curStr = br.readLine();
            if (curStr != null && !Objects.equals(curStr, "")) {
                try{
                    curStr = "{" + curStr + "}";
                    JSONObject jsonObject = new JSONObject(curStr);
                    JSONArray choicesArray = jsonObject.getJSONObject("data").getJSONArray("choices");
                    JSONObject firstChoice = choicesArray.getJSONObject(0);
                    String content = firstChoice.getJSONObject("delta").getString("content");
                    if (!content.equals("")){
                        onResponseReceivedChatGPTListener.onResponseReceivedChatGPT(content);
                    }
                } catch (Exception e){

                }
            }
        }
        br.close();


//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                if (onResponseReceivedChatGPTListener != null) {
//                    onResponseReceivedChatGPTListener.onResponseReceivedChatGPT(line);
//                }
//            }
//            reader.close();
//        } catch (IOException e) {
//            throw new IOException("Error reading response from the server", e);
//        }

        return true;
    }


    private boolean openConnection() throws Exception {
        connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + APIkey);
        //connection.setRequestProperty("Authorization", String.join("","Bearer ", APIkey));
        // тут в будущем проверка коннекта
        int responseCode = connection.getResponseCode();
        if (responseCode < 400) {
            return true;
        } else {
            return false;
        }
    }

    public boolean connectTest() throws Exception {
        return openConnection();
    }

    protected void finalize() {
        connection.disconnect();
    }
}


//    public static void chatGPT(String text) throws Exception {
//        String url = "https://api.openai.com/v1/completions";
//        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Content-Type", "application/json");
//        connection.setRequestProperty("Authorization", "Bearer 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");
//
//        JSONObject data = new JSONObject();
//        data.put("model", "text-davinci-003");
//        data.put("prompt", text);
//        data.put("max_tokens", 4000);
//        data.put("temperature", 1.0);
//
//        connection.setDoOutput(true);
//        connection.getOutputStream().write(data.toString().getBytes());
//
//        String output = new BufferedReader(new InputStreamReader(connection.getInputStream())).lines()
//                .reduce((a, b) -> a + b).get();
//
//        System.out.println(new JSONObject(output).getJSONArray("choices").getJSONObject(0).getString("text"));
//    }

