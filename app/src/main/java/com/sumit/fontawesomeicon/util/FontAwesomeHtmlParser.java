package com.sumit.fontawesomeicon.util;

/**
 * Created by Sumit on 7/28/2017.
 */

public class FontAwesomeHtmlParser {

    private static final String FONT_AWESOME_CHEATSHEET_URL = "https://fontawesome.com/cheatsheet/free/solid";

    /*public static ArrayList<FontAwesomeIcon> getAllFontIconList(Context context) {

        Document doc = null;

        try {

            // Parse the tags which contains the icon unicode and name

            *//*<div class="col-md-4 col-sm-6 col-lg-3 col-print-4">
            <small class="text-muted pull-right">4.4</small>
            <i class="fa fa-fw" aria-hidden="true" title="Copy to use 500px">&#xf26e;</i>
                    fa-500px
            <span class="text-muted">[&amp;#xf26e;]</span>
            </div>*//*

            doc = Jsoup.connect(FONT_AWESOME_CHEATSHEET_URL).get();
            Elements faIconTags = doc.select("dl.dt.collapse.w-100.ma0.pa0");

            ArrayList<FontAwesomeIcon> fontAwesomeIcons = new ArrayList<>();

            if (faIconTags != null && faIconTags.size() > 0) {
                for (Element element : faIconTags) {
                    FontAwesomeIcon fontAwesomeIcon = new FontAwesomeIcon();
                    fontAwesomeIcon.setIconUnicode(element.text());
                    String iconName = StringUtils.substringBetween(element.parent().toString(), "</i>", "<").trim();
                    fontAwesomeIcon.setIconClassName(StringUtils.replaceChars(iconName, '-', '_'));
                    fontAwesomeIcons.add(fontAwesomeIcon);
                }
            }

            return fontAwesomeIcons;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }*/
}
