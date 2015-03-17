## Evaluation ##

Server: Sun X4150, 2 Quad-Core CPU (3.12GHz), 16MB memory, CentOS 5<br>
Client: a CentOS 5 VM with 256M<br>
Network: 1Gbps<br>
<br>
<h3>Stability Test</h3>
Transfer a 1G file 30 times continuously, including upload and download.<br>

<img src='http://webdavis.googlecode.com/svn/wiki/attachments/evaluation/1gtest.png' />

Transfer a 500M file 30 times continuously, including upload and download.<br>

<img src='http://webdavis.googlecode.com/svn/wiki/attachments/evaluation/500mtest.png' />

Upload/download files (4M, 8M, 16M, 32M, 64M, 128M, 256M, 512M, 1024M, 2048M) 20 times continuously with iput, iget, curl(HTTP and HTTPS).[<a href='BR.md'>BR</a>]<br>
<br>
<img src='http://webdavis.googlecode.com/svn/wiki/attachments/evaluation/copytest.png' />

<h3>Stress Test</h3>
IOZone (<a href='http://www.iozone.org'>http://www.iozone.org</a>)<br>
<pre><code>[root@ngspare /tmp]# /usr/bin/iozone -i 0 -i 1 f /tmp/webdav/sss -s 1m -s 10m -s 100m -s 200m -s 500m -r 256 -s 700m -s 900m -s 1000m<br>
        Iozone: Performance Test of File I/O<br>
                Version $Revision: 3.315 $<br>
                Compiled for 32 bit mode.<br>
                Build: linux<br>
<br>
        Contributors:William Norcott, Don Capps, Isom Crawford, Kirby Collins<br>
                     Al Slater, Scott Rhine, Mike Wisner, Ken Goss<br>
                     Steve Landherr, Brad Smith, Mark Kelly, Dr. Alain CYR,<br>
                     Randy Dunlap, Mark Montague, Dan Million, Gavin Brebner,<br>
                     Jean-Marc Zucconi, Jeff Blomberg, Benny Halevy,<br>
                     Erik Habbinga, Kris Strecker, Walter Wong, Joshua Root.<br>
<br>
        Run began: Sat Apr  4 21:42:35 2009<br>
<br>
        File size set to 1024 KB<br>
        File size set to 10240 KB<br>
        File size set to 102400 KB<br>
        File size set to 204800 KB<br>
        File size set to 512000 KB<br>
        Record Size 256 KB<br>
        File size set to 716800 KB<br>
        File size set to 921600 KB<br>
        File size set to 1024000 KB<br>
        Command line used: /usr/bin/iozone -i 0 -i 1 -s 1m -s 10m -s 100m -s 200m -s 500m -r 256 -s 700m -s 900m -s 1000m f /tmp/webdav/sss<br>
        Output is in Kbytes/sec<br>
        Time Resolution = 0.000001 seconds.<br>
        Processor cache size set to 1024 Kbytes.<br>
        Processor cache line size set to 32 bytes.<br>
        File stride size set to 17 * record size.<br>
                                                            random  random    bkwd   record   stride<br>
              KB  reclen   write rewrite    read    reread    read   write    read  rewrite     read   fwrite frewrite   fread  freread<br>
            1024     256   78952 1110526  1962003  3251552<br>
           10240     256  369473  912659  2207813  2455016<br>
          102400     256  334929  860526  2882312  2683717<br>
          204800     256   98627   99702    54287    55984<br>
          512000     256   39523   45810    25613    58536<br>
          716800     256   47975   50485    54618    56984<br>
          921600     256   50037   50389    59173    65754<br>
         1024000     256   52920   51370    65720    56534<br>
<br>
iozone test complete.<br>
</code></pre>
<pre><code>[root@ngspare /tmp]# /usr/bin/iozone -i 0 -i 1 f /tmp/nfs/test/sss -s 1m -s 10m -s 100m -s 200m -s 500m -r 256 -s 700m -s 900m -s 1000m<br>
        Iozone: Performance Test of File I/O<br>
                Version $Revision: 3.315 $<br>
                Compiled for 32 bit mode.<br>
                Build: linux<br>
<br>
        Contributors:William Norcott, Don Capps, Isom Crawford, Kirby Collins<br>
                     Al Slater, Scott Rhine, Mike Wisner, Ken Goss<br>
                     Steve Landherr, Brad Smith, Mark Kelly, Dr. Alain CYR,<br>
                     Randy Dunlap, Mark Montague, Dan Million, Gavin Brebner,<br>
                     Jean-Marc Zucconi, Jeff Blomberg, Benny Halevy,<br>
                     Erik Habbinga, Kris Strecker, Walter Wong, Joshua Root.<br>
<br>
        Run began: Sat Apr  4 21:49:36 2009<br>
<br>
        File size set to 1024 KB<br>
        File size set to 10240 KB<br>
        File size set to 102400 KB<br>
        File size set to 204800 KB<br>
        File size set to 512000 KB<br>
        Record Size 256 KB<br>
        File size set to 716800 KB<br>
        File size set to 921600 KB<br>
        File size set to 1024000 KB<br>
        Command line used: /usr/bin/iozone -i 0 -i 1 -s 1m -s 10m -s 100m -s 200m -s 500m -r 256 -s 700m -s 900m -s 1000m f /tmp/nfs/test/sss<br>
        Output is in Kbytes/sec<br>
        Time Resolution = 0.000001 seconds.<br>
        Processor cache size set to 1024 Kbytes.<br>
        Processor cache line size set to 32 bytes.<br>
        File stride size set to 17 * record size.<br>
                                                            random  random    bkwd   record   stride<br>
              KB  reclen   write rewrite    read    reread    read   write    read  rewrite     read   fwrite frewrite   fread  freread<br>
            1024     256  192014 1223356  2730873  4162469<br>
           10240     256  393741  935754  2587183  2793250<br>
          102400     256  332111  842083  2846109  2891426<br>
          204800     256  102254  108898    61124    60275<br>
          512000     256   56108   51387    58677    63079<br>
          716800     256   54376   57618    61197    63648<br>
          921600     256   53312   46257    37116    66973<br>
         1024000     256   50486   49398    65675    53385<br>
<br>
iozone test complete.<br>
</code></pre>
<pre><code>[root@ngspare /tmp]# /usr/bin/iozone -i 0 -i 1 f /tmp/samba/ -s 1m -s 10m -s 100m -s 200m -s 500m -r 256 -s 700m -s 900m -s 1000m<br>
        Iozone: Performance Test of File I/O<br>
                Version $Revision: 3.315 $<br>
                Compiled for 32 bit mode.<br>
                Build: linux<br>
<br>
        Contributors:William Norcott, Don Capps, Isom Crawford, Kirby Collins<br>
                     Al Slater, Scott Rhine, Mike Wisner, Ken Goss<br>
                     Steve Landherr, Brad Smith, Mark Kelly, Dr. Alain CYR,<br>
                     Randy Dunlap, Mark Montague, Dan Million, Gavin Brebner,<br>
                     Jean-Marc Zucconi, Jeff Blomberg, Benny Halevy,<br>
                     Erik Habbinga, Kris Strecker, Walter Wong, Joshua Root.<br>
<br>
        Run began: Sat Apr  4 22:07:16 2009<br>
<br>
        File size set to 1024 KB<br>
        File size set to 10240 KB<br>
        File size set to 102400 KB<br>
        File size set to 204800 KB<br>
        File size set to 512000 KB<br>
        Record Size 256 KB<br>
        File size set to 716800 KB<br>
        File size set to 921600 KB<br>
        File size set to 1024000 KB<br>
        Command line used: /usr/bin/iozone -i 0 -i 1 -s 1m -s 10m -s 100m -s 200m -s 500m -r 256 -s 700m -s 900m -s 1000m f /tmp/samba/<br>
        Output is in Kbytes/sec<br>
        Time Resolution = 0.000001 seconds.<br>
        Processor cache size set to 1024 Kbytes.<br>
        Processor cache line size set to 32 bytes.<br>
        File stride size set to 17 * record size.<br>
                                                            random  random    bkwd   record   stride<br>
              KB  reclen   write rewrite    read    reread    read   write    read  rewrite     read   fwrite frewrite   fread  freread<br>
            1024     256  409101 1269048  2908473  4355745<br>
           10240     256  397654  952738  2896745  3034177<br>
          102400     256  310717  866591  2897652  2958682<br>
          204800     256  103238  108725    65314    60771<br>
          512000     256   57924   53948    56908    54876<br>
          716800     256   56819   51787    66512    66636<br>
          921600     256   52569   52768    49531    65012<br>
         1024000     256   52013   50930    56612    67807<br>
<br>
iozone test complete.<br>
</code></pre>

<img src='http://webdavis.googlecode.com/svn/wiki/attachments/evaluation/stresstest.png' />