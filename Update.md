It is not necessary to reinstall for each release. Most of the time, you only need to

  1. stop davis.
  1. download davis.jar and put it in DAVIS\_HOME/webapps/root/WEB-INF/lib. (sometimes web.xml as well)
  1. start davis.

However, Davis now has an easy configuration script which makes a full re-installation much easier. To do a full re-install:

```
service davis stop
cd /opt/davis/
wget http://webdavis.googlecode.com/files/davis-*version*.tar.gz
tar -zxvf davis-*version*
rm davis
ln -s /opt/davis/davis-*version*/ davis
cd /opt/davis/davis/bin
sh davis-configure.sh
```

Then modify slcs-client.properties to point to the appropriate slcs server:

```
cd /opt/davis/davis/webapps/root/WEB-INF/classes/

slcs.server=https://*slcs server*/SLCS/login
ssl.blindtrust=false
```

Now check the settings in the davis host config file at /opt/davis/etc/host-local.properties.

And finally:

```
service davis start
```