## Configuration ##

$JETTY\_HOME/webapps/root/WEB-INF/web.xml now contains a list of properties files for Davis. The parameter 'config-files' is a comma separated list of properties files to be read by Davis. Any items in later files will override identical items in earlier files. This allows configuration to be done in an hierarchical manner with general properties being specified in earlier files and more specific organisation or host related properties being encapsulated in files which appear later in the list.

The default set of properties files is davis.properties, davis-organisation.properties, davis-host.properties, davis-dev.properties, but any set of files may be specified instead. If these file names are given in relative form, Davis will expect to find it in the same directory as web.xml. Otherwise an absolute path may be given.

The current Davis distribution includes examples of these files in the same directory as web.xml. These files contain:

> davis.properties::
> > Contains all configurable items and their default values. It is recommended that this file be left as is and overrides be specified in later files.


> davis-organisation.properties::
> > Contains organisation specific overrides. eg. 'organisation-name'


> davis-host.properties::
> > Contains host specific overrides. eg. 'server-name'


> davis-dev.properties::
> > Reserved for developer overrides.

To create a new Davis configuration, you may like to look through davis.properties first to see the range of configurable items. Next you'll need to edit the example organisation and host files, making changes where appropriate. Common items which may require editing appear in the table below.

| **Param name** | **Description** |
|:---------------|:----------------|
|server-type|server type, ''srb'' or ''irods'', lower case|
|default-idp|default IdP name for shibboleth authentication|
|server-name|default server name|
|default-domain|default domain, for password login|
|default-resource|default resource to use in this order: <br>1. resource_name equals this value; <br>2. resource_name contains this value; <br>3. first resource in the list<br>
<tr><td>proxy-host</td><td>proxy host for slcs, set to empty if no proxy is needed</td></tr>
<tr><td>proxy-port</td><td>proxy port for slcs, set to empty if no proxy is needed</td></tr>
<tr><td>proxy-username</td><td>proxy username for slcs, set to empty if no proxy is needed</td></tr>
<tr><td>proxy-password</td><td>proxy password for slcs, set to empty if no proxy is needed</td></tr>
<tr><td>webdavis.Log.threshold</td><td>log level, set to DEBUG if you want all information, otherwise set to ERROR</td></tr>
<tr><td>anonymousCredentials</td><td>Specifies an account to use for anonymous browsing. Syntax is username:password</td></tr>
<tr><td>anonymousCollections</td><td>Specifies collections for anonymous browsing. Separated by comma, e.g. /ARCS/projects/public,/ARCS/projects/open</td></tr></tbody></table>

A full list of configurable items can be found in the attachment below.<br>
<br>
Log files are retained for 30 days by default. If you want to change that, have a look at $JETTY_HOME/etc/jetty-logging.xml<br>
<pre><code>    &lt;New id="ServerLog" class="java.io.PrintStream"&gt;<br>
      &lt;Arg&gt;<br>
        &lt;New class="org.mortbay.util.RolloverFileOutputStream"&gt;<br>
          &lt;Arg&gt;&lt;SystemProperty name="jetty.home" default="."/&gt;/logs/yyyy_mm_dd.stderrout.log&lt;/Arg&gt;<br>
          &lt;Arg type="boolean"&gt;false&lt;/Arg&gt;<br>
          &lt;Arg type="int"&gt;30&lt;/Arg&gt;<br>
          &lt;Arg&gt;&lt;Call class="java.util.TimeZone" name="getTimeZone"&gt;&lt;Arg&gt;GMT&lt;/Arg&gt;&lt;/Call&gt;&lt;/Arg&gt;<br>
          &lt;Get id="ServerLogName" name="datedFilename"/&gt;<br>
        &lt;/New&gt;<br>
      &lt;/Arg&gt;<br>
    &lt;/New&gt;<br>
</code></pre>
Change the value "30" to any number you want.<br>
<br>
You need to '''restart''' jetty after changing either of the xml files or any of the configuration parameter files.<br>
<br>
<h3>Configuration for real shibboleth authentication</h3>
<table><thead><th> <b>Param name</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td>insecureConnection</td><td>Indicates which authentication method will be used if users establish an insecure connection (HTTP). The supported values are:<br>shib (shibboleth authentication)<br>basic (basic authentication, really not secure, use it if you know what you are doing!)<br>block (doesn't allow insecure connections)<br>The default value is: block</td></tr>
<tr><td>shared-token-header-name</td><td>HTTP header name of shared token, this is a unique ID to identify an IdP user, you can use something else but it has to be unique</td></tr>
<tr><td>cn-header-name</td><td>HTTP header name of common name, this is a display name for an IdP user</td></tr>
<tr><td>admin-cert-file</td><td>Cert file path that is used to establish a connection as rodsadmin</td></tr>
<tr><td>admin-key-file</td><td>Key file path that is used to establish a connection as rodsadmin</td></tr></tbody></table>

We need a few changes to the iRODS server.<br>
<br>
<ol><li>The admin cert/key file is a certificate that is set to a rodsadmin user, e.g. rods. (rods is the only user we can use now, due to some issues in jargon) I suggest we use iRODS server certificate, since it is already on the server and secure. Then use the following command to set it to rods. Then Davis can use rods to find/create users.<br>
<pre><code>iadmin moduser rods DN "&lt;SERVER_DN&gt;"<br>
</code></pre>
</li><li>Put createUser script in <br>
<br>
<IRODS_HOME><br>
<br>
/server/bin/cmd. This script accepts three parameters, common name, shared token, and random password. You can use the attached script, remember to change its name to '''createuUser'''. This script will find an existing account with the given shared token. If an account is found, the random password is set to it, then the user will log in with this account. If no account is found, a new one will be created according to your common name. Then the random password is set and the user uses it to log in.</li></ol>

<br />


<h2>See also:</h2>
<blockquote>Configuration details for QuickShare.</blockquote>

<h2>attachments</h2>
<a href='http://code.google.com/p/webdavis/source/browse/wiki/attachments/configuration/davis.properties'>http://code.google.com/p/webdavis/source/browse/wiki/attachments/configuration/davis.properties</a>

<a href='http://code.google.com/p/webdavis/source/browse/wiki/attachments/configuration/createUser.pl-v3.04'>http://code.google.com/p/webdavis/source/browse/wiki/attachments/configuration/createUser.pl-v3.04</a>