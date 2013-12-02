/**
 * storage.js, (c) 2011, Immanuel Albrecht; Dresden University of Technology,
 * Professur für die Psychologie des Lernen und Lehrens
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * creates a new Storage object that takes care of storing and reloading
 * information
 */

function Storage() {
	this.dataFields = [];
	this.dataFieldNames = [];
	this.localOnlyDataFields = [];

	this.tickingFields = [];

	this.storageInterval = null;

	/**
	 * return true, if we use the loglet applet to store/retrieve data on the
	 * server
	 */

	this.useLoglet = function() {
		return true;
	};

	/**
	 * registers a data field with a unique name, data field objects should
	 * provide:
	 * 
	 * .GetValue() which returns a string representation of the current state
	 * data
	 * 
	 * .SetValue(v) which restores a state given by a previously returned state
	 * data string
	 */

	this.RegisterField = function(datafield, name, onlylocal) {
		/**
		 * try to fix name collisions
		 */

		var collision = true;
		var append_nbr = 0;
		var good_name = name;

		while (collision == true) {
			collision = false;

			for ( var int = 0; int < this.dataFieldNames.length; int++) {
				if (this.dataFieldNames[int] == good_name) {
					collision = true;
					append_nbr = 1 + append_nbr;
					good_name = name + "(" + append_nbr + ")";
					break;
				}
			}
		}

		this.dataFields.push(datafield);
		this.dataFieldNames.push(good_name);

		if (onlylocal) {
			this.localOnlyDataFields.push(datafield);
		}
	};

	/**
	 * request to be regularly ticked before auto-storage
	 */

	this.RequestTicks = function(datafield) {
		if (this.tickingFields.lastIndexOf(datafield) < 0) {
			this.tickingFields.push(datafield);
		}
	};

	/**
	 * stores all data in the storage space using name to avoid collisions,
	 * storage can be sessionStorage or localStorage
	 */

	this.StoreIn = function(storage, name) {
		
		//myLogger.Log("myStorage: update volatile");
		
		/**
		 * update "volatile" input elements
		 */

		for ( var int = 0; int < this.tickingFields.length; int++) {
			var obj = this.tickingFields[int];

			if (obj.Tick) {
				obj.Tick();
			}
		}
		
		/**
		 * store in database and/or locally
		 */
		
		//myLogger.Log("myStorage: store data");

		for ( var int = 0; int < this.dataFieldNames.length; int++) {
			var obj = this.dataFields[int];

			if (obj.GetValue) {
				var v = obj.GetValue();
				var spacedname = name + "---" + this.dataFieldNames[int];
				var keyname = "myStorage" + spacedname;

				try {
					storage.setItem(keyname, v);
				} catch (e) {
					/** local storage not working */
				}

				if ((this.useLoglet())
						&& (this.localOnlyDataFields.lastIndexOf(obj) == -1)) {
					/**
					 * use server storage too
					 */
					if (v === null) {
						doSetIfDifferent(spacedname, "-null");
					} else {
						doSetIfDifferent(spacedname, "v" + v);
					}
				}

			}	
		}
		//myLogger.Log("myStorage: done.");
	};

	/**
	 * restores all data in the storage space using name to avoid collisions,
	 * storage can be sessionStorage or localStorage
	 */

	this.RestoreFrom = function(storage, name) {
		if (typeof myHover == "object") {
			if (myHover.CrashDown) {
				myHover.CrashDown();
			}
		}
		
		/**
		 * todo: try to get all possible entries from server, and restore values
		 */

		var server_entries = {};

		if (this.useLoglet()) {
			server_entries = doGetAll();
		}

		for ( var int = 0; int < this.dataFieldNames.length; int++) {
			var obj = this.dataFields[int];

			if (obj.SetValue) {
				var spacedname = name + "---" + this.dataFieldNames[int];
				var keyname = "myStorage" + spacedname;

				if (this.useLoglet()
						&& (this.localOnlyDataFields.lastIndexOf(obj) == -1)) {

					if (spacedname in server_entries) {
						var v = server_entries[spacedname];

						if (v.charAt(0) == 'v') {
							obj.SetValue(v.substr(1));
						}
					}

				} else {

					var v;
					try {
						v = storage.getItem(keyname);
					} catch (e) {
						v = null;
					}

					if (v !== null) {
						obj.SetValue(v);
					}
				}
			}

		}
	};

	/**
	 * this is called periodically and before answer checking...
	 */

	this.AutoUpdateAndStore = function() {

		myStorage.StoreIn(myStorageLocation, myStorageName);

	};

	/**
	 * set up automatic storage and restoring of contents
	 */
	this.SetupAutoRestore = function(storage, name) {
		myStorageLocation = storage;
		myStorageName = name;

		myStorageAncientUnloader = window.onunload;

		window.onunload = function() {
			myStorage.StoreIn(myStorageLocation, myStorageName);

			if (myStorageAncientUnloader) {
				myStorageAncientUnloader();
			}
		};

		myStorage.RestoreFrom(myStorageLocation, myStorageName);

		/**
		 * periodically save current page state
		 */

		this.storageInterval = setInterval(this.AutoUpdateAndStore, 900 + Math
				.floor(Math.random() * 200));
	};

}

myStorage = new Storage();