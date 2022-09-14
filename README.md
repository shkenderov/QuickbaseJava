# Quickbase Demo Project
This is a small java Command Line application, designed to copy an authenticated user's [GitHub](https://github.com) account into the Contacts list of the [Freshdesk](https://www.freshdesk.com) platform when supplied with its subdomain name.
## Minimum requirements
[Java JDK v.11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
[Gradle](https://gradle.org/)
### GitHub and Freshbase Tokens
You need to have generated both keys with appropriate access.
[Creating a GitHub Personal Access Token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
[Finding your Freshbase API Key](https://support.freshdesk.com/en/support/solutions/articles/215517)


 This Application requires having both tokens set into values of environment variables as follows:
| Source  | Name of Variable|
|---------|-----------------|
| GitHub  | GITHUB_TOKEN    |
|FreshDesk| FRESHDESK_TOKEN |

For Instructions on how to set your Environment variables you can use [Twilio's guide](https://www.twilio.com/blog/2017/01/how-to-set-environment-variables.html) on the topic, covering all common operating systems.

### Build
	gradle build
This will also run all Unit Tests.
** IMPORTANT ** You need to change the `domainName` String in `UserTest.java` with the domain of your Freshdesk API KEY prior to building the application.
### Test
	gradle test
### Run
	 gradle run



# Further Information
This application consists of a User Object, where all the necessary methods are implemented. In the Main class, they are called as follows:
1.  All relevant information is received from the GitHub Account, using the access token:
* Name
* Primary Email
* Other Emails
* Twitter  
	This is saved into the `User` Object's attributes.
	If the request is not successful, the program returns.
2. The Freshdesk Contact ID with the same username and ID is set as the `freshdesk_id` attribute.
* If Attribute is set successfully, the `updateUser()` method is called, updating the Freshdesk Contact with the GitHub Information.
* If not, the `saveToFreshdesk()` method is called. It saves this GitHub user as a new Freshdesk contact.
3. The Program returns, displaying a text message of what has happened.
#### Functionality Notes
1. According to the [Freshdesk API Docs](https://developers.freshdesk.com/api/#account), the subdomain name must be present in all URLs when sending requests. Hence, the retreival of this name just by the API Key is not possible, hence part of the third Project Requirement is not implemented.
2. Depending on the [Freshdesk Acoount Plan](https://freshdesk.com/pricing), there might be restrictions on the number of API Calls permitted. After reaching them, the API Returns `"message": "You have exceeded the limit of requests per hour"` and this application will not work as intended.
3. Possible conflicts when registering a Contact with information existing in another Contact:
* E-Mail (including non-primary emails from a GitHub profile)
* Twitter handle
#### Limitations
1. The Application does not support persistence of the `User` object.
2. The Application does not consider any other than the `primary` email of the GitHub User when updating the Freshdesk Contact
3. Does not support the transfer of the GitHub profile [Avatar](https://docs.github.com/en/rest/users/users#get-the-authenticated-user) <sup>(see "Example Response" -> `"avatar_url"`)</sup><br> as [FreshDesk Contact Avatar](https://developers.freshdesk.com/api/#create_contact_with_avatar).
4. Limited Unit Tests
5. When deleting a Contact, sometimes the email remains reserved and remains so after 24 hours. The recurrence of this remains unclear, but causes the following results in the Application:
* The `setFreshdeskContactIDByEmail()` Method of the User Object does not set the `freshdesk_id` as the `GET` Request returns an Empty JSON Array (`[]`] in the Request Body.
* This causes the `saveToFreshdesk()` method to be called, which then returns a `409 Conflict`, as in the Request Response. 
* This Causes the Program to return `"Error Registering User. Check if user with such Emails and Twitter already exists."`
* A short video clip of this issue is [uploaded](https://github.com/shkenderov/QuickbaseJava/blob/master/FreshDeskAPIBug.mp4).
