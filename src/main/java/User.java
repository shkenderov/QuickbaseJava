import com.google.gson.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class User {
    private String name;
    private String twitter_handle;
    private String email;
    private String freshdesk_id;
    private Date created_at;//freshdesk created,for persistance
    private Date updated_at;//---||----
    private ArrayList<String> otherEmails;

    public User(String name, String twitter_handle, String email, String freshdesk_id, Date created_at, Date updated_at) {//for testing
        this.name = name;
        this.twitter_handle = twitter_handle;
        this.email = email;
        this.freshdesk_id = freshdesk_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.otherEmails = new ArrayList<String>(0);
    }

    public User() {
        this.created_at=null;
        this.updated_at=null;
        this.freshdesk_id=null;
        this.name=null;
        this.twitter_handle=null;
        this.email=null;
        this.otherEmails = new ArrayList<String>(0);
    }

    public Integer setFreshdeskContactIDByEmail(String domainName){


        String encodedString = Base64.getEncoder().encodeToString(System.getenv("FRESHDESK_TOKEN").getBytes());
        Integer responseCode=0;
        try {
            URL url = new URL("https://"+domainName+".freshdesk.com/api/v2/contacts?email="+getEmail());
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestProperty("Authorization", "Basic "+encodedString+"==");
            responseCode=http.getResponseCode();
            System.out.println(responseCode);
            if(responseCode/100!=2) return responseCode;
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                JsonArray jsonArray = (JsonArray) JsonParser.parseString(inputLine);
                if(!jsonArray.isEmpty()) {
                    setFreshdesk_id(jsonArray.get(0).getAsJsonObject().get("id").toString());
                    break;  //always 1 line, email is unique
                }
            }
            http.disconnect();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return responseCode;
    }//tests written

    public Integer saveToFreshdesk(String domainName){
        if(getCreated_at()!=null||getUpdated_at()!=null||getEmail()==null)return 0;
        try{

            String encodedString = Base64.getEncoder().encodeToString(System.getenv("FRESHDESK_TOKEN").getBytes());
            URL url = new URL("https://"+domainName+".freshdesk.com/api/v2/contacts");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Authorization", "Basic "+encodedString);
            String data = "{ \"email\":\""+getEmail()+"\"";
            if(getName()!=""){
                data+=",\"name\":\""+getName()+"\"";
            }
            if(getOtherEmails().size()>0){
                data+=", \"other_emails\": [";
                for(int i=0;i<otherEmails.size();i++){
                    data+="\""+otherEmails.get(i)+"\",";
                }
                data=data.substring(0,data.length()-1); //removing the last comma
                data+="]";
            }
            if(getTwitter_handle()!=null){
                data+=", \"twitter_id\":\""+getTwitter_handle()+"\"}";
            }
            else{
                data+="}";
            }
            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = http.getOutputStream();
            stream.write(out);
            if(http.getResponseCode()==201) {
                BufferedInputStream bis = new BufferedInputStream(http.getInputStream());
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int bisRead = bis.read();
                while (bisRead != -1) {
                    buf.write((byte) bisRead);
                    bisRead = bis.read();
                }
                JsonObject jsonObject = JsonParser.parseString(buf.toString()).getAsJsonObject();
                Date dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(jsonObject.get("created_at").getAsString().substring(0, jsonObject.get("created_at").getAsString().length() - 1));
                setCreated_at(dateTime);


            }
            http.disconnect();
            return http.getResponseCode();
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }//tests written

    public Integer getEmails(){
        HttpClient httpClient =HttpClient.newHttpClient();
        Integer statuscode=null;
        try {
            URI uri = new URI("https://api.github.com/user/emails");
            HttpRequest getRequest=HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .header("Authorization","Bearer "+System.getenv("GITHUB_TOKEN"))
                    .build();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            statuscode=getResponse.statusCode();
            if(statuscode==200) {
                ArrayList<String> otherEmailsIn=new ArrayList<String>(0);
                JsonArray jsonArray=JsonParser.parseString(getResponse.body()).getAsJsonArray();
                for (int i = 0 ; i<jsonArray.size(); i++) {
                    if(jsonArray.get(i).getAsJsonObject().get("primary").getAsString()=="true"){
                        setEmail(jsonArray.get(i).getAsJsonObject().get("email").getAsString());
                    }
                    else {
                        if(!jsonArray.get(i).getAsJsonObject().get("email").getAsString().contains("@users.noreply.github.com"))
                        otherEmailsIn.add(jsonArray.get(i).getAsJsonObject().get("email").getAsString());
                    }
                }
                setOtherEmails(otherEmailsIn);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return statuscode;
    }

    public Integer updateUser(String domainName){
        String encodedString = Base64.getEncoder().encodeToString(System.getenv("FRESHDESK_TOKEN").getBytes());

        try {
            URL url = new URL("https://"+domainName+".freshdesk.com/api/v2/contacts/"+getFreshdesk_id());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("PUT");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Authorization", "Basic "+encodedString+"==");
            String data = "{ \"email\":\""+getEmail()+"\"";
            if(getName()!=""){
                data+=",\"name\":\""+getName()+"\"";
            }
            if(getOtherEmails().size()>0){
                data+=", \"other_emails\": [";
                for(int i=0;i<otherEmails.size();i++){
                    data+="\""+otherEmails.get(i)+"\",";
                }
                data=data.substring(0,data.length()-1); //removing the last comma
                data+="]";
            }
            if(getTwitter_handle()!=null){
                data+=", \"twitter_id\":\""+getTwitter_handle()+"\"}";
            }
            else{
                data+="}";
            }


            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = http.getOutputStream();
            stream.write(out);
            if(http.getResponseCode()/100==2) {
                BufferedInputStream bis = new BufferedInputStream(http.getInputStream());
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int bisRead = bis.read();
                while (bisRead != -1) {
                    buf.write((byte) bisRead);
                    bisRead = bis.read();
                }
                JsonObject jsonObject = JsonParser.parseString(buf.toString()).getAsJsonObject();
                Date dateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(jsonObject.get("created_at").getAsString().substring(0, jsonObject.get("created_at").getAsString().length() - 1));
                setUpdated_at(dateTime);
            }
            http.disconnect();
            return http.getResponseCode();
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public Integer getGithubAcc(){
        HttpClient httpClient =HttpClient.newHttpClient();
        this.created_at=null;
        this.updated_at=null;
        this.freshdesk_id=null;
        this.name=null;
        this.twitter_handle=null;
        this.email=null;
        try {
            URI uri = new URI("https://api.github.com/user");
            HttpRequest getRequest=HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .header("Authorization","Bearer "+System.getenv("GITHUB_TOKEN"))
                    .build();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            if(getResponse.statusCode()==200) {
                JsonObject jsonObject = JsonParser.parseString(getResponse.body()).getAsJsonObject();
                //
                //Contact

                if (!jsonObject.get("name").isJsonNull()) {
                    setName(jsonObject.get("name").getAsString());
                }
                    getEmails();

                if (!jsonObject.get("twitter_username").isJsonNull()) {
                    setTwitter_handle(jsonObject.get("twitter_username").getAsString());
                }
            }
            return getResponse.statusCode();
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }//tests written

    public void setOtherEmails(ArrayList<String> otherEmails) {
        this.otherEmails = otherEmails;
    }

    public ArrayList<String> getOtherEmails() {
        return otherEmails;
    }

    public void setFreshdesk_id(String freshdesk_id) {
        this.freshdesk_id = freshdesk_id;
    }

    public String getFreshdesk_id() {
        return freshdesk_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTwitter_handle(String twitter_handle) {
        this.twitter_handle = twitter_handle;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getName() {
        return name;
    }

    public String getTwitter_handle() {
        return twitter_handle;
    }

    public String getEmail() {
        return email;
    }

    public Date getCreated_at() {
        return created_at;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", twitter_handle='" + twitter_handle + '\'' +
                ", email='" + email + '\'' +
                ", freshdesk_id='" + freshdesk_id + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", otherEmails=" + otherEmails +
                '}';
    }
}
