New features:

  * Davis can now restore the iRODS connection automatically when an iRODSAgent freezes (not just when an agent dies).

  * New search facility implemented.

  * A new 'tag metadata' (or 'Simple Metadata') facility has been added, which allows simple tags to be assigned to files or collections. The existing metadata dialog is now called 'Advanced Metadata' and can be accessed from the 'Advanced' button of the Simple Metadata dialog.

  * Column widths for the main listing table can now be reset via the header right-click menu.

  * Sort on QuickShare column now works.

  * New status popup indicates what Davis is doing when it's busy (eg reading contents of a collection during navigation).

  * In davis config files, the ui-include items now allow a file to be specified. If the value for the item begins with '<', it is assumed to be code, otherwise it will be treated as a file path.

[[BR](BR.md)]

Bug Fixes and Updates:

  * Davis' UI now has a style sheet. By default this is davis/webapps/include/davis.css but can be changed with the davis-style-sheet config item. NOTE: if you are using Apache in front of Davis, you may need to change Apache's configuration to pass requests for the 'include' webapp to Davis, or add a soft link to 'davis/webapps/include' in Apache's document root. Davis also looks for a second style sheet (default is davis/webapps/include/davis-override.css) which will be read after the first. By default this file doesn't exist, and so a custom styles file can be added in order to override the look of the UI.

  * Fixed the alignment of the listing grid header right click menu.

  * If a user logged in via https myproxy and then used an http url, the connection used the existing myproxy auth rather than establishing a new shib connection. Now the user will have an authenticated shib session.

  * For the scenario described by the last item, no logout button would appear when the http URL was used. This has now been fixed.

  * The 'advanced metadata' dialog behaves more intuitively when adding metadata. The user no longer needs to click outside the table fields for new entries to be recognised. This overrides the Dojo library's default behaviour.

  * Consecutive spaces in metadata items in the advanced metadata dialog are no longer displayed compacted to a single space. Dojo's behaviour has been overridden to display multiple spaces verbatim.

  * Recursive permissions operations no longer fail when applied to multiple selected items where some selected items are files.


[[BR](BR.md)]

Issues:

  * Internet Explorer 9 currently doesn't display Davis correctly. This will be investigated.