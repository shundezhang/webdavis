Updates (since 0.8.2):

  * Error reporting has been improved to give more informative messages for a number of actions (eg rename, restore, or upload to an already existing file).

  * Refreshing/updating of the display has been optimised, so you shouldn't see much in the way of flicker or redraws when not strictly necessary.

  * Uploading an existing file now produces an error dialog.

  * Davis now uses dojo 1.4.2.

  * Added a new config parameter "disableSLCSAuthentication". Default is true. You can enable SLCS by setting this to false.

[[BR](BR.md)]
New features:

  * File listing is dojo grid-based rather than pure html which means:
    * Row selection rather than checkboxes for item selection
    * Listing has its own scrollbar so you don't lose the breadcrumb or table headings while scrolling
    * Columns can be reordered by dragging the header cell (order is not saved between sessions though)
    * Column sorting is supported by clicking header cells
    * Column hiding is supported by right clicking on any header cell and selecting which columns you want to see (not preserved between sessions at the moment)
    * Columns can be resized by dragging header cell edges. Note that once you resize a column, the table will no longer auto size if you resize the browser.

  * The listing table is now backed by an on-demand store (dojo's QueryReadStore) which only returns the items that the table widget actually needs for display. So a directory containing 1000 items will display almost as quickly as one containing 50 items. The down side is that as you scroll down, you see empty cells until another call to the store is complete.

  * New Move button. User can chose a destination to move files to.

  * New status line below the breadcrumb to show how many items are listed and how many are selected.

  * Long file names in the file listing are now wrapped to multiple lines where necessary.

  * Added a new configuration item 'organisation-support' whose value will appear in various places when the user is advised to contact user support.

  * UI can be reloaded dynamically now. See [wiki:HowTo/DynamicUIReload here] for details

  * New Help/About button. Config item 'helpURL' causes given URL to be launched in new browser window, or if not a URL, displays given test in a dialog.

  * Davis now logs memory usage information every hour (on first service request since 1hr after previous logging).

[[BR](BR.md)]
Bug Fixes:

  * Fixed major WebDAV bug introduced in 0.9.0.

  * Select All/None button behaves correctly. It used to often not match the state of selections if an error occurred or when changing directories.

  * The icon in the files column of the listing table is now part of the link, so you can click on the icon to change directory too.

  * File names containing multiple contiguous spaces appeared as if they had one space. Fixed.

  * Upload won't submit now if the file name field is empty.

  * Fixed bug where a user doesn't set adequate permissions for another user to read a file direct from its URL. Server would return 500 with a stack trace - now it returns 403.

  * User lists for access control are no longer limited to 300 items.

  * File listing now ensures each item listed is a clean replica (rather than always using replica 0).

  * Downloading/clicking on a file link now always returns an uncorrupt copy, although the copy may be dirty. A modified version of Jargon was needed for this (included). The next release will hopefully ensure a clean copy is always returned.


[[BR](BR.md)]
Notes:

  * The Apple key is used to select additional items on a Mac rather than the control key.