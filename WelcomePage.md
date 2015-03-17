# Welcome to Davis (A WebDAV-iRODS/SRB gateway) #

Davis is a Java web application aiming to provide a webdav interface for accessing the SDSC iRods/SRB data grid. Its [wiki:idea idea] is "Enabling iRODS/SRB to the rest of the world", with the use of general Webdav protocol and HTTP protocol.

iRODS/SRB is widely used by many research groups in the world. Quite a few different clients have been developed, e.g. iCommands/Scommands, Hermes, JUX. However, they all required a third-party software, and a non-HTTP port, 1247/5544. The issues are: some software may require a certain IT knowledge to install; some sites may have firewall blocking non-HTTP(S) access.

[wiki:Webdav Webdav] is a protocol designed for web page development. It offers basic file management methods, such as directory browsing, basic file information retrieving, creating/deleting directories, file upload/download. Also it runs on port 80(HTTP) or 443(HTTPS), which is not likely be blocked by firewalls (For some organizations, a http proxy will be used).

## Features ##

  * Webdav version 1, 2. Supports locking mechanism.
  * Supports Windows, Linux and Mac
  * iRODS and Shibboleth authentication with WAYF
  * Supports user password and Shibboleth authentication on SRB
  * Uses tilde(~) for home directory
  * Supports browser mode, where users can download and browse files
  * Change permissions, add/modify/delete metadata in Browse mode
  * Support multi-threaded and resumable download, e.g. using [FlashGet](http://www.flashget.com/download.htm)

## How to Use ##

### [General](General.md) ###

### [Windows](Windows.md) ###

### [Mac](Mac.md) ###

### [Linux](Linux.md) ###

### [Browser Mode](Browser.md) ###

### [iPhone](iPhone.md) ###

### [Symbian S60](SymbianS60.md) ###

### [Command-line Clients](CLI.md) ###

### [OpenDAP](OpenDAP.md) ###

### [REST API](REST.md) ###

## How to Install ##

### [Installation](Installation.md) ###

### [Configuration](Configuration.md) ###

### [Update](Update.md) ###

### [QuickShare](QuickShare.md) ###

### [Dynamic Objects](DynamicObjects.md) ###

### [Dynamic Configuration Reload](DynamicUIReload.md) ###

### [Anonymous Access](AnonymousAccess.md) ###

### [Shibboleth SP installation and integration with Davis](Shibboleth.md) ###

### [Evaluation](Evaluation.md) ###

### [Publications & Presentations](Publications.md) ###

## Release Notes ##

  * [0.9.6](ReleaseNotes096.md) - coming soon!
  * [0.9.5](ReleaseNotes095.md) - 28 June 2012
  * [0.9.4](ReleaseNotes094.md) - 2 February 2011
  * [0.9.3.1 federated zones UI patch](ReleaseNotes0931fedpatch.md) - 21 January 2011
  * [0.9.3.1](ReleaseNotes0931.md) - 16 December 2010
  * [0.9.3](ReleaseNotes093.md) - 3 December 2010
  * [0.9.2](ReleaseNotes092.md) - 6 August 2010
  * [0.9.1](ReleaseNotes091.md) - 6 May 2010
  * [0.8.2](ReleaseNotes082.md) - 25 November 2009
  * [0.8.1](ReleaseNotes081.md) - 2 November 2009
  * [0.7.3](ReleaseNotes073.md) - 7 August 2009
  * [0.7.2](ReleaseNotes072.md) - 16 June 2009
  * [0.7.1](ReleaseNotes071.md) - 5 June 2009
  * [0.7.0](ReleaseNotes070.md) - 25 May 2009
  * [0.6.1](ReleaseNotes061.md) - 13 January 2009
  * [0.6.0](ReleaseNotes060.md) - 8 December 2008
  * [0.5.1](ReleaseNotes051.md) - 5 December 2008
  * [0.5.0](ReleaseNotes050.md) - 23 November 2008

## Contact ##

For any problems or comments, please contact me at shunde(dot)zhang(at)arcs(dot)org(dot)au

## Mailing List ##

You can join the user mailing list: davis-chat@googlegroups.com

Mailing list website: http://groups.google.com/group/davis-chat

## License ##

This software is open source and under [Apache 2](http://www.apache.org/licenses/LICENSE-2.0.html) and [LGPL](http://www.gnu.org/copyleft/lesser.txt).

## Acknowledgment ##

Davis is implemented with the source code of a famous Webdav-SMB gateway software, [Davenport](http://davenport.sourceforge.net/). Many thanks to the author, Eric Glass.