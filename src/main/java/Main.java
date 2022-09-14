import java.io.Console;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        if(System.getenv("FRESHDESK_TOKEN")==null||System.getenv("GITHUB_TOKEN")==null){
            System.out.println("ENV VARIABLES NOT SET. RETURNING");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your Domain Name");
        String domainName = scanner.nextLine();
        User user = new User();
        user.getGithubAcc();

        if(user.getGithubAcc()==200){
                if(user.setFreshdeskContactIDByEmail(domainName)==404){
                    System.out.println("Bad Domain Name");
                    return;
                }
                if(user.getFreshdesk_id()!=null){
                    if(user.updateUser(domainName)==200){
                        System.out.println("Successfully Updated User Credentials");
                    }
                    else{
                        System.out.println("User not found, Registering..");
                    }
                }
                else{
                    if(user.saveToFreshdesk(domainName)==201){
                        System.out.println("Successfully Registered");
                    }
                    else{
                        System.out.println("Error Registering User. Check if user with such Emails and Twitter already exists.");
                    }
                }
        }
        else System.out.println("No such Github user found.");
    }
}
