Davis + OPeNDAP

Davis essentially turns an SRB folder into a locally accessible folder.  Using this, we have been able to server files stored in SRB through OPeNDAP.  This has been tested on Mac OS X, using Java 1.5.0\_13 and running the [THREDDS Data Server](http://www.unidata.ucar.edu/projects/THREDDS/) (TDS).


To map your SRB folder, follow the instruction here:
[Howto - General](General.md)

The directory is now accessible on your machine as /Volumes/~.  Depending on how you have specified your SRB collection, the folder name may be something other than tilda (~).

To install a THREDDS Data Server, follow the installation [instructions](http://www.unidata.ucar.edu/projects/THREDDS/tech/tutorial/schedule.html) on the UCAR website.

For this example, I created a new directory called "opendap" under my home directory in SRB, where I copied a number of NetCDF files (all of these end with the .nc extension).

To configure the dataset, edit the catalog.xml file.  This should be under /usr/local/apache-tomcat/contents/thredds/catalog.xml.  Add the following dataset definition to your catalog:

```
<datasetScan name="Davis Test dataset scan" ID="DavisTest" path="DavisTest" location="/Volumes/~/opendap/">
    <metadata inherited="true">
        <serviceName>allServices</serviceName>
        <authority>TPAC</authority>
             <dataType>Grid</dataType>
            <dataFormat>NetCDF</dataFormat>
    </metadata>
    <filter>
        <include wildcard="*.nc" />
    </filter>
</datasetScan>
```

Restart Tomcat.

To see if your newly added dataset is working, open up a browser and browse to http://localhost:8080/thredds/catalog/DavisTest/catalog.html and you should see a list of NetCDF files, something like the following:

![http://webdavis.googlecode.com/svn/wiki/attachments/opendap/catalog.jpg](http://webdavis.googlecode.com/svn/wiki/attachments/opendap/catalog.jpg)

The following is an example of looking at a file through OPeNDAP - the dataset view and the OPeNDAP query form.

![http://webdavis.googlecode.com/svn/wiki/attachments/opendap/file.jpg](http://webdavis.googlecode.com/svn/wiki/attachments/opendap/file.jpg)

![http://webdavis.googlecode.com/svn/wiki/attachments/opendap/form.jpg](http://webdavis.googlecode.com/svn/wiki/attachments/opendap/form.jpg)

The following is a screencap GoogleEarth with a WMS overlay of a NCEP1 variable.  It is generated through a modified TDS using ([WMS](http://www.opengeospatial.org/standards/wms)).  The server is reading data which is stored in SRB which has been mapped to a directory accessibly by TDS using Davis.

![http://webdavis.googlecode.com/svn/wiki/attachments/opendap/googleEarth.jpg](http://webdavis.googlecode.com/svn/wiki/attachments/opendap/googleEarth.jpg)