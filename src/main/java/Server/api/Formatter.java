package Server.api;
import Util.ApiPropertyUtil;
import Util.StringUtils;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static Util.StringUtils.convertEscapeCharacters;

public class Formatter {
    private String URL = ApiPropertyUtil.getString("formatter.url");
    private final String script;
    private static WebClient webClient;
    private static final Map<String, HtmlPage> PAGES = new HashMap<>();

    public static void init() {
        new Thread(() -> {
            if (webClient == null) {
                webClient = new WebClient(BrowserVersion.CHROME);
                webClient.getOptions().setCssEnabled(false);
            }
            /*String[] supportedLanguage = new String[] { "java", "python", "php", "c" };
            String domain = ApiPropertyUtil.getString("formatter.url");
            for (String language : supportedLanguage) {
                String url = domain + "online_" + language + "_formatter.htm";
                PAGES.computeIfAbsent(url, s -> {
                    try {
                        return webClient.getPage(url);
                    } catch (Exception e) {
                        throw new IllegalStateException();
                    }
                });
            }*/
        }).start();
    }

    /**
     * @param language Truyền đúng cú pháp: java, python2, cpp, php, c
     */
    public Formatter (String script, String language) {
        this.script = convertEscapeCharacters(script).replace("'", "\\'");;

        if (language.equals("python2"))
            language = "python";
        else if (language.equals("cpp"))
            language= "c";

        this.URL = URL + "online_" + language + "_formatter.htm";
        //System.out.println(this.URL);


        Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }

    public String format() {
        String javaScriptCode;
        String formatted = "";
        try {
            HtmlPage page = PAGES.computeIfAbsent(URL, s -> {
                try {
                    return webClient.getPage(URL);
                } catch (Exception e) {
                    throw new IllegalStateException();
                }
            });

            //Đẩy script lên
            javaScriptCode = "editor.setValue('" + script + "')";
            if (page != null) {
                page.executeJavaScript(javaScriptCode);
            } else return format();

            //Click nút format
            HtmlElement btn_Beautify = page.getHtmlElementById("beautify");
            btn_Beautify.click();

            //Lấy script formatted
            webClient.waitForBackgroundJavaScript(3 * 1000); //Đợi page thực thi format
            javaScriptCode = "outputeditor.getValue()";
            //Lặp cho tới khi lấy code thành công.
            do {
                //System.out.println("try");
                formatted = page.executeJavaScript(javaScriptCode).getJavaScriptResult().toString();
            } while (StringUtils.isBlank(formatted));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatted;
    }

    // testcase
//    public static void main (String[] args) {
//        Formatter formatter = new Formatter(read("demoFiles/sum.java"), "java");
//        System.out.println(formatter.format());
//        StringTokenizer string;
//        String temp;
//        long totalTime = 0;
//        int times;
//        for (times=1; times<=1000; times++) {
//            long start = System.currentTimeMillis();
//            string = new StringTokenizer(formatter.format(), "\n");
//            temp = string.nextToken();
//            temp = temp.substring(0, temp.length()-1);
//            System.out.println(temp);
//            if (!temp.equals("import java.io.*")) { // So sánh với hàng đầu tiên của script đã truyền vào
//                System.out.println("false");
//                break;
//            }
//            long leftTime = System.currentTimeMillis()-start;
//            System.out.println(times + " " + leftTime);
//            totalTime += leftTime;
//        }
//        System.out.println("average time: " + totalTime/times);
//    }
}
