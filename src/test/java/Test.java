
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author truongnq
 */
public class Test {

    public static void main(String[] args) {

        String demo = "{}";
        JSONObject json = new JSONObject(demo);
        if(!json.has("results")) return;
        JSONArray results = json.getJSONArray("results");
        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            if(!result.has("alternatives")) continue;
            JSONArray alternatives = result.getJSONArray("alternatives");
            for (int j = 0; j < alternatives.length(); j++) {
                JSONObject alternative = alternatives.getJSONObject(j);
                String text = alternative.getString("transcript");
                System.out.println(text);
            }
        }
    }
}