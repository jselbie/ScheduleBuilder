package com.selbie.schedulebuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowMap {

    // so that we don't have to keep updating the code to fixup a show that is missing a description or
    // thumbnail icon, we can use the map file
    // It has the following schema:
    /*
    {
        "map": [
        {
            "title": "lost in oscillation",
            "genre": "Minimal Wave, Minimal Synth, Synthpop, EBM",
            "logo": "http://www.selbie.com/wrek/lio.png"
        },
        {
            "title": "The Foobar Show",
            "genre": "foobar, heavy foobar, death foobar",
            "logo": "http://www.selbie.com/wrek/foobar.png"
        }
        ]
    }
    */

    public static HashMap<String, Show> load(String filename) {

        HashMap<String, Show> map = new HashMap<String, Show>();

        try {

            System.out.println("Reading map file: " + filename);
            StringBuilder sb = new StringBuilder();

            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                sb.append(line);
            }
            br.close();

            String jsonText = sb.toString();
            JSONObject json = new JSONObject(jsonText);

            JSONArray jsonArray = json.getJSONArray("map");

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject item = jsonArray.getJSONObject(i);
                Show show = new Show();
                show.Title = item.optString("title").trim().toLowerCase();
                show.Genres = item.optString("genre").trim();
                show.LogoURL = item.optString("logo").trim();

                if (!show.Title.isEmpty())
                {
                    System.out.println("Mapfile has entry for: " + show.Title);
                    map.put(show.Title, show);
                }
            }
        }
        catch (IOException|JSONException ex) {
            System.out.println("Error parsing mapfile: " + ex.toString());
            ex.printStackTrace();
        }

        return map;
    }
}
