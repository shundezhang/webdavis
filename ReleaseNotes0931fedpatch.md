## Patched UI for Federated Zones ##

A post on davis-chat (thanks Edwin) correctly pointed out that navigation in the Davis web UI fails in the root directory. As we've never used Davis with federated zones, we've never configured Apache to allow access that high in the directory tree!

The patch to fix this for Davis 0.9.3.1 can be installed by replacing webapps/root/WEB-INF/ui.html with the [fixed version](https://projects.arcs.org.au/trac/davis/attachment/wiki/WikiStart/ui.html) in your Davis installation. This fix will also be incorporated into the next Davis release.