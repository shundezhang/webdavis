## Dynamic Configuration Reload ##

As of version 0.9.3, Davis supports dynamic reloading of its configuration and the user interface (UI). This is achieved by adding a '?reload-config' query string to a request. When Davis receives a request with this query parameter it will immediately reload its configuration and the user interface html file.

Note that Davis now recognises a config parameter 'administrators'. This parameter is a comma separated list of users who can perform a reload-config operation. If a user is not in this list, a reload-config request will be ignored.


The recommended approach for updating the UI is as follows:

  1. Produce a new ui.html and add it to the davis installation as ''davisroot''/webapps/root/WEB-INF/ui-new.html
  1. In ''davisroot''/webapps/root/WEB-INF:
```
cp ui.html ui.html.orig
ln -sf ui-new.html ui.html
```
> 3. Access the reload URL - ''davishomeURL''?reload-config
```
eg. https://df.arcs.org.au/ARCS/home?reload-config
```
  1. Davis will immediately reload its configuration and ui.html and subsequently serve that.

Step 2 doesn't affect existing sessions because ui.html is loaded into memory once when Davis is first started and is then not needed.