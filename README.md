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
### Test
	gradle test
### Run
	 gradle run
