import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {
    String domainName = "iicct";//test domain name, corresponding with Token
    Date today=new Date();
    @Test
    void testGithubRetreivalWithEmptyObj(){
        User nullUser = new User();
        nullUser.getGithubAcc();
        String mail = nullUser.getEmail();
        assertNotEquals(null,mail);//email is the only required field
    }
    @Test
    void testGithubObjOverwrite(){
        User user = new User("Petkan","pet26","petio@abv.bg",null,null,null);
        user.getGithubAcc();
        assertAll("Should not contain any of: Petko, p@abv.bg, p144 ",
                () -> assertNotEquals("petio@abv.bg",user.getEmail()),
                () -> assertNotEquals("Petkan",user.getName()),
                () -> assertNotEquals("pet26",user.getTwitter_handle())
        );
    }



    @Test
    void saveToFreshdeskWithNotNullDates(){
        User user = new User("Grigoriy","gr998","grshta@abv.bg",null,today,today);//assuming such user does not exist in contacts
        Integer res = user.saveToFreshdesk(domainName);
        assertEquals(0,res);
    }
    @Test
    void saveToFreshdeskWithBadEmail(){
        User user = new User("Checko","szt3","something",null,null,null);//assuming such user does not exist in contacts
        Integer res = user.saveToFreshdesk(domainName);
        assertEquals(400,res);
    }
    @Test
    void saveEmptyToFreshdesk(){
        User user = new User();
        Integer res = user.saveToFreshdesk(domainName);
        assertEquals(0,res);
    }


    @Test
    void getByEmailEmpty(){
        User user2 = new User();//assuming such user does not exist in contacts
        Integer result = user2.setFreshdeskContactIDByEmail(domainName);
        assertEquals(400,result);
    }

    @Test
    void updateUser() {
        User usertoUpdate = new User("Bobby Tree","pp21","bob.tree@freshdesk.com",null,null,null);//assuming such user exist in contacts
        usertoUpdate.setFreshdeskContactIDByEmail(domainName);
        Integer reposnseCode = usertoUpdate.updateUser(domainName);
        assertAll("ID must be set, should return 200",
                () -> assertNotEquals(null,usertoUpdate.getFreshdesk_id()),
                () ->    assertEquals(200,reposnseCode)//will throw 404 if limit exceeded
         );



    }

    @Test
    void getIdWithBadDomain(){
        User user = new User("Bob Trheeton","pp21","patthew33@gmail.com",null,null,null);//assuming such user does not exist in contacts
        Integer returnCode = user.setFreshdeskContactIDByEmail("baddomain");
        assertEquals(404,returnCode);
    }

}