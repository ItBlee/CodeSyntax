package Server.api;

import Util.ApiPropertyUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import static Util.StringUtils.convertEscapeCharacters;

public class Compiler {
    //API: https://docs.jdoodle.com/compiler-api/compiler-api
    private static final String API_URL = ApiPropertyUtil.getString("compiler.url");
    private static final String API_KEY = ApiPropertyUtil.getString("compiler.key"); //Đăng nhập rồi lấy ở https://www.jdoodle.com/compiler-api/
    private static final String API_SECRET = ApiPropertyUtil.getString("compiler.secret"); //Đăng nhập rồi lấy ở https://www.jdoodle.com/compiler-api/

    private HttpURLConnection connection;
    private String request, console, statusCode, memory, cpuTime;

    /**
     * @param stdin Truyền sẵn dữ liệu đầu vào
     * @param language Truyền đúng cú pháp: java, python2, cpp, php, c
     */
    public Compiler (String script, String stdin, String language) {
        String versionIndex = getVersionIndex(language);
        script = convertEscapeCharacters(script);
        stdin = convertEscapeCharacters(stdin);

        try {
            URL url = new URL(API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            request = "{\"clientId\": \"" + API_KEY
                    + "\",\"clientSecret\":\"" + API_SECRET
                    + "\",\"script\":\"" + script
                    + "\",\"stdin\":\"" + stdin
                    + "\",\"language\":\"" + language
                    + "\",\"versionIndex\":\"" + versionIndex + "\"} ";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Console
     * @throws IOException Kết nối API thất bại
     * @throws RuntimeException API_SECRET hết hạn hoặc quá số lần gọi API trong ngày
     */
    public String compile() throws IOException, RuntimeException{
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(request.getBytes());
        outputStream.flush();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            connection.disconnect();
            console ="Please check your inputs : HTTP error code : " + connection.getResponseCode();
            statusCode = "null";
            memory = "null";
            cpuTime = "null";
            return console;
        }

        BufferedReader bf_InputStream = new BufferedReader(
                new InputStreamReader((connection.getInputStream())));
        String response;
        try {
            if ((response = bf_InputStream.readLine()) != null) {
                JSONObject jsonObject = new JSONObject(response);
                //System.out.println(response);
                console = jsonObject.get("output").toString();
                statusCode = jsonObject.get("statusCode").toString();
                memory = jsonObject.get("memory").toString();
                cpuTime = jsonObject.get("cpuTime").toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        connection.disconnect();
        return console;
    }

    /**
     * @return second
     */
    public String getCpuTime() {
        return cpuTime;
    }

    /**
     * @return kilobyte
     */
    public String getMemory() {
        return memory;
    }

    public String getStatusCode() {
        return statusCode;
    }

    /**
     * @return Số lần còn lại có thể gọi API
     */
    public static String getCreditSpent() {
        String used = "";
        try {
            URL url = new URL("https://api.jdoodle.com/v1/credit-spent");
            HttpURLConnection JdoodleConnection = (HttpURLConnection) url.openConnection();
            JdoodleConnection.setDoOutput(true);
            JdoodleConnection.setRequestMethod("POST");
            JdoodleConnection.setRequestProperty("Content-Type", "application/json");

            String request = "{\"API_KEY\": \"" + API_KEY
                    + "\",\"API_SECRET\":\"" + API_SECRET + "\"} ";

            OutputStream outputStream = JdoodleConnection.getOutputStream();
            outputStream.write(request.getBytes());
            outputStream.flush();

            BufferedReader bf_InputStream = new BufferedReader(
                    new InputStreamReader((JdoodleConnection.getInputStream())));
            String response;
            if ((response = bf_InputStream.readLine()) != null) {
                JSONObject jsonObject = new JSONObject(response);
                used = jsonObject.get("used").toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return used;
    }

    private String getVersionIndex (String language) {
        switch (language) {
            case "python2":
                return "3";
            case "java":
            case "php":
                return "4";
            case "c":
            case "cpp":
                return "5";
            default:
                return "0";
        }
    }

// My test ----------------------------------------------------------------------------------------------------------
//    public static void main(String[] args) throws IOException{
//        Compiler compiler;
//        compiler = new Compiler();
//        System.out.println(compiler.getCreditSpent());
//
//        System.out.println("java");
//        compiler = new Compiler(
//                read("demoFiles/sum.php"), "", "php");
//        System.out.println(compiler.compile());
//        System.out.println("cpuTime: " + compiler.getCpuTime());
//        System.out.println("memory: " + compiler.getMemory());
//
//        System.out.println("\npython");
//        compiler = new Compiler(
//                readFile("scriptFIles\\sum.py"), "", "python3");
//        System.out.println(compiler.compile());
//        System.out.println("cpuTime: " + compiler.getCpuTime());
//        System.out.println("memory: " + compiler.getMemory());
//
//        System.out.println("\nC++");
//        compiler = new Compiler(
//                readFile("scriptFIles\\sum.cpp"), "", "cpp");
//        System.out.println(compiler.compile());
//        System.out.println("cpuTime: " + compiler.getCpuTime());
//        System.out.println("memory: " + compiler.getMemory());
//
//        System.out.println("\nC");
//        compiler = new Compiler(
//                readFile("scriptFiles\\sum.c"), "", "c");
//        System.out.println(compiler.compile());
//        System.out.println("cpuTime: " + compiler.getCpuTime());
//        System.out.println("memory: " + compiler.getMemory());
//
//        System.out.println("\nphp");
//        compiler = new Compiler(
//                "", "", "php");
//        System.out.println(compiler.compile());
//        System.out.println("cpuTime: " + compiler.getCpuTime());
//        System.out.println("memory: " + compiler.getMemory());
//    }
}
