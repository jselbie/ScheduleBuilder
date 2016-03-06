package com.selbie.schedulebuilder;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Importer
{
    
    public Importer()
    {
        
    }
    
    public ArrayList<Show> buildSchedulePage() throws IOException
    {
        Document doc = Jsoup.connect("http://www.wrek.org/schedule/").get();
        ArrayList<Show> showsAll = new ArrayList<Show>();
        ArrayList<Show> showsDay;
        
        Elements elements = doc.select(".schedule-list");

        for (Element elem : elements)
        {
            showsDay = parseDay(elem);
            showsAll.addAll(showsDay);
        }
        return showsAll;
    }    
    
    public ArrayList<ShowDescription> buildShowDescriptions() throws IOException
    {
        Document doc = Jsoup.connect("http://www.wrek.org/shows/specialty/").get();
        ArrayList<ShowDescription> descriptions = new ArrayList<ShowDescription>();
        
        Elements elements = doc.select(".mosaic-block");
        
        for (Element elem : elements)
        {
            ShowDescription desc = parseSpecialtyShowBlock(elem);
            if (desc != null)
            {
                descriptions.add(parseSpecialtyShowBlock(elem));
            }
        }
        
        return descriptions;
    }
    
    private ArrayList<Show> parseDay(Element scheduleListElement)
    {
        String stringDay = getTextFromFirstElement(scheduleListElement.select(".schedule-day"));
        ArrayList<Show> shows = new ArrayList<Show>();
        
        if (stringDay.toLowerCase().equals("key"))
        {
            return shows;
        }
        
        
        Elements entries = scheduleListElement.select(".schedule-entry");
        
        for (Element e : entries)
        {
            Show show = generateShowFromScheduleEntry(e, stringDay);
            shows.add(show);
        }
        
        return shows;
    }    
    
    
    private static String textFixup(String str)
    {
        char nbsp = 0xA0;
        
        str = str.replace(nbsp, ' ');
        
        str = str.trim();
        
        return str;
    }    
    
    private static String getTextFromFirstElement(Elements elements)
    {
        if (elements.first() != null)
        {
            return elements.first().text();
        }
        return "";
    }
    
    private Show.ShowType getShowTypeFromScheduleEntry(Element entry)
    {
        String [] classTypes = {"schedule-block", "schedule-specialty", "schedule-pubaff", "schedule-sports"};
        Show.ShowType [] showtypes = {Show.ShowType.SoundBlock, Show.ShowType.SpecialtyShow, Show.ShowType.PublicAffairs, Show.ShowType.Sports};
        
        for (int x = 0; x < classTypes.length; x++)
        {
            if (entry.classNames().contains(classTypes[x]))
            {
                return showtypes[x];
            }
        }
        
        return Show.ShowType.Unknown;
    }
    
    private int GetIntFromText(String str)
    {
        int result = 0;
        
        for (int x = 0; x < str.length(); x++)
        {
            char c = str.charAt(x);
            if ((c >= '0') && (c <= '9'))
            {
                result = result * 10 + (c - '0');
            }
        }
        
        return result;
    }
    
    private String getURLForShow(Element scheduleEntry, boolean high)
    {
        Elements archive = scheduleEntry.select(".schedule-archive");
        
        Elements anchors = archive.select("a");
        Element anchor1=null, anchor2=null;
        String text1 = "";
        String text2 = "";
        int val1 = 0;
        int val2 = 0;
        String href1 = "";
        String href2 = "";
        String hrefHigh, hrefLow;

        if (anchors.size() >= 1)
        {
            anchor1 = anchors.get(0);
            text1 = anchor1.text();
            val1 = GetIntFromText(text1);
            href1 = anchor1.attr("href");
        }
        
        if (anchors.size() >= 2)
        {
            anchor2 = anchors.get(1);
            text2 = anchor2.text();
            val2 = GetIntFromText(text2);
            href2 = anchor2.attr("href");
        }
        
        if (val1 > val2)
        {
            hrefHigh = href1;
            hrefLow = href2;
        }
        else
        {
            hrefHigh = href2;
            hrefLow = href1;
        }
        
       return high ? hrefHigh : hrefLow;
    }
    
    
    private Show generateShowFromScheduleEntry(Element scheduleEntry, String day)
    {
        Show show = new Show();
        
        show.Time = getTextFromFirstElement(scheduleEntry.select(".schedule-time"));
        show.Time = textFixup(show.Time);
        
        show.Title = getTextFromFirstElement(scheduleEntry.select(".schedule-show"));
        show.Title = textFixup(show.Title);
        
        show.TypeOfShow = getShowTypeFromScheduleEntry(scheduleEntry);
        
        show.DayText = textFixup(day);
        
        show.M3U_Low = getURLForShow(scheduleEntry, false);
        show.M3U_High = getURLForShow(scheduleEntry, true);
        return show;
        
    }
    


    
    private ShowDescription parseSpecialtyShowBlock(Element element)
    {
        ShowDescription desc = new ShowDescription();
        
        desc.Genres = textFixup(getTextFromFirstElement(element.select(".specialty-genres")));
        Element img = element.select("img").first();
        
        if (img != null)
        {
            desc.LogoURL = img.attr("src");
        }
        else
        {
            desc.LogoURL = "";
        }
        
        desc.Title = textFixup(getTextFromFirstElement(element.select("h4")));
        
        if (desc.Title != "")
        {
           return desc;
        }
        return null;
    }
    
    
}
