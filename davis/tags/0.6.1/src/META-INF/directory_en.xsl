<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:D="DAV:">
    <xsl:output method="html"/>
    <xsl:param name="dojoroot"/>
    <xsl:param name="servertype"/>
    <xsl:param name="href"/>
    <xsl:param name="url"/>
    <xsl:param name="unc"/>
    <xsl:param name="parent"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>Davis - <xsl:value-of select="$url"/></title>
                <meta HTTP-EQUIV="Pragma" CONTENT="no-cache"/>
                <meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache"/>
                <meta HTTP-EQUIV="Expires" CONTENT="0"/>
    			<style type="text/css">
    @import "<xsl:value-of select="$dojoroot"/>dojoroot/dijit/themes/tundra/tundra.css";
    @import "<xsl:value-of select="$dojoroot"/>dojoroot/dojox/grid/resources/Grid.css";
    @import "<xsl:value-of select="$dojoroot"/>dojoroot/dojox/grid/resources/tundraGrid.css";
    @import "<xsl:value-of select="$dojoroot"/>dojoroot/dojo/resources/dojo.css"
    			</style>
                <style>
    body {
        font-family: Verdana, Tahoma, Helvetica, Arial, sans-serif;
        background: white;
        font-size: 10pt;
    }
    p {
        font-size: 10pt;
    }
    td {
        font-family: Verdana, Tahoma, Helvetica, Arial, sans-serif;
        font-size: 10pt;
    }
    a {
        font-family: Verdana, Tahoma, Helvetica, Arial, sans-serif;
        color: black;
        text-decoration: none;
    }
    a:hover {
        color: green;
    }
    a.hidden {
        font-style: italic;
    }
    a.directory {
        font-weight: bold;
        color: green;
    }
    a.directory:hover {
        color: black;
    }
    a.hiddendirectory {
        font-weight: bold;
        color: #99aa88;
    }
    a.hiddendirectory:hover {
        color: #777777;
    }
    a.parent {
        font-weight: bold;
        color: green;
    }
    a.parent:hover {
        color: #bbccaa;
    }
    .properties {
        font-size: 8pt;
    }
    a.title {
        behavior: url(#default#AnchorClick);
        font-size: 16pt;
        font-weight: bold;
        color: green;
    }
    a.title:hover {
        color: #bbccaa;
    }
    a.unc {
        behavior: url(#default#AnchorClick);
        font-size: 10pt;
        font-weight: bold;
        color: black;
    }
    a.unc:hover {
        color: green;
    }
    
    .dojoxGrid-row-odd td {
        background:#e8f2ff;
	}
    #metadataGrid {
        border: 1px solid #333;
        width: 400px;
        height: 300px;
    }
	#permissionGrid {
        border: 1px solid #333;
        width: 600px;
        height: 200px;
    }
    table { 
    	border: none; 
    }
    
                </style>
    			<xsl:text disable-output-escaping="yes">&lt;script type="text/javascript" src="</xsl:text><xsl:value-of select="$dojoroot"/><xsl:text disable-output-escaping="yes">dojoroot/dojo/dojo.js" djConfig="isDebug: false, parseOnLoad: true">&lt;/script></xsl:text>
    			<script type="text/javascript">
    // Load Dojo's code relating to the Button widget
    dojo.require("dojox.grid.compat.Grid");
    dojo.require("dojox.grid.DataGrid");
	dojo.require("dojox.grid.compat._grid.edit");
    dojo.require("dojo.data.ItemFileWriteStore");
    dojo.require("dojo.data.ItemFileReadStore");
    dojo.require("dijit.form.Button");
    dojo.require("dijit.Dialog");
	dojo.require("dijit.form.TextBox");
	dojo.require("dojo.parser");
	dojo.require("dijit.form.FilteringSelect");
    var layout1= [{
			defaultCell: { editable: true, type: dojox.grid.cells._Widget, styles: 'text-align: left;'  },
			rows: [
        { field: "name", width: "200px", name: "Name", editable: true},
        { field: "value", width: "auto", name: "Value", editable: true}
        ]}
    ];

    var layout2= [
        { field: "username", width: "200px", name: "Username"},
        <xsl:if test="$servertype='srb'">
        { field: "domain", width: "200px", name: "Domain"},
        </xsl:if>
        { field: "permission", width: "auto", name: "Permission"}
    ];
	var model2 = new dojox.grid.data.Table(null, []);
	var store1=new dojo.data.ItemFileWriteStore({data: []});
	var store2=new dojo.data.ItemFileWriteStore({data: []});
	var store3=new dojo.data.ItemFileWriteStore({data: []});
	var store4=new dojo.data.ItemFileWriteStore({data: []});
	var server_url;
	var ori_url;
	
	function getDomains(urlString){
	  	dojo.xhrPost({
    		url: urlString,
    		load: function(responseObject, ioArgs){
      
//				console.dir(formDomain);  // Dump it to the console
//         		console.dir(responseObject.items[0].username);  // Prints username     			
				store3=new dojo.data.ItemFileWriteStore({data: responseObject});
				var formDomainObj = dijit.byId('formDomain');
				formDomainObj.store=store3;
				formDomainObj.onChange=function(val){
//					alert(server_url);
					dijit.byId('formUsername').textbox.value = "";
					dijit.byId('formUsername').valueNode.value = "";
					getUsers(ori_url+"?method=userlist&amp;domain="+formDomainObj.item.name);
//					console.dir(formDomainObj.item);
				}
//				alert("xx");
//      			formDomain.setStore(store3);
//       			return responseObject;
    		},
    		error: function(response, ioArgs){
      			alert("Error when loading domain list.");
      			return response;
    		},
    		handleAs: "json"
  		});
	}
	function getUsers(urlString){
	  	dojo.xhrPost({
    		url: urlString,
    		load: function(responseObject, ioArgs){
      
//				console.dir(formDomain);  // Dump it to the console
//         		console.dir(responseObject.items[0].username);  // Prints username     			
				store4=new dojo.data.ItemFileWriteStore({data: responseObject});
				var formUsernameObj = dijit.byId('formUsername');
				formUsernameObj.store=store4;
    		},
    		error: function(response, ioArgs){
      			alert("Error when loading domain list.");
      			return response;
    		},
    		handleAs: "json"
  		});
	}
	function getPermission(urlString){
	  	dojo.xhrPost({
    		url: urlString,
    		load: function(responseObject, ioArgs){
      
//				console.dir(responseObject);  // Dump it to the console
//         		console.dir(responseObject.items[0].username);  // Prints username     			
       			populatePermission(responseObject);
      			
//       			return responseObject;
    		},
    		error: function(response, ioArgs){
      			alert("Error when loading permissions.");
      			return response;
    		},
    		handleAs: "json"
  		});
	
	}
	function loadMetadataFromServer(urlString){
	  	dojo.xhrPost({
    		url: urlString,
    		load: function(responseObject, ioArgs){
      
//				console.dir(responseObject);  // Dump it to the console
//         		console.dir(responseObject.items[0].username);  // Prints username     			
				store1=new dojo.data.ItemFileWriteStore({data: responseObject});
				metadataGrid.setStore(store1);
      			
//       			return responseObject;
    		},
    		error: function(response, ioArgs){
      			alert("Error when loading metadata.");
      			return response;
    		},
    		handleAs: "json"
  		});
	}
	function populatePermission(perms){
		store2=new dojo.data.ItemFileWriteStore({data: perms});
		permissionGrid.setStore(store2);
//		alert(perms.sticky);
		if (perms.sticky!=null){
			if (perms.sticky=="true"){
				document.getElementById("stickyControl").innerHTML="This directory is sticky.&lt;button onclick=\"unsetSticky()\">Unset&lt;/button>";
			}else{
				document.getElementById("stickyControl").innerHTML="This directory is not sticky.&lt;button onclick=\"setSticky()\">Set&lt;/button>";
			}
		}else{
			document.getElementById("stickyControl").innerHTML="";
		}
	}
	function getMetadata(url){
		ori_url=url;
		server_url=url+"?method=metadata";
		loadMetadataFromServer(server_url);
		dijit.byId('dialog1').show();
	}
	function refreshMetadata(){
		loadMetadataFromServer(server_url);
	}
	function getFilePermission(url){
		ori_url=url;
		dojo.byId("recursive").disabled=true;
		if (document.getElementById("servertype").value=="srb"){
			server_url=url+"?method=domains";
			getDomains(server_url);
		}else{
			server_url=url+"?method=userlist";
			getUsers(server_url);
		}
		server_url=url+"?method=permission";
		getPermission(server_url);
		dijit.byId('dialog2').show();
	}
	function getDirPermission(url){
		ori_url=url;
		dojo.byId("recursive").disabled=false;
		if (document.getElementById("servertype").value=="srb"){
			server_url=url+"?method=domains";
			getDomains(server_url);
		}else{
			server_url=url+"?method=userlist";
			getUsers(server_url);
		}
		server_url=url+"?method=permission";
		getPermission(server_url);
		dijit.byId('dialog2').show();
	}
	function savePermission(){
		var recursiveValue="";
		var usernameValue=dojo.byId("formUsername").value;
		var domainValue;
		var permValue=dojo.byId("formPerm").options[dojo.byId("formPerm").selectedIndex].value;
		if (document.getElementById("servertype").value=="srb"){
			domainValue=dojo.byId("formDomain").value;
			if (!dijit.byId("formDomain").isValid()||domainValue==""){
				alert("Please enter a valid domain.");
				return;
			}
		}
		if (!dijit.byId("formUsername").isValid()||usernameValue==""){
			alert("Please enter a valid username.");
			return;
		}
		if (dojo.byId("recursive").disabled==false){
			recursiveValue="&amp;recursive="+(dojo.byId("recursive").checked);
		}
		var form_url;
		if (document.getElementById("servertype").value=="srb"){
			form_url=server_url+"&amp;username="+usernameValue+"&amp;domain="+domainValue+"&amp;permission="+permValue+recursiveValue;
		}else{
			form_url=server_url+"&amp;username="+usernameValue+"&amp;permission="+permValue+recursiveValue;
		}
//		alert(form_url);
		getPermission(form_url);
	}
	function setSticky(){
		var form_url=server_url+"&amp;sticky=true";
		getPermission(form_url);
	}
	function unsetSticky(){
		var form_url=server_url+"&amp;sticky=false";
		getPermission(form_url);
	}
	function getSelectedPermission(e){
		//console.dir(e.rowIndex);
		//console.dir(permissionGrid.getItem(e.rowIndex).username);
		//console.dir(store2.items[e.rowIndex]);
		//console.dir(dojo.byId("formUsername").value);
		dojo.byId("formUsername").value=permissionGrid.getItem(e.rowIndex).username;
		dojo.byId("formDomain").value=permissionGrid.getItem(e.rowIndex).domain;
		for (var i=0;i&lt;dojo.byId("formPerm").options.length;i++){
			if (dojo.byId("formPerm").options[i].text==permissionGrid.getItem(e.rowIndex).permission) 
				dojo.byId("formPerm").options[i].selected=true;
			else
				dojo.byId("formPerm").options[i].selected=false;
		}
	}
	function addMetadata(){
        // set the properties for the new item:
        var myNewItem = {name: "name", value: "value"};
        // Insert the new item into the store:
        // (we use store3 from the example above in this example)
        store1.newItem(myNewItem);
	}
	function delMetadata(){
        // Get all selected items from the Grid:
        var items = metadataGrid.selection.getSelected();
        if(items.length){
            // Iterate through the list of selected items.
            // The current item is available in the variable
            // "selectedItem" within the following function:
            dojo.forEach(items, function(selectedItem) {
                if(selectedItem !== null) {
                    // Delete the item from the data store:
                    store1.deleteItem(selectedItem);
                } // end if
            }); // end forEach
        } // end if
	}
	function saveMetadata(){
		var rowCount=dijit.byId('metadataGrid').rowCount;
		var data="[";
		for (var i=0;i&lt;rowCount;i++){
			if (i>0) data+=",";
			data+="{\"name\":\""+dijit.byId('metadataGrid').getItem(i).name+"\", \"value\":\""+dijit.byId('metadataGrid').getItem(i).value+"\"}";
		}
		data+="]";
//		alert(dijit.byId('metadataGrid').getItem(0).name);
//		alert("data:"+data);
	  	dojo.rawXhrPost({
    		url: server_url,
		    headers: {
		        "content-length": data.length,
		        "content-type": "text/x-json"
		    },
		    postData: data,
    		load: function(responseObject, ioArgs){
      
//				console.dir(responseObject);  // Dump it to the console
//         		console.dir(responseObject.items[0].username);  // Prints username     			
				store1=new dojo.data.ItemFileWriteStore({data: responseObject});
				metadataGrid.setStore(store1);
      			
//       			return responseObject;
    		},
    		error: function(response, ioArgs){
      			alert("Error when loading metadata.");
      			return response;
    		},
    		handleAs: "json"
  		});
	}
    			</script>		
            </head>
            <body class="tundra">
                <xsl:apply-templates select="D:multistatus"/>
                <input type="hidden" name="servertype" id="servertype">
	                <xsl:attribute name="value">
	                	<xsl:value-of select="$servertype"/>
	                </xsl:attribute>
                </input>
            <!-- Dialogs begin-->
            	<div dojoType="dijit.Dialog" id="dialog1" title="Metadata">
				<table>
					<tr>
						<td>
        					<button onclick="refreshMetadata()">Refresh</button>
        					<button onclick="addMetadata()">Add Metadata</button>
        					<button onclick="dijit.byId('metadataGrid').removeSelectedRows()">Remove Metadata</button>
        					<button onclick="saveMetadata()">Save</button>
        					<button onclick="dijit.byId('dialog1').hide()">Cancel</button>
						</td>
					</tr>
					<tr>
						<td>
							<div id="metadataGrid" jsId="metadataGrid" dojoType="dojox.grid.DataGrid" structure="layout1"></div>
						</td>
					</tr>
				</table>
				</div>
				<div dojoType="dijit.Dialog" id="dialog2" title="Permissions">
				<table>
					<tr>
						<td width="600px">   <!-- dojoType="" structure="layout2" dojox.Grid store="store2"-->
							<div id="permissionGrid"  structure="layout2" dojoType="dojox.grid.DataGrid" jsId="permissionGrid" selectionMode="single" onRowClick="getSelectedPermission"></div>
						</td>
						<td valign="top">
							<table>
								<tr>
									<td colspan="2"><span id="stickyControl"></span></td>
								</tr>
								<xsl:if test="$servertype='srb'">
								<tr>
									<td>Domain</td>
									<td><input name="domain" id="formDomain" jsId="formDomain" dojoType="dijit.form.FilteringSelect" autocomplete="true" searchAttr="name" store="store3"/></td>
								</tr>
								</xsl:if>
								<tr>
									<td>Username</td>
									<td><input name="username" id="formUsername" jsId="formUsername" dojoType="dijit.form.FilteringSelect" autocomplete="true" searchAttr="name" store="store4"/></td>
								</tr>
								<tr>
									<td>Permission</td>
									<td>
										<select id="formPerm" typdojoType="dijit.form.Select" name="permission">
											<option value="all">all</option>
											<option value="w">write</option>
											<option value="r">read</option>
											<option value="n">null</option>
											<xsl:if test="$servertype='srb'">
												<option value="c">curate</option>
												<option value="t">annotate</option>
											</xsl:if>
										<!-- 	<option value="o">owner</option> -->
										</select>
									</td>
								</tr>
								<tr>
									<td>Recursive</td>
									<td><input type="checkbox" name="recursive" id="recursive" dojoType="dijit.form.CheckBox"/></td>
								</tr>
								<tr>
									<td rowspan="2" align="center">
										<button onclick="savePermission()">Apply</button>
										<button onclick="dijit.byId('dialog2').hide();">Cancel</button>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
				</div>
            <!-- Dialogs end -->
            </body>
        </html>
    </xsl:template>
    <xsl:template match="D:multistatus">
        <xsl:apply-templates select="D:response[D:href = $href]" mode="base"/>
        <xsl:choose>
            <xsl:when test="D:response[D:href != $href]">
                <p>
                    <xsl:text>Total </xsl:text>
                    <xsl:value-of select="format-number(sum(D:response[not(D:propstat/D:prop/D:resourcetype/D:collection)]/D:propstat/D:prop/D:getcontentlength), '#,##0 bytes')"/>
                    <xsl:text> (</xsl:text>
                    <xsl:value-of select="format-number(round(sum(D:response[not(D:propstat/D:prop/D:resourcetype/D:collection)]/D:propstat/D:prop/D:getcontentlength) div 1024), '#,##0 KB')"/>
                    <xsl:text>).</xsl:text><br/>
                    <xsl:value-of select="format-number(count(D:response[D:href != $href]), '#,##0')"/>
                    <xsl:text> objects (</xsl:text>
                    <xsl:value-of select="format-number(count(D:response[D:href != $href][D:propstat/D:prop/D:resourcetype/D:collection]), '#,##0')"/>
                    <xsl:text> directories, </xsl:text>
                    <xsl:value-of select="format-number(count(D:response[not(D:propstat/D:prop/D:resourcetype/D:collection)]), '#,##0')"/>
                    <xsl:text> files):</xsl:text>
                </p>
                <table border="0" cellpadding="0" cellspacing="0">
                   	<tr valign="top">
                       <td>
                           <table border="0" cellpadding="6" cellspacing="0">
                    			<xsl:if test="D:response[D:href != $href][D:propstat/D:prop/D:resourcetype/D:collection]">
                                    <xsl:apply-templates select="D:response[D:href != $href][D:propstat/D:prop/D:resourcetype/D:collection]" mode="directory">
                                        <xsl:sort select="D:propstat/D:prop/D:displayname"/>
                                    </xsl:apply-templates>
                    			</xsl:if>
                    			<xsl:if test="D:response[not(D:propstat/D:prop/D:resourcetype/D:collection)]">
                                    <xsl:apply-templates select="D:response[not(D:propstat/D:prop/D:resourcetype/D:collection)]" mode="file">
                                        <xsl:sort select="D:propstat/D:prop/D:displayname"/>
                                    </xsl:apply-templates>
                    			</xsl:if>
                            </table>
                        </td>
                	</tr>
                </table>
            </xsl:when>
            <xsl:otherwise>
                <p>
                    <i>(Directory is empty)</i>
                </p>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="D:response" mode="base">
        <p>
            <a class="title" href="{$href}" folder="{$href}"><xsl:value-of select="$url"/></a><br/>
            <a class="unc" href="{$href}" folder="{$href}"><xsl:value-of select="$unc"/></a><br/>
            <xsl:text>Last modified on </xsl:text>
            <xsl:value-of select="D:propstat/D:prop/D:getlastmodified"/>
            <xsl:text>.</xsl:text>
            <xsl:if test="$url != '/'">
                <br/><a href="{$parent}" class="parent">Parent</a>
            </xsl:if>
        </p>
    </xsl:template>
    <xsl:template match="D:response" mode="directory">
        <tr valign="top">
            <td nowrap="nowrap">
		        <a href="{D:href}" class="directory">
		            <xsl:if test="D:propstat/D:prop/D:ishidden = '1'">
		                <xsl:attribute name="class">hiddendirectory</xsl:attribute>
		            </xsl:if>
		            <xsl:value-of select="D:propstat/D:prop/D:displayname"/>
		        </a>
            </td>
            <td align="right">
            	<xsl:text>directory</xsl:text>
            </td>
            <td class="properties">
                <xsl:value-of select="D:propstat/D:prop/D:getlastmodified"/>
            </td>
            <td nowrap="nowrap">
                <button onclick="getMetadata('{D:href}')">M</button>
                <button onclick="getDirPermission('{D:href}')">P</button>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="D:response" mode="file">
        <tr valign="top">
            <td nowrap="nowrap">
                <xsl:if test="position() mod 2 = 1">
                    <xsl:attribute name="bgcolor">#eeffdd</xsl:attribute>
                </xsl:if>
                <a href="{D:href}">
                    <xsl:if test="D:propstat/D:prop/D:ishidden = '1'">
                        <xsl:attribute name="class">hidden</xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="D:propstat/D:prop/D:displayname"/>
                </a>
            </td>
            <td align="right">
                <xsl:if test="position() mod 2 = 1">
                    <xsl:attribute name="bgcolor">#eeffdd</xsl:attribute>
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="number(D:propstat/D:prop/D:getcontentlength) > 1024">
                        <xsl:value-of select="format-number(round(number(D:propstat/D:prop/D:getcontentlength) div 1024), '#,##0 KB')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="format-number(D:propstat/D:prop/D:getcontentlength, '#,##0 bytes')"/>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td class="properties">
                <xsl:if test="position() mod 2 = 1">
                    <xsl:attribute name="bgcolor">#ddeecc</xsl:attribute>
                </xsl:if>
                <xsl:apply-templates select="D:propstat/D:prop" mode="properties"/>
            </td>
            <td nowrap="nowrap">
                <xsl:if test="position() mod 2 = 1">
                    <xsl:attribute name="bgcolor">#eeffdd</xsl:attribute>
                </xsl:if>
                <button onclick="getMetadata('{D:href}')">M</button>
                <button onclick="getFilePermission('{D:href}')">P</button>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="D:prop" mode="properties">
        <xsl:value-of select="D:getlastmodified"/>
        <xsl:if test="D:getcontenttype != 'application/octet-stream'">
            <br/><xsl:value-of select="D:getcontenttype"/>
        </xsl:if>
        <xsl:if test="D:isreadonly = '1'">
            <br/><xsl:text>Read-Only</xsl:text>
        </xsl:if>
        <xsl:if test="D:ishidden = '1'">
            <br/><i><xsl:text>Hidden</xsl:text></i>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
