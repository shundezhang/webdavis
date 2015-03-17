Linux users use '''Konqueror''', '''Gnome Nautilus''' or '''DAVFS''' as webdav client.

## Konqueror ##
To connect to WebDAV server using Konqueror, type the url in the address field starting with webdav:// for HTTP or starting with webdavs:// for HTTP/SSL. You can also use Konqueror Location->Open Location menu.

## Gnome Nautilus ##
To connect to WebDAV server using Nautilus, in File menu select Connect to Server. In the Service Type field select WebDAV (HTTP) or Secure WebDAV (HTTPS) and fill in at last Server field.
![http://webdavis.googlecode.com/svn/wiki/attachments/linux/linux-1.png](http://webdavis.googlecode.com/svn/wiki/attachments/linux/linux-1.png)

![http://webdavis.googlecode.com/svn/wiki/attachments/linux/linux-2.png](http://webdavis.googlecode.com/svn/wiki/attachments/linux/linux-2.png)

![http://webdavis.googlecode.com/svn/wiki/attachments/linux/linux-3.png](http://webdavis.googlecode.com/svn/wiki/attachments/linux/linux-3.png)

'''NOTICE''':

There is a bug in gnome-utils 2.24: https://bugs.launchpad.net/ubuntu/+source/nautilus/+bug/222532

You need to use the ''Custom Location'' Option for this version, and the URI is like davs://server\_name/~.

![http://webdavis.googlecode.com/svn/wiki/attachments/linux/linux-4.png](http://webdavis.googlecode.com/svn/wiki/attachments/linux/linux-4.png)

![http://webdavis.googlecode.com/svn/wiki/attachments/linux/linux-5.png](http://webdavis.googlecode.com/svn/wiki/attachments/linux/linux-5.png)

## DAVFS ##
Advanced users can use DAVFS.<br />
http://www.sfu.ca/itservices/linux/webdav-linux.html <br />
http://dav.sourceforge.net/

Note: davfs2 has trouble uploading (putting) files to the server. Uploads result in zero length files, probably due to incorrectly implemented locking in Davis. To avoid this issue, you need to add a configuration item to your ~/.davfs2/davfs2.conf file as follows:


```
use_locks       0
drop_weak_etags   1
```


This will disable the use of locks and so will require care if files are accessed from multiple places simultaneously.

Note: To prevent davfs2 from timing out when very large directories (thousands of files) are being used, it may be necessary to add the following configuration item to your ~/.davfs2/davfs2.conf file:


```
read_timeout    300
```


This will increase the read timeout period to 5 minutes rather than the default 30 seconds.
> <br />
User generated docs:

http://webdavis.googlecode.com/svn/wiki/attachments/linux/arcs-df-filesystem_HOWTO.odt

http://webdavis.googlecode.com/svn/wiki/attachments/linux/How%20to%20get%20webdav%20working%20on%20a%20Clark%20Connect%20server%20running%204.doc