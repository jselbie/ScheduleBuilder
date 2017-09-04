package com.selbie.schedulebuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Resolver
{
    
    static final String LONGBOARD_ICON_URL = "http://www.selbie.com/wrek/LB3.png";
    static final String THELOCALS_ICON_URL = "http://www.selbie.com/wrek/locals.png";
    static final String TAPAS_ICON_URL = "http://www.selbie.com/wrek/tapas.png";
    static final String POPUP_ICON_URL = "http://www.selbie.com/wrek/pop.png";
    static final String BROADWAY_ICON_URL = "http://www.selbie.com/wrek/broadway.png";
    static final String FREAKERS_ICON_URL = "http://www.selbie.com/wrek/freakers.png";
    static final String STORYTIME_ICON_URL = "http://www.selbie.com/wrek/storytime.png";
    static final String LIO_ICON_URL = "http://www.selbie.com/wrek/lio.png";

    static final String LIVE_STREAM_128KBS_URL = "http://streaming.wrek.org:8000/wrek_live-128kb.m3u";
    static final String LIVE_STREAM_24KBS_URL = "http://streaming.wrek.org:8000/wrek_live-24kb-mono.m3u";
    static final String LIVE_STREAM_ICON_URL = "http://www.selbie.com/wrek/radio2.png";
    
    static final String SUBNET_STREAM_128KBS_URL = "http://streaming.wrek.org:8000/wrek_HD-2.m3u";
    static final String SUBNET_STREAM_ICON_URL = "http://www.selbie.com/wrek/hd.png";
    
    static final String LOGO_URL_ATMOSPHERICS = "http://www.selbie.com/wrek/atmospherics.png";
    static final String LOGO_URL_CLASSICS = "http://www.selbie.com/wrek/classics.png";
    static final String LOGO_URL_JUSTJAZZ = "http://www.selbie.com/wrek/justjazz.png";
    static final String LOGO_URL_BLUEPLATE = "http://www.selbie.com/wrek/blueplate.png";
    static final String LOGO_URL_RRR = "http://www.selbie.com/wrek/rrr.png";
    
    static final String TECHNIQUES_ICON_URL = "http://www.selbie.com/wrek/techniques.png";
    static final String SLOWRIOT_ICON_URL = "http://www.selbie.com/wrek/slowriot.png";

    
    
    public Resolver()
    {
        
    }
    
    private ArrayList<String> CrackM3U(String m3uURL)
    {
        ArrayList<String> urls = new ArrayList<String>();
        URL url = null;
        StringBuilder stringBuilder = new StringBuilder();
        
        if ((m3uURL == null) || m3uURL.isEmpty())
        {
            return urls;
        }
        
        System.out.println("Cracking " + m3uURL + " into component parts");
        
        try {
            url = new URL(m3uURL);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return urls;
        }
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            char [] buffer = new char[5000];
            while (true)
            {
                int result = reader.read(buffer);
                if (result < 0)
                {
                    break;
                }
                stringBuilder.append(buffer, 0, result);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return urls;
        }
        
        String str = stringBuilder.toString();
        String [] lines = str.split("\\r?\\n");
        
        for (String line : lines)
        {
            line = line.trim();
            if (line.length() > 0)
            {
                urls.add(line);
                System.out.println("   " + line);
            }
        }
        
        return urls;
    }
    
    
    private void mergeDescriptionsIntoShows(ArrayList<Show> shows, ArrayList<ShowDescription> descriptions)
    {
        HashMap<String, ShowDescription> mapDescriptions = new HashMap<String, ShowDescription>();
        
        for (ShowDescription desc : descriptions)
        {
            mapDescriptions.put(desc.Title, desc);
        }
        
        for (Show show : shows)
        {
            ShowDescription desc = mapDescriptions.get(show.Title);
            if (desc != null)
            {
                show.Genres = desc.Genres;
                show.LogoURL = desc.LogoURL;
            }
        }
        
    }
    
    
    ArrayList<Show> getLiveShows() {
        
        Show s1 = new Show();
        Show s2 = new Show();
        
        s1.Title = "Live air stream";
        s1.Genres = "What's on the air right now";
        s1.LiveStream = true;
        s1.M3U_High = LIVE_STREAM_128KBS_URL;
        s1.M3U_Low = LIVE_STREAM_24KBS_URL;
        s1.LogoURL = LIVE_STREAM_ICON_URL;
        s1.TypeOfShow = Show.ShowType.Live;
        s1.HasIcyMetaInt = true;
        
        s2.Title = "HD2 Subchannel";
        s2.Genres = "Alternate programming";
        s2.LiveStream = true;
        s2.M3U_High = SUBNET_STREAM_128KBS_URL;
        s2.LogoURL = SUBNET_STREAM_ICON_URL;
        s2.TypeOfShow = Show.ShowType.Live;
        s2.HasIcyMetaInt = true;
        
        ArrayList<Show> shows = new ArrayList<Show>();
        
        shows.add(s1);
        shows.add(s2);
        
        return shows;
    }
    
    public void fixupDates(Show show)
    {
        String dayOfWeek = "";
        String datePart = ""; 
        int month = 0;
        int day = 0;
               
        
        if (show.DayText.isEmpty())
        {
            return;
        }
        
        String [] splits = show.DayText.split(" ");
        
        if (splits.length >= 2)
        {
            dayOfWeek = splits[0];
            datePart = splits[1];
            
            String [] subsplit = datePart.split("/");
            
            if (subsplit.length >= 2)
            {
                month = Integer.parseInt(subsplit[0]);
                day = Integer.parseInt(subsplit[1]);
            }
        }
        
        if ((month != 0) && (day != 0))
        {
            show.DayText = dayOfWeek;
            show.MonthInt = month;
            show.DayInt = day;
        }
        
    }
    
    public void fixupGenreAndLogos(Show show)
    {
        String [] titles = {"atmospherics", "the classics", "just jazz", "rock, rhythm, and roll", "blue plate special", "the locals", "tapas and tunes", "pop up show", "a bit off broadway", "freaker's ball", "storytime", "slow riot", "techniques", "lost in oscillation"};
        
        String [] genres = {"Ambient, drone, spaced-out",
                            "Classical (Traditional and contemporary)",
                            "Jazz",
                            "Indie rock, Blues, Hip-Hop, and more",
                            "Diverse",
                            "Atlanta area music",
                            "Latin and Spanish",
                            "New show",
                            "Musical",
                            "Classic Rock",
                            "Concept album",
                            "Post rock, instrumental, math rock",
                            "Turntablism, Instrumental Hip Hop, Chopped and Screwed",
                            "Minimal Wave, Minimal Synth, Synthpop, EBM"
                           };
        
        String [] logos = {LOGO_URL_ATMOSPHERICS,
                           LOGO_URL_CLASSICS,
                           LOGO_URL_JUSTJAZZ,
                           LOGO_URL_RRR,
                           LOGO_URL_BLUEPLATE,
                           THELOCALS_ICON_URL,
                           TAPAS_ICON_URL,
                           POPUP_ICON_URL,
                           BROADWAY_ICON_URL,
                           FREAKERS_ICON_URL,
                           STORYTIME_ICON_URL,
                           SLOWRIOT_ICON_URL,
                           TECHNIQUES_ICON_URL,
                           LIO_ICON_URL
                           };
        
                            
        
        if (show.Genres.isEmpty())
        {
            for (int x = 0; x < titles.length; x++)
            {
                if (show.Title.toLowerCase().equals(titles[x]))
                {
                    show.Genres = genres[x];
                }
            }
        }
        
        if (show.LogoURL.isEmpty())
        {
            for (int x = 0; x < titles.length; x++)
            {
                if (show.Title.toLowerCase().equals(titles[x]))
                {
                    show.LogoURL = logos[x];
                }
            }
        }
        
        // The Longboards show has been missing a show logo. So let's give it one.
        if (show.Title.toLowerCase().equals("the longboards show") && show.LogoURL.isEmpty())
        {
            show.LogoURL = LONGBOARD_ICON_URL;
        }
        
    }
    
    
    public void resolve(ArrayList<Show> shows, ArrayList<ShowDescription> descriptions)
    {
        mergeDescriptionsIntoShows(shows, descriptions);
        
        // add in the live shows - they get their M3Us cracked as well
        ArrayList<Show> showsLive = this.getLiveShows();
        
        for (int x = showsLive.size()-1; x >= 0; x--)
        {
            // prepend the live shows to the show list since they get shown first
            shows.add(0, showsLive.get(x));
        }

        // turn each M3U into a playlist
        for (Show s : shows)
        {
            if (!s.M3U_High.isEmpty() && !s.M3U_High.contains("http"))
            {
                s.M3U_High = "https://www.wrek.org" + s.M3U_High;
            }
            
            if (!s.M3U_Low.isEmpty() && !s.M3U_Low.contains("http"))
            {
                s.M3U_Low = "https://www.wrek.org" + s.M3U_Low;
            }
            
            s.urls_High  = CrackM3U(s.M3U_High);
            s.urls_Low  = CrackM3U(s.M3U_Low);
        }
        
        // do fixups
        for (Show s : shows)
        {
            fixupDates(s);
            fixupGenreAndLogos(s);
        }
        
        for (Show show : shows)
        {
            System.out.println("\n============================================");
            System.out.println(show.Title);
            System.out.println(show.TypeOfShow.toString());
            System.out.println(show.Genres);
            System.out.println(show.LogoURL);
            System.out.println(show.DayText + " (" + show.MonthInt + "/" + show.DayInt + ")");
            System.out.println(show.Time);
            System.out.println(show.M3U_High);
            for (String url : show.urls_High)
                System.out.println("   " + url);
            System.out.println(show.M3U_Low);
            for (String url : show.urls_Low)
                System.out.println("   " + url);
        }
        

    }
    
    
    public ArrayList<Show> filterForToday(ArrayList<Show> shows)
    {
        // Cull out of the show list anything that isn't a specialty show or live stream
        // But do include the latest versions of "Atmospherics", "Just Jazz", "The Classics", "Rock, Rhythm, and Roll" into the schedule
        
        // let's figure out what today it is today
        Calendar calendar = Calendar.getInstance();
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
        
        Show justjazz = null;
        Show classics = null;
        Show atmospherics = null;
        Show rockroll = null;
        
        // roll back to Friday if it's the weekend
        if (dayofweek == Calendar.SATURDAY)
        {
            calendar.add(Calendar.DAY_OF_WEEK, -1);
        }
        else if (dayofweek == Calendar.SUNDAY)
        {
            calendar.add(Calendar.DAY_OF_WEEK, -2);
        }
        
        int targetMonth = calendar.get(Calendar.MONTH) + 1;  // months are zero-indexed.  DAY_OF_MONTH is 1 based. go figure...
        int targetDay = calendar.get(Calendar.DAY_OF_MONTH);
        
        for (Show show : shows)
        {
            if ((show.MonthInt == targetMonth) && (show.DayInt == targetDay))
            {
                String title = show.Title.toLowerCase();
                if (title.equals("just jazz"))
                {
                    justjazz = show;
                }
                else if (title.equals("the classics"))
                {
                    classics = show;
                }
                else if (title.equals("atmospherics"))
                {
                    atmospherics = show;
                }
                else if (title.equals("rock, rhythm, and roll"))
                {
                    rockroll = show;
                }
            }
        }
        
        ArrayList<Show> newlist = new ArrayList<Show>();
        
        // copy over the live streams
        for (Show show : shows)
        {
            if (show.LiveStream)
            {
                newlist.add(show);
            }
        }
        
        // now add the format blocks for today
        if (atmospherics != null)
        {
            newlist.add(atmospherics);
        }
        if (classics != null)
        {
            newlist.add(classics);
        }
        if (justjazz != null)
        {
            newlist.add(justjazz);
        }
        if (rockroll != null)
        {
            newlist.add(rockroll);
        }
        
        // now copy over all the specialty shows
        for (Show show : shows)
        {
            if ((show.TypeOfShow == Show.ShowType.SpecialtyShow) && !show.Title.equalsIgnoreCase("the best of our knowledge"))
            {
                newlist.add(show);
            }
        }
        
        return newlist;
        
    }

    
    
}
