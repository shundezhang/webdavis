# User Authentication #

Davis supports traditional username/password and GSI authentication, as well the latest Shibboleth authentication. In order to be compatible with existing WebDAV clients, HTTP basic authentication must be used. To protect the password sent by basic authentication, we use HTTPS. There is no need to use HTTPS for direct Shibboleth login, as the connection to IdP is via HTTPS. After authentication is finished, HTTP can be used for data transfer, given that the data is not important or confidential.

![http://webdavis.googlecode.com/svn/wiki/attachments/general/login.png](http://webdavis.googlecode.com/svn/wiki/attachments/general/login.png)

## HTTP Access ##

When accessing Davis via HTTP, users are directed to WAYF. In this case, Davis is set up as a normal SP with Apache and mod\_shib. IdPs have to release some compulsory attributes to Davis so that Davis can identify the user. These attributes are CN (may be changed to First Name/Last Name) and Shared Token. CN is used to construct a username for iRODS, while the Shared Token is used as a unique ID for a particular user. Because of the limitation of current WebDAV clients, this only works for web browsers (and any clients support Shibboleth).

The procedure is:
  * User accesses Davis via HTTP
  * User is redirected to WAYF (HTTPS)
  * User selects an IdP (HTTPS)
  * User is redirected to IdP login page (HTTPS)
  * User enters username/password (HTTPS)
  * If success, User is redirected back to Davis (HTTP)
  * Davis gets CN and sharedToken from HTTP header
  * Davis executes createUser script (in a rule) with three arguments, CN, sharedToken and a random password
  * createUser script checks whether there is a user with the same sharedToken (sharedToken is stored in ''USER\_INFO'' field). If yes, update CN if needed (e.g. user has changed the surname because of marriage). If no, create a new user with the CN.
  * createUser script sets the random password to that user
  * Davis queries the user with sharedToken, and will find a user if the script runs correctly
  * User logs in with this user and the random password

## HTTPS Access ##
To use Davis with existing WebDAV clients, users have to use HTTP basic authentication with HTTPS protecting the password (hence it protects the data too). As there are only two text boxes in the basic authentication login popup, we need to include additional information in the username text box, so that it can support various authentication methods.

The procedure is:
  * User accesses Davis via HTTPS
  * User gets a popup asking username/password (if the user has not logged in)
  * User enters credentials
  * Davis analyzes the value of username box to see if there is a slash(/) or back-slash(\)
    * If no, this is a username of iRODS's user
    * If yes, this is an "enhanced" username. Davis retrieves the string before slash and the string after slash. GSI authentication will be used.
      * If the first string is "myproxy", the second string is a username to retrieve a proxy from myproxy server. Then a proxy will be obtained from myproxy server.
      * If not, the first string is the name of an IdP, Davis uses SLCS-Client to check whether there is such IdP, if yes, the second string is the username for this IdP, and SLCS-Client will retrieve a SLCS certificate from SLCS server.
      * If any of these fails, user will be prompted for username/password again.