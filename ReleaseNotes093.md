New features:

  * Logout button to end a Davis session.

  * Adding a query parameter ''embed=true'' causes the UI to appear without the items above the breadcrumb.

  * New config item ''disable-replicas-button'' controls whether the replicas button will appear in the UI.

  * New config items to 'ghost' any number of leading segments in the UI breadcrumb trail. See ''ghost-breadcrumb'' and ''ghost-trash-breadcrumb'' in davis.properties for more details.

  * ''reload-ui'' mechanism (via URL query parameter) for reloading the UI has been replaced with ''reload-config'' query parameter which re-reads Davis' configuration and then reloads the UI. This feature is useful for changing the Davis or Jargon logging levels without restarting Davis. See [wiki:HowTo/DynamicUIReload Dynamic Configuration Reload] for details.

  * New config parameter ''administrators'' lists which users can perform a reload-config operation. See [wiki:HowTo/DynamicUIReload Dynamic Configuration Reload] for details.

  * The Replicas button is now disabled for users by default, but visible for Davis administrators.

  * Copy button added.

  * New config items ''ui-include-head'', ''ui-include-body-header'' and ''ui-include-body-footer'' allow javascript and html fragments to be included in the UI. See davis.properties for details.

  * Progress indication added to upload dialog.

  * QuickShare facility allows users to easily share files with others. See [wiki:QuickShare] for details.

  * Login button provided for anonymous user to login to user's real account.

  * New config items webdavUserAgents and browserUserAgents allow a comma separated list of user-agent strings to be specified. When a client connects and provides a user-agent header beginning with one of the specified strings, Davis will treat it as either a WebDAV or web browser client accordingly. See davis.properties for more details. The example davis-organisation.properties file includes some useful strings for these items.

  * The owner of a resource is now shown in the permissions dialog.

[[BR](BR.md)]

Bug Fixes and Updates:

  * When downloading a file, Davis now handles iRODS resources being unavailable by returning a 404 with message, rather than returning a zero length file.

  * When downloading, Davis now checks that there's at least one 'up' or 'auto-up' resource. If not, it returns a 404 with message.

  * After the Davis server is restarted, a client which was talking to the server before the restart may make a server call which references non existent cached data. In this case the UI will now notify the user that it is out of sync and then reload the browser page.

  * When the breadcrumb is very long and wraps over several lines, the buttons layout is no longer messed up.

  * UI handles Davis server going offline better. If the server is taken down during a session, the UI will notify the user.

  * Davis warns user that most browsers can't upload >2GB files, and in most cases can detect the resulting stalled upload.

  * Home and trash buttons disappear for anonymous access.

  * Jargon now handles recursion when recursive access control operations are performed in the web interface. This should result in faster completion for large collections.

  * Davis config values may now all include leading and trailing whitespace. Previously, boolean and password items weren't trimmed.

  * Updated Davis REST document.