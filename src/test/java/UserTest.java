import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {
    Date today = new Date();
    String domainName = "iict-help";//test domain name, corresponding with Token

    @Test
    void testGithubRetreivalWithEmptyObj(){
        User nullUser = new User();
        nullUser.getGithubAcc();
        String mail = nullUser.getEmail();
        assertNotEquals(null,mail);//email is the only required field
    }
    @Test
    void testGithubObjOverwrite(){
        User user = new User("Petkan","pet26","petio@abv.bg","00023",today,today);
        user.getGithubAcc();
        assertAll("Should not contain any of: Petko, p@abv.bg, p144, 0000 ",
                () -> assertNotEquals("p@abv.bg",user.getEmail()),
                () -> assertNotEquals("Petko",user.getName()),
                () -> assertNotEquals("p144",user.getTwitter_handle()),
                () -> assertNotEquals("0000",user.getFreshdesk_id())
        );
    }

    @Test
    void testFreshdeskDuplicateSubmit() {//assuming no such user is in contacts already

        User user = new User("Petko","p144","p@abv.bg","0000",null,null);
        Integer res = user.saveToFreshdesk(domainName);
        //assertEquals(201,res); //works only first time, after you have to change email
        //res = user.saveToFreshdesk();
        assertEquals(409,res);

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
    void getPrivateEmail() {
    }

    @Test
    void updateUser() {
        User user = new User("Pat James","pp21","patthew33@gmail.com",null,null,null);//assuming such user does not exist in contacts
        user.saveToFreshdesk(domainName);
        user.setFreshdeskContactIDByEmail(domainName);
        user.setName("Patty James");
        Integer reposnseCode = user.updateUser(domainName);
        Date updatedUpdatedDate = user.getUpdated_at();
        assertEquals(200,reposnseCode);

    }

    @Test
    void getIdWithBadDomain(){
        User user = new User("Pat James","pp21","patthew33@gmail.com","151003143328",null,null);//assuming such user does not exist in contacts
        Integer returnCode = user.setFreshdeskContactIDByEmail("baddomain");
        assertEquals(404,returnCode);
    }

}