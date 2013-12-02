/**
 * stringids.js, (c) 2013, Immanuel Albrecht; Dresden University of Technology,
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
  * this class provides functionality of an n-ary string cross product
  */
  
function StringProduct(vfactor) {
	
	this.arity = vfactor.length;
	
	this.vfactor = vfactor;
	
	this.vupper = [];
	
	if (this.arity)
		this.cardinality = 1;
	else
		this.cardinality = 0;
	
	this.modules = [];
	
	for (var int=0; int < this.arity; int++) {
		var factor = vfactor[int];
		this.cardinality *= factor.length;
		this.modules.push(factor.length);
		var upper = [];
		for (var int2=0; int2<factor.length; ++int2) {
			upper.push(factor[int2].toUpperCase());
		}
		
		this.vupper.push(upper);
	}
	
	/**
	 * @returns the number of strings in the product
	 */
	
	this.GetCount = function() {
		return this.cardinality;
	};
	
	/**
	 * @returns the n-th string in the product
	 */
	
	this.GetString = function(n) {
		if (n>=this.cardinality)
			return "!!ERROR--String-Id-Too-Big!!";
		var suffix = "";
		var sigma = n;
		for (var i = this.arity; i > 0;) {
			i -= 1;
			
			var sigma_i = sigma % this.modules[i];
			var factor = this.vfactor[i];
			sigma = Math.floor(sigma / this.modules[i]);
			
			suffix = factor[sigma_i] + suffix;
		}		
		return suffix;
	};
	
	
	/**
	 * @returns the nbr of the given string in the product, or -1
	 */
	 
	this.GetIndex = function(s) {
		var normalized = s.toUpperCase().trim();
		var normal_len = normalized.length;
		var sigma = [];
		for (var i = 0; i < this.arity; ++i) {
			sigma.push(0);
		}
		
		var found = false;
		
		while (!found) {
			found = true;
			
			var start = 0;
			for (var i = 0; i < this.arity; ++i) {
				var cmp = this.vupper[i][sigma[i]];
				var len = cmp.length;
				var part = normalized.substr(start,len);
				if (cmp == part) {
					
					start += len;
				} else {
					found = false;
					
					
					sigma[i] += 1;
					for (var j = i+1; j<this.arity;++j)
					{
						sigma[j] = 0;
					}
					
					while (sigma[i] >= this.modules[i]) {
						sigma[i] = 0;
						i-=1;
						if (i < 0)
							return -1;
						sigma[i] += 1;
					}
					break;
				}
				
			}	
			
			if (found)
			{
				if (start != normal_len) {
					found = false;
					
					var i = this.arity - 1;
					
					sigma[i] += 1;
					
					
					while (sigma[i] >= this.modules[i]) {
						sigma[i] = 0;
						i-=1;
						if (i < 0)
							return -1;
						sigma[i] += 1;
					}
				}		
			}
		}
				
		var idx = sigma[0];
		for (var i = 1; i < this.arity; ++i)
		{
			idx *= this.modules[i-1];
			idx += sigma[i];
		}
		
		return idx;
	};
	return this;
}

/**
 * this class provides functionality to make ids from strings, i.e. this class
 * maps filled sentence templates to integer ids and vice versa
 */

function StringIds() {
	this.count = 0;
	this.products = [];
	

	/**
	 * add another string to the id database
	 * 
	 * @returns this
	 */

	this.AddString = function(s) {
		this.products.push(new StringProduct([[s]]));
		this.count += 1;
		return this;
	};

	/**
	 * add a Cartesian concatenation of sets of strings to the id database
	 * 
	 * @param vfactor
	 *            vector of factor sets
	 * 
	 * @returns this
	 */
	this.AddStringProduct = function(vfactor) {
		if (vfactor.length < 1) {
			return this;
		}
		
		var product = new StringProduct(vfactor);
		
		this.products.push(product);
		
		this.count += product.GetCount();

		return this;
	};

	/**
	 * s input string
	 * 
	 * @returns the id of the given string, or -1 if the string is unknown
	 */

	this.ToId = function(s) {
		var add = 0;
		for (var int=0;int<this.products.length;++int){
			var product = this.products[int];
			var id = product.GetIndex(s);
			if (id < 0)
				add += product.GetCount();
			else
				return id + add;
		}
		
		return -1;
	};
	
	/**
	 * vs  array of input strings
	 * 
	 * @returns  an array that contains the ids of the given strings, or -1 for unknown strings
	 */

	this.ToIds = function(vs) {
		var rs = [];
		for ( var int = 0; int < vs.length; int++) {
			var s = vs[int];
			rs.push(this.ToId(s));
		}
		return rs;
	};

	/**
	 * id
	 * 
	 * @returns the string that corresponds to the given id
	 */

	this.FromId = function(id) {
		if ((id < 0)||(id >= this.count))
			return "!!UNKNOWN STRING!!";
		for (var int=0;int<this.products.length;++int){
			var product = this.products[int];
			var c = product.GetCount();
			if (c > id) {
				return product.GetString(id);
			} else {
				id -= c;
			}
		}
	};
	
	/**
	 * vid   array of ids
	 * 
	 * @returns  an array that contains the strings corresponding to the ids
	 */

	this.FromIds = function(vid) {
		var rs = [];
		for ( var int = 0; int < vid.length; int++) {
			var id = vid[int];
			rs.push(this.FromId(id));
		}
		return rs;
	};

	
	return this;
}


/**
 * this class provides functionality to make ids from strings, i.e. this class
 * maps filled sentence templates to integer ids and vice versa
 */

function StringIdsLEGACY() {
	this.count = 0;
	this.to_id = {};
	this.from_id = {};

	/**
	 * add another string to the id database
	 * 
	 * @returns this
	 */

	this.AddString = function(s) {
		var unified = s.toUpperCase().trim();
		if (!(unified in this.to_id)) {
			this.to_id[unified] = this.count;
			this.from_id[this.count] = s;
			this.count += 1;
		}

		return this;
	};

	/**
	 * add a Cartesian concatenation of sets of strings to the id database
	 * 
	 * @param vfactor
	 *            vector of factor sets
	 * 
	 * @returns this
	 */
	this.AddStringProduct = function(vfactor) {
		if (vfactor.length < 1) {
			return this;
		}

		var sigma = [];

		for ( var int = 0; int < vfactor.length; int++) {
			var factor = vfactor[int];
			if (factor.length < 1) {
				return this;
			}
			sigma.push(0);
		}
		
		var stop_criterion = vfactor[0].length;
		
		while (sigma[0] < stop_criterion) {
			/**
			 * add the string corresponding to sigma
			 */
			
			var concat = "";
			
			for (var i = 0; i < sigma.length; ++i) {
				concat += vfactor[i][sigma[i]];
			}
			
			this.AddString(concat);
			
			/**
			 * next permutation
			 */

			sigma[sigma.length - 1] += 1;
			for (var i = sigma.length - 1; i > 0; --i) {
				if (sigma[i] >= vfactor[i].length) {
					sigma[i] = 0;
					sigma[i - 1] += 1;
				} else {
					break;
				}
			}
			
		}

		return this;
	};

	/**
	 * s input string
	 * 
	 * @returns the id of the given string, or -1 if the string is unknown
	 */

	this.ToId = function(s) {
		var unified = s.toUpperCase().trim();
		if (unified in this.to_id)
			return this.to_id[unified];
		return -1;
	};
	
	/**
	 * vs  array of input strings
	 * 
	 * @returns  an array that contains the ids of the given strings, or -1 for unknown strings
	 */

	this.ToIds = function(vs) {
		var rs = [];
		for ( var int = 0; int < vs.length; int++) {
			var s = vs[int];
			rs.push(this.ToId(s));
		}
		return rs;
	};

	/**
	 * id
	 * 
	 * @returns the string that corresponds to the given id
	 */

	this.FromId = function(id) {
		if (id in this.from_id)
			return this.from_id[id];
		return "!!UNKNOWN STRING!!";

	};
	
	/**
	 * vid   array of ids
	 * 
	 * @returns  an array that contains the strings corresponding to the ids
	 */

	this.FromIds = function(vid) {
		var rs = [];
		for ( var int = 0; int < vid.length; int++) {
			var id = vid[int];
			rs.push(this.FromId(id));
		}
		return rs;
	};

	
	return this;
}


